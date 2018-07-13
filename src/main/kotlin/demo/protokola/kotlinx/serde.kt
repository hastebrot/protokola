package demo.protokola.kotlinx

import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerialClassDesc
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Optional
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerialContext
import kotlinx.serialization.SerialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import protokola.demo
import kotlin.reflect.jvm.jvmName

fun main(args: Array<String>) {
    demo {
        val value = "hello"
        println(JSON.stringify(StringSerializer, value))
    }

    demo {
        val value = "hello"
        val json = JSON(context = SerialContext().apply {
            registerSerializer(String::class, StringSerializer)
        })
        println(json.stringify(value))
    }

    val json = JSON(
        indented = false,
        nonstrict = true,
        context = SerialContext()
    )

    demo {
        val string = """123"""
        println(json.parse<OtherData>(string).id)
//        println(json.stringify(OtherData(123)))
    }

    demo {
        val data = Data(123, "foo")
        println(json.stringify(data))
    }

    demo {
        val string = """
            { "id": 123, "s": "foo" }
        """
        println(json.parse(Data.serializer(), string))
    }

    demo {
        val string = """
            [{ "id": 123, "s": "foo" }, { "id": 234, "s": "foo" }]
        """
        println(json.parse(Data.serializer().list, string))
    }

    demo {
        val string = """
            [{ "id": 123, "s": "foo" }, { "id": 234, "b": true }]
        """
        println(json.parse(DataWithBool.serializer().list, string))
    }

    demo {
        val json = JSON(
            indented = false,
            nonstrict = true,
            context = SerialContext().apply {
                registerSerializer(Data::class, Data.serializer())
                registerSerializer(DataWithBool::class, DataWithBool.serializer())
            }
        )

        val string = """
            [["demo.protokola.kotlinx.Data", { "id": 123, "s": "foo" }],
             ["demo.protokola.kotlinx.DataWithBool", { "id": 234, "b": true }]]
        """
        println(json.parse(PolymorphicSerializer.list, string))
    }

    demo {
        // with single explicit serializer.
        val s = "hello"
        println(JSON.stringify(StringSerializer, s))

        // with serializer by class.
        val json = JSON(context = SerialContext().apply {
            registerSerializer(String::class, StringSerializer)
//            registerSerializer(Map::class, MapSerializer)
        })
        println(json.stringify(s))
    }
}

@Serializable
data class Data(@SerialId(123) val id: Int,
                @SerialName("s") val str: String)

@Serializable
data class DataWithBool(@SerialId(234) val id: Int,
                        @Optional @SerialName("b") val bool: Boolean = false)

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
