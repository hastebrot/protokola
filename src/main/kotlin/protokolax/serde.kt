package protokolax

//import kotlinx.serialization.KInput
import kotlinx.serialization.SerialContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
//import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JSON

@Serializable
data class Data(val id: Int,
                @SerialName("s") val str: String)

@Serializable
data class OtherData(val id: Int) {

//    @Serializer(forClass = OtherData::class)
//    companion object {
//        override fun load(input: KInput) = OtherData(input.readIntValue())
//    }

}

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
        val string = """ { "id": 123, "s": "foo" } """
        println(json.parse<Data>(string))
    }

    run {
        val string = """ { "id": 123 } """
        println(json.parse<OtherData>(string).id)
    }
}
