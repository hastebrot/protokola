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
import protokola.MessageBus
import java.util.concurrent.ConcurrentHashMap

sealed class Transport {
    data class Request(val url: String,
                       val body: String) : Transport()
    data class Response(val status: Int,
                        val body: String) : Transport()
    data class SessionCookie(val cookie: String) : Transport()
    data class DolphinClientId(val id: String) : Transport()
}

fun main(args: Array<String>) {
    val bus = MessageBus()
    bus.subscribe { println(it.payload) }

    val client = OkHttpClient.Builder()
//            .cookieJar(simpleCookieJar())
            .build()

    val dolphinUrl = "http://localhost:8080/dolphin"

    val request1 = Message(Transport.Request(dolphinUrl, listOf(
        mapOf("id" to "CreateContext")
    ).toJson()))

    val request2 = Message(Transport.Request(dolphinUrl, listOf(
        mapOf("id" to "CreateController", "n" to "FooController", "c_id" to null)
    ).toJson()))

    val request3 = Message(Transport.Request(dolphinUrl, listOf(
        mapOf("id" to "StartLongPoll")
    ).toJson()))

    var dolphinClientId: String? = null
    var cookieSessionId: String? = null

    fun fetch(requestMessage: Message<Transport.Request>) {
        bus.dispatch(requestMessage)
        val request = createDolphinRequest(requestMessage.payload.body, requestMessage.payload.url, dolphinClientId, cookieSessionId)
        val call = client.newCall(request)
        call.execute().use { response ->
            dolphinClientId = response.header(clientIdKey)
            cookieSessionId = cookieSessionId ?: response.headers(setCookieKey).firstOrNull()
            val responseMessage = Message(Transport.Response(response.code(), response.body()!!.string()))
            bus.dispatch(responseMessage)
        }
    }

    fetch(request1)
    fetch(request2)
    fetch(request3)
}

private val cookieKey = "Cookie"
private val setCookieKey = "Set-Cookie"
private val clientIdKey = "dolphin_platform_intern_dolphinClientId"

private fun createDolphinRequest(
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
            addHeader(cookieKey, sessionCookie)
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
