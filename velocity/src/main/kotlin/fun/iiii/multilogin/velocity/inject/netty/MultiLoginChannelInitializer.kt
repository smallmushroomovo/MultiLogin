package `fun`.iiii.multilogin.velocity.inject.netty

import com.velocitypowered.proxy.connection.MinecraftConnection
import com.velocitypowered.proxy.network.ConnectionManager
import `fun`.iiii.multilogin.velocity.main.MultiLoginVelocity
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

class MultiLoginChannelInitializer(
    val originChannel: ChannelInitializer<Channel>
) : ChannelInitializer<Channel>() {

    companion object {
        const val MULTI_LOGIN_HANDLE_NAME = "multilogin-handler"
        private val INIT_CHANNEL_METHOD_HANDLER: MethodHandle;

        init {
            val methodLookup = MethodHandles.lookup()
            INIT_CHANNEL_METHOD_HANDLER = methodLookup.unreflect(
                ChannelInitializer::class.java.getDeclaredMethod("initChannel", Channel::class.java).apply {
                    isAccessible = true
                }
            )
        }

        fun init(plugin: MultiLoginVelocity) {
            val connectionManager: ConnectionManager = Class.forName("com.velocitypowered.proxy.VelocityServer")
                .getDeclaredField("cm").apply {
                    isAccessible = true
                }.let {
                    it.get(plugin.server) as ConnectionManager
                }

            val serverChannelInitializerHolder = connectionManager.getServerChannelInitializer()
            serverChannelInitializerHolder.set(
                MultiLoginChannelInitializer(serverChannelInitializerHolder.get())
            )
        }
    }


    override fun initChannel(channel: Channel) {
        INIT_CHANNEL_METHOD_HANDLER.invoke(originChannel, channel)

        channel.pipeline().addBefore(
            "handler", MULTI_LOGIN_HANDLE_NAME, MultiLoginChannelHandler(
                channel.pipeline().get("handler") as MinecraftConnection
            )
        )
    }
}