package protokola.registry

import kotlin.reflect.full.memberProperties

data class Person(var firstName: String? = null,
                  var lastName: String? = null)

fun main(args: Array<String>) {
    val instance = Person()

    val registry = DolphinRegistry()
    registry.add(instance)
}

class DolphinRegistry {

    fun <T : Any> add(instance: T) {
        val properties = instance::class.memberProperties
        properties.forEach {
            println(it.name + ": " + it.returnType)
        }
    }

}

