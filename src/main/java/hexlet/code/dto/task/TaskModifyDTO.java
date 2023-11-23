package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TaskModifyDTO {
    private Integer index;
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private Set<Long> taskLabelIds = new HashSet<>();
}
