package com.auca.library.domain;

import com.auca.library.domain.enums.Gender;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Super class of {@link User}. Per project spec this class is NOT mapped to
 * its own database table -- it only contributes its columns to whichever
 * entity extends it (hence @MappedSuperclass rather than @Entity).
 */
@MappedSuperclass
public abstract class Person {

    @Id
    @GeneratedValue
    @Column(name = "person_id", updatable = false, nullable = false)
    private UUID personId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    protected Person() {
    }

    protected Person(String firstName, String lastName, Gender gender, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
