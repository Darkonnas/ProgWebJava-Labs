package demo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TaskModel implements BaseModel {
    private String id;
    @NotBlank(message = "Please provide a task title.")
    private String title;
    private String description;
    @NotBlank(message = "Please assign the task to someone.")
    private String assignedTo;
    @NotNull(message = "Please provide the task's status.")
    private Status status;
    @NotNull(message = "Please provide the task's severity.")
    private Severity severity;

    public TaskModel() {
        id = UUID.randomUUID().toString();
    }

    public TaskModel(String id, String title, String description, String assignedTo, Status status, Severity severity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.status = status;
        this.severity = severity;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void update(TaskModel task) {
        if (task != null) {
            this.title = task.title;
            this.description = task.description;
            this.assignedTo = task.assignedTo;
            this.status = task.status;
            this.severity = task.severity;
        }
    }

    public void patch(TaskModel task) {
        if (task != null) {
            if (task.title != null) {
                this.title = task.title;
            }

            if (task.description != null) {
                this.description = task.description;
            }

            if (task.assignedTo != null) {
                this.assignedTo = task.assignedTo;
            }

            if (task.status != null) {
                this.status = task.status;
            }

            if (task.severity != null) {
                this.severity = task.severity;
            }

        }
    }

    public enum Severity {
        LOW,
        NORMAL,
        HIGH
    }

    public enum Status {
        OPEN,
        IN_PROGRESS,
        CLOSED
    }
}