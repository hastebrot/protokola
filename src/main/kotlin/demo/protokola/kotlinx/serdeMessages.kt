package demo.protokola.kotlinx

import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTreeMapper
import kotlinx.serialization.json.content

fun main(args: Array<String>) {
    val inputFoo = """{"id": 0, "type": "foo", "payload": {"value": 1}}"""
    val inputBar = """{"id": 1, "type": "bar", "payload": {"value": "a"}}"""

    val json = JSON()
    json.parse(MessageSerializer, inputFoo).println
    json.parse(MessageSerializer, inputBar).println
}

@Serializable
data class Message(
    val id: Int,
    val type: String,
    @Serializable val payload: MessagePayload
)

sealed class MessagePayload {
    @Serializable data class Foo(val value: Int) : MessagePayload()
    @Serializable data class Bar(val value: String) : MessagePayload()
}

@Serializer(forClass = Message::class)
object MessageSerializer : KSerializer<Message> {
    override fun load(input: KInput): Message {
        val jsonReader = input as? JSON.JsonInput
            ?: throw SerializationException("This class can be loaded only by JSON")
        val tree = jsonReader.readAsTree() as? JsonObject
            ?: throw SerializationException("Expected JSON object")
        tree.println

        val type = tree["type"].content
        type.println

        val payload = when (type) {
            "foo" -> JsonTreeMapper().readTree(tree["payload"], MessagePayload.Foo.serializer())
            "bar" -> JsonTreeMapper().readTree(tree["payload"], MessagePayload.Bar.serializer())
            else -> TODO("message type not supported")
        }

        return Message(0, type, payload)
    }

    override fun save(output: KOutput, obj: Message) = Unit
}
