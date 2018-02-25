package protokola.registry

import protokola.MessageBus
import protokola.demo
import protokola.observable.Bean
import protokola.observable.bean
import protokola.observable.set
import protokola.observable.splice
import protokola.println
import protokola.registry.ObserveType.CHANGE
import protokola.registry.ObserveType.LINK
import protokola.registry.ObserveType.SPLICE
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
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.PROPERTY)
annotation class Observe(vararg val types: ObserveType)

enum class ObserveType {
    CHANGE, SPLICE, LINK
}

data class Person(
    @Observe(CHANGE) var firstName: String? = null,
    @Observe(CHANGE) var lastName: String? = null,
    @Observe(SPLICE) var foods: List<String> = mutableListOf(),
    @Observe(SPLICE, LINK) var friends: List<Person> = mutableListOf()
)

fun main(args: Array<String>) {
    demo("register observable object") {
        val instance = Person("foo", "bar")

        val bus = MessageBus()

        val registry = DolphinRegistry()
        registry.dispatchTo(bus)

        registry.register(instance)
    }

    demo("register property paths") {
        val instance = Person("foo", "bar")

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

    fun dispatchTo(messageBus: MessageBus) {
        messageBus.subscribe { }
    }

    fun <T : Any> register(instance: T) {
        val observable = bean(instance)
        val properties = properties(instance)

        properties.forEach { property ->
            val propertyName = property.name
            val propertyType = property.returnType
            val propertyImpl = property::class.simpleName
            val observeTypes = observeTypes(property)

            println("$propertyName: $propertyType [$propertyImpl] $observeTypes")

            if (CHANGE in observeTypes) {
                observable.property<Any?>(property.ofMutable()).bind {
                    println(it.payload)
                }
            }

            if (SPLICE in observeTypes) {
                observable.property<Any?>(property.ofMutable()).bind {
                    println(it.payload)
                }
            }
        }

        (observable as Bean<Person>).property(Person::firstName)
            .set("bar")
        (observable as Bean<Person>).property(Person::firstName)
            .set("baz")

        (observable as Bean<Person>).property<MutableList<Any?>>(Person::foods.ofMutable())
            .splice(0, 0, "foo", "bar")
        (observable as Bean<Person>).property<MutableList<Any?>>(Person::foods.ofMutable())
            .splice(1, 1, "baz", "quux")
    }

    private fun <T: Any> properties(instance: T): Collection<KProperty1<out T, *>>
        = instance::class.memberProperties

    private fun observeTypes(property: KProperty1<*, *>)
        = property.findAnnotation<Observe>()?.types?.toList() ?: emptyList()

    @Suppress("UNCHECKED_CAST")
    private fun <T, R, T1, R1> KProperty1<T, R>.ofMutable()
        = this as KMutableProperty1<T1, R1>

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