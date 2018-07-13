package demo.protokola.kotlinx

import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTreeMapper
import kotlinx.serialization.json.content

fun main(args: Array<String>) {
    val inputOk = """{"id": 0, "payload": {"msg": "Hello world", "from": 42, "to": 43}, "timestamp": 1000}"""
    val inputFail = """{"id": 1, "payload": {"error": "Connection timed out"}, "timestamp": 1001}"""

    JSON.parse(Event.serializer(), inputOk).println
    JSON.parse(Event.serializer(), inputFail).println
}

@Serializable
data class Event(
    val id: Int,
    @Serializable(with = PayloadEitherSerializer::class) val payload: PayloadEither,
    val timestamp: Long
)

sealed class PayloadEither {
    data class Fail(val errorMsg: String) : PayloadEither()
    data class Ok(val data: Payload) : PayloadEither()
}

@Serializable
data class Payload(val from: Long, val to: Long, val msg: String)

@Serializer(forClass = PayloadEither::class)
object PayloadEitherSerializer : KSerializer<PayloadEither> {
    override fun load(input: KInput): PayloadEither {
        val jsonReader = input as? JSON.JsonInput
            ?: throw SerializationException("This class can be loaded only by JSON")
        val tree = jsonReader.readAsTree() as? JsonObject
            ?: throw SerializationException("Expected JSON object")
        return when {
            tree.containsKey("error") -> PayloadEither.Fail(tree["error"].content)
            else -> PayloadEither.Ok(JsonTreeMapper().readTree(tree, Payload.serializer()))
        }
    }

    override fun save(output: KOutput, obj: PayloadEither) {
        val jsonWriter = output as? JSON.JsonOutput
            ?: throw SerializationException("This class can be saved only by JSON")
        val tree = when (obj) {
            is PayloadEither.Fail -> JsonObject(mapOf("error" to JsonPrimitive(obj.errorMsg)))
            is PayloadEither.Ok -> JsonTreeMapper().writeTree(obj.data, Payload.serializer())
        }
        jsonWriter.writeTree(tree)
    }
}

internal val <T> T.println get() = println(this)

