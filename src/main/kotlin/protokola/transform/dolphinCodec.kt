package protokola.transform

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import protokola.transport.Transport

fun main(args: Array<String>) {
    val moshi = Moshi.Builder().build()

    val listOfCommands = Types.newParameterizedType(
        List::class.java,
        Map::class.java
    )

    val jsonAdapter = moshi.adapter<List<Map<String, *>>>(listOfCommands)

    val responseContext = Transport.Response(200, """
        [{"p_id":"f6a23423-d104-41a8-84a9-c793ae2cee32","t":"@@@ HIGHLANDER_BEAN @@@","a":[{"n":"@@@ SOURCE_SYSTEM @@@","a_id":"920S","v":"server"},{"n":"controllerName","a_id":"921S","v":null},{"n":"controllerId","a_id":"922S","v":null},{"n":"model","a_id":"923S","v":null}],"id":"CreatePresentationModel"}]
    """.trimIndent())

    println(responseContext)
    println(jsonAdapter.fromJson(responseContext.body))
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
