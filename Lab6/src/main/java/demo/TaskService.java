package demo;

import demo.TaskModel.Severity;
import demo.TaskModel.Status;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(@Qualifier("database") TaskRepository repository) {
        this.repository = repository;
    }


    public List<TaskModel> getTasks(String title, String description, String assignedTo, Status status, Severity severity) {
        return this.repository.findAll().stream().filter((task) -> isMatch(task, title, description, assignedTo, status, severity)).collect(Collectors.toList());
    }

    public boolean isMatch(TaskModel task, String title, String description, String assignedTo, Status status, Severity severity) {
        return (title == null || task.getTitle().toLowerCase(Locale.ROOT).startsWith(title.toLowerCase(Locale.ROOT)))
                && (description == null || task.getDescription().toLowerCase(Locale.ROOT).startsWith(description.toLowerCase(Locale.ROOT)))
                && (assignedTo == null || task.getAssignedTo().toLowerCase(Locale.ROOT).startsWith(assignedTo.toLowerCase(Locale.ROOT)))
                && (status == null || task.getStatus().equals(status))
                && (severity == null || task.getSeverity().equals(severity));
    }

    public Optional<TaskModel> getTask(String id) {
        return this.repository.findById(id);
    }

    public TaskModel addTask(TaskModel task) throws IOException {
        repository.save(task);
        return task;
    }

    public boolean updateTask(String id, TaskModel task) throws IOException {
        Optional<TaskModel> existingTask = this.repository.findById(id);
        if (existingTask.isEmpty()) {
            return false;
        } else {
            task.setId(id);
            this.repository.save(task);
            return true;
        }
    }

    public boolean patchTask(String id, TaskModel task) throws IOException {
        Optional<TaskModel> existingTask = this.repository.findById(id);
        if (existingTask.isEmpty()) {
            return false;
        } else {
            existingTask.get().patch(task);
            this.repository.save(existingTask.get());
            return true;
        }
    }

    public boolean deleteTask(String id) throws IOException {
        return this.repository.deleteById(id);
    }
}
