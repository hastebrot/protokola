package protokola.transform

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import protokola.transport.Transport

fun main(args: Array<String>) {
    val jsonAdapter = simpleJsonAdapter()

    val responseContext = Transport.ServerResponse(200, """
        [{"p_id":"f6a23423-d104-41a8-84a9-c793ae2cee32","t":"@@@ HIGHLANDER_BEAN @@@","a":[{"n":"@@@ SOURCE_SYSTEM @@@","a_id":"920S","v":"server"},{"n":"controllerName","a_id":"921S","v":null},{"n":"controllerId","a_id":"922S","v":null},{"n":"model","a_id":"923S","v":null}],"id":"CreatePresentationModel"}]
    """.trimIndent())

    println(responseContext)
    println(jsonAdapter.fromJson(responseContext.body))
}

private fun simpleJsonAdapter(): JsonAdapter<List<Map<String, *>>> {
    val moshi = Moshi.Builder().build()
    val listOfCommands = Types.newParameterizedType(
        List::class.java,
        Map::class.java
    )
    return moshi.adapter(listOfCommands)
}

object DolphinCommand {
    class CreateContext
    class DestroyContext

    class CreatePresentationModel
    class DeletePresentationModel

    class CreateController
    class DestroyController

    class ValueChanged
    class CallAction
}
