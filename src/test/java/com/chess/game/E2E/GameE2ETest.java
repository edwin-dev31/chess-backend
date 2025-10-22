package com.chess.game.E2E;

import com.chess.game.application.dto.game.CreateGameDTO;
import com.chess.game.application.dto.game.GameResponseDTO;
import com.chess.game.application.dto.move.MoveResponseDTO;
import com.chess.game.application.dto.move.CreateMoveDTO;
import com.chess.game.application.dto.player.AuthResponse;
import com.chess.game.application.dto.player.CreatePlayerDTO;
import org.hashids.Hashids;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameE2ETest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private WebSocketStompClient stompClient;

    private static String player1Jwt;
    private static Long player1Id;
    private static String player2Jwt;
    private static Long player2Id;
    private static String gameId;

    private static final Hashids hashids = new Hashids(null, 6, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    @BeforeEach
    void setup() {
        restTemplate = new TestRestTemplate();

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    @Order(1)
    void testRegisterAndLoginPlayers() {
        CreatePlayerDTO player1Dto = new CreatePlayerDTO();
        player1Dto.setUsername("player1");
        player1Dto.setEmail("p1@example.com");
        player1Dto.setPassword("password123");
        ResponseEntity<AuthResponse> response1 = restTemplate.postForEntity(
                "http://localhost:" + port + "/chess/auth/register", player1Dto, AuthResponse.class);
        assertThat(response1.getStatusCode().is2xxSuccessful()).isTrue();
        AuthResponse authResponse1 = response1.getBody();
        assertThat(authResponse1).isNotNull();
        player1Jwt = authResponse1.getToken();
        player1Id = authResponse1.getPlayer().getId();
        assertThat(player1Jwt).isNotNull();
        assertThat(player1Id).isNotNull();

        CreatePlayerDTO player2Dto = new CreatePlayerDTO();
        player2Dto.setUsername("player2");
        player2Dto.setEmail("p2@example.com");
        player2Dto.setPassword("password123");
        ResponseEntity<AuthResponse> response2 = restTemplate.postForEntity(
                "http://localhost:" + port + "/chess/auth/register", player2Dto, AuthResponse.class);
        assertThat(response2.getStatusCode().is2xxSuccessful()).isTrue();
        AuthResponse authResponse2 = response2.getBody();
        assertThat(authResponse2).isNotNull();
        player2Jwt = authResponse2.getToken();
        player2Id = authResponse2.getPlayer().getId();
        assertThat(player2Jwt).isNotNull();
        assertThat(player2Id).isNotNull();
    }

    @Test
    @Order(2)
    void testCreateAndStartGame() {
        CreateGameDTO createGameDTO = new CreateGameDTO(player1Id, player2Id, "5+0");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(player1Jwt);
        HttpEntity<CreateGameDTO> entity = new HttpEntity<>(createGameDTO, headers);

        ResponseEntity<GameResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/chess/api/games", HttpMethod.POST, entity, GameResponseDTO.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        GameResponseDTO gameResponse = response.getBody();
        assertThat(gameResponse).isNotNull();

        long numericGameId = gameResponse.getId();
        gameId = hashids.encode(numericGameId);
        assertThat(gameId).isNotNull();

        HttpEntity<Void> startEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> startResponse = restTemplate.exchange(
                "http://localhost:" + port + "/chess/api/games/" + gameId + "/start?time=5m", HttpMethod.POST, startEntity, Void.class);
        assertThat(startResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @Order(3)
    void testPlayFirstMove() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<MoveResponseDTO> future = new CompletableFuture<>();

        StompSession stompSession = stompClient.connectAsync("ws://localhost:" + port + "/chess/ws", new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/topic/moves/" + gameId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MoveResponseDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                future.complete((MoveResponseDTO) payload);
            }
        });

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/moves/" + gameId);
        stompHeaders.add("Authorization", "Bearer " + player1Jwt);
        CreateMoveDTO move = new CreateMoveDTO("e2", "e4");
        stompSession.send(stompHeaders, move);

        MoveResponseDTO receivedMove = future.get(5, TimeUnit.SECONDS);

        assertThat(receivedMove).isNotNull();
        assertThat(receivedMove.getFen()).contains("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
    }

    @Test
    @Order(4)
    void testInvalidMove() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Map> errorFuture = new CompletableFuture<>();
        CompletableFuture<MoveResponseDTO> moveFuture = new CompletableFuture<>();

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + player2Jwt);

        StompSession stompSession = stompClient.connectAsync("ws://localhost:" + port + "/chess/ws", (WebSocketHttpHeaders) null, connectHeaders, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/user/queue/errors", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorFuture.complete((Map) payload);
            }
        });

        stompSession.subscribe("/topic/moves/" + gameId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MoveResponseDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                moveFuture.complete((MoveResponseDTO) payload);
            }
        });

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/moves/" + gameId);
        stompHeaders.add("Authorization", "Bearer " + player2Jwt);
        CreateMoveDTO move = new CreateMoveDTO("d7", "d4"); // Invalid move
        stompSession.send(stompHeaders, move);

        Map<String, String> error = errorFuture.get(5, TimeUnit.SECONDS);
        assertThat(error).isNotNull();
        assertThat(error.get("message")).isEqualTo("Illegal move: Piece cannot move that way.");

        assertThrows(TimeoutException.class, () -> {
            moveFuture.get(2, TimeUnit.SECONDS);
        });
    }

    @Test
    @Order(5)
    void testMoveWhenNotYourTurn() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Map> errorFuture = new CompletableFuture<>();

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + player1Jwt);

        StompSession stompSession = stompClient.connectAsync("ws://localhost:" + port + "/chess/ws", (WebSocketHttpHeaders) null, connectHeaders, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/user/queue/errors", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorFuture.complete((Map) payload);
            }
        });

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/moves/" + gameId);
        stompHeaders.add("Authorization", "Bearer " + player1Jwt);
        CreateMoveDTO move = new CreateMoveDTO("d2", "d4");
        stompSession.send(stompHeaders, move);

        Map<String, String> error = errorFuture.get(5, TimeUnit.SECONDS);
        assertThat(error).isNotNull();
        assertThat(error.get("message")).isEqualTo("It's not your turn.");
    }
}