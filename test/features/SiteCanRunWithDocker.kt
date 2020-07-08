package features

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import tools.client.Client
import java.nio.file.Path
import kotlin.random.Random
import kotlin.random.nextInt

@Tag("slow")
class SiteCanRunWithDocker {

    init {
        println("Building kliche image...")
        ImageFromDockerfile("kliche", false)
            .withDockerfile(Path.of("./Dockerfile"))
            .get()
        println("done.")
    }

    @Test
    internal fun `run site with kliche dockerfile`() {
        val randomPort = Random.nextInt(5000..9000)
        val container: GenericContainer<*> = GenericContainer<Nothing>(
            ImageFromDockerfile("site-with-docker")
                .withDockerfile(
                    Path.of(
                        SiteCanRunWithDocker::class.java.getResource("/site-with-docker/Dockerfile")
                            .toURI()
                    )
                )
        ).apply {
            withEnv("PORT", randomPort.toString())
            withExposedPorts(randomPort)
        }

        Client.forContainer().withContainerRunning(container) {
            val response = get("/")
            assertEquals(200, response.statusCode)
            assertEquals("Hello, world!", response.text)
        }
    }
}