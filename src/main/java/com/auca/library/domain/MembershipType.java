package com.auca.library.domain;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Defines the three membership tiers: Gold, Silver, Striver.
 * price = daily late fee rate in Rwf, maxBooks = concurrent borrow limit.
 */
@Entity
@Table(name = "membership_types")
public class MembershipType {

    @Id
    @GeneratedValue
    @Column(name = "membership_type_id", updatable = false, nullable = false)
    private UUID membershipTypeId;

    @Column(name = "membership_name", nullable = false, unique = true)
    private String membershipName;

    @Column(name = "max_books", nullable = false)
    private int maxBooks;

    /**
     * Daily rate in Rwf, also used as the late-fee-per-day rate.
     */
    @Column(name = "price", nullable = false)
    private int price;

    public MembershipType() {
    }

    public MembershipType(String membershipName, int maxBooks, int price) {
        this.membershipName = membershipName;
        this.maxBooks = maxBooks;
        this.price = price;
    }

    public UUID getMembershipTypeId() {
        return membershipTypeId;
    }

    public void setMembershipTypeId(UUID membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
    }

    public String getMembershipName() {
        return membershipName;
    }

    public void setMembershipName(String membershipName) {
        this.membershipName = membershipName;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(int maxBooks) {
        this.maxBooks = maxBooks;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
