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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
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
    public ResponseEntity<Void> addTask(@RequestBody String payload, @RequestHeader(required = false, name = "X-Action") String action) {
        try {
            if ("bulk".equals(action)) {
                for (TaskModel taskModel : new ObjectMapper().readValue(payload, TaskModel[].class)) {
                    service.addTask(taskModel);
                }
                return ResponseEntity.noContent().build();
            } else {
                TaskModel taskModel = service.addTask(new ObjectMapper().readValue(payload, TaskModel.class));
                URI uri = WebMvcLinkBuilder.linkTo(getClass()).slash(taskModel.getId()).toUri();
                return ResponseEntity.created(uri).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update a task", operationId = "updateTask")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task was updated"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable String id, @RequestBody TaskModel task) {
        try {
            if (service.updateTask(id, task)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Patch a task", operationId = "patchTask")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task was patched"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchTask(@PathVariable String id, @RequestBody TaskModel task) {
        try {
            if (service.patchTask(id, task)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Delete a task", operationId = "deleteTask")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task was deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        try {
            if (service.deleteTask(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
                                              @RequestHeader(required = false, name = "Content-Type", defaultValue = "application/json") String contentType) {
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
                        formattedString = tasks.toString();
                }
            } else {
                switch (contentType) {
                    case "text/csv":
                        formattedString = ObjectUtil.toCSVString(tasks);
                        break;
                    case "application/xml":
                        formattedString = ObjectUtil.toXMLString(tasks);
                        break;
                    default:
                        formattedString = tasks.toString();
                }
            }

            return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_TYPE, contentType).body(formattedString);
        }
    }
}