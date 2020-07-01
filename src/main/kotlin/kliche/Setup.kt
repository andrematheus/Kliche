package kliche

class SiteSetup {
    val sourcesBuilders = mutableListOf<SourceSetup>()

    val sources: List<Source>
        get() = sourcesBuilders.map { it.buildSource() }

    fun staticSource(setup: StaticSourceSetup.() -> Unit) {
        val staticSourceSetup = StaticSourceSetup()
        staticSourceSetup.setup()
        this.sourcesBuilders.add(staticSourceSetup)
    }

    interface SourceSetup {
        fun buildSource(): Source
    }

    class StaticSourceSetup : SourceSetup {
        internal val routes = mutableMapOf<String, String>()

        fun route(path: String, response: String) {
            this.routes[path] = response
        }

        override fun buildSource() = StaticSource(routes.toMap())
    }
}
