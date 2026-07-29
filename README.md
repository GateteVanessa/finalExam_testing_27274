# AUCA Library Management System

Java 21 · Maven · Hibernate ORM 6.5 · PostgreSQL · JUnit 4

Manages hard-copy book borrowing: readers register for a membership tier
(Gold / Silver / Striver), borrow up to their tier's limit, and are charged
a late fee (tier's daily rate x days late) if they return a book past its
due date.

## Package layout

```
com.auca.library
 ├── domain        entities (Person is a @MappedSuperclass, not its own table)
 ├── domain.enums   Gender, LocationType, Role, BookStatus, MembershipStatus
 ├── dao            Hibernate Session-based DAOs (GenericDao/AbstractDao + one per entity)
 ├── service        business rules (one class per requirement group)
 ├── exception      custom runtime exceptions used by the services
 └── util           HibernateUtil (SessionFactory singleton)
```

## Requirements -> implementation map

| # | Requirement | Class / method |
|---|---|---|
| 1 | Create Location hierarchy | `LocationService.createLocation(Location, UUID)` |
| 2 | Village id -> province name | `LocationService.getProvinceNameByVillageId(UUID)` |
| 3 | Person id -> province name | `LocationService.getProvinceNameByPersonId(UUID)` |
| 4 | Authenticate user | `AuthService.authenticate(String, String)` |
| 5 | Register membership | `MembershipService.registerMembership(UUID, UUID)` |
| 6 | Borrow a book | `BorrowService.borrowBook(UUID, UUID)` |
| 7 | Validate borrow limit | `BorrowService.validateBorrowLimit(UUID)` |
| 8 | Assign book to shelf | `ShelfService.assignBookToShelf(UUID, UUID)` |
| 9 | Assign shelf to room | `ShelfService.assignShelfToRoom(UUID, UUID)` |
| 10 | Count books in a room | `RoomService.countBooksInRoom(UUID)` |
| 11 | Room with fewest books | `RoomService.findRoomWithFewestBooks()` |
| 12 | Late fee calculation | `BorrowService.calculateLateFee(UUID)` |

All 21 model JUnit 4 test cases from the assignment brief are implemented
under `src/test/java/com/auca/library/service/`, one test class per service.

## Local setup

1. Install PostgreSQL and create two databases:
   ```bash
   createdb auca_library_db
   createdb auca_library_test_db
   ```
2. Edit `src/main/resources/hibernate.cfg.xml` (and
   `src/test/resources/hibernate-test.cfg.xml`) if your Postgres
   username/password differ from `postgres` / `postgres`.
3. Build and run the tests:
   ```bash
   mvn clean test
   ```
   Hibernate will auto-create the schema (`hbm2ddl.auto=update` for the main
   config, `create-drop` for the test config), so no manual DDL is needed.

## Notes

- `Person` is intentionally a `@MappedSuperclass`, not an `@Entity` — per the
  spec it has no table of its own; only `User` (which extends it) is persisted.
- Passwords are stored as BCrypt hashes (`AuthService.hashPassword`), never
  plaintext.
- `BorrowService.LOAN_PERIOD_DAYS` (14) is the fixed loan period used to
  compute a due date from the pickup date.
