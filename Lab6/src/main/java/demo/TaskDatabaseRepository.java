package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Optional;

@Repository("database")
public class TaskDatabaseRepository implements TaskRepository {
    public final RowMapper<TaskModel> mapper = (rs, rowNum) -> new TaskModel(
            rs.getString("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("assignedTo"),
            TaskModel.Status.valueOf(rs.getString("status")),
            TaskModel.Severity.valueOf(rs.getString("severity"))
    );
    private NamedParameterJdbcTemplate template;

    @Autowired
    public void setDataSource(final DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Collection<TaskModel> findAll() {
        return template.query("SELECT * FROM task", mapper);
    }

    @Override
    public Optional<TaskModel> findById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        return template.query("SELECT * FROM task WHERE id = :id", parameters, mapper).stream().findFirst();
    }

    @Override
    public void save(TaskModel task) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", task.getId())
                .addValue("title", task.getTitle())
                .addValue("description", task.getDescription())
                .addValue("assignedTo", task.getAssignedTo())
                .addValue("status", task.getStatus().name())
                .addValue("severity", task.getSeverity().name());

        int affectedRows = template.update("UPDATE task SET title = :title, description = :description, assignedTo = :assignedTo, status = :status, severity = :severity WHERE id = :id", parameters);

        if (affectedRows == 0) {
            template.update("INSERT INTO task (id, title, description, assignedTo, status, severity) VALUES (:id, :title, :description, :assignedTo, :status, :severity)", parameters);
        }
    }

    @Override
    public boolean deleteById(String id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);

        return template.update("DELETE FROM task WHERE id = :id", parameters) > 0;
    }
}
