package protokola.registry

import protokola.observable.Bean
import protokola.println
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

data class Person(var firstName: String? = null,
                  var lastName: String? = null)

fun main(args: Array<String>) {
    val instance = Person()

    val registry = DolphinRegistry()
    registry.register(instance)

    val paths = PropertyPaths()
    paths.query(instance).println

    paths.register(instance, "foo")
    paths.query(instance).println

    paths.register(instance, "bar")
    paths.query(instance).println

    paths.unregister(instance, "foo")
    paths.query(instance).println

    paths.unregister(instance, "bar")
    paths.query(instance).println
}

class DolphinRegistry {

    fun <T : Any> register(instance: T) {
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

class PropertyPaths {

    private val registry = mutableMapOf<Any, MutableList<String>>()

    fun query(bean: Any) =
        registry.getOrElse(bean) { mutableListOf() }

    fun register(bean: Any,
                 propertyPath: String) {
        val propertyPaths = query(bean)
        if (propertyPaths.isEmpty()) {
            registry[bean] = propertyPaths
        }
        propertyPaths += propertyPath
    }

    fun unregister(bean: Any,
                   propertyPath: String) {
        val propertyPaths = query(bean)
        propertyPaths -= propertyPath
        if (propertyPaths.isEmpty()) {
            registry.remove(bean)
        }
    }

}