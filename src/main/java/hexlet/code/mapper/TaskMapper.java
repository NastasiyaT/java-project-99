package hexlet.code.mapper;

import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "labelsToIds")
    public abstract TaskDTO mapToDto(Task model);

    @Named("labelsToIds")
    public final Set<Long> toDTO(Set<Label> labels) {
        return labels.isEmpty() ? new HashSet<>() : labels.stream()
                                                            .map(Label::getId)
                                                            .collect(Collectors.toSet());
    }

    @Mappings({
            @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToTaskStatus"),
            @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "idToUser"),
            @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "idsToLabels"),
            @Mapping(target = "name", source = "title"),
            @Mapping(target = "description", source = "content"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    public abstract Task mapToEntity(TaskDTO data);

    @InheritConfiguration(name = "mapToEntity")
    public abstract Task update(TaskDTO model, @MappingTarget Task data);

    @Named("slugToTaskStatus")
    public final TaskStatus toEntity(String status) {
        return taskStatusRepository.findBySlug(status)
                .orElseThrow();
    }

    @Named("idToUser")
    public final User toEntity(Long assigneeId) {
        var user = new User();
        user.setId(assigneeId);
        return assigneeId == null ? null : user;
    }

    @Named("idsToLabels")
    public final Set<Label> toEntity(Set<Long> labelIds) {
        return labelIds.isEmpty() ? new HashSet<>() : labelIds.stream()
                                                                .map(labelId -> labelRepository.findById(labelId).get())
                                                                .collect(Collectors.toSet());
    }
}
