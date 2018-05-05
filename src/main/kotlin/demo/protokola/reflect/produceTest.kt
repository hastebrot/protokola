package demo.protokola.reflect

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

class Context {
    fun <R> ReadWriteProperty<R>.set(value: R) {
        val oldValue = property.get()
        property.set(value)
//    emit(ValueChange(value, oldValue))
    }
}

fun observe(action: Context.() -> Unit) {
    action(Context())
}

sealed class Property<out R1, R2>
data class ReadOnlyProperty<out R1>(val property: KProperty0<R1>) : Property<R1, Nothing>()
data class ReadWriteProperty<R2>(val property: KMutableProperty0<R2>) : Property<Nothing, R2>()

val <R> KProperty0<R>.p get() = ReadOnlyProperty(this)
val <R> KMutableProperty0<R>.p get() = ReadWriteProperty(this)

fun main(args: Array<String>) {
    val e = Movie("foo", 1985)
    println(e)

    observe {
        e::title.p.set("bar")
        println(e)
    }
}

data class Movie(var title: String, var year: Int)

// add property wrapper class for observable property.
// - KProperty*, KMutableProperty* is complicated.
// - KMutableProperty0.set() is not observable.
// - use observe() context and .w extension property

// a function type with receiver

//    val draft = Person("foo", "bar")
//    draft::firstName.p
//    draft::lastName.p

//    immer.produce(state, draft => {
//        draft.foo = 1
//    })
//
//    state = Automerge.change(state, "description", draft => {
//        draft.foo = 1
//    })

