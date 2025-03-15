import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import controller.CategoryController;
import model.Category;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import java.util.Optional;

public class CategoryControllerTest {

    private CategoryController categoryController;

    @Before
    public void setUp() {
        categoryController = new CategoryController();
        // Clean up any existing test data
        cleanUpDatabase();
    }

    @After
    public void tearDown() {
        // Clean up test data after each test
        cleanUpDatabase();
    }

    private void cleanUpDatabase() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            // Delete all categories - be careful with foreign key constraints
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
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
    public void testCreateCategory() {
        // Generate unique category name to avoid conflicts
        String categoryName = "Education-" + System.currentTimeMillis();
        Optional<Category> category = categoryController.createCategory(categoryName);
        assertTrue("Category creation should succeed", category.isPresent());
        assertEquals("Category name should match", categoryName, category.get().getName());
    }

    @Test
    public void testUpdateCategory() {
        // First create a category
        String originalName = "Education-" + System.currentTimeMillis();
        Optional<Category> category = categoryController.createCategory(originalName);
        assertTrue("Category creation should succeed", category.isPresent());
        Long categoryId = category.get().getId();

        // Now update it
        String newName = "Updated-" + System.currentTimeMillis();
        boolean updated = categoryController.updateCategory(categoryId, newName);
        assertTrue("Category update should succeed", updated);

        // Verify the update
        Optional<Category> updatedCategory = categoryController.getCategoryById(categoryId);
        assertTrue("Updated category should exist", updatedCategory.isPresent());
        assertEquals("Category name should be updated", newName, updatedCategory.get().getName());
    }

    @Test
    public void testDeleteCategory() {
        // First create a category
        String categoryName = "ToDelete-" + System.currentTimeMillis();
        Optional<Category> category = categoryController.createCategory(categoryName);
        assertTrue("Category creation should succeed", category.isPresent());
        Long categoryId = category.get().getId();

        // Now delete it
        boolean deleted = categoryController.deleteCategory(categoryId);
        assertTrue("Category deletion should succeed", deleted);

        // Verify it's gone
        Optional<Category> deletedCategory = categoryController.getCategoryById(categoryId);
        assertFalse("Deleted category should not exist", deletedCategory.isPresent());
    }
}