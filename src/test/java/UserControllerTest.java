import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import controller.UserController;
import model.User;
import java.util.Optional;

public class UserControllerTest {

    private UserController userController;

    @Before
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void testRegisterUserValid() {
        Optional<User> user = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(user.isPresent());
        assertEquals("John Doe", user.get().getName());
    }

    @Test
    public void testRegisterUserInvalidEmail() {
        Optional<User> user = userController.registerUser("Jane Doe", "invalid-email", "password123");
        assertFalse(user.isPresent());
    }

    @Test
    public void testRegisterUserDuplicateEmail() {
        userController.registerUser("John Doe", "john.doe@example.com", "password123");
        Optional<User> user = userController.registerUser("Jane Doe", "john.doe@example.com", "password123");
        assertFalse(user.isPresent());
    }

    @Test
    public void testLoginValid() {
        userController.registerUser("John Doe", "john.doe@example.com", "password123");
        Optional<User> user = userController.login("john.doe@example.com", "password123");
        assertTrue(user.isPresent());
        assertEquals("John Doe", user.get().getName());
    }

    @Test
    public void testLoginInvalid() {
        Optional<User> user = userController.login("john.doe@example.com", "wrongpassword");
        assertFalse(user.isPresent());
    }

    @Test
    public void testUpdateUserProfile() {
        Optional<User> registeredUser = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(registeredUser.isPresent());
        Long userId = registeredUser.get().getId();

        boolean updated = userController.updateUserProfile(userId, "Jane Doe", "jane.doe@example.com", "newpassword");
        assertTrue(updated);

        Optional<User> updatedUser = userController.getUserById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals("Jane Doe", updatedUser.get().getName());
        assertEquals("jane.doe@example.com", updatedUser.get().getEmail());
    }

    @Test
    public void testDeleteUser() {
        Optional<User> registeredUser = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(registeredUser.isPresent());
        Long userId = registeredUser.get().getId();

        boolean deleted = userController.deleteUser(userId);
        assertTrue(deleted);

        Optional<User> deletedUser = userController.getUserById(userId);
        assertFalse(deletedUser.isPresent());
    }
}
