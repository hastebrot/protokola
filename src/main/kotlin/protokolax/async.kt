package protokolax

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    val job = launch {
        delay(1000)
        println("Hello from Kotlin Coroutines!")
    }
    job.join()
}
