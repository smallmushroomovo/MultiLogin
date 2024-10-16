package moe.caa.multilogin.api.eventbus

/**
 * 表示一个事件处理器
 */
interface EventHandler<Event> {
    fun handle(event: Event)
}