package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository("file")
public class TaskFileRepository implements TaskRepository {
    private final File file;
    private final Map<String, TaskModel> tasks;

    public TaskFileRepository(@Value("${repository}") String repository) throws IOException {
        this.file = Paths.get(repository).toFile();
        this.tasks = this.file.exists() ? Arrays.stream((new ObjectMapper()).readValue(this.file, TaskModel[].class)).collect(Collectors.toMap(TaskModel::getId, (task) -> task)) : new HashMap();
    }

    @Override
    public Collection<TaskModel> findAll() {
        return this.tasks.values();
    }

    @Override
    public Optional<TaskModel> findById(String id) {
        return !this.tasks.containsKey(id) ? Optional.empty() : Optional.of(this.tasks.get(id));
    }

    @Override
    public void save(TaskModel task) throws IOException {
        if (!this.tasks.containsKey(task.getId())) {
            this.tasks.put(task.getId(), task);
        } else {
            this.tasks.get(task.getId()).update(task);
        }

        this.writeToFile();
    }

    @Override
    public boolean deleteById(String id) throws IOException {
        if (!this.tasks.containsKey(id)) {
            return false;
        } else {
            this.tasks.remove(id);
            this.writeToFile();
            return true;
        }
    }

    public void writeToFile() throws IOException {
        (new ObjectMapper()).writeValue(this.file, this.findAll());
    }
}