package protokola

class Protokola {
    fun resource(name: String): Resource = Resource(name)
}

class Resource(val name: String) {
    fun messages(): Sequence<Messages> = emptySequence()
}

data class Messages(val data: Map<String, Any>)
