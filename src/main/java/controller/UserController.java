package controller;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


public class UserController {
    
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    

    public Optional<User> registerUser(String name, String email, String password) {
        // Input validation
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Error: Name cannot be empty");
            return Optional.empty();
        }
        
        if (email == null || !Pattern.matches(EMAIL_REGEX, email)) {
            System.err.println("Error: Invalid email format");
            return Optional.empty();
        }
        
        if (password == null || password.length() < 6) {
            System.err.println("Error: Password must be at least 6 characters long");
            return Optional.empty();
        }
        
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Check if email already exists
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            
            try {
                User existingUser = query.getSingleResult();
                if (existingUser != null) {
                    System.err.println("Error: Email already registered");
                    return Optional.empty();
                }
            } catch (NoResultException e) {
                // Email not found, which is what we want
            }
            
            transaction = session.beginTransaction();
            
            // Create new user
            User newUser = new User(name, email, password);
            session.persist(newUser);
            
            transaction.commit();
            return Optional.of(newUser);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error during user registration: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public Optional<User> login(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                "FROM User WHERE email = :email AND password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            
            try {
                User user = query.getSingleResult();
                return Optional.of(user);
            } catch (NoResultException e) {
                System.err.println("Invalid email or password");
                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public Optional<User> getUserById(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            System.err.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public boolean updateUserProfile(Long userId, String name, String email, String password) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            User user = session.get(User.class, userId);
            if (user == null) {
                System.err.println("User not found with ID: " + userId);
                return false;
            }
            
            // Update only provided fields
            if (name != null && !name.trim().isEmpty()) {
                user.setName(name);
            }
            
            if (email != null && !email.trim().isEmpty()) {
                if (!Pattern.matches(EMAIL_REGEX, email)) {
                    System.err.println("Invalid email format");
                    return false;
                }
                
                // Check if email is already in use by another user
                Query<User> query = session.createQuery(
                    "FROM User WHERE email = :email AND id != :userId", User.class);
                query.setParameter("email", email);
                query.setParameter("userId", userId);
                
                if (!query.getResultList().isEmpty()) {
                    System.err.println("Email already in use by another user");
                    return false;
                }
                
                user.setEmail(email);
            }
            
            if (password != null && password.length() >= 6) {
                user.setPassword(password);
            } else if (password != null) {
                System.err.println("Password must be at least 6 characters long");
                return false;
            }
            
            session.merge(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error updating user profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

    public boolean deleteUser(Long userId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            User user = session.get(User.class, userId);
            if (user == null) {
                System.err.println("User not found with ID: " + userId);
                return false;
            }
            
            session.remove(user);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Cannot delete user - may have associated records: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}