package protokola.property

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

fun <T, R : Any?> get(bean: T,
                      property: KProperty1<T, R?>): R?
    = property.get(bean)

fun <T, R : Any?> set(bean: T,
                      property: KMutableProperty1<T, R?>,
                      value: R?): Unit
    = property.set(bean, value)

fun <T, R : MutableList<V?>, V> push(bean: T,
                                     property: KProperty1<T, R?>,
                                     items: Collection<V?>): Unit
    = property.get(bean)!!.push(items)

fun <T, R : MutableList<V?>, V> pop(bean: T,
                                     property: KProperty1<T, R?>): V?
    = property.get(bean)!!.pop()

fun <T, R : MutableList<V?>, V> splice(bean: T,
                                       property: KProperty1<T, R?>,
                                       startIndex: Int,
                                       removedCount: Int,
                                       addedItems: Collection<V?>): Collection<V?>
    = property.get(bean)!!.splice(startIndex, removedCount, addedItems)

private fun <V> MutableList<V?>.push(items: Collection<V?>) {
    addAll(items)
}

private fun <V> MutableList<V?>.pop(): V? {
    return removeAt(lastIndex)
}

private fun <V> MutableList<V?>.splice(startIndex: Int,
                                       removedCount: Int,
                                       addedItems: Collection<V?>): Collection<V?> {
    val spliceList = subList(startIndex, startIndex + removedCount)
    val removedItems = spliceList.toList()
    spliceList.clear()
    spliceList.addAll(addedItems)
    return removedItems
}