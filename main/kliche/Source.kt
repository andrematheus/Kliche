package kliche

interface Source {
    fun handles(requestPath: String): Boolean
    fun handle(requestPath: String): String
}

class EmbeddedSource(val routes: Map<String, String>) : Source {
    override fun handles(requestPath: String) = requestPath in routes

    override fun handle(requestPath: String): String {
        return routes[requestPath]!!
    }
}