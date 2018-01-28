package protokola.observable

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

// we need a plain object with mutable fields.
data class Person(var firstName: String? = null,
                  var lastName: String? = null)

fun main(args: Array<String>) {
    // wrap a plain object into a bean to make it an explicit observable object.
    val bean = Bean(Person())
//    val bean = Bean(Person::class)

    // bind fields of the plain object into observable values.
    bean.property(Person::lastName)
        .binding(false) { println(it.newValue) }
    bean.property(Person::firstName)
        .binding(false) { println(it.newValue) }

    // change a property value.
    bean.property(Person::lastName, "Feuerstein")
    bean.property(Person::firstName, "Herbert")

    // change another property value.
    bean.property(Person::lastName, "Schmidt")
    bean.property(Person::firstName, "Harald")

//    val fishes = listOf("angel", "clown", "mandarin", "surgeon")
//    println(fishes)
}

// a bean serves as a wrapper for a plain object, that provides access to observable values.
class Bean<T : Any>(private val instance: T) {

    private val properties = mutableMapOf<KMutableProperty1<T, *>, Property<T, *>>()

    constructor(type: KClass<T>) : this(type.java.newInstance()!!)

    @Suppress("UNCHECKED_CAST")
    fun <R> property(property: KMutableProperty1<T, R>): Property<T, R> =
        if (property in properties) {
            properties[property] as Property<T, R>
        }
        else {
            Property(property, instance)
                .apply { properties[property] = this }
        }

    fun <R> property(property: KMutableProperty1<T, R>,
                     newValue: R): Property<T, R> =
        property(property)
            .apply { value = newValue }

}

// allows bean creation with bean<Person>() instead of Bean(Person::class).
inline fun <reified T : Any> bean() = Bean(T::class)

// a property allows to observe value changes via bindings.
class Property<T, R>(private val property: KMutableProperty1<T, R>,
                     private val instance: T) {

    private val handlers = mutableListOf<Handler<ValueChange<R>>>()

    var value: R
        get() = property.get(instance)
        set(newValue) {
            val oldValue = property.get(instance)
            property.set(instance, newValue)
            handlers.forEach { handler ->
                handler(ValueChange(newValue, oldValue))
            }
        }

    fun binding(initial: Boolean = true,
                handler: Handler<ValueChange<R>>): Binding {
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
