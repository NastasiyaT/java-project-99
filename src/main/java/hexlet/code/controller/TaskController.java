package hexlet.code.controller;

import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskModifyDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public final class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskDTO> show(@PathVariable Long id) {
        var task = taskService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(task);
    }

    @GetMapping(path = "")
    public ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params) {
        var tasks = taskService.getAll(params);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    @PostMapping(path = "")
    public ResponseEntity<TaskDTO> create(@Valid @RequestBody TaskModifyDTO data) {
        try {
            var task = taskService.create(data);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(task);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<TaskDTO> update(@Valid @RequestBody TaskModifyDTO data, @PathVariable Long id) {
        try {
            var task = taskService.update(data, id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(task);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
