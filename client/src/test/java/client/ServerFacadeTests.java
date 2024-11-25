package client;

import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private ServerFacade facade;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setup() throws Exception {
        server.clearDB();
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    void cleanup() {
        server.clearDB();
    }

    @Test
    public void registerPositive() {
        assertTrue(registerUser("username", "password", "email"));
    }

    @Test
    public void registerNegative() {
        registerUser("username", "password", "email");
        assertFalse(registerUser("username", "password", "email"));
    }

    @Test
    public void loginPositive() {
        registerUser("username", "password", "email");
        assertTrue(loginUser("username", "password"));
    }

    @Test
    public void loginNegative() {
        registerUser("username", "password", "email");
        assertFalse(loginUser("username", "pass"));
    }

    @Test
    public void logoutPositive() {
        registerAndLoginUser("username", "password", "email");
        assertTrue(facade.logout());
    }

    @Test
    public void logoutNegative() {
        assertFalse(facade.logout());
    }

    @Test
    public void createGamePositive() {
        registerAndLoginUser("username", "password", "email");
        assertTrue(facade.createGame("gameName") >= 0);
    }

    @Test
    public void createGameNegative() {
        assertEquals(-1, facade.createGame("gameName"));
    }

    @Test
    public void listGamesPositive() {
        registerAndLoginUser("username", "password", "email");
        createGame("gameName");
        assertEquals(1, facade.listGames().size());
    }

    @Test
    public void listGamesNegative() {
        assertEquals(facade.listGames(), new HashSet<>());
    }

    @Test
    public void joinGamePositive() {
        registerAndLoginUser("username", "password", "email");
        int gameId = createGame("gameName");
        assertTrue(facade.joinGame(gameId, "WHITE"));
    }

    @Test
    public void joinGameNegative() {
        registerAndLoginUser("username", "password", "email");
        int gameId = createGame("gameName");
        facade.joinGame(gameId, "WHITE");
        assertFalse(facade.joinGame(gameId, "WHITE"));
    }

    private boolean registerUser(String username, String password, String email) {
        return facade.register(username, password, email);
    }

    private boolean loginUser(String username, String password) {
        return facade.login(username, password);
    }

    private void registerAndLoginUser(String username, String password, String email) {
        registerUser(username, password, email);
        loginUser(username, password);
    }

    private int createGame(String gameName) {
        return facade.createGame(gameName);
    }
}