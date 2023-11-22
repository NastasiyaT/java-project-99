package hexlet.code.service;

import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskModifyDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
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
        var task = new Task();
        merge(task, data);
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskModifyDTO data, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with ID %s not found", id)));
        merge(task, data);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private void merge(Task model, TaskModifyDTO data) {
        if (data.getIndex() != null) {
            model.setIndex(data.getIndex());
        }
        if (data.getAssigneeId() != null) {
            var assignee = userRepository.findById(data.getAssigneeId()).get();
            model.setAssignee(assignee);
        }
        if (data.getTitle() != null) {
            model.setName(data.getTitle());
        }
        if (data.getContent() != null) {
            model.setDescription(data.getContent());
        }
        if (data.getStatus() != null) {
            var taskStatus = taskStatusRepository.findBySlug(data.getStatus()).get();
            model.setTaskStatus(taskStatus);
            taskStatus.getTasks().add(model);
            taskStatusRepository.save(taskStatus);
        }
        if (!data.getTaskLabelIds().isEmpty()) {
            for (Long item : data.getTaskLabelIds()) {
                var label = labelRepository.findById(item).get();
                model.getTaskLabels().add(label);
                label.getTasks().add(model);
                labelRepository.save(label);
            }
        }
    }
}
