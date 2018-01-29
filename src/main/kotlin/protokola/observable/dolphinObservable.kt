package protokola.observable

import protokola.demo
import protokola.property.get
import protokola.property.pop
import protokola.property.push
import protokola.property.set
import protokola.property.splice
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

// we need a plain object with mutable fields.
data class Person(var firstName: String? = null,
                  var lastName: String? = null)

data class FishShop(var fishes: MutableList<String>? = mutableListOf())

fun main(args: Array<String>) {
    demo {
        // wrap a plain object into a bean to make it an explicit observable object.
        val bean = Bean(Person())
//        val bean = Bean(Person::class)

        // bind fields of the plain object into observable values.
        bean.property(Person::lastName)
            .bind(true) { println(it.value) }
        bean.property(Person::firstName)
            .bind(true) { println(it.value) }

        // change a property value.
        bean.property(Person::lastName).set("Feuerstein")
        bean.property(Person::firstName).set("Herbert")

        // change another property value.
        bean.property(Person::lastName).set("Schmidt")
        bean.property(Person::firstName).set("Harald")

        bean.removeProperty(Person::lastName)
        bean.removeProperty(Person::firstName)
    }

    demo {
        val bean = Bean(FishShop())

        bean.property(FishShop::fishes)
            .bind(true) { println(it.value) }
        println(bean.instance.fishes)

        bean.property(FishShop::fishes)
            .push("angel", "clown", "mandarin", "surgeon")
        println(bean.instance.fishes)

        bean.property(FishShop::fishes)
            .splice(1, 2, "foo", "bar")
        println(bean.instance.fishes)
    }

}

// a bean serves as a wrapper for a plain object, that provides access to observable values.
class Bean<T : Any>(val instance: T) {

    private val properties = mutableMapOf<KMutableProperty1<T, *>, Property<T, *>>()

    constructor(type: KClass<T>) : this(type.java.newInstance()!!)

    fun <R : Any?> property(property: KMutableProperty1<T, R?>): Property<T, R> =
        if (property in properties) retrieveProperty(property)
        else addProperty(property)

    fun <R : Any?> removeProperty(property: KMutableProperty1<T, R?>) {
        properties[property]!!.unbindAll()
        properties.remove(property)
    }

    private fun <R : Any?> addProperty(property: KMutableProperty1<T, R?>) =
        Property(property, instance)
            .apply { properties[property] = this }

    @Suppress("UNCHECKED_CAST")
    private fun <R : Any?> retrieveProperty(property: KMutableProperty1<T, R?>) =
        properties[property] as Property<T, R>

}

// allows bean creation with bean<Person>() instead of Bean(Person::class).
inline fun <reified T : Any> bean() = Bean(T::class)

// a property allows to observe value changes via bindings.
class Property<T, R>(val property: KMutableProperty1<T, R?>,
                     val instance: T) {

    private val handlers = mutableListOf<Handler<ValueChange<R>>>()

    fun bind(initial: Boolean = true,
             handler: Handler<ValueChange<R>>): Binding {
        handlers += handler
        val handle = {
            emit(ValueChange(get(), null))
        }
        if (initial) {
            handle()
        }
        return {
            handlers -= handler
        }
    }

    fun unbindAll() { handlers.clear() }

    fun emit(valueChange: ValueChange<R>) {
        handlers.forEach { handler ->
            handler(valueChange)
        }
    }

}

fun <T, R : Any?> Property<T, R>.get(): R?
    = get(instance, property)

fun <T, R : Any?> Property<T, R>.set(value: R?) {
    val oldValue = get(instance, property)
    set(instance, property, value)
    emit(ValueChange(value, oldValue))
}

fun <T, R : MutableList<V>?, V : Any?> Property<T, R>.push(vararg items: V)
    = push(instance, property, items.toList())

fun <T, R : MutableList<V>?, V : Any?> Property<T, R>.pop()
    = pop(instance, property)

fun <T, R : MutableList<V>?, V : Any?> Property<T, R>.splice(startIndex: Int,
                                                             removedCount: Int,
                                                             vararg addedItems: V)
    = splice(instance, property, startIndex, removedCount, addedItems.toList())


// a binding receives value changes.
typealias Binding = () -> Unit

typealias Handler<T> = (T) -> Unit

data class ValueChange<out T>(val value: T?,
                              val oldValue: T?)

data class ValueSplice<out T>(val items: List<T>?,
                              val startIndex: Int,
                              val removedItems: List<T>?,
                              val addedCount: Int)
