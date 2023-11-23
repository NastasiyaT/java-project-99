package hexlet.code.service;

import hexlet.code.dto.task_status.TaskStatusDTO;
import hexlet.code.dto.task_status.TaskStatusModifyDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class TaskStatusService {

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public List<TaskStatusDTO> getAll() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task status with ID %s not found", id)));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO create(TaskStatusModifyDTO data) {
        var taskStatus = taskStatusMapper.map(data);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO update(TaskStatusModifyDTO data, Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task status with ID %s not found", id)));
        taskStatusMapper.update(data, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public void delete(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task status with ID %s not found", id)));
        var tasks = taskStatus.getTasks();

        if (tasks.isEmpty()) {
            taskStatusRepository.deleteById(id);
        } else {
            throw new RuntimeException("Task status has active tasks");
        }
    }
}
