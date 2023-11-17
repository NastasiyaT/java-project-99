package hexlet.code.service;

import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskModifyDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    private LabelRepository labelRepository;

    public List<TaskDTO> getAll(Map<String, String> params) {
        var tasks = taskRepository.findAll();
        filter(tasks, params);
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
        if (data.getLabelNames() != null) {
            for (String item : data.getLabelNames()) {
                var label = labelRepository.findByName(item).get();
                model.getLabels().add(label);
                label.getTasks().add(model);
                labelRepository.save(label);
            }
        }
    }

    private void filter(List<Task> items, Map<String, String> parameters) {
        if (parameters.containsKey("titleCont")) {
            items = items.stream()
                    .filter(item -> {
                        var titleCont = parameters.get("titleCont");
                        return item.getName().contains(titleCont);
                    })
                    .toList();
        }
        if (parameters.containsKey("assigneeId")) {
            items = items.stream()
                    .filter(item -> {
                        var assigneeId = Long.valueOf(parameters.get("assigneeId"));
                        return item.getAssignee().getId().equals(assigneeId);
                    })
                    .toList();
        }
        if (parameters.containsKey("status")) {
            items = items.stream()
                    .filter(item -> {
                        var status = parameters.get("status");
                        return item.getTaskStatus().getSlug().equals(status);
                    })
                    .toList();
        }
        if (parameters.containsKey("labelId")) {
            items = items.stream()
                    .filter(item -> {
                        var labelId = Long.valueOf(parameters.get("labelId"));
                        var label = labelRepository.findById(labelId).get();
                        return item.getLabels().contains(label);
                    })
                    .toList();
        }
    }
}
