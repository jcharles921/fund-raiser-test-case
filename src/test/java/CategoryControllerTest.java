import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import controller.CategoryController;
import model.Category;
import java.util.Optional;

public class CategoryControllerTest {

    private CategoryController categoryController;

    @Before
    public void setUp() {
        categoryController = new CategoryController();
    }

    @Test
    public void testCreateCategory() {
        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());
        assertEquals("Education", category.get().getName());
    }

    @Test
    public void testUpdateCategory() {
        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());
        Long categoryId = category.get().getId();

        boolean updated = categoryController.updateCategory(categoryId, "Health");
        assertTrue(updated);

        Optional<Category> updatedCategory = categoryController.getCategoryById(categoryId);
        assertTrue(updatedCategory.isPresent());
        assertEquals("Health", updatedCategory.get().getName());
    }

    @Test
    public void testDeleteCategory() {
        Optional<Category> category = categoryController.createCategory("Education");
        assertTrue(category.isPresent());
        Long categoryId = category.get().getId();

        boolean deleted = categoryController.deleteCategory(categoryId);
        assertTrue(deleted);

        Optional<Category> deletedCategory = categoryController.getCategoryById(categoryId);
        assertFalse(deletedCategory.isPresent());
    }
}
