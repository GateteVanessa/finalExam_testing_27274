package auca.ac.rw.domain;

import auca.ac.rw.domain.enums.LocationType;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Represents one node in the administrative hierarchy:
 * Province -> District -> Sector -> Cell -> Village.
 * Self-referencing via parentId (kept as a raw UUID rather than a
 * lazy-loaded association to keep the DAO layer simple for this project).
 */
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue
    @Column(name = "location_id", updatable = false, nullable = false)
    private UUID locationId;

    @Column(name = "location_code", nullable = false, unique = true)
    private String locationCode;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType;

    /**
     * Null for PROVINCE (top of hierarchy). Points to the parent Location's id
     * for DISTRICT, SECTOR, CELL, VILLAGE.
     */
    @Column(name = "parent_id")
    private UUID parentId;

    public Location() {
    }

    public Location(String locationCode, String locationName, LocationType locationType, UUID parentId) {
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.locationType = locationType;
        this.parentId = parentId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", locationCode='" + locationCode + '\'' +
                ", locationName='" + locationName + '\'' +
                ", locationType=" + locationType +
                ", parentId=" + parentId +
                '}';
    }
}
