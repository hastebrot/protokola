package protokolax

import kotlinx.serialization.SerialContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON

@Serializable
data class Data(val id: Int,
                val str: String)

fun main(args: Array<String>) {
    val json = JSON(
        indented = false,
        context = SerialContext()
    )

    run {
        val data = Data(123, "foo")
        println(json.stringify(data))
    }

    run {
        val string = """ { "id": 123, "str": "foo" } """
        println(json.parse<Data>(string))
    }
}
