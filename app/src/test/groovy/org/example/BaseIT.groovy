package org.example

import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@ActiveProfiles("integration-test")
class BaseIT extends Specification {

    private static final WIREMOCK_SERVER_DOCKER_IMAGE = DockerImageName.parse("wiremock/wiremock:3.9.2")

    static WIREMOCK_EXPOSED_PORT = 8080

    @Shared
    static wiremockServer = new GenericContainer(WIREMOCK_SERVER_DOCKER_IMAGE)
            .withExposedPorts(WIREMOCK_EXPOSED_PORT)
            .withCommand("--global-response-templating")
            .waitingFor(Wait.forHttp("/__admin/mappings")
                    .withMethod("GET")
                    .forStatusCode(200))

    @Shared
    WireMock wireMock

    @Autowired
    protected TestRestTemplate template

    @LocalServerPort
    private int webEnvironmentPort

    protected UriComponentsBuilder baseUrl

    def setupSpec() {
        wiremockServer
                .withClasspathResourceMapping("wiremock/stubs", "/home/wiremock", BindMode.READ_ONLY)
                .start()

        wireMock = new WireMock(wiremockServer.getHost(), wiremockServer.getMappedPort(WIREMOCK_EXPOSED_PORT))

        WireMock.configureFor(wireMock)
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        //wiremock
        def wiremockServerPort = wiremockServer.getMappedPort(WIREMOCK_EXPOSED_PORT)
        registry.add("spring.main.urls.poke-url=", () -> wiremockServer.getHost() + ":" + wiremockServerPort + '/pokemon')
    }

    def setup() {
        baseUrl = UriComponentsBuilder.fromUriString("http://localhost:${webEnvironmentPort}")
    }
}
