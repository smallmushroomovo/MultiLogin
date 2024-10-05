package moe.caa.multilogin.api.eventbus

import moe.caa.multilogin.api.event.IEvent
import moe.caa.multilogin.api.exception.IllegalStaticListenerRegistrationException
import java.lang.reflect.Modifier

class EventBus {
    fun callEvent(event: IEvent){

    }

    fun registerListener(any: Any){
        for (method in any::class.java.declaredMethods) {
            val listener = method.getAnnotation(Listener::class.java) ?: continue
            if (Modifier.isStatic(method.modifiers)) {
                throw IllegalStaticListenerRegistrationException("listener method $method must not be static")
            }
        }
    }

    fun unregisterListener(any: Any){

    }
}