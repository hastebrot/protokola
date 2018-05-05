import protokola.demo
import protokola.println
import kotlin.reflect.KClass

data class Message<out T>(val payload: T)

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Message<*>.of() = this as Message<T>

fun main(args: Array<String>) {
    demo("types") {
        val bus = Bus()

        bus.listen(String::class).subscribe {
            println(it)
        }

        bus.listen(Any::class).subscribe {
            when (it) {
                is String -> println("string: " + it)
                else -> println(it)
            }
        }

        bus.publish("foo")
    }

    demo("boxed") {
        val bus = Bus()

        bus.listen(Message::class).subscribe {
            println("simple: $it")
        }

        fun handleString(msg: Message<String>) = ("str: ${msg.payload}").println
        fun handleInt(msg: Message<Int>) = ("int: ${msg.payload}").println

        bus.listen(Message::class).subscribe {
            when (it.payload) {
                is String -> handleString(it.of())
                is Int -> handleInt(it.of())
            }
        }

        bus.publish(Message("foo"))
        bus.publish(Message(32))
    }
}

class Bus {

    private val listeners = mutableListOf<Listener<*>>()

    fun <T : Any> publish(instance: T) {
        listeners.forEach {
            if (it.type.isInstance(instance)) {
                it.action(instance)
            }
        }
    }

    fun <T : Any> listen(type: KClass<T>): Listener<T> {
        val listener = Listener(type)
        listeners += listener
        return listener
    }

}

class Listener<T : Any>(val type: KClass<T>) {

    private var action: ((T) -> Unit)? = null

    fun action(instance: Any) = action?.invoke(instance as T)

    fun subscribe(action: (T) -> Unit): Listener<T> {
        this.action = action
        return this
    }

    fun unsubscribe() = Unit

}
