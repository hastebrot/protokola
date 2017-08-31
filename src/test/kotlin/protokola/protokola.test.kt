package protokola

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.todo

class ProtokolaTest {

    @Test
    fun `should retrieve resource`() {
        // given:
        val proto = Protokola()

        // expect:
        assertEquals("foo", proto.resource("foo").name)
    }

    @Test
    fun `should retrieve messages`() {
        // given:
        val proto = Protokola()

        // expect:
        assertEquals(emptyList(), proto.resource("foo").messages().toList())
    }

    @Test
    fun `should generate message`() {
        // given:
        val message = Message(listOf(prop("name"), value("bar"), prop("age"), value(45)))

        // expect:
        assertEquals(listOf("name", "bar", "age", 45), message.data)
    }

    @Test
    fun `should test something`() {
        todo {
            assertEquals(4, 2 + 2)
        }
    }

    data class Person(var name: String, var age: Int)
    data class PersonList(var personList: List<Person>)

    fun prop(name: String) = name
    fun value(value: Any) = value

}
