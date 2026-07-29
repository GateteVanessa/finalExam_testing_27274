package com.auca.library.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue
    @Column(name = "room_id", updatable = false, nullable = false)
    private UUID roomId;

    @Column(name = "room_code", nullable = false, unique = true)
    private String roomCode;

    public Room() {
    }

    public Room(String roomCode) {
        this.roomCode = roomCode;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}
