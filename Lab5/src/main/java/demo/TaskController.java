package demo;

import demo.TaskModel.Severity;
import demo.TaskModel.Status;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"api/tasks"})
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TaskModel>> getTasks(@RequestParam(required = false) String title, @RequestParam(required = false) String description, @RequestParam(required = false) String assignedTo, @RequestParam(required = false) Status status, @RequestParam(required = false) Severity severity) {
        List<TaskModel> tasks = this.service.getTasks(title, description, assignedTo, status, severity);
        return tasks.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tasks);
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<TaskModel> getTaskById(@PathVariable String id) {
        Optional<TaskModel> task = this.service.getTask(id);
        return task.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok((TaskModel)task.get());
    }

    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody TaskModel task) {
        try {
            this.service.addTask(task);
            URI uri = ((WebMvcLinkBuilder)WebMvcLinkBuilder.linkTo(this.getClass()).slash(task.getId())).toUri();
            return ResponseEntity.created(uri).build();
        } catch (IOException var3) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<Void> updateTask(@PathVariable String id, @RequestBody TaskModel task) {
        try {
            return this.service.updateTask(id, task) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IOException var4) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping({"/{id}"})
    public ResponseEntity<Void> patchTask(@PathVariable String id, @RequestBody TaskModel task) {
        try {
            return this.service.patchTask(id, task) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IOException var4) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        try {
            return this.service.deleteTask(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IOException var3) {
            return ResponseEntity.internalServerError().build();
        }
    }
}