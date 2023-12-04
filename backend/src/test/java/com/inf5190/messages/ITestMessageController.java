package com.inf5190.messages;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.HttpCookie;
import java.util.concurrent.ExecutionException;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.NewMessageRequest;
import com.inf5190.chat.messages.repository.FirestoreMessage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootConfiguration()
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:firebase.properties")
public class ITestMessageController {
    private final FirestoreMessage message1 = new FirestoreMessage("u1", Timestamp.now(), "t1", null);
    private final FirestoreMessage message2 = new FirestoreMessage("u2", Timestamp.now(), "t2", null);

    @Value("${firebase.project.id}")
    private String firebaseProjectId;

    @Value("${firebase.emulator.port}")
    private String emulatorPort;

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private Firestore firestore;

    private String messagesEndpointUrl;
    private String loginEndpointUrl;

    @BeforeAll
    public static void checkRunAgainstEmulator() {
        checkEmulators();
    }

    @BeforeEach
    public void setup() throws InterruptedException, ExecutionException {
        this.restTemplate = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
        this.messagesEndpointUrl = "http://localhost:" + port + "/messages";
        this.loginEndpointUrl = "http://localhost:" + port + "/auth/login";

        // Pour ajouter deux message dans firestore au début de chaque test.
        this.firestore.collection("messages").document("1")
                .create(this.message1).get();
        this.firestore.collection("messages").document("2")
                .create(this.message2).get();
    }

    @AfterEach
    public void testDown() {
        // Pour effacer le contenu de l'émulateur entre chaque test.
        this.restTemplate.delete(
                "http://localhost:" + this.emulatorPort + "/emulator/v1/projects/"
                        + this.firebaseProjectId
                        + "/databases/(default)/documents");
    }

    @Test
    public void getMessagesWithValidToken() {
        final String sessionCookie = this.login();
        final HttpHeaders header = createHeadersWithSessionCookie(sessionCookie);
        final HttpEntity<Object> headers = new HttpEntity<>(header);
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(this.messagesEndpointUrl,
                HttpMethod.GET, headers, Message[].class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void postMessageWithValidToken() {
        final String sessionCookie = this.login();
        final HttpHeaders header = createHeadersWithSessionCookie(sessionCookie);
        final NewMessageRequest messageRequest = createSampleMessageRequest();
        final HttpEntity<NewMessageRequest> requestEntity = createRequestEntityWithSessionCookie(messageRequest,
                sessionCookie);
        final ResponseEntity<Message> response = this.restTemplate.exchange(this.messagesEndpointUrl,
                HttpMethod.POST, requestEntity, Message.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getMessageWithoutToken() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(this.messagesEndpointUrl,
                String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void postMessageWithoutValidToken() {
        ResponseEntity<String> response = this.restTemplate.postForEntity(this.messagesEndpointUrl,
                createSampleMessageRequest(), String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void getMessagesWithFromIdParameter() {
        final String sessionCookie = this.login();
        final HttpHeaders header = createHeadersWithSessionCookie(sessionCookie);
        final HttpEntity<Object> headers = new HttpEntity<>(header);
        final String fromId = "1";
        final String urlWithParameter = this.messagesEndpointUrl + "?fromId=" + fromId;
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(urlWithParameter,
                HttpMethod.GET, headers, Message[].class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getMessagesWithoutFromIdParameter() {
        final String sessionCookie = this.login();
        final HttpHeaders header = createHeadersWithSessionCookie(sessionCookie);
        final HttpEntity<Object> headers = new HttpEntity<>(header);
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(this.messagesEndpointUrl,
                HttpMethod.GET, headers, Message[].class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getMessagesWithMoreThanTwentyMessages() throws InterruptedException, ExecutionException {
        for (int i = 3; i <= 25; i++) {
            this.firestore.collection("messages").document(Integer.toString(i))
                    .create(new FirestoreMessage("u" + i, Timestamp.now(), "t" + i, null)).get();
        }

        final String sessionCookie = this.login();
        final HttpHeaders header = createHeadersWithSessionCookie(sessionCookie);
        final HttpEntity<Object> headers = new HttpEntity<>(header);
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(this.messagesEndpointUrl,
                HttpMethod.GET, headers, Message[].class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().length).isEqualTo(20);
    }

    @Test
    public void getMessagesWithInvalidFromId() {
        final String sessionCookie = this.login();
        final HttpHeaders header = createHeadersWithSessionCookie(sessionCookie);
        final HttpEntity<Object> headers = new HttpEntity<>(header);
        final String invalidFromId = "invalidId";
        final String urlWithInvalidFromId = this.messagesEndpointUrl + "?fromId=" + invalidFromId;
        final ResponseEntity<String> response = this.restTemplate.exchange(urlWithInvalidFromId,
                HttpMethod.GET, headers, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    private NewMessageRequest createSampleMessageRequest() {
        return new NewMessageRequest("username", "Sample Text", null);
    }

    private String login() {
        ResponseEntity<LoginResponse> response = this.restTemplate.postForEntity(this.loginEndpointUrl,
                new LoginRequest("username", "password"), LoginResponse.class);

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        HttpCookie sessionCookie = HttpCookie.parse(setCookieHeader).get(0);
        return sessionCookie.getName() + "=" + sessionCookie.getValue();
    }

    private HttpEntity<NewMessageRequest> createRequestEntityWithSessionCookie(NewMessageRequest messageRequest,
            String cookieValue) {
        HttpHeaders header = createHeadersWithSessionCookie(cookieValue);
        return new HttpEntity<>(messageRequest, header);
    }

    private HttpHeaders createHeadersWithSessionCookie(String cookieValue) {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.COOKIE, cookieValue);
        return header;
    }

    private static void checkEmulators() {
        final String firebaseEmulator = System.getenv().get("FIRESTORE_EMULATOR_HOST");
        if (firebaseEmulator == null || firebaseEmulator.length() == 0) {
            System.err.println(
                    "**********************************************************************************************************");
            System.err.println(
                    "******** You need to set FIRESTORE_EMULATOR_HOST=localhost:8181 in your system properties. ********");
            System.err.println(
                    "**********************************************************************************************************");
        }
        assertThat(firebaseEmulator).as(
                "You need to set FIRESTORE_EMULATOR_HOST=localhost:8181 in your system properties.")
                .isNotEmpty();
        final String storageEmulator = System.getenv().get("FIREBASE_STORAGE_EMULATOR_HOST");
        if (storageEmulator == null || storageEmulator.length() == 0) {
            System.err.println(
                    "**********************************************************************************************************");
            System.err.println(
                    "******** You need to set FIREBASE_STORAGE_EMULATOR_HOST=localhost:9199 in your system properties. ********");
            System.err.println(
                    "**********************************************************************************************************");
        }
        assertThat(storageEmulator).as(
                "You need to set FIREBASE_STORAGE_EMULATOR_HOST=localhost:9199 in your system properties.")
                .isNotEmpty();
    }
}
