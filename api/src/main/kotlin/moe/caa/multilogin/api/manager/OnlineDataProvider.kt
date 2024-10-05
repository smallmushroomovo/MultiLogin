package moe.caa.multilogin.api.manager

import moe.caa.multilogin.api.data.LoginSource
import moe.caa.multilogin.api.player.MultiLoginPlayer
import java.util.*

interface OnlineDataProvider {
    /**
     * 通过游戏内UUID获取当前在线档案持有用户登录信息
     */
    fun findLoginSourceByInGameUUID(inGameUUID: UUID): LoginSource?

    /**
     * 通过角色实例获取当前在线档案持有用户登录信息
     */
    fun findLoginSourceByPlayer(player: MultiLoginPlayer) = findLoginSourceByInGameUUID(player.profile.minimalProfile.id)
}