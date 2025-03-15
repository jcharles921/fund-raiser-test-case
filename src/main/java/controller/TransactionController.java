package controller;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.Transaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Transaction-related operations
 * Handles transaction creation, viewing, and processing
 */
public class TransactionController {

    /**
     * Create a new transaction
     *
     * @param amount Transaction amount
     * @param paymentMethod Payment method used
     * @param transactionDate Date and time of the transaction
     * @return Optional containing the created transaction if successful, empty if creation failed
     */
    public Optional<Transaction> createTransaction(BigDecimal amount, String paymentMethod, LocalDateTime transactionDate) {
        org.hibernate.Transaction dbTransaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            dbTransaction = session.beginTransaction();

            Transaction newTransaction = new Transaction(amount, paymentMethod, transactionDate);
            session.persist(newTransaction);

            dbTransaction.commit();
            return Optional.of(newTransaction);
        } catch (Exception e) {
            if (dbTransaction != null) {
                dbTransaction.rollback();
            }
            System.err.println("Error creating transaction: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get a transaction by ID
     *
     * @param transactionId ID of the transaction to retrieve
     * @return Optional containing the transaction if found, empty otherwise
     */
    public Optional<Transaction> getTransactionById(Long transactionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.get(Transaction.class, transactionId);
            return Optional.ofNullable(transaction);
        } catch (Exception e) {
            System.err.println("Error retrieving transaction: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get all transactions
     *
     * @return List of all transactions
     */
    public List<Transaction> getAllTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Transaction", Transaction.class).list();
        } catch (Exception e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Delete a transaction
     *
     * @param transactionId ID of the transaction to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteTransaction(Long transactionId) {
        org.hibernate.Transaction dbTransaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            dbTransaction = session.beginTransaction();

            Transaction transaction = session.get(Transaction.class, transactionId);
            if (transaction == null) {
                System.err.println("Transaction not found with ID: " + transactionId);
                return false;
            }

            session.remove(transaction);
            dbTransaction.commit();
            return true;
        } catch (PersistenceException e) {
            if (dbTransaction != null) {
                dbTransaction.rollback();
            }
            System.err.println("Cannot delete transaction - may have associated records: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            if (dbTransaction != null) {
                dbTransaction.rollback();
            }
            System.err.println("Error deleting transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
