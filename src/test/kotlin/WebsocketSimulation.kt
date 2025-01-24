import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

class WebsocketSimulation : Simulation() {

    private val httpProtocol = http
            .wsBaseUrl("ws://localhost:8081")

    private val scenario = scenario("WebSocket Test")
            .exec(
                    ws("Connect to Channel").connect("/chat/ch1").onConnected(
                            repeat(3, "i").on(
                                    exec(
                                            ws("Send Broadcast Message")
                                                    .sendText("Hello")
                                    ).pause(10)
                            )
                    )
            )
            .exec(
                    ws("Close Publisher").close()
            )

    init {
        setUp(
                scenario.injectOpen(
                        rampUsers(1000).during(30), // 30초 동안 1000명 점진적 연결
//                        constantUsersPerSec(100.0).during(120) // 2분 동안 초당 100명 연결
                )
        ).protocols(httpProtocol)
    }
}
