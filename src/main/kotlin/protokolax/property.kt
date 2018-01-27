package protokolax

import kotlin.reflect.KMutableProperty
//import kotlin.reflect.KProperty

fun main(args: Array<String>) {
    val bean = FooBean()

    println(bean.property(Foo::int, 1))
    println(bean.property(Foo::str, "a"))
    println(bean.property(Foo::bool, true))

//    println(bean.property(Foo::str, 1))
//    println(bean.property(Foo::int, "a"))
}

data class Foo(var str: String,
               var int: Int,
               var bool: Boolean)

class FooBean {
    fun <T> property(property: KMutableProperty<T>,
                     value: T) = FooProperty(property.name, value)
}

data class FooProperty<out T>(val name: String,
                              val value: T)
