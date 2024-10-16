package moe.caa.multilogin.api.eventbus.internal

import moe.caa.multilogin.api.eventbus.EventBus
import moe.caa.multilogin.api.eventbus.EventHandler
import moe.caa.multilogin.api.exception.DuplicateRegistrationEventHandleException
import java.util.concurrent.CopyOnWriteArrayList

internal class InternalHandlerList<Event : Any> {
    private val handlerDataList = CopyOnWriteArrayList<EventBus.HandlerData<Event>>()

    @Synchronized
    internal fun addHandlerData(eventHandler: EventHandler<Event>, priority: Int = 0) {
        if (handlerDataList.any { it.eventHandler == eventHandler }) {
            throw DuplicateRegistrationEventHandleException("Duplicate registration event handler $eventHandler")
        }

        handlerDataList.add(EventBus.HandlerData(eventHandler, priority))
        handlerDataList.sortByDescending { it.priority }
    }

    internal fun removeHandlerData(test: (EventBus.HandlerData<Event>) -> Boolean) {
        handlerDataList.removeAll(test)
    }

    internal fun callEvent(event: Event) {
        handlerDataList.forEach { it.eventHandler.handle(event) }
    }
}