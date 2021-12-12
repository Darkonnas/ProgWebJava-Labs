package demo;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number", nullable = false)
    private long number;

    @Column(name = "floor", nullable = false)
    private long floor;

    @Column(name = "capacity", nullable = false)
    private long capacity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room")
    @JsonBackReference
    private Set<Track> tracks;

    public Room() {
    }

    public Room(long number, long floor, long capacity) {
        this.number = number;
        this.floor = floor;
        this.capacity = capacity;
    }

    public long getId() {
        return id;
    }

    public long getNumber() {
        return number;
    }

    public long getFloor() {
        return floor;
    }

    public long getCapacity() {
        return capacity;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void update(Room room) {
        this.number = room.number;
        this.floor = room.floor;
        this.capacity = room.capacity;
    }
}
