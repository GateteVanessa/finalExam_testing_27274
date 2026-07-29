package com.auca.library.service;

import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;

import java.util.List;
import java.util.UUID;

/**
 * Requirement 10 & 11: counting books physically located in a room, and
 * finding the room with the fewest books (e.g. to decide where to shelve
 * new stock).
 */
public class RoomService {

    private final RoomDao roomDao;
    private final ShelfDao shelfDao;

    public RoomService() {
        this(new RoomDao(), new ShelfDao());
    }

    public RoomService(RoomDao roomDao, ShelfDao shelfDao) {
        this.roomDao = roomDao;
        this.shelfDao = shelfDao;
    }

    /**
     * Requirement 10: sums the available stock (books physically present)
     * across every shelf located in the given room. Zero if the room has no
     * shelves.
     */
    public int countBooksInRoom(UUID roomId) {
        List<Shelf> shelves = shelfDao.findByRoomId(roomId);
        int total = 0;
        for (Shelf shelf : shelves) {
            total += shelf.getAvailableStock();
        }
        return total;
    }

    /**
     * Requirement 11: returns the Room with the lowest book count. Useful
     * for deciding where to place newly acquired books.
     */
    public Room findRoomWithFewestBooks() {
        List<Room> rooms = roomDao.findAll();
        if (rooms.isEmpty()) {
            throw new IllegalStateException("No rooms exist yet");
        }

        Room fewest = null;
        int fewestCount = Integer.MAX_VALUE;
        for (Room room : rooms) {
            int count = countBooksInRoom(room.getRoomId());
            if (count < fewestCount) {
                fewestCount = count;
                fewest = room;
            }
        }
        return fewest;
    }
}
