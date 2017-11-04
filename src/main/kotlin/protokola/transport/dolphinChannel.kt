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
import protokola.transport.Transport.ClientRequest
import protokola.transport.Transport.DolphinClientId
import protokola.transport.Transport.SessionCookie
import java.util.concurrent.ConcurrentHashMap

fun main(args: Array<String>) {
    val bus = MessageBus()
    bus.subscribe { println(it.payload) }

    val channel = DolphinChannel()
    channel.dispatchTo(bus)

    val dolphinEndpointUrl = "http://localhost:8080/dolphin"

    val requestContext = ClientRequest(dolphinEndpointUrl, listOf(
        mapOf("id" to "CreateContext")
    ).toJson())

    val requestController = ClientRequest(dolphinEndpointUrl, listOf(
        mapOf("id" to "CreateController", "n" to "FooController", "c_id" to null)
    ).toJson())

    val requestLongPoll = ClientRequest(dolphinEndpointUrl, listOf(
        mapOf("id" to "StartLongPoll")
    ).toJson())

    bus.dispatch(Message(requestContext))
    bus.dispatch(Message(requestController))
    bus.dispatch(Message(requestLongPoll))
}

object Transport {
    data class ClientRequest(val url: String,
                             val body: String)

    data class ServerResponse(val status: Int,
                              val body: String)

    data class DolphinClientId(val value: String)

    data class SessionCookie(val value: String)
}

class DolphinChannel {
    private val client = OkHttpClient.Builder()
//        .cookieJar(simpleCookieJar())
        .build()

    private var dolphinClientId: DolphinClientId? = null

    private var sessionCookie: SessionCookie? = null

    fun dispatchTo(messageBus: MessageBus) {
        messageBus.subscribe { message ->
            @Suppress("UNCHECKED_CAST")
            when (message.payload) {
                is ClientRequest -> fetch(messageBus, message as Message<ClientRequest>)
            }
        }
    }

    private fun fetch(bus: MessageBus,
                      clientRequest: Message<ClientRequest>) {
        val request = createRequest(
            clientRequest.payload,
            dolphinClientId,
            sessionCookie
        )

        val call = client.newCall(request)
        call.execute().use { response ->
            val dolphinClientIdValue = response.header(headerDolphinClientId)
            val sessionCookieValue = response.headers(headerSetCookieKey).firstOrNull()

            val serverResponse = Message(Transport.ServerResponse(
                response.code(), response.body()!!.string()
            ))
            bus.dispatch(serverResponse)

            dolphinClientIdValue?.let {
                dolphinClientId = DolphinClientId(it)
                bus.dispatch(Message(dolphinClientId))
            }
            sessionCookieValue?.let {
                sessionCookie = SessionCookie(it)
                bus.dispatch(Message(sessionCookie))
            }
        }
    }
}

private val headerConnectionKey = "Connection"
private val headerCookieKey = "Cookie"
private val headerSetCookieKey = "Set-Cookie"
private val headerDolphinClientId = "dolphin_platform_intern_dolphinClientId"

private fun createRequest(
    clientRequest: ClientRequest,
    dolphinClientId: DolphinClientId?,
    sessionCookie: SessionCookie?): Request {

    val mediaType = MediaType.parse("application/json")
    return Request.Builder().apply {
        url(clientRequest.url)
        post(RequestBody.create(mediaType, clientRequest.body))
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
