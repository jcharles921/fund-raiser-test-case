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


public class DonationController {

	public Optional<Donation> createDonation(BigDecimal amount, User donor, Campaign campaign, Transaction transaction) {
	    Session session = null;
	    org.hibernate.Transaction dbTransaction = null;
	    try {
	        session = HibernateUtil.getSessionFactory().openSession();
	        dbTransaction = session.beginTransaction();
	        
	        
	        User managedDonor = session.merge(donor);
	        Campaign managedCampaign = session.merge(campaign);
	        Transaction managedTransaction = session.merge(transaction);
	        
	        Donation newDonation = new Donation(amount, managedDonor, managedCampaign, managedTransaction);
	        managedTransaction.setDonation(newDonation); // Set the bidirectional relationship
	        
	        session.persist(newDonation);
	        dbTransaction.commit();
	        return Optional.of(newDonation);
	    } catch (Exception e) {
	        if (dbTransaction != null && dbTransaction.isActive()) {
	            try {
	                dbTransaction.rollback();
	            } catch (Exception rollbackEx) {
	                System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
	            }
	        }
	        System.err.println("Error creating donation: " + e.getMessage());
	        e.printStackTrace();
	        return Optional.empty();
	    } finally {
	        if (session != null && session.isOpen()) {
	            session.close();
	        }
	    }
	}
	public Optional<Donation> getDonationById(Long donationId) {
	    Session session = null;
	    try {
	        session = HibernateUtil.getSessionFactory().openSession();
	        Donation donation = session.get(Donation.class, donationId);
	        return Optional.ofNullable(donation);
	    } catch (Exception e) {
	        System.err.println("Error retrieving donation: " + e.getMessage());
	        e.printStackTrace();
	        return Optional.empty();
	    } finally {
	        if (session != null && session.isOpen()) {
	            session.close();
	        }
	    }
	}

public boolean deleteDonation(Long donationId) {
    Session session = null;
    org.hibernate.Transaction transaction = null;
    try {
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();
        
        Donation donation = session.get(Donation.class, donationId);
        if (donation == null) {
            System.err.println("Donation not found with ID: " + donationId);
            transaction.rollback();
            return false;
        }
        
        session.remove(donation);
        transaction.commit();
        return true;
    } catch (PersistenceException e) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
            }
        }
        System.err.println("Cannot delete donation - may have associated records: " + e.getMessage());
        e.printStackTrace();
        return false;
    } catch (Exception e) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
            }
        }
        System.err.println("Error deleting donation: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
}
