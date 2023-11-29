package hexlet.code.service;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(EntityNotFoundException::new);
        return taskMapper.map(task);
    }

    public TaskDTO create(TaskDTO data) {
        var task = taskMapper.map(data);

        if (data.getContent() == null) {
            task.setDescription("No description provided");
        }

        if (!data.getTaskLabelIds().isEmpty()) {
            for (Label label : task.getLabels()) {
                task.addLabel(label);
                labelRepository.save(label);
            }
        }

        var taskStatus = taskStatusRepository.findBySlug(data.getStatus()).get();
        task.setTaskStatus(taskStatus);
        taskStatus.getTasks().add(task);
        taskStatusRepository.save(task.getTaskStatus());

        var taskSaved = taskRepository.findByName(task.getName()).get();
        var assignee = data.getAssigneeId() == null ? null : userRepository.findById(data.getAssigneeId()).get();
        taskSaved.setAssignee(assignee);
        if (assignee != null) {
            assignee.getTasks().add(taskSaved);
            userRepository.save(assignee);
        }

        return taskMapper.map(taskSaved);
    }

    public TaskDTO update(TaskDTO data, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        taskMapper.update(data, task);

        if (!data.getTaskLabelIds().isEmpty()) {
            for (Label label : task.getLabels()) {
                task.addLabel(label);
                labelRepository.save(label);
            }
        }

        var taskStatus = data.getStatus() == null ? null : taskStatusRepository.findBySlug(data.getStatus()).get();
        if (taskStatus != null) {
            task.setTaskStatus(taskStatus);
            taskStatus.getTasks().add(task);
            taskStatusRepository.save(task.getTaskStatus());
        }

        var assignee = data.getAssigneeId() == null ? null : userRepository.findById(data.getAssigneeId()).get();
        task.setAssignee(assignee);
        if (assignee != null) {
            assignee.getTasks().add(task);
            userRepository.save(assignee);
        } else {
            taskRepository.save(task);
        }

        return taskMapper.map(task);
    }

    public void delete(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (!task.getLabels().isEmpty()) {
            for (Label item : task.getLabels()) {
                task.removeLabel(item);
                labelRepository.save(item);
            }
        }

        if (task.getAssignee() != null) {
            var assignee = task.getAssignee();
            assignee.getTasks().remove(task);
            userRepository.save(assignee);
        }

        var taskStatus = task.getTaskStatus();
        taskStatus.getTasks().remove(task);
        taskStatusRepository.save(taskStatus);

        taskRepository.deleteById(id);
    }
}
