package protokola

data class Message<out T>(val payload: T)

typealias MessageHandler = (message: Message<*>) -> Unit

class MessageBus {
    private val messageHandlers = mutableListOf<MessageHandler>()

    fun subscribe(messageHandler: MessageHandler): MessageHandlerSubscription {
        messageHandlers += messageHandler
        return object : MessageHandlerSubscription {
            override fun unsubscribe() {
                messageHandlers -= messageHandler
            }
        }
    }

    fun dispatch(message: Message<*>) {
        messageHandlers.forEach { handler -> handler(message) }
    }
}

interface MessageHandlerSubscription {
    fun unsubscribe()
}