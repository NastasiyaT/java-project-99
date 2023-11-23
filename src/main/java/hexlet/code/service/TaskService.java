package hexlet.code.service;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskModifyDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskSpecification taskSpecification;

    @Autowired
    private LabelRepository labelRepository;

    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = taskSpecification.build(params);
        var tasks = taskRepository.findAll(spec);
        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with ID %s not found", id)));
        return taskMapper.map(task);
    }

    public TaskDTO create(TaskModifyDTO data) {
        var task = taskMapper.map(data);

        if (data.getContent() == null) {
            task.setDescription("No description provided");
        }

        modify(task, data);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskModifyDTO data, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with ID %s not found", id)));
        taskMapper.update(data, task);
        modify(task, data);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with ID %s not found", id)));

        if (!task.getLabels().isEmpty()) {
            for (Label item : task.getLabels()) {
                task.removeLabel(item);
                labelRepository.save(item);
            }
        }

        var taskStatus = task.getTaskStatus();
        taskStatus.removeTask(task);
        taskStatusRepository.save(taskStatus);

        if (task.getAssignee() != null) {
            var assignee = task.getAssignee();
            assignee.removeTask(task);
            userRepository.save(assignee);
        }

        taskRepository.deleteById(id);
    }

    private void modify(Task task, TaskModifyDTO data) {

        if (data.getAssigneeId() != null) {
            var assignee = userRepository.findById(data.getAssigneeId()).get();
            assignee.addTask(task);
            userRepository.save(assignee);
        }

        if (!data.getTaskLabelIds().isEmpty()) {
            for (Label label : task.getLabels()) {
                task.addLabel(label);
                labelRepository.save(label);
            }
        }

        if (data.getStatus() != null) {
            var taskStatus = taskStatusRepository.findBySlug(data.getStatus()).get();
            taskStatus.addTask(task);
            taskStatusRepository.save(taskStatus);
        }
    }
}
