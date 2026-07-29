package com.auca.library.service;

import com.auca.library.dao.LocationDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.User;
import com.auca.library.domain.enums.LocationType;
import com.auca.library.exception.DuplicateLocationCodeException;
import com.auca.library.exception.LocationNotFoundException;
import com.auca.library.exception.PersonNotFoundException;

import java.util.UUID;

/**
 * Requirement 1, 2, 3: build and query the Province -> Village hierarchy.
 */
public class LocationService {

    private final LocationDao locationDao;
    private final UserDao userDao;

    public LocationService() {
        this(new LocationDao(), new UserDao());
    }

    public LocationService(LocationDao locationDao, UserDao userDao) {
        this.locationDao = locationDao;
        this.userDao = userDao;
    }

    /**
     * Creates a Location node. A PROVINCE has no parent; every other type
     * (DISTRICT/SECTOR/CELL/VILLAGE) must reference an existing parent.
     */
    public Location createLocation(Location location, UUID parentId) {
        if (locationDao.findByCode(location.getLocationCode()).isPresent()) {
            throw new DuplicateLocationCodeException(
                    "A location with code '" + location.getLocationCode() + "' already exists");
        }

        if (location.getLocationType() != LocationType.PROVINCE) {
            if (parentId == null) {
                throw new LocationNotFoundException(
                        location.getLocationType() + " must have a parent location");
            }
            locationDao.findById(parentId)
                    .orElseThrow(() -> new LocationNotFoundException(
                            "Parent location not found for id: " + parentId));
        }

        location.setParentId(parentId);
        return locationDao.save(location);
    }

    /**
     * Walks up the parent chain from the given village until it reaches the
     * PROVINCE node, and returns its name.
     */
    public String getProvinceNameByVillageId(UUID villageId) {
        Location current = locationDao.findById(villageId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found for id: " + villageId));

        while (current.getLocationType() != LocationType.PROVINCE) {
            if (current.getParentId() == null) {
                throw new LocationNotFoundException(
                        "Broken location hierarchy: no PROVINCE ancestor for id " + villageId);
            }
            current = locationDao.findById(current.getParentId())
                    .orElseThrow(() -> new LocationNotFoundException(
                            "Parent location not found for id: " + current.getParentId()));
        }
        return current.getLocationName();
    }

    /**
     * Looks up the person's (User's) village, then resolves the province
     * name from that village.
     */
    public String getProvinceNameByPersonId(UUID personId) {
        User user = userDao.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found for id: " + personId));

        if (user.getVillageId() == null) {
            throw new LocationNotFoundException("Person " + personId + " has no village assigned");
        }
        return getProvinceNameByVillageId(user.getVillageId());
    }
}
