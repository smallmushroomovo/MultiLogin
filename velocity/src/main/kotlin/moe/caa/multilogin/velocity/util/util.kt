package moe.caa.multilogin.velocity.util

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.VelocityBrigadierMessage
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.util.GameProfile
import com.velocitypowered.proxy.connection.client.ConnectedPlayer
import io.netty.channel.Channel
import moe.caa.multilogin.velocity.auth.GameData
import moe.caa.multilogin.velocity.inject.VelocityServerChannelInitializerInjector
import moe.caa.multilogin.velocity.main.MultiLoginVelocity
import moe.caa.multilogin.velocity.netty.ChannelInboundHandler
import net.kyori.adventure.text.Component
import java.io.File
import java.lang.reflect.AccessibleObject
import java.security.DigestInputStream
import java.security.MessageDigest
import java.sql.SQLIntegrityConstraintViolationException
import java.util.*


/**
 * 给定路径获得 Jar 包内资源
 */
fun getResource(resource: String) = MultiLoginVelocity::class.java
    .getResourceAsStream("/$resource") ?: throw Exception("Resource '$resource' not found")

/**
 * 保存 Jar 包内资源到指定文件夹中
 * @param resource 资源路径
 * @param cover 如果指定目标存在, 是否覆盖
 */
fun saveDefaultResource(folder: File, resource: String, cover: Boolean = false): File {
    val file = File(folder, resource)
    val exist = file.exists()

    if (!cover && exist) return file

    file.parentFile?.mkdirs()
    getResource(resource).use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file
}

fun File.md5Digest(): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    DigestInputStream(inputStream(), md).use {
        val buf = ByteArray(1024)
        while (it.read(buf) != -1) {}
        it.close()
    }
    return md.digest()
}

fun String.hexToByteArray(): ByteArray {
    val data = ByteArray(length / 2)
    for (i in indices step 2) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
    }
    return data
}

fun <T : AccessibleObject> T.access(): T {
    isAccessible = true
    return this
}

fun <T> Class<T>.enumConstant(name: String): T {
    return enumConstants.first { (it as Enum<*>).name == name }
}

// 大驼峰转下划线
fun String.camelCaseToUnderscore(): String {
    return Regex("([a-z])([A-Z])").replace(this) {
        "${it.groupValues[1]}_${it.groupValues[2].lowercase()}"
    }.lowercase()
}

fun String.componentText() = Component.text(this)

fun String.toUUIDOrNull(): UUID? {
    try {
        return UUID.fromString(
            this.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                "$1-$2-$3-$4-$5"
            )
        )
    } catch (ignored: java.lang.Exception) {
    }
    return null
}

fun Throwable.logCausedSQLIntegrityConstraintViolationOrThrow(throwable: Throwable = this) {
    if (cause !is SQLIntegrityConstraintViolationException) {
        throw throwable
    }
    MultiLoginVelocity.instance.logDebug(this.message, this)
}

fun <T : ArgumentBuilder<CommandSource, T>> ArgumentBuilder<CommandSource, T>.handler(handle: CommandContext<CommandSource>.() -> Unit): ArgumentBuilder<CommandSource, T> {
    executes {
        handle.invoke(it)
        return@executes 0
    }
    return this
}

fun <T : ArgumentBuilder<CommandSource, T>> ArgumentBuilder<CommandSource, T>.permission(
    permission: String
): ArgumentBuilder<CommandSource, T> {
    requires {
        it.hasPermission(permission)
    }
    return this
}

fun ArgumentBuilder<CommandSource, *>.thenLiteral(
    literal: String,
    literalBuilder: ArgumentBuilder<CommandSource, *>.() -> Unit
): ArgumentBuilder<*, *> {

    val builder = LiteralArgumentBuilder.literal<CommandSource>(literal)
    literalBuilder.invoke(builder)

    return this.then(builder)
}

fun ArgumentBuilder<CommandSource, *>.thenArgument(
    argument: String,
    argumentType: ArgumentType<*>,
    argumentBuilder: ArgumentBuilder<CommandSource, *>.() -> Unit
): ArgumentBuilder<*, *> {

    val builder: ArgumentBuilder<CommandSource, *> = RequiredArgumentBuilder.argument(argument, argumentType)
    argumentBuilder.invoke(builder)

    return this.then(builder)
}

fun ArgumentBuilder<CommandSource, *>.thenArgumentOptional(
    argument: String,
    argumentType: ArgumentType<*>,
    argumentBuilder: ArgumentBuilder<CommandSource, *>.() -> Unit
): ArgumentBuilder<*, *> {

    val builder: ArgumentBuilder<CommandSource, *> = RequiredArgumentBuilder.argument(argument, argumentType)
    argumentBuilder.invoke(builder)
    argumentBuilder.invoke(this)

    return this.then(builder)
}

fun <V> CommandContext<*>.getArgumentOrNull(name: String, clazz: Class<V>) =
    if (arguments.containsKey(name)) {
        getArgument(name, clazz)
    } else {
        null
    }

val CommandContext<CommandSource>.player: Player
    get() = source as? Player ?: throw SimpleCommandExceptionType(
        VelocityBrigadierMessage.tooltip(
            MultiLoginVelocity.instance.message.message("command_require_player_execute")
        )
    ).create()


val Player.channel: Channel
    get() = (this as ConnectedPlayer).connection.channel


var Player.gameData: GameData?
    get() = channel.getMultiLoginInboundHandler().gameData
    set(value){
        channel.getMultiLoginInboundHandler().gameData = value
    }


fun Channel.getMultiLoginInboundHandler(): ChannelInboundHandler {
    return this.pipeline().get(VelocityServerChannelInitializerInjector.MULTI_LOGIN_PACKET_INBOUND_HANDLER_NAME)
            as ChannelInboundHandler
}

fun moe.caa.multilogin.api.profile.GameProfile.toVelocityGameProfile(): GameProfile {
    return GameProfile(this.uuid, this.username, this.properties.map {
        GameProfile.Property(it.name, it.value, it.signature)
    })
}