package com.auca.library.service;

import com.auca.library.dao.LocationDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Location;
import com.auca.library.domain.User;
import com.auca.library.domain.enums.Gender;
import com.auca.library.domain.enums.LocationType;
import com.auca.library.domain.enums.Role;
import com.auca.library.exception.DuplicateLocationCodeException;
import com.auca.library.exception.LocationNotFoundException;
import com.auca.library.exception.PersonNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocationServiceTest extends BaseServiceTest {

    private LocationService locationService;
    private LocationDao locationDao;
    private UserDao userDao;

    @Before
    public void setUp() {
        locationDao = new LocationDao();
        userDao = new UserDao();
        locationService = new LocationService(locationDao, userDao);
    }

    // ---------- Requirement 1: createLocation ----------

    @Test
    public void createProvince_withNoParent_succeeds() {
        Location province = new Location("PROV-01", "Kigali City", LocationType.PROVINCE, null);

        Location saved = locationService.createLocation(province, null);

        assertNotNull(saved.getLocationId());
        assertNull(saved.getParentId());
        assertEquals(LocationType.PROVINCE, saved.getLocationType());
    }

    @Test
    public void createDistrict_withValidProvinceParent_succeeds() {
        Location province = locationService.createLocation(
                new Location("PROV-02", "Northern Province", LocationType.PROVINCE, null), null);

        Location district = new Location("DIST-02", "Musanze", LocationType.DISTRICT, null);
        Location saved = locationService.createLocation(district, province.getLocationId());

        assertNotNull(saved.getLocationId());
        assertEquals(province.getLocationId(), saved.getParentId());
    }

    @Test(expected = LocationNotFoundException.class)
    public void createDistrict_withMissingParent_throwsException() {
        Location district = new Location("DIST-03", "Huye", LocationType.DISTRICT, null);
        locationService.createLocation(district, null);
    }

    @Test(expected = DuplicateLocationCodeException.class)
    public void createLocation_duplicateLocationCode_throwsException() {
        locationService.createLocation(
                new Location("PROV-DUP", "Eastern Province", LocationType.PROVINCE, null), null);

        locationService.createLocation(
                new Location("PROV-DUP", "Eastern Province Copy", LocationType.PROVINCE, null), null);
    }

    // ---------- Requirement 2: getProvinceNameByVillageId ----------

    @Test
    public void validVillageId_returnsCorrectProvinceName() {
        Location province = locationService.createLocation(
                new Location("PROV-V", "Southern Province", LocationType.PROVINCE, null), null);
        Location district = locationService.createLocation(
                new Location("DIST-V", "Huye", LocationType.DISTRICT, null), province.getLocationId());
        Location sector = locationService.createLocation(
                new Location("SECT-V", "Tumba", LocationType.SECTOR, null), district.getLocationId());
        Location cell = locationService.createLocation(
                new Location("CELL-V", "Mukura", LocationType.CELL, null), sector.getLocationId());
        Location village = locationService.createLocation(
                new Location("VILL-V", "Nyakabungo", LocationType.VILLAGE, null), cell.getLocationId());

        String provinceName = locationService.getProvinceNameByVillageId(village.getLocationId());

        assertEquals("Southern Province", provinceName);
    }

    // ---------- Requirement 3: getProvinceNameByPersonId ----------

    @Test
    public void validPersonId_returnsCorrectProvinceName() {
        Location province = locationService.createLocation(
                new Location("PROV-P", "Western Province", LocationType.PROVINCE, null), null);
        Location district = locationService.createLocation(
                new Location("DIST-P", "Rubavu", LocationType.DISTRICT, null), province.getLocationId());
        Location sector = locationService.createLocation(
                new Location("SECT-P", "Gisenyi", LocationType.SECTOR, null), district.getLocationId());
        Location cell = locationService.createLocation(
                new Location("CELL-P", "Kivumu", LocationType.CELL, null), sector.getLocationId());
        Location village = locationService.createLocation(
                new Location("VILL-P", "Kabaya", LocationType.VILLAGE, null), cell.getLocationId());

        User user = new User("Jane", "Uwase", Gender.FEMALE, "0788000000",
                "jane.uwase", AuthService.hashPassword("Secret123"), Role.STUDENT, village.getLocationId());
        user = userDao.save(user);

        String provinceName = locationService.getProvinceNameByPersonId(user.getPersonId());

        assertEquals("Western Province", provinceName);
    }

    @Test(expected = PersonNotFoundException.class)
    public void unknownPersonId_throwsException() {
        locationService.getProvinceNameByPersonId(java.util.UUID.randomUUID());
    }
}
