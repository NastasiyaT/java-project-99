package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskDTO {
    private Long id;

    @JsonRawValue
    private Integer index;

    private LocalDate createdAt;
    private Long assigneeId;
    private String title;

    @JsonRawValue
    private String content;

    private String status;
}
