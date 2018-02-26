package protokola.registry

import com.winterbe.expekt.expect
import protokola.MessageBus
import kotlin.test.Test

class DolphinRegistryTest {

    @Test
    fun `register`() {
        // given:
        val registry = DolphinRegistry()
        val bus = MessageBus()
        registry.dispatchTo(bus)

        // when:
        val instance = Person("foo", "bar")
        registry.register(instance)

        // then:
        expect(registry.observables).to.have.keys(instance)
    }

}