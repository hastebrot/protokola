package protokola.transport

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer

data class HttpRequest(val body: String)
data class HttpResponse(val status: Int, val body: String)

// cors (cross-origin resource sharing)
// transport.EndpointUrl()
// transport.CookieSessionId()
// transport.DolphinClientId()
// codec.CreateContext()
// codec.CreateController()

fun main(args: Array<String>) {
    val client = OkHttpClient.Builder()
            .cookieJar(simpleCookieJar())
            .build()

    val dolphinUrl = "http://localhost:8080/dolphin"

    val request1 = HttpRequest(listOf(
        mapOf("id" to "CreateContext")
    ).toJson())

    val request2 = HttpRequest(listOf(
        mapOf("id" to "CreateController", "n" to "FooController", "c_id" to null)
    ).toJson())

    val request3 = HttpRequest(listOf(
        mapOf("id" to "StartLongPoll")
    ).toJson())

    var clientId: String? = null

    fun fetch(httpRequest: HttpRequest): HttpResponse {
        val request = makeDolphinRequest(httpRequest.body, dolphinUrl, clientId)
//        println(request.body()!!.string())
//        println(request.body()!!.contentType())
//        println(request.headers())

        val response = client.newCall(request).execute()
        clientId = response.header(clientIdKey)

        val httpResponse = HttpResponse(response.code(), response.body()!!.string())
        response.close()
        return httpResponse
    }

    println(request1)
    println(fetch(request1))

    println(request2)
    println(fetch(request2))

    println(request3)
    println(fetch(request3))
}

private val clientIdKey = "dolphin_platform_intern_dolphinClientId"

private fun makeDolphinRequest(
        commands: String,
        endpoint: String = "http://localhost:8080/dolphin",
        clientId: String? = null): Request {
    val mediaType = MediaType.parse("application/json")
    return Request.Builder().apply {
        url(endpoint)
        post(RequestBody.create(mediaType, commands))
        header("connection", "keep-alive")
        if (clientId != null) {
            header(clientIdKey, clientId)
        }
    }.build()
}

private fun List<*>.toJson() = toString()
private fun Map<*, *>.toJson() = toString()

private fun simpleCookieJar() = object : CookieJar {
    private var cookieMap = mutableMapOf<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl,
                                  cookies: List<Cookie>) {
        cookieMap[url.host()] = cookies
    }

    override fun loadForRequest(url: HttpUrl) =
        cookieMap[url.host()] ?: listOf()
}

private fun RequestBody.string() = Buffer()
        .also { writeTo(it) }
        .readUtf8()
