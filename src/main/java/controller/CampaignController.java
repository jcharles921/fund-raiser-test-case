package controller;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Campaign;
import model.Category;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Campaign-related operations
 * Handles campaign creation, updating, viewing, and deletion
 */
public class CampaignController {

    /**
     * Create a new campaign
     *
     * @param title Campaign title
     * @param description Campaign description
     * @param goalAmount Campaign goal amount
     * @param createdBy User who created the campaign
     * @param category Campaign category
     * @return Optional containing the created campaign if successful, empty if creation failed
     */
    public Optional<Campaign> createCampaign(String title, String description, BigDecimal goalAmount, User createdBy, Category category) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Campaign newCampaign = new Campaign(title, description, goalAmount, createdBy, category);
            session.persist(newCampaign);

            transaction.commit();
            return Optional.of(newCampaign);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error creating campaign: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get a campaign by ID
     *
     * @param campaignId ID of the campaign to retrieve
     * @return Optional containing the campaign if found, empty otherwise
     */
    public Optional<Campaign> getCampaignById(Long campaignId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Campaign campaign = session.get(Campaign.class, campaignId);
            return Optional.ofNullable(campaign);
        } catch (Exception e) {
            System.err.println("Error retrieving campaign: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get all campaigns
     *
     * @return List of all campaigns
     */
    public List<Campaign> getAllCampaigns() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Campaign", Campaign.class).list();
        } catch (Exception e) {
            System.err.println("Error retrieving campaigns: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Update a campaign
     *
     * @param campaignId ID of the campaign to update
     * @param title New title (or null to keep existing)
     * @param description New description (or null to keep existing)
     * @param goalAmount New goal amount (or null to keep existing)
     * @param category New category (or null to keep existing)
     * @return true if update successful, false otherwise
     */
    public boolean updateCampaign(Long campaignId, String title, String description, BigDecimal goalAmount, Category category) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Campaign campaign = session.get(Campaign.class, campaignId);
            if (campaign == null) {
                System.err.println("Campaign not found with ID: " + campaignId);
                return false;
            }

            if (title != null && !title.trim().isEmpty()) {
                campaign.setTitle(title);
            }

            if (description != null && !description.trim().isEmpty()) {
                campaign.setDescription(description);
            }

            if (goalAmount != null && goalAmount.compareTo(BigDecimal.ZERO) > 0) {
                campaign.setGoalAmount(goalAmount);
            }

            if (category != null) {
                campaign.setCategory(category);
            }

            session.merge(campaign);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating campaign: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a campaign
     *
     * @param campaignId ID of the campaign to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCampaign(Long campaignId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Campaign campaign = session.get(Campaign.class, campaignId);
            if (campaign == null) {
                System.err.println("Campaign not found with ID: " + campaignId);
                return false;
            }

            session.remove(campaign);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Cannot delete campaign - may have associated records: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting campaign: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
