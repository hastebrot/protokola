package protokola.observable

import protokola.Message
import protokola.demo
import protokola.property.get
import protokola.property.push
import protokola.property.set
import protokola.property.splice
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

// we need a plain object with mutable fields.
data class Person(var firstName: String? = null,
                  var lastName: String? = null)

data class FishShop(var fishes: MutableList<String?>? = mutableListOf())

fun main(args: Array<String>) {
    demo("bind changes") {
        // wrap a plain object into a bean to make it an explicit observable object.
        val bean = Bean(Person())
//        val bean = Bean(Person::class)

        // bind fields of the plain object into observable values.
        bean.property(Person::lastName)
            .bind { println(it.payload) }
        bean.property(Person::firstName)
            .bind { println(it.payload) }

        bean.property(Person::lastName).emitInitialChange()
        bean.property(Person::firstName).emitInitialChange()

        // change a property value.
        bean.property(Person::lastName).set("Feuerstein")
        bean.property(Person::firstName).set("Herbert")

        // change another property value.
        bean.property(Person::lastName).set("Schmidt")
        bean.property(Person::firstName).set("Harald")

        bean.removeProperty(Person::lastName)
        bean.removeProperty(Person::firstName)
    }

    demo("bind splices") {
        val bean = Bean(FishShop())

        bean.property(FishShop::fishes)
            .bind { println(it.payload) }

        bean.property(FishShop::fishes).emitInitialSplice()

        bean.property(FishShop::fishes)
            .push("angel", "clown", "mandarin", "surgeon")

        bean.property(FishShop::fishes)
            .splice(1, 2, "foo", "bar")

        bean.property(FishShop::fishes)
            .push("baz", "quux")
    }

}

// a bean serves as a wrapper for a plain object, that provides access to observable values.
class Bean<T : Any>(private val instance: T) {

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
        Property(instance, property)
            .apply { properties[property] = this }

    @Suppress("UNCHECKED_CAST")
    private fun <R : Any?> retrieveProperty(property: KMutableProperty1<T, R?>) =
        properties[property] as Property<T, R>

}

// allows bean creation with bean<Person>() instead of Bean(Person::class).
inline fun <reified T : Any> bean() = Bean(T::class)

fun <T: Any> bean(instance: T) = Bean(instance)

// a property allows to observe value changes via bindings.
class Property<T, R>(val instance: T,
                     val property: KMutableProperty1<T, R?>) {

    private val handlers = mutableListOf<Handler<Message<*>>>()

    fun bind(handler: Handler<Message<*>>): Binding {
        handlers += handler
        return {
            handlers -= handler
        }
    }

    fun unbindAll() {
        handlers.clear()
    }

    fun emit(valueChange: ValueChange<R>) {
        handlers.forEach { handler ->
            handler(Message(valueChange))
        }
    }

    fun <R : List<V?>, V> emit(valueSplice: ValueSplice<R, V>) {
        handlers.forEach { handler ->
            handler(Message(valueSplice))
        }
    }

    fun emitInitialChange() {
        val value = get() as R?
        emit(ValueChange(value, null))
    }

    fun emitInitialSplice() {
        val items = get() as List<Any?>?
        emit(ValueSplice(items, 0, listOf(), items!!.size))
    }

}

fun <T, R : Any?> Property<T, R>.get(): R? {
    return get(instance, property)
}

fun <T, R : Any?> Property<T, R>.set(value: R?) {
    val oldValue = get(instance, property)
    set(instance, property, value)
    emit(ValueChange(value, oldValue))
}

fun <T, R : MutableList<V?>, V> Property<T, R>.push(vararg addedItems: V?) {
    val startIndex = get(instance, property)!!.size
    push(instance, property, addedItems.toList())
    val items = get(instance, property)
    emit(ValueSplice(items, startIndex, listOf<V>(), addedItems.size))
}

//fun <T, R : MutableList<V?>, V> Property<T, R>.pop(): V? {
//    val startIndex = get(instance, property)!!.size - 1
//    val removedItem = pop(instance, property)
//    val items = get(instance, property)
//    emit(ValueSplice(items, startIndex, listOf(removedItem), 1))
//    return removedItem
//}

fun <T, R : MutableList<V?>, V> Property<T, R>.splice(startIndex: Int,
                                                      removedCount: Int,
                                                      vararg addedItems: V?) {
    val removedItems = splice(instance, property, startIndex, removedCount, addedItems.toList())
    val items = get(instance, property)
    emit(ValueSplice(items, startIndex, removedItems.toList(), addedItems.size))
}


typealias Binding = () -> Unit

typealias Handler<T> = (T) -> Unit

data class ValueChange<out R>(val value: R?,
                              val oldValue: R?)

data class ValueSplice<out R : List<V?>, out V>(val items: R?,
                                                val startIndex: Int,
                                                val removedItems: R,
                                                val addedCount: Int)
