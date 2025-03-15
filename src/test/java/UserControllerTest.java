import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.UserController;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.UUID;

public class UserControllerTest {

    private UserController userController;
    private User testUser;

    @Before
    public void setUp() {
        userController = new UserController();
    }

    @After
    public void tearDown() {
        if (testUser != null) {
            userController.deleteUser(testUser.getId());
        }
    }

    @Test
    public void testRegisterUserWithValidData() {
        String email = "test_" + UUID.randomUUID() + "@example.com";
        Optional<User> result = userController.registerUser("Valid User", email, "password123");
        assertTrue(result.isPresent());
        testUser = result.get();
        assertEquals(email, testUser.getEmail());
    }

    @Test
    public void testRegisterUserWithDuplicateEmail() {
        String email = "duplicate_" + UUID.randomUUID() + "@example.com";
        userController.registerUser("User 1", email, "password");
        Optional<User> result = userController.registerUser("User 2", email, "password");
        assertFalse(result.isPresent());
    }

    @Test
    public void testRegisterUserWithInvalidEmail() {
        Optional<User> result = userController.registerUser("User", "invalid-email", "password");
        assertFalse(result.isPresent());
    }

    @Test
    public void testRegisterUserWithShortPassword() {
        String email = "shortpass_" + UUID.randomUUID() + "@example.com";
        Optional<User> result = userController.registerUser("User", email, "short");
        assertFalse(result.isPresent());
    }

    @Test
    public void testLoginWithValidCredentials() {
        String email = "loginvalid_" + UUID.randomUUID() + "@example.com";
        String password = "password123";
        userController.registerUser("Login User", email, password);
        Optional<User> result = userController.login(email, password);
        assertTrue(result.isPresent());
        testUser = result.get();
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        String email = "logininvalid_" + UUID.randomUUID() + "@example.com";
        userController.registerUser("Login User", email, "password123");
        Optional<User> result = userController.login(email, "wrongpass");
        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateUserProfile() {
        String email = "update_" + UUID.randomUUID() + "@example.com";
        testUser = userController.registerUser("Update User", email, "password").get();
        String newEmail = "updated_" + UUID.randomUUID() + "@example.com";
        boolean success = userController.updateUserProfile(testUser.getId(), "New Name", newEmail, "newpassword");
        assertTrue(success);
        Optional<User> updatedUser = userController.getUserById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("New Name", updatedUser.get().getName());
        assertEquals(newEmail, updatedUser.get().getEmail());
        assertEquals("newpassword", updatedUser.get().getPassword());
    }

    @Test
    public void testUpdateUserEmailToExisting() {
        String email1 = "existing1_" + UUID.randomUUID() + "@example.com";
        User user1 = userController.registerUser("User 1", email1, "password").get();
        String email2 = "existing2_" + UUID.randomUUID() + "@example.com";
        testUser = userController.registerUser("User 2", email2, "password").get();
        boolean success = userController.updateUserProfile(testUser.getId(), null, email1, null);
        assertFalse(success);
        userController.deleteUser(user1.getId());
    }

    @Test
    public void testDeleteUser() {
        String email = "delete_" + UUID.randomUUID() + "@example.com";
        testUser = userController.registerUser("Delete User", email, "password").get();
        boolean deleted = userController.deleteUser(testUser.getId());
        assertTrue(deleted);
        Optional<User> result = userController.getUserById(testUser.getId());
        assertFalse(result.isPresent());
        testUser = null;
    }
}