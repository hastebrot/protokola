package protokola.registry

import protokola.observable.Bean
import kotlin.reflect.KMutableProperty1
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
        val observable = Bean(instance)
        val properties = fetchProperties(instance)

        properties.forEach { property ->
            println(property.name + ": " + property.returnType)
            println(observable.property(property))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Any> fetchProperties(instance: T):
            List<KMutableProperty1<T, *>> {
        val properties = instance::class.memberProperties

        val mutableProperties = mutableListOf<KMutableProperty1<T, *>>()
        properties.forEach { property ->
            if (property is KMutableProperty1<*, *>) {
                val mutableProperty = property as KMutableProperty1<T, *>
                mutableProperties += mutableProperty
            }
        }

        return mutableProperties
    }

}

