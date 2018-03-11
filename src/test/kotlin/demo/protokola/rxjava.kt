package demo.protokola

import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.assertions.shouldHave
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.complete
import com.rubylichtenstein.rxtest.matchers.noErrors
import io.reactivex.Observable

fun main(args: Array<String>) {
    Observable.just("Hello world!")
        .subscribe { println(it) }

    Observable.just("Hello world!")
        .test {
            it shouldEmit "Hello world!"
            it should complete()
            it shouldHave noErrors()
        }
}
