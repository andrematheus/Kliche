package features

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import tools.client.Client
import java.nio.file.Path

@Tag("slow")
class SiteCanRunWithDocker {
    init {
        print("Building kliche image...")
        ImageFromDockerfile("kliche", false)
            .withDockerfile(Path.of("./Dockerfile"))
            .get()
        println("done.")
    }

    @Test
    internal fun `run site with kliche dockerfile`() {
        val container: GenericContainer<*> = GenericContainer<Nothing>(
            ImageFromDockerfile("site-with-docker")
                .withDockerfile(
                    Path.of(
                        SiteCanRunWithDocker::class.java.getResource("/site-with-docker/Dockerfile")
                            .toURI()
                    )
                )
        ).withExposedPorts(9300)

        Client.forContainer().withContainerRunning(container) {
            val response = get("/")
            assertEquals(200, response.statusCode)
            assertEquals("Hello, world!", response.text)
        }
    }
}