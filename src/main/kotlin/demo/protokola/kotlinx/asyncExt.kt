package demo.protokola.kotlinx

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.NonCancellable
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.withTimeoutOrNull
import protokola.demo
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    // cancellation: cooperative
    // channels: fair
    // mutex: critical section

    demo {
        var counter = 0
        runBlocking<Unit> {
            massiveRun(CommonPool) {
                counter++
            }
            println("Counter = $counter")
        }
    }

    demo("atomic integer") {
        val counter = AtomicInteger(0)
        runBlocking<Unit> {
            massiveRun(CommonPool) {
                counter.incrementAndGet()
            }
            println("Counter = ${counter.get()}")
        }
    }

    demo("fine-grained thread confinement") {
        val counterContext = newSingleThreadContext("CounterContext")
        var counter = 0
        runBlocking<Unit> {
            massiveRun(CommonPool) {
                withContext(counterContext) {
                    counter++
                }
            }
            println("Counter = $counter")
        }
    }

    demo("coarse-grained thread confinement") {
        val counterContext = newSingleThreadContext("CounterContext")
        var counter = 0
        runBlocking<Unit> {
            massiveRun(counterContext) {
                counter++
            }
            println("Counter = $counter")
        }
    }

    demo("mutex") {
        val mutex = Mutex()
        var counter = 0
        runBlocking<Unit> {
            massiveRun(CommonPool) {
                mutex.withLock {
                    counter++
                }
            }
            println("Counter = $counter")
        }
    }

    demo("actor") {
        runBlocking<Unit> {
            val counter = counterActor()
            massiveRun(CommonPool) {
                counter.send(IncCounter)
            }
            val response = CompletableDeferred<Int>()
            counter.send(GetCounter(response))
            println("Counter = ${response.await()}")
            counter.close()
        }
    }


    demo {
        runBlocking<Unit> {
            val channel = Channel<Int>()
            launch {
                for (x in 1..5) channel.send(x * x)
                channel.close() // we're done sending
            }
            // here we print received values using `for` loop (until the channel is closed)
            for (y in channel) println(y)
            println("Done!")
        }
    }

    demo {
        runBlocking<Unit> {
            val request = launch {
                repeat(3) { i ->
                    launch(coroutineContext)  {
                        delay((i + 1) * 200L)
                        println("Coroutine $i is done")
                    }
                }
                println("request: I'm done and I don't explicitly join my children that are still active")
            }
            request.join()
            println("Now processing of the request is complete")
        }
    }

    demo {
        runBlocking<Unit> {
            val job = Job()
            val coroutines = List(10) { index ->
                launch(coroutineContext, parent = job) {
                    delay((index + 1) * 200L)
                    println("Coroutine $index is done")
                }
            }
            println("Launched ${coroutines.size} coroutines")
            delay(500L)
            println("Cancelling the job!")
            job.cancelAndJoin()
        }
    }

    demo {
        runBlocking<Unit> {
            val jobs = mutableListOf<Job>()
            jobs += launch(Unconfined) {
                log("unconfined")
            }
            jobs += launch(coroutineContext) {
                log("coroutine context")
            }
            jobs += launch(CommonPool) {
                log("common pool")
            }
            jobs += launch(newSingleThreadContext("MyOwnThread")) {
                log("new single thread context")
            }
            jobs.forEach { it.join() }
        }
    }

    demo {
        runBlocking {
            val job = launch {
                doWorld()
            }
            job.join()
        }
    }

    demo {
        runBlocking<Unit> {
            val job = launch {
                try {
                    repeat(1000) { i ->
                        println("I'm sleeping $i ...")
                        delay(500L)
                    }
                }
                finally {
                    withContext(NonCancellable) {
                        println("I'm running finally")
                        delay(1000L)
                        println("I've just delayed")
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin()
            println("main: Now I can quit.")
        }
    }

    demo {
        runBlocking<Unit> {
            val result = withTimeoutOrNull(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
                "Done"
            }
            println("Result is $result")
        }
    }

    demo {
        runBlocking<Unit> {
            val time = measureTimeMillis {
                val one = async { doSomethingUsefulOne() }
                val two = async { doSomethingUsefulTwo() }
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")
        }
    }
}

private suspend fun doWorld() {
    delay(1000)
    println("hello coroutine!")
}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

suspend fun massiveRun(context: CoroutineContext, action: suspend () -> Unit) {
    val n = 1000 // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        val jobs = List(n) {
            launch(context) {
                repeat(k) { action() }
            }
        }
        jobs.forEach { it.join() }
    }
    println("Completed ${n * k} actions in $time ms")
}

sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()

fun counterActor() = actor<CounterMsg> {
    var counter = 0
    for (msg in channel) {
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}
