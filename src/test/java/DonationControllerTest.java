import static org.junit.Assert.*;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class DonationControllerTest {

    private DonationController donationController;
    private UserController userController;
    private CampaignController campaignController;
    private CategoryController categoryController;
    private TransactionController transactionController;

    @Before
    public void setUp() {
        donationController = new DonationController();
        userController = new UserController();
        campaignController = new CampaignController();
        categoryController = new CategoryController();
        transactionController = new TransactionController();
    }

    @Test
    public void testCreateDonation() {
        Optional<User> user = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(user.isPresent());

        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());

        Optional<Campaign> campaign = campaignController.createCampaign("Education Fund", "Fund for education", new BigDecimal("10000.00"), user.get(), category.get());
        assertTrue(campaign.isPresent());

        Optional<Transaction> transaction = transactionController.createTransaction(new BigDecimal("100.00"), "Credit Card", LocalDateTime.now());
        assertTrue(transaction.isPresent());

        Optional<Donation> donation = donationController.createDonation(new BigDecimal("100.00"), user.get(), campaign.get(), transaction.get());
        assertTrue(donation.isPresent());
        assertEquals(new BigDecimal("100.00"), donation.get().getAmount());
    }

    @Test
    public void testDeleteDonation() {
        Optional<User> user = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(user.isPresent());

        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());

        Optional<Campaign> campaign = campaignController.createCampaign("Education Fund", "Fund for education", new BigDecimal("10000.00"), user.get(), category.get());
        assertTrue(campaign.isPresent());

        Optional<Transaction> transaction = transactionController.createTransaction(new BigDecimal("100.00"), "Credit Card", LocalDateTime.now());
        assertTrue(transaction.isPresent());

        Optional<Donation> donation = donationController.createDonation(new BigDecimal("100.00"), user.get(), campaign.get(), transaction.get());
        assertTrue(donation.isPresent());
        Long donationId = donation.get().getId();

        boolean deleted = donationController.deleteDonation(donationId);
        assertTrue(deleted);

        Optional<Donation> deletedDonation = donationController.getDonationById(donationId);
        assertFalse(deletedDonation.isPresent());
    }
}
