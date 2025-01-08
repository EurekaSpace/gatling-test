import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

class WebsocketSimulation : Simulation() {

    private val httpProtocol = http
            .wsBaseUrl("ws://localhost:8080")

    private var senderId = 1

    // 구독자 시나리오
    private val subscriberScenario = scenario("Chat Room Subscriber Test")
            .exec(
                    ws("Connect to Chat Room").connect("/chat/gatling").await(60).on(
                            ws.checkTextMessage("Message Received")
                                    .check(jsonPath("$.content").exists())
                    ),
            )
            .pause(1)
            .exec(
                    ws("Close Connection").close()
            )

    // 발신자 시나리오
    private val publisherScenario = scenario("Message Publisher Test")
            .pause(30)
            .exec(
                    ws("Connect as Publisher").connect("/chat/gatling")
            )
            .exec(
                    ws("Send Broadcast Message")
                            .sendText("Hello, I'm ${senderId++}")
            )
            .pause(1)
            .exec(
                    ws("Close Publisher").close()
            )

    init {
        setUp(
                subscriberScenario.injectOpen(rampUsers(10000).during(30)),
                publisherScenario.injectOpen(rampUsers(1000).during(5)),
        ).protocols(httpProtocol)
    }
}
