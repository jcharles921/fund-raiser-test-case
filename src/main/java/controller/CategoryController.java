package controller;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Category;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Category-related operations
 * Handles category creation, updating, viewing, and deletion
 */
public class CategoryController {

    /**
     * Create a new category
     *
     * @param name Category name
     * @return Optional containing the created category if successful, empty if creation failed
     */
    public Optional<Category> createCategory(String name) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Category newCategory = new Category(name);
            session.persist(newCategory);

            transaction.commit();
            return Optional.of(newCategory);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error creating category: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get a category by ID
     *
     * @param categoryId ID of the category to retrieve
     * @return Optional containing the category if found, empty otherwise
     */
    public Optional<Category> getCategoryById(Long categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Category category = session.get(Category.class, categoryId);
            return Optional.ofNullable(category);
        } catch (Exception e) {
            System.err.println("Error retrieving category: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get all categories
     *
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Category", Category.class).list();
        } catch (Exception e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Update a category
     *
     * @param categoryId ID of the category to update
     * @param name New name (or null to keep existing)
     * @return true if update successful, false otherwise
     */
    public boolean updateCategory(Long categoryId, String name) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Category category = session.get(Category.class, categoryId);
            if (category == null) {
                System.err.println("Category not found with ID: " + categoryId);
                return false;
            }

            if (name != null && !name.trim().isEmpty()) {
                category.setName(name);
            }

            session.merge(category);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a category
     *
     * @param categoryId ID of the category to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCategory(Long categoryId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Category category = session.get(Category.class, categoryId);
            if (category == null) {
                System.err.println("Category not found with ID: " + categoryId);
                return false;
            }

            session.remove(category);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Cannot delete category - may have associated records: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
