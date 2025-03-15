import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import controller.CampaignController;
import controller.CategoryController;
import controller.UserController;
import model.Campaign;
import model.Category;
import model.User;
import java.math.BigDecimal;
import java.util.Optional;

public class CampaignControllerTest {

    private CampaignController campaignController;
    private UserController userController;
    private CategoryController categoryController;

    @Before
    public void setUp() {
        campaignController = new CampaignController();
        userController = new UserController();
        categoryController = new CategoryController();
    }

    @Test
    public void testCreateCampaign() {
        Optional<User> user = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(user.isPresent());

        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());

        Optional<Campaign> campaign = campaignController.createCampaign("Education Fund", "Fund for education", new BigDecimal("10000.00"), user.get(), category.get());
        assertTrue(campaign.isPresent());
        assertEquals("Education Fund", campaign.get().getTitle());
    }

    @Test
    public void testUpdateCampaign() {
        Optional<User> user = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(user.isPresent());

        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());

        Optional<Campaign> campaign = campaignController.createCampaign("Education Fund", "Fund for education", new BigDecimal("10000.00"), user.get(), category.get());
        assertTrue(campaign.isPresent());
        Long campaignId = campaign.get().getId();

        boolean updated = campaignController.updateCampaign(campaignId, "New Title", "New Description", new BigDecimal("20000.00"), category.get());
        assertTrue(updated);

        Optional<Campaign> updatedCampaign = campaignController.getCampaignById(campaignId);
        assertTrue(updatedCampaign.isPresent());
        assertEquals("New Title", updatedCampaign.get().getTitle());
    }

    @Test
    public void testDeleteCampaign() {
        Optional<User> user = userController.registerUser("John Doe", "john.doe@example.com", "password123");
        assertTrue(user.isPresent());

        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());

        Optional<Campaign> campaign = campaignController.createCampaign("Education Fund", "Fund for education", new BigDecimal("10000.00"), user.get(), category.get());
        assertTrue(campaign.isPresent());
        Long campaignId = campaign.get().getId();

        boolean deleted = campaignController.deleteCampaign(campaignId);
        assertTrue(deleted);

        Optional<Campaign> deletedCampaign = campaignController.getCampaignById(campaignId);
        assertFalse(deletedCampaign.isPresent());
    }
}
