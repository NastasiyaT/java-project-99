package hexlet.code.service;

import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class TaskService {

    @Autowired
    private TaskMapper taskMapper;

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
                .map(taskMapper::mapToDto)
                .toList();
    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id).orElseThrow();
        return taskMapper.mapToDto(task);
    }

    public TaskDTO create(TaskDTO data) {
        var task = taskMapper.mapToEntity(data);

        if (data.getContent() == null) {
            task.setDescription("No description provided");
        }

        taskRepository.save(task);
        return taskMapper.mapToDto(task);
    }

    public TaskDTO update(TaskDTO data, Long id) {
        var task = taskRepository.findById(id).orElseThrow();
        taskMapper.update(data, task);
        taskRepository.save(task);
        return taskMapper.mapToDto(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
