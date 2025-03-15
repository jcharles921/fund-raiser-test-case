package util;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import model.*;

public class HibernateUtil {

    private static SessionFactory sessionFactory = null;

    @SuppressWarnings("deprecation")
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                Properties settings = new Properties();

                settings.setProperty(Environment.URL, "jdbc:postgresql://localhost:5432/midtesting");
                settings.setProperty(Environment.USER, "boubou");
                settings.setProperty(Environment.PASS, "boubou#123");
                settings.setProperty(Environment.HBM2DDL_AUTO, "update");
                settings.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.setProperty(Environment.SHOW_SQL, "true");

                configuration.setProperties(settings);
                
                // Add all entity classes here
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Campaign.class);
                configuration.addAnnotatedClass(Category.class);
                configuration.addAnnotatedClass(Donation.class);
                configuration.addAnnotatedClass(Transaction.class);

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}