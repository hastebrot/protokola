package protokola.transport

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer

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

    var clientId: String? = null

    run {
        val jsonString = listOf(
            mapOf("id" to "CreateContext")
        ).toJson()
        val request = makeDolphinRequest(jsonString, dolphinUrl)
//        println(request.body()!!.string())
//        println(request.body()!!.contentType())

        val response = client.newCall(request).execute()
        println(response.code())
        println(response.body()!!.string())
//        println(response.header(clientIdKey))
        clientId = response.header(clientIdKey)
        response.close()
    }

    run {
        val jsonString = listOf(
            mapOf("id" to "CreateController", "n" to "FooController", "c_id" to null)
        ).toJson()
        val request = makeDolphinRequest(jsonString, dolphinUrl, clientId)
//        println(request.body()!!.string())
//        println(request.body()!!.contentType())
//        println(request.headers())

        val response = client.newCall(request).execute()
        println(response.code())
        println(response.body()!!.string())
//        println(response.header(clientIdKey))
        clientId = response.header(clientIdKey)
        response.close()
    }

    run {
        val jsonString = listOf(
            mapOf("id" to "StartLongPoll")
        ).toJson()
        val request = makeDolphinRequest(jsonString, dolphinUrl, clientId)
//        println(request.body()!!.string())
//        println(request.body()!!.contentType())
//        println(request.headers())

        val response = client.newCall(request).execute()
        println(response.code())
        println(response.body()!!.string())
//        println(response.header(clientIdKey))
        clientId = response.header(clientIdKey)
        response.close()
    }
}

private val clientIdKey = "dolphin_platform_intern_dolphinClientId"

private fun makeDolphinRequest(
        commands: String,
        endpoint: String = "http://localhost:8080/dolphin",
        clientId: String? = null): Request {
//    val mediaType = MediaType.parse("application/json")
    val mediaType = MediaType.parse("text/plain")
    return Request.Builder().apply {
        url(endpoint)
        post(RequestBody.create(mediaType, commands))
        header("connection", "keep-alive")
//        header("Access-Control-Allow-Origin", "*")
//        header("Access-Control-Allow-Headers", "Content-Type")
//        header("Access-Control-Allow-Methods", "POST")
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
