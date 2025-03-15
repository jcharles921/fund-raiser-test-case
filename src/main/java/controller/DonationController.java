package controller;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Campaign;
import model.Donation;
import model.Transaction;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Donation-related operations
 * Handles donation creation, viewing, and processing
 */
public class DonationController {

    /**
     * Create a new donation
     *
     * @param amount Donation amount
     * @param donor User who made the donation
     * @param campaign Campaign to which the donation is made
     * @param transaction Transaction associated with the donation
     * @return Optional containing the created donation if successful, empty if creation failed
     */
    public Optional<Donation> createDonation(BigDecimal amount, User donor, Campaign campaign, Transaction transaction) {
        org.hibernate.Transaction dbTransaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            dbTransaction = session.beginTransaction();

            Donation newDonation = new Donation(amount, donor, campaign, transaction);
            session.persist(newDonation);

            dbTransaction.commit();
            return Optional.of(newDonation);
        } catch (Exception e) {
            if (dbTransaction != null) {
                dbTransaction.rollback();
            }
            System.err.println("Error creating donation: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get a donation by ID
     *
     * @param donationId ID of the donation to retrieve
     * @return Optional containing the donation if found, empty otherwise
     */
    public Optional<Donation> getDonationById(Long donationId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Donation donation = session.get(Donation.class, donationId);
            return Optional.ofNullable(donation);
        } catch (Exception e) {
            System.err.println("Error retrieving donation: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get all donations
     *
     * @return List of all donations
     */
    public List<Donation> getAllDonations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Donation", Donation.class).list();
        } catch (Exception e) {
            System.err.println("Error retrieving donations: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Delete a donation
     *
     * @param donationId ID of the donation to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteDonation(Long donationId) {
        org.hibernate.Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Donation donation = session.get(Donation.class, donationId);
            if (donation == null) {
                System.err.println("Donation not found with ID: " + donationId);
                return false;
            }

            session.remove(donation);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Cannot delete donation - may have associated records: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting donation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
