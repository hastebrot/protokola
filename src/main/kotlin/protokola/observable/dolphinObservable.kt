package protokola.observable

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

// we need a plain object with mutable fields.
data class Person(var firstName: String? = null,
                  var lastName: String? = null)

fun main(args: Array<String>) {
    // wrap a sample object into a bean to make it an observable object.
    val bean = Bean(Person())
//    val bean = Bean(Person::class)

    // bind fields of the sample object into observable values.
    bean.property(Person::lastName)
        .binding { println(it.newValue) }
    bean.property(Person::firstName)
        .binding { println(it.newValue) }

    // change a property value.
    bean.property(Person::lastName, "Feuerstein")
    bean.property(Person::firstName, "Herbert")

    // change another property value.
    bean.property(Person::lastName, "Schmidt")
    bean.property(Person::firstName, "Harald")
}

// a bean serves as a wrapper for an instance, that provides access to observable properties.
class Bean<R : Any>(private val instance: R) {

    private val properties = mutableMapOf<KMutableProperty1<R, *>, Property<R, *>>()

    constructor(type: KClass<R>) : this(type.java.newInstance()!!)

    @Suppress("UNCHECKED_CAST")
    fun <T> property(property: KMutableProperty1<R, T>): Property<R, T> =
        if (property in properties) {
            properties[property] as Property<R, T>
        }
        else {
            Property(property, instance)
                .apply { properties[property] = this }
        }

    fun <T> property(property: KMutableProperty1<R, T>,
                     newValue: T): Property<R, T> =
        property(property)
            .apply { value = newValue }

}

// allows bean creation with bean<Person>() instead of Bean(Person::class).
inline fun <reified R : Any> bean() = Bean(R::class)

// a property allows to observe value changes via bindings.
class Property<R, T>(private val property: KMutableProperty1<R, T>,
                     private val instance: R) {

    private val handlers = mutableListOf<Handler<ValueChange<T>>>()

    var value: T
        get() = property.get(instance)
        set(newValue) {
            val oldValue = property.get(instance)
            property.set(instance, newValue)
            handlers.forEach { handler ->
                handler(ValueChange(newValue, oldValue))
            }
        }

    fun binding(initial: Boolean = true,
                handler: Handler<ValueChange<T>>): Binding {
        val handle = {
            handler(ValueChange(value, null))
        }
        if (initial) {
            handle()
        }
        handlers += handler
        return {
            handlers -= handler
        }
    }

}

// a binding receives value changes.
typealias Binding = () -> Unit

typealias Handler<T> = (T) -> Unit

data class Value<out T>(val value: T?)

data class ValueChange<out T>(val newValue: T?,
                              val oldValue: T?)
