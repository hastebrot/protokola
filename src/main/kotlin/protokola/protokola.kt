package protokola

class Protokola {
    fun resource(name: String): Resource = Resource(name)
}

class Resource(val name: String) {
    fun messages(): Sequence<Message> = emptySequence()
}

data class Message(val data: List<Any>)
