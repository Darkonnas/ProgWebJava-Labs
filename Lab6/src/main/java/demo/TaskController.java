package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.utils.MapUtil;
import demo.utils.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/tasks")
public class TaskController {
    private final TaskService service;

    private final Validator validator;

    public TaskController(TaskService service, Validator validator) {
        this.service = service;
        this.validator = validator;
    }

    @Operation(summary = "Search tasks", operationId = "getTasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found tasks",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = java.lang.Object[].class))}
            ),
            @ApiResponse(responseCode = "204", description = "No tasks found")
    })
    @GetMapping
    public ResponseEntity<List<java.lang.Object>> getTasks(@RequestParam(required = false) String title,
                                                           @RequestParam(required = false) String description,
                                                           @RequestParam(required = false) String assignedTo,
                                                           @RequestParam(required = false) TaskModel.Status status,
                                                           @RequestParam(required = false) TaskModel.Severity severity,
                                                           @RequestHeader(required = false, name = "X-Fields") String fields,
                                                           @RequestHeader(required = false, name = "X-Sort") String sort) {
        List<TaskModel> tasks = service.getTasks(title, description, assignedTo, status, severity);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (sort != null && !sort.isBlank()) {
                tasks = tasks.stream().sorted((first, second) -> BaseModel.sorter(sort).compare(first, second)).collect(Collectors.toList());
            }
            List<java.lang.Object> items;
            if (fields != null && !fields.isBlank()) {
                items = tasks.stream().map(task -> task.sparseFields(fields.split(","))).collect(Collectors.toList());
            } else {
                items = new ArrayList<>(tasks);
            }
            return ResponseEntity.ok(items);
        }
    }


    @Operation(summary = "Get a task", operationId = "getTask")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found task",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = java.lang.Object.class))}
            ),
            @ApiResponse(responseCode = "404", description = "No task found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<java.lang.Object> getTaskById(@PathVariable String id, @RequestHeader(required = false, name = "X-Fields") String fields) {
        Optional<TaskModel> task = service.getTask(id);
        if (task.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            if (fields != null && !fields.isBlank()) {
                return ResponseEntity.ok(task.get().sparseFields(fields.split(",")));
            } else {
                return ResponseEntity.ok(task.get());
            }
        }
    }

    @Operation(summary = "Create a task", operationId = "addTask")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task was created",
                    headers = {@Header(name = "location", schema = @Schema(type = "String"))}
            ),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "204", description = "Bulk tasks created")
    })
    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody String payload, @RequestHeader(required = false, name = "X-Action") String action) throws IOException, ValidationException {
        if ("bulk".equals(action)) {
            TaskModel[] tasks = new ObjectMapper().readValue(payload, TaskModel[].class);
            validate(tasks);
            for (TaskModel taskModel : tasks) {
                service.addTask(taskModel);
            }
            return ResponseEntity.noContent().build();
        } else {
            TaskModel task = new ObjectMapper().readValue(payload, TaskModel.class);
            validate(new Object[]{task});
            TaskModel taskModel = service.addTask(task);
            URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(taskModel.getId()).toUri();
            return ResponseEntity.created(uri).build();
        }
    }

    @Operation(summary = "Update a task", operationId = "updateTask")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task was updated"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable String id, @Valid @RequestBody TaskModel task) throws IOException {
        if (service.updateTask(id, task)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Patch a task", operationId = "patchTask")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task was patched"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchTask(@PathVariable String id, @Valid @RequestBody TaskModel task) throws IOException {
        if (service.patchTask(id, task)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a task", operationId = "deleteTask")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) throws IOException {
        if (service.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Check a task", operationId = "checkTask")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task was found"),
            @ApiResponse(responseCode = "404", description = "Task was not found")
    })
    @RequestMapping(method = RequestMethod.HEAD, value = "/{id}")
    public ResponseEntity<Void> checkTask(@PathVariable String id) {
        Optional<TaskModel> taskModel = service.getTask(id);
        return taskModel.isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Export tasks", operationId = "exportTasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exported tasks",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = java.lang.Object[].class)),
                            @Content(mediaType = MediaType.APPLICATION_XML_VALUE)
                    }
            ),
            @ApiResponse(responseCode = "204", description = "No tasks found",
                    content = {
                            @Content(mediaType = MediaType.ALL_VALUE)
                    }
            )
    })
    @GetMapping("/export")
    public ResponseEntity<String> exportTasks(@RequestParam(required = false) String title,
                                              @RequestParam(required = false) String description,
                                              @RequestParam(required = false) String assignedTo,
                                              @RequestParam(required = false) TaskModel.Status status,
                                              @RequestParam(required = false) TaskModel.Severity severity,
                                              @RequestHeader(required = false, name = "X-Fields") String fields,
                                              @RequestHeader(required = false, name = "X-Sort") String sort,
                                              @RequestHeader(name = "Content-Type") String contentType) {
        List<TaskModel> tasks = service.getTasks(title, description, assignedTo, status, severity);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            if (sort != null && !sort.isBlank()) {
                tasks = tasks.stream().sorted((first, second) -> BaseModel.sorter(sort).compare(first, second)).collect(Collectors.toList());
            }

            String formattedString;

            if (fields != null && !fields.isBlank()) {
                var sparseTasks = tasks.stream().map(task -> task.sparseFields(fields.split(","))).collect(Collectors.toList());

                switch (contentType) {
                    case "text/csv":
                        formattedString = MapUtil.toCSVString(sparseTasks);
                        break;
                    case "application/xml":
                        formattedString = MapUtil.toXMLString(sparseTasks);
                        break;
                    default:
                        return ResponseEntity.badRequest().body("Cannot currently handle content type " + contentType);
                }
            } else {
                switch (contentType) {
                    case "text/csv":
                        formattedString = ObjectUtil.toCSVString(List.copyOf(tasks));
                        break;
                    case "application/xml":
                        formattedString = ObjectUtil.toXMLString(List.copyOf(tasks));
                        break;
                    default:
                        return ResponseEntity.badRequest().body("Cannot currently handle content type " + contentType);
                }
            }

            return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, contentType).body(formattedString);
        }
    }

    private void validate(Object[] objects) throws ValidationException {
        String message = Arrays.stream(objects)
                .map(o -> validator.validate(o).stream()
                        .map(ConstraintViolation::getMessage)
                        .filter(error -> !error.isBlank())
                        .collect(Collectors.joining("|"))
                ).collect(Collectors.joining("|"));
        if (!message.isBlank()) {
            throw new ValidationException(message);
        }
    }
}