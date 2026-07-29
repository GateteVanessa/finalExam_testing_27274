package com.auca.library.service;

import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;
import org.junit.After;
import org.junit.BeforeClass;

/**
 * Points HibernateUtil at hibernate-test.cfg.xml (auca_library_test_db,
 * schema recreated each JVM run) and wipes every table after each test so
 * tests stay independent of one another.
 *
 * Requires a local Postgres with a database named auca_library_test_db,
 * e.g.:  createdb auca_library_test_db
 */
public abstract class BaseServiceTest {

    @BeforeClass
    public static void configureTestDatabase() {
        HibernateUtil.useConfig("hibernate-test.cfg.xml");
    }

    @After
    public void cleanDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            session.createMutationQuery("delete from Borrower").executeUpdate();
            session.createMutationQuery("delete from Membership").executeUpdate();
            session.createMutationQuery("delete from MembershipType").executeUpdate();
            session.createMutationQuery("delete from Book").executeUpdate();
            session.createMutationQuery("delete from Shelf").executeUpdate();
            session.createMutationQuery("delete from Room").executeUpdate();
            session.createMutationQuery("delete from User").executeUpdate();
            session.createMutationQuery("delete from Location").executeUpdate();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
