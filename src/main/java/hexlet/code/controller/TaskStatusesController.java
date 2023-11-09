package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
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
@RequestMapping("/api/task_statuses")
public final class TaskStatusesController {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskStatusDTO> show(@PathVariable Long id) {
        var taskStatus = taskStatusService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskStatus);
    }

    @GetMapping(path = "")
    public ResponseEntity<List<TaskStatusDTO>> index() {
        var taskStatuses = taskStatusService.getAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskStatuses);
    }

    @PostMapping(path = "")
    public ResponseEntity<TaskStatusDTO> create(@Valid @RequestBody TaskStatusCreateDTO data) {
        var taskStatus = taskStatusService.create(data);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskStatus);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<TaskStatusDTO> update(@Valid @RequestBody TaskStatusUpdateDTO data, @PathVariable Long id) {
        try {
            var taskStatus = taskStatusService.update(data, id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(taskStatus);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
