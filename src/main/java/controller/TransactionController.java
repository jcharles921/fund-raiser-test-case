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


public class TransactionController {

  
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

  
    public List<Transaction> getAllTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Transaction", Transaction.class).list();
        } catch (Exception e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


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
