package protokola

data class Message<out T>(val payload: T)

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

typealias MessageHandler = (message: Message<*>) -> Unit

interface MessageHandlerSubscription {
    fun unsubscribe()
}