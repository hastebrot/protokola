package demo.protokola.reflect

import protokola.demo
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

class FooVars(var str: String? = null,
              var int: Int? = null,
              var bool: Boolean? = null,
              var list: MutableList<String?>? = null,
              var coll: Collection<Int>? = null)

class FooVals(val str: String,
              val int: Int,
              val bool: Boolean,
              val list: MutableList<String?>,
              val coll: Collection<Int>)

fun main(args: Array<String>) {
    demo("foo vars") {
        val foo = FooVars()
        FooVars::str.set(foo, "a")
        FooVars::int.set(foo, 1)
        FooVars::bool.set(foo, true)
        FooVars::list.set(foo, mutableListOf("a", "b"))
        FooVars::list.get(foo)!!.addAll(listOf("c", "d"))
        FooVars::coll.set(foo, mutableListOf(1, 2))
//        FooVars::coll.get(foo)!!.addAll(listOf(3, 4))
    }

    demo("foo vals") {
        val foo = FooVals("a", 1, true, mutableListOf("a", "b"), listOf(1, 2))
        FooVals::str.get(foo)
        FooVals::int.get(foo)
        FooVals::bool.get(foo)
        FooVals::list.get(foo)
        FooVals::coll.get(foo)
    }

    demo("wrapper foo vars push") {
        val foo = FooVars("a", 1, true, mutableListOf("a", "b"), listOf(1, 2))
        println(foo.str)
        println(foo.list)

        MutableWrapper(foo, FooVars::str).set("b")
        Wrapper(foo, FooVars::list).push(listOf("c", "d"))
        println(foo.str)
        println(foo.list)

        MutableWrapper(foo, FooVars::str).set(null)
        Wrapper(foo, FooVars::list).push(listOf(null))
        println(foo.str)
        println(foo.list)
    }

    demo("wrapper foo vals push") {
        val foo = FooVals("a", 1, true, mutableListOf("a", "b"), mutableListOf(1, 2))
        println(foo.str)
        println(foo.list)

//        Wrapper(foo, FooVals::str).set("b")
        Wrapper(foo, FooVals::list).push(listOf("c", "d"))
        println(foo.str)
        println(foo.list)

//        Wrapper(foo, FooVals::str).set(null)
        Wrapper(foo, FooVals::list).push(listOf(null))
        println(foo.str)
        println(foo.list)
    }

    demo("wrapper foo vars splice") {
        val foo = FooVars(list = mutableListOf("a", "b", "c", "d", "e"))
        println(foo.list)

        Wrapper(foo, FooVars::list).splice(2, 2, listOf("x", "y", "z"))
        println(foo.list)
    }
}

class Wrapper<T, out R>(val bean: T,
                        val property: KProperty1<T, R?>)

class MutableWrapper<T, R>(val bean: T,
                           val property: KMutableProperty1<T, R?>)

private fun <T, R : Any?> Wrapper<T, R>.get(): R?
    = get(bean, property)

private fun <T, R : Any?> MutableWrapper<T, R>.set(value: R?)
    = set(bean, property, value)

private fun <T, R : MutableList<V>?, V : Any?> Wrapper<T, R>.push(addedItems: Collection<V>)
    = push(bean, property, addedItems)

private fun <T, R : MutableList<V>?, V : Any?> Wrapper<T, R>.pop()
    = pop(bean, property)

private fun <T, R : MutableList<V>?, V : Any?> Wrapper<T, R>.splice(startIndex: Int,
                                                                                           removedCount: Int,
                                                                                           addedItems: Collection<V>)
    = splice(bean, property, startIndex, removedCount, addedItems)

fun <T, R : Any?> get(bean: T,
                      property: KProperty1<T, R?>): R?
    = property.get(bean)

fun <T, R : Any?> set(bean: T,
                      property: KMutableProperty1<T, R?>,
                      value: R): Unit
    = property.set(bean, value)

fun <T, R : MutableList<V>?, V : Any?> push(bean: T,
                                            property: KProperty1<T, R>,
                                            addedItems: Collection<V>): Unit
    = property.get(bean)!!.run { this!!.addAll(addedItems) }

fun <T, R : MutableList<V>?, V : Any?> pop(bean: T,
                                           property: KProperty1<T, R?>): V
    = property.get(bean)!!.run { this!!.removeAt(size - 1) }

fun <T, R : MutableList<V>?, V : Any?> splice(bean: T,
                                              property: KProperty1<T, R>,
                                              startIndex: Int,
                                              removedCount: Int,
                                              addedItems: Collection<V>): Unit
    = property.get(bean)!!.subList(startIndex, startIndex + removedCount)
        .apply { clear() }
        .run { addAll(addedItems) }
