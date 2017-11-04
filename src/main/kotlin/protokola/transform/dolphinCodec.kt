package protokola.transform

import protokola.transport.Transport

fun main(args: Array<String>) {
    val responseContext = Transport.Response(200, """
        [{"p_id":"f6a23423-d104-41a8-84a9-c793ae2cee32","t":"@@@ HIGHLANDER_BEAN @@@","a":[{"n":"@@@ SOURCE_SYSTEM @@@","a_id":"920S","v":"server"},{"n":"controllerName","a_id":"921S","v":null},{"n":"controllerId","a_id":"922S","v":null},{"n":"model","a_id":"923S","v":null}],"id":"CreatePresentationModel"}]
    """.trimIndent())

    println(responseContext)
}

sealed class DolphinCommand {
    class CreateContext() : DolphinCommand()
    class DestroyContext() : DolphinCommand()

    class CreatePresentationModel() : DolphinCommand()
    class DeletePresentationModel() : DolphinCommand()

    class CreateController() : DolphinCommand()
    class DestroyController() : DolphinCommand()

    class ValueChanged() : DolphinCommand()
    class CallAction() : DolphinCommand()
}
