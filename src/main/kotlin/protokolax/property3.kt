package protokolax

import protokola.println
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

data class TestBean(
    var string: String = "foo",
    var integer: Int = 1,
    val stringReadonly: String = "bar",
    val integerReadonly: Int = 2,
    val array: MutableList<String> = mutableListOf(),
    val bean: TestBean? = null
)

fun main(vararg args: String) {
    val bean = TestBean("foo", 42,
        bean = TestBean("bar", 36)
    )

    properties(TestBean::class).forEach {
        val propertyName = it.name
        val propertyType = it.returnType
        val propertyImpl = it::class.simpleName
        println("$propertyName: $propertyType ($propertyImpl)")
    }

//    TestBean::str.get(bean).println
//    TestBean::int.get(bean).println
//    TestBean::bean.get(bean).println
//    TestBean::list.get(bean).println
//
//    TestBean::str.get(bean.bean!!).println
//    TestBean::int.get(bean.bean).println
//    TestBean::bean.get(bean.bean).println
//    TestBean::list.get(bean.bean).println

    TestBean::string.let {
        val property: KProperty1<*, Any?> = it

        property.asProperty<Any>().get(bean).println
        property.asProperty<String>().get(bean).println
//        property.asProperty<Int>().get(bean).println
//        property.asProperty<List<String>>().get(bean).println
//        property.asProperty<List<Int>>().get(bean).println

        property.asMutableProperty<Any>()?.get(bean).println
        property.asMutableProperty<String>()?.get(bean).println
//        property.asMutableProperty<Int>().get(bean).println
//        property.asMutableProperty<List<String>>().get(bean).println
//        property.asMutableProperty<List<Int>>().get(bean).println
    }

    properties(bean::class).forEach { property ->
        println("-- " + property.name)
        println(property.returnType)

        property.asMutableProperty<Any?>()?.let {
            println("mutable")
            it.get(bean).println
        }

        if (property.returnType.isListLike()) {
            println("list like")
            property.asProperty<MutableList<*>>().get(bean)
        }
    }
}

private fun <T: Any> properties(type: KClass<T>): Collection<KProperty1<T, *>>
    = type.memberProperties

private fun KType.isListLike(): Boolean {
    val listType = List::class.starProjectedType.withNullability(true)
    return isSubtypeOf(listType)
}

private fun KType.isBeanLike(): Boolean {
    return false
}

@Suppress("UNCHECKED_CAST")
private inline fun <reified R : Any?> KProperty1<*, *>.asProperty(): KProperty1<Any, R> {
    require(returnType.isSubtypeOf(R::class.starProjectedType.withNullability(true))) {
        "$returnType is not of subtype ${R::class.starProjectedType.withNullability(true)}"
    }
    return this as KProperty1<Any, R>
}

@Suppress("UNCHECKED_CAST")
private inline fun <reified R : Any?> KProperty1<*, *>.asMutableProperty(): KMutableProperty1<Any, R>? {
    require(returnType.isSubtypeOf(R::class.starProjectedType.withNullability(true))) {
        "$returnType is not of subtype ${R::class.starProjectedType.withNullability(true)}"
    }
    return when {
        this is KMutableProperty1<*, *> -> this as KMutableProperty1<Any, R>
        else -> null
    }
}

