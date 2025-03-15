import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import controller.CampaignController;
import controller.CategoryController;
import controller.UserController;
import model.Campaign;
import model.Category;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import java.math.BigDecimal;
import java.util.Optional;

public class CampaignControllerTest {

    private CampaignController campaignController;
    private UserController userController;
    private CategoryController categoryController;
    private User testUser;
    private Category testCategory;

    @Before
    public void setUp() {
        campaignController = new CampaignController();
        userController = new UserController();
        categoryController = new CategoryController();
        
        // Create test user and category for all tests to use
        createTestUserAndCategory();
    }
    
    private void createTestUserAndCategory() {
        // Create a test user
        String uniqueEmail = "test.user" + System.currentTimeMillis() + "@example.com";
        Optional<User> user = userController.registerUser("Test User", uniqueEmail, "password123");
        if (user.isPresent()) {
            testUser = user.get();
        } else {
            fail("Failed to create test user");
        }
        
        // Create a test category
        String uniqueCategoryName = "TestCategory" + System.currentTimeMillis();
        Optional<Category> category = categoryController.createCategory(uniqueCategoryName);
        if (category.isPresent()) {
            testCategory = category.get();
        } else {
            fail("Failed to create test category");
        }
    }

    @After
    public void tearDown() {
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Delete all campaigns first (due to foreign key constraints)
            session.createMutationQuery("DELETE FROM Campaign").executeUpdate();
            // Delete all donations
            session.createMutationQuery("DELETE FROM Donation").executeUpdate();
            // Delete all categories
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            // Delete all users
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Test
    public void testCreateCampaign() {
        String uniqueTitle = "Test Campaign " + System.currentTimeMillis();
        Optional<Campaign> campaign = campaignController.createCampaign(
            uniqueTitle, 
            "A test campaign description",
            new BigDecimal("10000.00"),
            testUser,
            testCategory
        );
        
        assertTrue("Campaign creation should succeed", campaign.isPresent());
        assertEquals("Campaign title should match", uniqueTitle, campaign.get().getTitle());
    }

    @Test
    public void testUpdateCampaign() {
        // First create a campaign
        String originalTitle = "Original Campaign " + System.currentTimeMillis();
        Optional<Campaign> campaign = campaignController.createCampaign(
            originalTitle,
            "Original description",
            new BigDecimal("10000.00"),
            testUser,
            testCategory
        );
        
        assertTrue("Campaign creation should succeed", campaign.isPresent());
        Long campaignId = campaign.get().getId();
        
        // Now update it
        String newTitle = "Updated Campaign " + System.currentTimeMillis();
        boolean updated = campaignController.updateCampaign(
            campaignId,
            newTitle,
            "Updated description",
            new BigDecimal("20000.00"),
            testCategory
        );
        
        assertTrue("Campaign update should succeed", updated);
        
        // Verify the update
        Optional<Campaign> updatedCampaign = campaignController.getCampaignById(campaignId);
        assertTrue("Updated campaign should exist", updatedCampaign.isPresent());
        assertEquals("Campaign title should be updated", newTitle, updatedCampaign.get().getTitle());
    }

    @Test
    public void testDeleteCampaign() {
        // First create a campaign
        String campaignTitle = "Campaign to Delete " + System.currentTimeMillis();
        Optional<Campaign> campaign = campaignController.createCampaign(
            campaignTitle,
            "This campaign will be deleted",
            new BigDecimal("10000.00"),
            testUser,
            testCategory
        );
        
        assertTrue("Campaign creation should succeed", campaign.isPresent());
        Long campaignId = campaign.get().getId();
        
        // Now delete it
        boolean deleted = campaignController.deleteCampaign(campaignId);
        assertTrue("Campaign deletion should succeed", deleted);
        
        // Verify it's gone
        Optional<Campaign> deletedCampaign = campaignController.getCampaignById(campaignId);
        assertFalse("Deleted campaign should not exist", deletedCampaign.isPresent());
    }
}