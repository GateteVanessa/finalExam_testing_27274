package auca.ac.rw.domain;

import auca.ac.rw.domain.enums.Gender;
import auca.ac.rw.domain.enums.Role;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * A person who can log into the system: a reader (student/teacher/dean/hod/manager)
 * or the librarian. Inherits id/name/gender/phone from {@link Person}.
 */
@Entity
@Table(name = "users")
public class User extends Person {

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    /**
     * BCrypt hash of the password - never store plaintext passwords.
     */
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "village_id")
    private UUID villageId;

    public User() {
        super();
    }

    public User(String firstName, String lastName, Gender gender, String phoneNumber,
                String userName, String password, Role role, UUID villageId) {
        super(firstName, lastName, gender, phoneNumber);
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.villageId = villageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UUID getVillageId() {
        return villageId;
    }

    public void setVillageId(UUID villageId) {
        this.villageId = villageId;
    }
}
