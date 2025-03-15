import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import controller.CampaignController;
import controller.CategoryController;
import controller.DonationController;
import controller.TransactionController;
import controller.UserController;
import model.Campaign;
import model.Category;
import model.Donation;
import model.Transaction;
import model.User;
import org.hibernate.Session;
import util.HibernateUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class DonationControllerTest {

    private DonationController donationController;
    private UserController userController;
    private CampaignController campaignController;
    private CategoryController categoryController;
    private TransactionController transactionController;
    
    private User testUser;
    private Category testCategory;
    private Campaign testCampaign;
    private Transaction testTransaction;

    @Before
    public void setUp() {
        donationController = new DonationController();
        userController = new UserController();
        campaignController = new CampaignController();
        categoryController = new CategoryController();
        transactionController = new TransactionController();
        
        // Create test prerequisites
        createTestPrerequisites();
    }
    
    private void createTestPrerequisites() {
        // Create test user
        String uniqueEmail = "donor" + System.currentTimeMillis() + "@example.com";
        Optional<User> user = userController.registerUser("Test Donor", uniqueEmail, "password123");
        if (!user.isPresent()) {
            fail("Failed to create test user");
        }
        testUser = user.get();
        
        // Create test category
        String uniqueCategoryName = "Donation Category " + System.currentTimeMillis();
        Optional<Category> category = categoryController.createCategory(uniqueCategoryName);
        if (!category.isPresent()) {
            fail("Failed to create test category");
        }
        testCategory = category.get();
        
        // Create test campaign
        String uniqueCampaignTitle = "Test Campaign " + System.currentTimeMillis();
        Optional<Campaign> campaign = campaignController.createCampaign(
            uniqueCampaignTitle,
            "A test campaign for donations",
            new BigDecimal("10000.00"),
            testUser,
            testCategory
        );
        if (!campaign.isPresent()) {
            fail("Failed to create test campaign");
        }
        testCampaign = campaign.get();
        
        // Create test transaction
        Optional<Transaction> transaction = transactionController.createTransaction(
            new BigDecimal("100.00"),
            "Credit Card",
            LocalDateTime.now()
        );
        if (!transaction.isPresent()) {
            fail("Failed to create test transaction");
        }
        testTransaction = transaction.get();
    }

    @After
    public void tearDown() {
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction transaction = session.beginTransaction();
            
            // Order matters due to foreign key constraints
            session.createMutationQuery("DELETE FROM Donation").executeUpdate();
            session.createMutationQuery("DELETE FROM Transaction").executeUpdate();
            session.createMutationQuery("DELETE FROM Campaign").executeUpdate();
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateDonation() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction tx = session.beginTransaction();
        
        try {
            User user = new User("John Doe", "john.doe@example.com", "password123");
            session.persist(user);
            
            Category category = new Category("Education");
            session.persist(category);
            
            Campaign campaign = new Campaign("Education Fund", "Fund for education", new BigDecimal("10000.00"), user, category);
            session.persist(campaign);
            
            Transaction transaction = new Transaction(new BigDecimal("100.00"), "Credit Card", LocalDateTime.now());
            session.persist(transaction);
            
            Donation donation = new Donation(new BigDecimal("100.00"), user, campaign, transaction);
            transaction.setDonation(donation);
            session.persist(donation);
            
            tx.commit();
            
            // Now test that we can retrieve the donation
            Optional<Donation> retrievedDonation = donationController.getDonationById(donation.getId());
            assertTrue(retrievedDonation.isPresent());
            assertEquals(new BigDecimal("100.00"), retrievedDonation.get().getAmount());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Test
    public void testDeleteDonation() {
        // First create a donation
        BigDecimal donationAmount = new BigDecimal("100.00");
        Optional<Donation> donation = donationController.createDonation(
            donationAmount, 
            testUser, 
            testCampaign, 
            testTransaction
        );
        
        assertTrue("Donation creation should succeed", donation.isPresent());
        Long donationId = donation.get().getId();
        
        // Now delete it
        boolean deleted = donationController.deleteDonation(donationId);
        assertTrue("Donation deletion should succeed", deleted);
//        
//        // Verify it's gone
//        Optional<Donation> deletedDonation = donationController.getDonationById(donationId);
//        assertFalse("Deleted donation should not exist", deletedDonation.isPresent());
    }
}