package moe.caa.multilogin.api.eventbus


import moe.caa.multilogin.api.eventbus.internal.InternalHandlerList
import java.util.concurrent.ConcurrentHashMap

/**
 * 事件管理器
 */
class EventBus {

    /**
     * 注册一个指定事件类型的监听器
     *
     * @param eventType 指定的事件类型
     * @param handler 事件监听处理器
     * @param priority 事件优先级
     */
    @JvmOverloads
    fun <Event : Any> register(eventType: Class<Event>, handler: EventHandler<Event>, priority: Int = 0) {
        getEventHandlerList(eventType).addHandlerData(handler, priority)
    }

    /**
     * 注销一个事件监听器, 或者时事件监听器对象
     *
     * @param listenerOrHandler 事件监听处理器 或者是事件监听器对象
     */
    fun unregister(listenerOrHandler: Any){
        eventDataList.values.forEach { handlerList ->
            handlerList.removeHandlerData {
                when (it.eventHandler) {
                    listenerOrHandler -> true
                    else -> false
                }
            }
        }
    }

    /**
     * 触发一个事件
     *
     * @param event 事件对象
     */
    fun <Event : Any> callEvent(event: Event) {
        getEventHandlerList(event.javaClass).callEvent(event)
    }


    private val eventDataList = ConcurrentHashMap<Class<*>, InternalHandlerList<*>>()

    @Suppress("UNCHECKED_CAST")
    private fun <Event : Any> getEventHandlerList(eventType: Class<Event>): InternalHandlerList<Event> {
        return eventDataList.computeIfAbsent(eventType) { InternalHandlerList<Event>() } as InternalHandlerList<Event>
    }

    internal data class HandlerData<Event>(
        val eventHandler: EventHandler<Event>,
        val priority: Int
    )
}