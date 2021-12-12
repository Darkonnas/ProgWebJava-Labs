package demo;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "track")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "speaker_id", referencedColumnName = "id")
    private Person speaker;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "attendee",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private Set<Person> attendees;

    public Track() {
    }

    public Track(String title, String description, Person speaker, Room room) {
        this.title = title;
        this.description = description;
        this.speaker = speaker;
        this.room = room;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Person getSpeaker() {
        return speaker;
    }

    public Room getRoom() {
        return room;
    }

    public Set<Person> getAttendees() {
        return attendees;
    }

    public void update(Track track) {
        this.title = track.title;
        this.description = track.description;
        this.speaker = track.speaker;
        this.room = track.room;
    }
}
