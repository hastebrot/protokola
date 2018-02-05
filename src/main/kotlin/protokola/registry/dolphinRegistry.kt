package protokola.registry

import protokola.demo
import protokola.observable.Bean
import protokola.observable.bean
import protokola.println
import protokola.registry.ObserveType.CHANGES
import protokola.registry.ObserveType.PATHS
import protokola.registry.ObserveType.SPLICES
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.forEach
import kotlin.collections.getOrElse
import kotlin.collections.minusAssign
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.plusAssign
import kotlin.collections.set
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
annotation class Observe(vararg val types: ObserveType)

enum class ObserveType {
    CHANGES, SPLICES, PATHS
}

data class Person(
    @Observe(CHANGES) var firstName: String? = null,
    @Observe(CHANGES) var lastName: String? = null,
    @Observe(SPLICES) val foods: List<String> = mutableListOf(),
    @Observe(SPLICES, PATHS) val friends: List<Person> = mutableListOf()
)

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
        val observable = bean(instance)
        val properties = properties(instance)

        properties.forEach { property ->
            val propertyName = property.name
            val propertyType = property.returnType
            val propertyImpl = property::class.simpleName

            println("-- $propertyName: $propertyType [$propertyImpl]")
            property.findAnnotation<Observe>()?.let {
                println("observe: " + it.types.toList())
            }
        }
    }

    private fun <T: Any> properties(instance: T): Collection<KProperty1<out T, *>>
        = instance::class.memberProperties
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