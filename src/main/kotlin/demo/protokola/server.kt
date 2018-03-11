package demo.protokola

import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.format.Moshi.auto
import protokola.demo

fun main(vararg args: String) {
    demo {
        val app = httpHandler { request ->
            Response(Status.OK)
                .body("Hello, ${request.query("name")}")
        }

        val request = Request(Method.GET, "/")
            .query("name", "World")

        val response = app(request)

        println(response)
    }

    demo {
        val client = OkHttp()

        val requestUriFilter = Filter { next -> { request ->
            println(request.uri)
            val response = next(request)
            response
        }}

        val request = Request(Method.GET, "http://pokeapi.co/api/v2/pokemon/")

        println(requestUriFilter.then(client)(request))
    }

    demo {
        val messageLens = Body.auto<Message>().toLens()

        val message = Message("hello", Email("alice@foo.org"), Email("bob@foo.org"))

        val messageRequest = messageLens.inject(message, Request(Method.GET, "/"))
        println(messageRequest)

        val extractedMessage = messageLens.extract(messageRequest)
        println(extractedMessage)
    }
}

private fun httpHandler(handler: HttpHandler) = handler

data class Message(val subject: String,
                   val from: Email,
                   val to: Email)

data class Email(val value: String)
