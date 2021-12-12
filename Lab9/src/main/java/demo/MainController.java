package demo;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MainController {
    private final PersonRepository personRepository;
    private final TrackRepository trackRepository;
    private final RoomRepository roomRepository;

    public MainController(PersonRepository personRepository, TrackRepository trackRepository, RoomRepository roomRepository) {
        this.personRepository = personRepository;
        this.trackRepository = trackRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping(value = "/persons", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Person>> getPersons() {
        Collection<Person> persons = (Collection<Person>) personRepository.findAll();

        if (persons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(persons);
    }

    @GetMapping(path = "/persons/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getPerson(@PathVariable("id") long id) {
        Optional<Person> person = personRepository.findById(id);

        if (person.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(person.get());
    }

    @PostMapping(path = "/persons", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> addPerson(String firstName, String lastName) {
        Person person = new Person(firstName, lastName);
        personRepository.save(person);
        URI uri = WebMvcLinkBuilder.linkTo(MainController.class).slash("persons").slash(person.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/persons/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePerson(@PathVariable("id") long id, @RequestBody Person person) {
        Optional<Person> existingPerson = personRepository.findById(id);

        if (existingPerson.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        existingPerson.get().update(person);
        personRepository.save(existingPerson.get());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/persons/{id}")
    public ResponseEntity<Void> removePerson(@PathVariable("id") long id) {
        Optional<Person> existingPerson = personRepository.findById(id);

        if (existingPerson.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        personRepository.delete(existingPerson.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/persons/{personId}/tracks")
    public ResponseEntity<Set<Track>> getPersonTracks(@PathVariable("personId") long personId) {
        Optional<Person> person = personRepository.findById(personId);

        if (person.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<Track> tracks = person.get().getTracks();

        if (tracks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tracks);
    }

    @PostMapping(path = "/persons/{personId}/tracks/{trackId}")
    public ResponseEntity<List<String>> addPersonTrack(@PathVariable("personId") long personId, @PathVariable("trackId") long trackId) {
        Optional<Person> person = personRepository.findById(personId);

        if (person.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Track> track = trackRepository.findById(trackId);

        if (track.isEmpty() || track.get().getSpeaker().getId() == personId) {
            return ResponseEntity.badRequest().build();
        }

        person.get().addTrack(track.get());
        personRepository.save(person.get());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/persons/{personId}/tracks/{trackId}")
    public ResponseEntity<List<String>> removePersonTrack(@PathVariable("personId") long personId, @PathVariable("trackId") long trackId) {
        Optional<Person> person = personRepository.findById(personId);

        if (person.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Track> track = trackRepository.findById(trackId);

        if (track.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        person.get().removeTrack(track.get());
        personRepository.save(person.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/tracks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Track>> getTracks() {
        Collection<Track> tracks = (Collection<Track>) trackRepository.findAll();

        if (tracks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tracks);
    }

    @GetMapping(path = "/tracks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Track> getTrack(@PathVariable("id") long id) {
        Optional<Track> track = trackRepository.findById(id);

        if (track.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(track.get());
    }

    @PostMapping(path = "/tracks", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> addTrack(String title, String description, long speakerId, long roomId) {
        Optional<Person> speaker = personRepository.findById(speakerId);

        if (speaker.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Room> room = roomRepository.findById(roomId);

        if (room.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Track track = new Track(title, description, speaker.get(), room.get());
        trackRepository.save(track);
        URI uri = WebMvcLinkBuilder.linkTo(MainController.class).slash("tracks").slash(track.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/tracks/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeTrack(@PathVariable("id") long id, @RequestBody Track track) {
        Optional<Track> existingTrack = trackRepository.findById(id);

        if (existingTrack.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        existingTrack.get().update(track);
        trackRepository.save(existingTrack.get());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/tracks/{id}")
    public ResponseEntity<Void> removeTrack(@PathVariable("id") long id) {
        Optional<Track> existingTrack = trackRepository.findById(id);

        if (existingTrack.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        trackRepository.delete(existingTrack.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/tracks/{trackId}/attendees")
    public ResponseEntity<Set<Person>> getTrackAttendees(@PathVariable("trackId") long trackId) {
        Optional<Track> track = trackRepository.findById(trackId);

        if (track.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<Person> attendees = track.get().getAttendees();

        if (attendees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(attendees);
    }

    @GetMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Room>> getRooms() {
        Collection<Room> rooms = (Collection<Room>) roomRepository.findAll();

        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rooms);
    }

    @GetMapping(path = "/rooms/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Room> getRoom(@PathVariable("id") long id) {
        Optional<Room> room = roomRepository.findById(id);

        if (room.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(room.get());
    }

    @PostMapping(path = "/rooms", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> addRoom(long number, long floor, long capacity) {
        Room room = new Room(number, floor, capacity);
        roomRepository.save(room);
        URI uri = WebMvcLinkBuilder.linkTo(MainController.class).slash("rooms").slash(room.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/rooms/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeRoom(@PathVariable("id") long id, @RequestBody Room room) {
        Optional<Room> existingRoom = roomRepository.findById(id);

        if (existingRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        existingRoom.get().update(room);
        roomRepository.save(existingRoom.get());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/rooms/{id}")
    public ResponseEntity<Void> removeRoom(@PathVariable("id") long id) {
        Optional<Room> existingRoom = roomRepository.findById(id);

        if (existingRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        roomRepository.delete(existingRoom.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/rooms/{roomId}/tracks")
    public ResponseEntity<Set<Track>> getRoomTracks(@PathVariable("roomId") long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);

        if (room.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<Track> tracks = room.get().getTracks();

        if (tracks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tracks);
    }
}
