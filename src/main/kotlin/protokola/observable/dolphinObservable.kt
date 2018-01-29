package protokola.observable

import protokola.demo
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

// we need a plain object with mutable fields.
data class Person(var firstName: String? = null,
                  var lastName: String? = null)

data class FishShop(val fishes: MutableList<String> = mutableListOf())

fun main(args: Array<String>) {
    demo {
        // wrap a plain object into a bean to make it an explicit observable object.
        val bean = Bean(Person())
//        val bean = Bean(Person::class)

        // bind fields of the plain object into observable values.
        bean.property(Person::lastName)
            .bind(false) { println(it.value) }
        bean.property(Person::firstName)
            .bind(false) { println(it.value) }

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

        bean.arrayProperty(FishShop::fishes)
            .bind(false) { println(it) }

        bean.arrayProperty(FishShop::fishes)
            .push("angel", "clown", "mandarin", "surgeon")
    }

}

// a bean serves as a wrapper for a plain object, that provides access to observable values.
class Bean<T : Any>(private val instance: T) {

    private val properties = mutableMapOf<KMutableProperty1<T, *>, Property<T, *>>()

    constructor(type: KClass<T>) : this(type.java.newInstance()!!)

    fun <R> property(property: KMutableProperty1<T, R>): Property<T, R> =
        if (property in properties) retrieveProperty(property)
        else addProperty(property)

    fun <R : MutableCollection<V>, V> arrayProperty(
        property: KProperty1<T, R>): ArrayProperty<T, R, V> {
        return ArrayProperty(property, instance)
    }

    fun <R> removeProperty(property: KMutableProperty1<T, R>) {
        properties[property]!!.unbindAll()
        properties.remove(property)
    }

    private fun <R> addProperty(property: KMutableProperty1<T, R>) =
        Property(property, instance)
            .apply { properties[property] = this }

    @Suppress("UNCHECKED_CAST")
    private fun <R> retrieveProperty(property: KMutableProperty1<T, R>) =
        properties[property] as Property<T, R>

}

// allows bean creation with bean<Person>() instead of Bean(Person::class).
inline fun <reified T : Any> bean() = Bean(T::class)

// a property allows to observe value changes via bindings.
class Property<T, R>(private val property: KMutableProperty1<T, R>,
                     private val instance: T) {

    private val handlers = mutableListOf<Handler<ValueChange<R>>>()

    private var value: R
        get() = property.get(instance)
        set(newValue) {
            val oldValue = property.get(instance)
            property.set(instance, newValue)
            handlers.forEach { handler ->
                handler(ValueChange(newValue, oldValue))
            }
        }

    fun get() = value

    fun set(newValue: R) { value = newValue }

    fun bind(initial: Boolean = true,
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

    fun unbindAll() { handlers.clear() }

}

class ArrayProperty<T, out R: MutableCollection<V>, V>(private val property: KProperty1<T, R>,
                          private val instance: T) {

    private val handlers = mutableListOf<Handler<ValueSplice<V>>>()

    private var value: Collection<V>
        get() = property.get(instance)
        set(newValue) {
            val oldValue = property.get(instance)
//            property.set(instance, newValue)
            handlers.forEach { handler ->
//                handler(ValueSplice(newValue, oldValue))
            }
        }

    fun push(vararg items: V) {
        (property.get(instance) as MutableCollection<V>).addAll(items)
    }

    fun bind(initial: Boolean = true,
             handler: Handler<ValueSplice<V>>): Binding {
        val handle = {
            handler(ValueSplice(value.toList(), 0, value.toList(), 0))
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

data class ValueChange<out T>(val value: T?,
                              val oldValue: T?)

data class ValueSplice<out T>(val items: List<T>?,
                              val startIndex: Int,
                              val removedItems: List<T>?,
                              val addedCount: Int)
