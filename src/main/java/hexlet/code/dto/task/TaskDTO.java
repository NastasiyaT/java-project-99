package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskDTO {
    private Long id;

    @JsonProperty
    private Integer index;

    private LocalDate createdAt;
    private Long assigneeId;
    private String title;

    @JsonProperty
    private String content;

    private String status;
}
