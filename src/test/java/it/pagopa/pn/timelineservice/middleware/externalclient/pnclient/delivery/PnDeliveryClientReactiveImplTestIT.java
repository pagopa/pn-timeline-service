package it.pagopa.pn.timelineservice.middleware.externalclient.pnclient.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.timelineservice.exceptions.PnNotFoundException;
import it.pagopa.pn.timelineservice.generated.openapi.msclient.delivery.model.SentNotificationV24;
import it.pagopa.pn.timelineservice.middleware.externalclient.delivery.PnDeliveryClientReactive;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "pn.delivery-push.delivery-base-url=http://localhost:9998",
})
class PnDeliveryClientReactiveImplTestIT  {
    @Autowired
    private PnDeliveryClientReactive client;
    
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void startMockServer() {

        mockServer = startClientAndServer(9998);
    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop();
    }

    @Test
    @Disabled("check error")
    void getSentNotification() throws JsonProcessingException {
        //Given
        String iun ="iunTest";
        SentNotificationV24 notification = new SentNotificationV24();
        notification.setIun(iun);
        
        String path = "/delivery-private/notifications/{iun}"
                .replace("{iun}",iun);
        
        ObjectMapper mapper = new ObjectMapper();
        String respjson = mapper.writeValueAsString(notification);

        new MockServerClient("localhost", 9998)
                .when(request()
                        .withMethod("GET")
                        .withPath(path))
                .respond(response()
                        .withBody(respjson)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withStatusCode(200));
        
        Mono<SentNotificationV24> response = client.getSentNotification(iun);

        SentNotificationV24 notificationResponse = response.block();
        Assertions.assertNotNull(notificationResponse);
        Assertions.assertEquals(notification, notificationResponse);
    }
    
    @Test
    void getSentNotificationError(){
        //Given
        String iun ="iunTest";
        SentNotificationV24 notification = new SentNotificationV24();
        notification.setIun(iun);

        String path = "/delivery-private/notifications/{iun}"
                .replace("{iun}",iun);
        
        new MockServerClient("localhost", 9998)
                .when(request()
                        .withMethod("GET")
                        .withPath(path))
                .respond(response()
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withStatusCode(400));

        client.getSentNotification(iun).onErrorResume(
                ex -> {
                    Assertions.assertNotNull(ex);
                    return Mono.empty();
                }
        );
    }

    @Test
    void getSentNotificationError404(){
        //Given
        String iun ="iunTest";
        SentNotificationV24 notification = new SentNotificationV24();
        notification.setIun(iun);

        String path = "/delivery-private/notifications/{iun}"
                .replace("{iun}",iun);

        new MockServerClient("localhost", 9998)
                .when(request()
                        .withMethod("GET")
                        .withPath(path))
                .respond(response()
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withStatusCode(404));

        client.getSentNotification(iun).onErrorResume(
                ex -> {
                    Assertions.assertNotNull(ex);
                    Assertions.assertEquals(PnNotFoundException.class, ex.getClass());
                    return Mono.empty();
                }
        );
    }

}