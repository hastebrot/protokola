package protokolax

import io.reactivex.Flowable

fun main(args: Array<String>) {
    Flowable.just("Hello world")
        .subscribe { println(it) }
}
