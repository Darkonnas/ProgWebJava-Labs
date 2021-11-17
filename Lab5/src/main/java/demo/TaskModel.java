package demo;

import java.util.UUID;

public class TaskModel {
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private String assignedTo;
    private TaskModel.Status status;
    private TaskModel.Severity severity;

    public TaskModel() {
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

    public TaskModel.Status getStatus() {
        return this.status;
    }

    public void setStatus(TaskModel.Status status) {
        this.status = status;
    }

    public TaskModel.Severity getSeverity() {
        return this.severity;
    }

    public void setSeverity(TaskModel.Severity severity) {
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

    public static enum Severity {
        LOW,
        NORMAL,
        HIGH;

        private Severity() {
        }
    }

    public static enum Status {
        OPEN,
        IN_PROGRESS,
        CLOSED;

        private Status() {
        }
    }
}