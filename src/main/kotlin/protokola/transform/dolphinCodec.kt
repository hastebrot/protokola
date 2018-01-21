package protokola.transform

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import protokola.println
import protokola.transport.Transport

fun main(vararg args: String) {
    val responseContext = Transport.ServerResponse(200, """
        [{"p_id":"f6a23423-d104-41a8-84a9-c793ae2cee32","t":"@@@ HIGHLANDER_BEAN @@@","a":[{"n":"@@@ SOURCE_SYSTEM @@@","a_id":"920S","v":"server"},{"n":"controllerName","a_id":"921S","v":null},{"n":"controllerId","a_id":"922S","v":null},{"n":"model","a_id":"923S","v":null}],"id":"CreatePresentationModel"}]
    """.trimIndent())
    responseContext.println

    val codec = DolphinCodec()

    val commands = codec.fromJson(responseContext)
    commands.println

    "commands".println
    commands.forEach {
        it.println
        it["id"].println
    }

    val commandsJson = codec.toJson(commands)
    commandsJson.println
}

class DolphinCodec {
    private val jsonAdapter = simpleJsonAdapter()

    fun fromJson(response: Transport.ServerResponse) =
        jsonAdapter.fromJson(response.body!!)!!

    fun toJson(commands: JsonCommandList) =
        jsonAdapter.toJson(commands)!!

    fun toCommand(response: Transport.ServerResponse): DolphinCommand.CreatePresentationModel {
        val commands = fromJson(response)
        val command = DolphinCommand.CreatePresentationModel(
            commands.index(0).keyed("id"),
            commands.index(0).keyed("p_id"),
            commands.index(0).keyed("t"),
            commands.index(0)!!.keyed<Any, List<Any>>("a").map {
                DolphinCommand.CreatePresentationModel.Attribute(
                    it.keyed("a_id"),
                    it.keyed("n"),
                    it.keyed("v")
                )
            }
        )
        return command
    }
}

object DolphinCommand {
    class CreateContext

    class DestroyContext

    data class CreatePresentationModel(val id: String,
                                       val presentationModelId: String,
                                       val type: String,
                                       val attributes: List<Attribute>) {
        data class Attribute(val attributeId: String,
                             val name: String,
                             val value: Any?)
    }

    class DeletePresentationModel

    class CreateController

    class DestroyController

    class ValueChanged

    class CallAction
}

private fun simpleJsonAdapter(): JsonAdapter<JsonCommandList> {
    val moshi = Moshi.Builder().build()
    val listOfCommands = Types.newParameterizedType(
        List::class.java,
        Map::class.java
    )
    return moshi.adapter(listOfCommands)
}

typealias JsonCommandList = List<Map<String, *>>

fun <T> T.index(index: Int) = (this as List<*>)[index]
fun <T> T.key(key: String) = (this as Map<*, *>)[key]

inline fun <T, reified R> T.indexed(index: Int) = index(index) as R
inline fun <T, reified R> T.keyed(key: String) = key(key) as R
