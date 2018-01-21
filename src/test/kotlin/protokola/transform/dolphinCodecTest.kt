package protokola.transform

import com.winterbe.expekt.expect
import protokola.transport.Transport
import kotlin.test.Test

class DolphinCodecTest {

    private val createPresentationModel = """
        [{"p_id":"f6a23423-d104-41a8-84a9-c793ae2cee32","t":"@@@ HIGHLANDER_BEAN @@@","a":[{"n":"@@@ SOURCE_SYSTEM @@@","a_id":"920S","v":"server"},{"n":"controllerName","a_id":"921S","v":null},{"n":"controllerId","a_id":"922S","v":null},{"n":"model","a_id":"923S","v":null}],"id":"CreatePresentationModel"}]
    """.trimIndent()

    @Test
    fun `CreatePresentationModel`() {
        // given:
        val codec = DolphinCodec()

        // when:
        val responseContext = Transport.ServerResponse(200, createPresentationModel)
//        val commands = codec.fromJson(responseContext)

        // then:
        val command = codec.toCommand(responseContext)

        expect(command.id).to.equal("CreatePresentationModel")
        expect(command.presentationModelId).to.equal("f6a23423-d104-41a8-84a9-c793ae2cee32")
        expect(command.type).to.equal("@@@ HIGHLANDER_BEAN @@@")

        expect(command.attributes[0].attributeId).to.equal("920S")
        expect(command.attributes[0].name).to.equal("@@@ SOURCE_SYSTEM @@@")
        expect(command.attributes[0].value).to.equal("server")

        expect(command.attributes[1].attributeId).to.equal("921S")
        expect(command.attributes[1].name).to.equal("controllerName")
        expect(command.attributes[1].value).to.equal(null)

        expect(command.attributes[2].attributeId).to.equal("922S")
        expect(command.attributes[2].name).to.equal("controllerId")
        expect(command.attributes[2].value).to.equal(null)

        expect(command.attributes[3].attributeId).to.equal("923S")
        expect(command.attributes[3].name).to.equal("model")
        expect(command.attributes[3].value).to.equal(null)
    }

}