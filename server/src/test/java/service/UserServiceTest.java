package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class UserServiceTest {

    private static UserService userService;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    private UserData defaultUser;

    @BeforeAll
    static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @BeforeEach
    void setup() {
        userDAO.clear();
        authDAO.clear();
        defaultUser = new UserData("Username", "password", "email");
    }

    @Test
    @DisplayName("Create Valid User")
    void createUserTestPositive() throws BadRequestException {
        AuthData resultAuth = userService.createUser(defaultUser);
        Assertions.assertNotNull(resultAuth);
        Assertions.assertEquals(defaultUser.username(), resultAuth.username());
    }

    @Test
    @DisplayName("Create Invalid User")
    void createUserTestNegative() throws BadRequestException {
        userService.createUser(defaultUser);
        Assertions.assertThrows(BadRequestException.class, () -> userService.createUser(defaultUser));
    }

    @Test
    @DisplayName("Proper Login User")
    void loginUserTestPositive() throws BadRequestException, UnauthorizedException {
        userService.createUser(defaultUser);
        AuthData authData = userService.loginUser(defaultUser);
        Assertions.assertNotNull(authData);
        Assertions.assertEquals(defaultUser.username(), authData.username());
    }

    @Test
    @DisplayName("Improper Login User")
    void loginUserTestNegative() {
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.loginUser(defaultUser));

        Assertions.assertDoesNotThrow(() -> userService.createUser(defaultUser));
        UserData badPassUser = new UserData(defaultUser.username(), "wrongPass", defaultUser.email());
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.loginUser(badPassUser));
    }

    @Test
    @DisplayName("Proper Logout User")
    void logoutUserTestPositive() throws BadRequestException, UnauthorizedException {
        AuthData auth = userService.createUser(defaultUser);
        Assertions.assertDoesNotThrow(() -> userService.logoutUser(auth.authToken()));
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logoutUser(auth.authToken()));
    }

    @Test
    @DisplayName("Improper Logout User")
    void logoutUserTestNegative() {
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logoutUser("badAuthToken"));
    }

    @Test
    @DisplayName("Proper Clear DB")
    void clearTestPositive() throws BadRequestException, UnauthorizedException {
        AuthData auth = userService.createUser(defaultUser);
        userService.clear();
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.loginUser(defaultUser));
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logoutUser(auth.authToken()));
    }

    @Test
    @DisplayName("Improper Clear DB")
    void clearTestNegative() {
        Assertions.assertDoesNotThrow(() -> userService.clear());
    }
}