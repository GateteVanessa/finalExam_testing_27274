package auca.ac.rw.service;

import auca.ac.rw.dao.UserDao;
import auca.ac.rw.domain.User;
import auca.ac.rw.domain.enums.Gender;
import auca.ac.rw.domain.enums.Role;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthServiceTest extends BaseServiceTest {

    private AuthService authService;
    private UserDao userDao;

    @Before
    public void setUp() {
        userDao = new UserDao();
        authService = new AuthService(userDao);

        User user = new User("Eric", "Niyonzima", Gender.MALE, "0788111111",
                "eric.niyonzima", AuthService.hashPassword("CorrectHorse1"), Role.LIBRARIAN, null);
        userDao.save(user);
    }

    @Test
    public void authenticate_correctCredentials_returnsTrue() {
        assertTrue(authService.authenticate("eric.niyonzima", "CorrectHorse1"));
    }

    @Test
    public void authenticate_wrongPassword_returnsFalse() {
        assertFalse(authService.authenticate("eric.niyonzima", "WrongPassword"));
    }

    @Test
    public void authenticate_unknownUsername_returnsFalse() {
        assertFalse(authService.authenticate("nobody.here", "whatever"));
    }

    @Test
    public void authenticate_nullOrBlankInput_returnsFalse() {
        assertFalse(authService.authenticate(null, "CorrectHorse1"));
        assertFalse(authService.authenticate("eric.niyonzima", null));
        assertFalse(authService.authenticate("", ""));
        assertFalse(authService.authenticate("   ", "   "));
    }
}
