package protokola

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MessageTest {
    @Test
    fun `subscribe`() {
        // given:
        val bus = MessageBus()
        val messages = mutableListOf<Message<*>>()

        // when:
        bus.subscribe { messages += it }
        bus.dispatch(Message("foo"))

        // then:
        assertEquals<Any>(listOf(Message("foo")), messages)
    }

    @Test
    fun `unsubscribe`() {
        // given:
        val bus = MessageBus()
        val messages = mutableListOf<Message<*>>()

        // when:
        bus.subscribe { messages += it }.unsubscribe()
        bus.dispatch(Message("foo"))

        // then:
        assertEquals<Any>(listOf<Any>(), messages)
    }
}