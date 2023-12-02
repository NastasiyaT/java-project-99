package hexlet.code.service;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        var task = taskRepository.findById(id).orElseThrow();
        return taskMapper.map(task);
    }

    public TaskDTO create(TaskDTO data) {
        var task = taskMapper.map(data);

        if (data.getContent() == null) {
            task.setDescription("No description provided");
        }

        merge(task, data);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskDTO data, Long id) {
        var task = taskRepository.findById(id).orElseThrow();
        taskMapper.update(data, task);
        merge(task, data);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private void merge(Task model, TaskDTO data) {
        if (data.getStatus() != null) {
            model.setTaskStatus(taskStatusRepository.findBySlug(data.getStatus()).get());
        }

        if (!data.getTaskLabelIds().isEmpty()) {
            var labels = data.getTaskLabelIds().stream()
                    .map(id -> {
                        var item = new Label();
                        item.setId(id);
                        return item;
                    })
                    .collect(Collectors.toSet());
            model.setLabels(labels);
        }

        if (data.getTitle() != null) {
            model.setName(data.getTitle());
        }

        if (data.getContent() != null) {
            model.setDescription(data.getContent());
        }

        if (data.getIndex() != null) {
            model.setIndex(data.getIndex());
        }

        if (data.getAssigneeId() != null) {
            var user = new User();
            user.setId(data.getAssigneeId());
            model.setAssignee(user);
        }
    }
}
