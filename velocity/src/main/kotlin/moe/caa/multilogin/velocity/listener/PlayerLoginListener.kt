package moe.caa.multilogin.velocity.listener

import moe.caa.multilogin.velocity.main.MultiLoginVelocity

class PlayerLoginListener(
    private val plugin: MultiLoginVelocity
) {
    fun setup(){
        plugin.server.eventManager.register(plugin, this)
    }
}