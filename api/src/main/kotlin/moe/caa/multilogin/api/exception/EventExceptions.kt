package moe.caa.multilogin.api.exception

sealed class EventException(message: String) : APIException(message)

sealed class ListenerRegistrationException(message: String) : EventException(message)

class IllegalStaticListenerRegistrationException(message: String) : ListenerRegistrationException(message)