import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import java.util.concurrent.atomic.AtomicInteger

class WebsocketSimulation : Simulation() {

    private val httpProtocol = http
            .wsBaseUrl("ws://localhost:8080")

/*    private val messageCount = AtomicInteger(0)

    private val subscriberScenario = scenario("Chat Room Subscriber Test")
            .exec(
                    ws("Connect to Chat Room")
                            .connect("/chat/gatling")
//                            .await(60).on(
//                                    ws.checkTextMessage("Message Received")
//                                            .check(jsonPath("$.content").`is`("Hello, I'm sender"))
//                            )
            )
            .pause(180)
            .exec(
                    // collect the last text message and store it in the Session
                    ws.processUnmatchedMessages { messages, session ->

                        check(messages.count().equals(messageCount.get()))
                    }
            )
            .exec(
                    ws("Close Connection").close()
            )*/

    private val scenario = scenario("WebSocket Test")
            .exec(
                    ws("Connect to Channel").connect("/chat/test-2").onConnected(
                            repeat(2, "i").on(
                                    exec(
                                            ws("Send Broadcast Message")
                                                    .sendText("Hello")
                                                    .await(3).on(
                                                            ws.checkTextMessage("Message Received")
                                                                    .check(jsonPath("$.content").`is`("Hello"))
                                                    )
                                    ).pause(1)
                            )
                    )
            )
            .exec(
                    ws("Close Publisher").close()
            )

    init {
        setUp(
                scenario.injectOpen(
                        rampUsers(10000).during(20),
                )
        ).protocols(httpProtocol)
    }
}
