package protokolax

import org.http4k.client.OkHttp
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun main(vararg args: String) {
    run {
        val app = httpHandler { request ->
            Response(Status.OK).body("Hello, ${request.query("name")}")
        }

        val request = Request(Method.GET, "/").query("name", "World")

        val response = app(request)

        println(response)
    }

    run {
        val request = Request(Method.GET, "http://pokeapi.co/api/v2/pokemon/")

        val client = OkHttp()

        client.with(Filter { next ->
            {
                println(it.uri)
                next(it)
            }
        })

       println(client(request))
   }
}

private fun httpHandler(handler: HttpHandler) = handler

