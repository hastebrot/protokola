package protokola.registry

import protokola.demo
import protokola.observable.Bean
import protokola.observable.Property
import protokola.observable.get
import protokola.observable.push
import protokola.observable.splice
import protokola.println
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

data class Person(var firstName: String? = null,
                  var lastName: String? = null,
                  var travelDestinations: List<String> = mutableListOf())

fun main(args: Array<String>) {
    val instance = Person("foo", "bar")

    demo("register observable object") {
        val registry = DolphinRegistry()
        registry.register(instance)
    }

    demo("register property paths") {
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
}

class DolphinRegistry {

    fun <T : Any> register(instance: T) {
        val observable = Bean(instance)
        val properties = fetchProperties(instance)

        properties
            .mapNotNull { it.asMutableProperty() }
            .forEach { property ->
                println(property.name + ": " + property.returnType)

                val observableProperty = observable.property(property)
                observableProperty.bindChanges { println(it) }
                if (property.hasListReturnType()) {
                    observableProperty.bindSplices { println(it) }
                }

                println(observableProperty)
                println(observableProperty.get())

                if (property.hasListReturnType()) {
                    val observablePropertyList = observableProperty as Property<T, MutableList<Any>>
                    observablePropertyList.push("foo", "bar", "baz")
                    println(observableProperty.get())

                    observablePropertyList.splice(1, 1, "quux", "quuux")
                    println(observableProperty.get())
                }
            }
    }

    private fun <T: Any> fetchProperties(instance: T) =
        instance::class.memberProperties

    @Suppress("UNCHECKED_CAST")
    private fun <T, R> KProperty1<out T, R>.asMutableProperty() = when (this) {
        is KMutableProperty1<out T, R> -> this as KMutableProperty1<T, Any?>
        else -> null
    }

    private fun <T, R> KProperty1<out T, R>.hasListReturnType(): Boolean {
        val collectionType = List::class.starProjectedType
        return returnType.isSubtypeOf(collectionType)
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