
import org.hibernate.Session;
import org.junit.Test;

import junit.framework.TestCase;

import static org.junit.Assert.assertNotNull;
import util.HibernateUtil;

@SuppressWarnings("unused")
public class ConnectionTest extends TestCase {

    @Test
    public void testConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            assertNotNull(session);
            System.out.println("Connection established successfully.");
        } catch (Exception e) {
            System.err.println("Error establishing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}