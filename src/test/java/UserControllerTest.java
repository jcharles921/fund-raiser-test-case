import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import controller.UserController;
import model.User;
import org.hibernate.Session;
import util.HibernateUtil;
import java.util.Optional;

public class UserControllerTest {

    private UserController userController;

    @Before
    public void setUp() {
        userController = new UserController();
        cleanUpDatabase(); // Start with a clean database
    }

    @After
    public void tearDown() {
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction transaction = session.beginTransaction();
            
            // Clean up users - be careful with foreign key constraints if users are referenced
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRegisterUserValid() {
        String uniqueEmail = "john.doe" + System.currentTimeMillis() + "@example.com";
        Optional<User> user = userController.registerUser("John Doe", uniqueEmail, "password123");
        
        assertTrue("User registration should succeed", user.isPresent());
        assertEquals("User name should match", "John Doe", user.get().getName());
        assertEquals("User email should match", uniqueEmail, user.get().getEmail());
    }

    @Test
    public void testRegisterUserInvalidEmail() {
        Optional<User> user = userController.registerUser("Jane Doe", "invalid-email", "password123");
        assertFalse("User registration with invalid email should fail", user.isPresent());
    }

    @Test
    public void testRegisterUserDuplicateEmail() {
        String duplicateEmail = "duplicate" + System.currentTimeMillis() + "@example.com";
        
        // First registration
        Optional<User> user1 = userController.registerUser("John Doe", duplicateEmail, "password123");
        assertTrue("First user registration should succeed", user1.isPresent());
        
        // Second registration with same email
        Optional<User> user2 = userController.registerUser("Jane Doe", duplicateEmail, "password123");
        assertFalse("User registration with duplicate email should fail", user2.isPresent());
    }

    @Test
    public void testLoginValid() {
        String email = "login" + System.currentTimeMillis() + "@example.com";
        String password = "password123";
        
        // Register a user first
        userController.registerUser("Login User", email, password);
        
        // Try to login
        Optional<User> loggedInUser = userController.login(email, password);
        assertTrue("Login with valid credentials should succeed", loggedInUser.isPresent());
        assertEquals("Logged in user name should match", "Login User", loggedInUser.get().getName());
    }

    @Test
    public void testLoginInvalid() {
        String email = "badlogin" + System.currentTimeMillis() + "@example.com";
        
        // Try to login without registering
        Optional<User> user = userController.login(email, "wrongpassword");
        assertFalse("Login with invalid credentials should fail", user.isPresent());
    }

    @Test
    public void testUpdateUserProfile() {
        // First register a user
        String originalEmail = "update" + System.currentTimeMillis() + "@example.com";
        Optional<User> registeredUser = userController.registerUser("Original Name", originalEmail, "password123");
        assertTrue("User registration should succeed", registeredUser.isPresent());
        Long userId = registeredUser.get().getId();

        // Now update the profile
        String newName = "Updated Name";
        String newEmail = "updated" + System.currentTimeMillis() + "@example.com";
        boolean updated = userController.updateUserProfile(userId, newName, newEmail, "newpassword");
        assertTrue("User profile update should succeed", updated);

        // Verify the update
        Optional<User> updatedUser = userController.getUserById(userId);
        assertTrue("Updated user should exist", updatedUser.isPresent());
        assertEquals("User name should be updated", newName, updatedUser.get().getName());
        assertEquals("User email should be updated", newEmail, updatedUser.get().getEmail());
    }

    @Test
    public void testDeleteUser() {
        // First register a user
        String email = "delete" + System.currentTimeMillis() + "@example.com";
        Optional<User> registeredUser = userController.registerUser("Delete User", email, "password123");
        assertTrue("User registration should succeed", registeredUser.isPresent());
        Long userId = registeredUser.get().getId();

        // Now delete the user
        boolean deleted = userController.deleteUser(userId);
        assertTrue("User deletion should succeed", deleted);

        // Verify the user is gone
        Optional<User> deletedUser = userController.getUserById(userId);
        assertFalse("Deleted user should not exist", deletedUser.isPresent());
    }
}