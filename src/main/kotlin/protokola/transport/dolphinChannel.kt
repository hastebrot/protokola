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

fun main(args: Array<String>) {
    val bus = MessageBus()
    bus.subscribe { println(it.payload) }

    val channel = DolphinChannel(bus)
    bus.subscribe { message ->
        @Suppress("UNCHECKED_CAST")
        when (message.payload) {
            is Transport.Request -> channel.fetch(message as Message<Transport.Request>)
        }
    }

    val dolphinEndpointUrl = "http://localhost:8080/dolphin"

    val requestContext = Transport.Request(dolphinEndpointUrl, listOf(
        mapOf("id" to "CreateContext")
    ).toJson())

    val requestController = Transport.Request(dolphinEndpointUrl, listOf(
        mapOf("id" to "CreateController", "n" to "FooController", "c_id" to null)
    ).toJson())

    val requestLongPoll = Transport.Request(dolphinEndpointUrl, listOf(
        mapOf("id" to "StartLongPoll")
    ).toJson())

    bus.dispatch(Message(requestContext))
    bus.dispatch(Message(requestController))
    bus.dispatch(Message(requestLongPoll))
}

object Transport {
    data class Request(val url: String,
                       val body: String)

    data class Response(val status: Int,
                        val body: String)

    data class DolphinClientId(val value: String)

    data class SessionCookie(val value: String)
}

class DolphinChannel(private val bus: MessageBus) {
    private val client = OkHttpClient.Builder()
//        .cookieJar(simpleCookieJar())
        .build()

    private var dolphinClientId: Transport.DolphinClientId? = null

    private var sessionCookie: Transport.SessionCookie? = null

    fun fetch(requestMessage: Message<Transport.Request>) {
        val request = createOkHttpRequest(
            requestMessage.payload,
            dolphinClientId,
            sessionCookie
        )

        val call = client.newCall(request)
        call.execute().use { response ->
            val dolphinClientIdValue = response.header(headerDolphinClientId)
            val sessionCookieValue = response.headers(headerSetCookieKey).firstOrNull()

            val responseMessage = Message(Transport.Response(
                response.code(), response.body()!!.string()
            ))
            bus.dispatch(responseMessage)

            dolphinClientIdValue?.let {
                dolphinClientId = Transport.DolphinClientId(it)
                bus.dispatch(Message(dolphinClientId))
            }
            sessionCookieValue?.let {
                sessionCookie = Transport.SessionCookie(it)
                bus.dispatch(Message(sessionCookie))
            }
        }
    }
}

private val headerConnectionKey = "Connection"
private val headerCookieKey = "Cookie"
private val headerSetCookieKey = "Set-Cookie"
private val headerDolphinClientId = "dolphin_platform_intern_dolphinClientId"

private fun createOkHttpRequest(
    request: Transport.Request,
    dolphinClientId: Transport.DolphinClientId?,
    sessionCookie: Transport.SessionCookie?): Request {

    val mediaType = MediaType.parse("application/json")
    return Request.Builder().apply {
        url(request.url)
        post(RequestBody.create(mediaType, request.body))
        header(headerConnectionKey, "keep-alive")
        if (dolphinClientId != null) {
            header(headerDolphinClientId, dolphinClientId.value)
        }
        if (sessionCookie != null) {
            addHeader(headerCookieKey, sessionCookie.value)
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
