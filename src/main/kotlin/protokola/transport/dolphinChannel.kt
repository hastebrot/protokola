package protokola.transport

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import protokola.Message
import java.util.concurrent.ConcurrentHashMap

sealed class Transport {
    data class Request(val body: String) : Transport()
    data class Response(val status: Int, val body: String): Transport()
}

fun main(args: Array<String>) {
    val client = OkHttpClient.Builder()
//            .cookieJar(simpleCookieJar())
            .build()

    val dolphinUrl = "http://localhost:8080/dolphin"

    val request1 = Message(Transport.Request(listOf(
        mapOf("id" to "CreateContext")
    ).toJson()))

    val request2 = Message(Transport.Request(listOf(
        mapOf("id" to "CreateController", "n" to "FooController", "c_id" to null)
    ).toJson()))

    val request3 = Message(Transport.Request(listOf(
        mapOf("id" to "StartLongPoll")
    ).toJson()))

    var dolphinClientId: String? = null
    var cookieSessionId: String? = null

    fun fetch(requestMessage: Message<Transport.Request>): Message<Transport.Response> {
        val request = makeDolphinRequest(requestMessage.payload.body, dolphinUrl, dolphinClientId, cookieSessionId)
        val call = client.newCall(request)
        return call.execute().use { response ->
            dolphinClientId = response.header(clientIdKey)
            cookieSessionId = cookieSessionId ?: response.headers(sessionIdKey).firstOrNull()
            Message(Transport.Response(response.code(), response.body()!!.string()))
        }
    }

    println(request1.payload)
    println(fetch(request1).payload)

    println(request2.payload)
    println(fetch(request2).payload)

    println(request3.payload)
    println(fetch(request3).payload)
}

private val sessionIdKey = "Set-Cookie"
private val clientIdKey = "dolphin_platform_intern_dolphinClientId"

private fun makeDolphinRequest(
        commands: String,
        endpoint: String = "http://localhost:8080/dolphin",
        clientId: String? = null,
        sessionCookie: String? = null): Request {
    val mediaType = MediaType.parse("application/json")
    return Request.Builder().apply {
        url(endpoint)
        post(RequestBody.create(mediaType, commands))
        header("connection", "keep-alive")
        if (clientId != null) {
            header(clientIdKey, clientId)
        }
        if (sessionCookie != null) {
            addHeader("Cookie", sessionCookie)
        }
    }.build()
}

private fun List<*>.toJson() = toString()
private fun Map<*, *>.toJson() = toString()

private fun simpleCookieJar() = object : CookieJar {
    private val cookieMap = ConcurrentHashMap<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl,
                                  unmodifiableCookies: List<Cookie>) {
        cookieMap[url.host()] = unmodifiableCookies.toMutableList()
    }

    override fun loadForRequest(url: HttpUrl) =
        cookieMap[url.host()] ?: mutableListOf()
}

private fun RequestBody.string() = Buffer()
        .also { writeTo(it) }
        .readUtf8()
