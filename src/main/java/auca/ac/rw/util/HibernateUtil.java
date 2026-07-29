package auca.ac.rw.util;

import auca.ac.rw.domain.Book;
import auca.ac.rw.domain.Borrower;
import auca.ac.rw.domain.Location;
import auca.ac.rw.domain.Membership;
import auca.ac.rw.domain.MembershipType;
import auca.ac.rw.domain.Person;
import auca.ac.rw.domain.Room;
import auca.ac.rw.domain.Shelf;
import auca.ac.rw.domain.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Builds and caches a single SessionFactory for the whole application.
 * Connection settings come from a .properties file on the classpath
 * (application.properties for the app, application-test.properties for
 * JUnit) rather than an XML hibernate.cfg.xml, so entities are registered here
 * with addAnnotatedClass instead of via XML &lt;mapping&gt; entries.
 *
 * Tests can point this at a different properties file by calling
 * {@link #useConfig(String)} before the first session is requested.
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static String propertiesFile = "application.properties";

    private HibernateUtil() {
    }

    /**
     * Must be called (if at all) before the first getSessionFactory() call,
     * e.g. from a JUnit @BeforeClass, to point tests at application-test.properties.
     */
    public static synchronized void useConfig(String propertiesFileName) {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
        propertiesFile = propertiesFileName;
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Properties properties = loadProperties(propertiesFile);

            Configuration configuration = new Configuration();
            configuration.addProperties(properties);

            configuration.addAnnotatedClass(Location.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Book.class);
            configuration.addAnnotatedClass(Borrower.class);
            configuration.addAnnotatedClass(Membership.class);
            configuration.addAnnotatedClass(MembershipType.class);
            configuration.addAnnotatedClass(Shelf.class);
            configuration.addAnnotatedClass(Room.class);
            // Person is a @MappedSuperclass (no table of its own) so it does
            // not need to be added here explicitly - User already inherits it.

            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }

    private static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IllegalStateException("Could not find " + fileName + " on the classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + fileName, e);
        }
        return properties;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
