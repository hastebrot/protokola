package protokolax

import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerialClassDesc
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.JSON
import protokola.demo
import kotlin.reflect.jvm.jvmName

@Serializable
data class Data(val id: Int,
                @SerialName("s") val str: String)

@Serializable
data class OtherData(val id: Int) {

    @Serializer(forClass = OtherData::class)
    companion object : KSerializer<OtherData> {
        override val serialClassDesc: KSerialClassDesc
            = SerialClassDescImpl(OtherData::class.jvmName)

        override fun load(input: KInput)
            = OtherData(input.readIntValue())

        override fun save(output: KOutput, obj: OtherData)
            = TODO("not implemented")
    }

}

fun main(args: Array<String>) {
    val json = JSON(
        indented = false,
        context = SerialContext()
    )

    demo {
        val data = Data(123, "foo")
        println(json.stringify(data))
    }

    demo {
        val string = """ { "id": 123, "s": "foo" } """
        println(json.parse<Data>(string))
    }

    demo {
        val string = """123"""
        println(json.parse<OtherData>(string).id)
    }
}
