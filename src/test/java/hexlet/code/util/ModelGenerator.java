package hexlet.code.util;

import hexlet.code.dto.label.LabelModifyDTO;
import hexlet.code.dto.task.TaskModifyDTO;
import hexlet.code.dto.task_status.TaskStatusModifyDTO;
import hexlet.code.dto.user.UserModifyDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;
    private Model<UserModifyDTO> userCreateDTOModel;

    private Model<TaskStatus> taskStatusModel;
    private Model<TaskStatusModifyDTO> taskStatusModifyDTOModel;

    private Model<Task> taskModel;
    private Model<TaskModifyDTO> taskModifyDTOModel;

    private Model<Label> labelModel;
    private Model<LabelModifyDTO> labelModifyDTOModel;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        Faker faker = new Faker();

        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getTasks))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .set(Select.field(User::getPasswordDigest), passwordEncoder.encode(faker.internet().password(3, 8)))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        userCreateDTOModel = Instancio.of(UserModifyDTO.class)
                .supply(Select.field(UserModifyDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserModifyDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserModifyDTO::getPassword), () -> faker.internet().password(3, 8))
                .supply(Select.field(UserModifyDTO::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .ignore(Select.field(TaskStatus::getTasks))
                .supply(Select.field(TaskStatus::getName), () -> faker.lorem().word())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
                .toModel();

        taskStatusModifyDTOModel = Instancio.of(TaskStatusModifyDTO.class)
                .supply(Select.field(TaskStatusModifyDTO::getName), () -> faker.lorem().word())
                .supply(Select.field(TaskStatusModifyDTO::getSlug), () -> faker.internet().slug())
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getCreatedAt))
                .ignore(Select.field(Task::getTaskStatus))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getLabels))
                .supply(Select.field(Task::getName), () -> faker.lorem().word())
                .supply(Select.field(Task::getDescription), () -> faker.lorem().sentence())
                .supply(Select.field(Task::getIndex), () -> faker.number().positive())
                .toModel();

        taskModifyDTOModel = Instancio.of(TaskModifyDTO.class)
                .ignore(Select.field(TaskModifyDTO::getAssigneeId))
                .ignore(Select.field(TaskModifyDTO::getStatus))
                .ignore(Select.field(TaskModifyDTO::getLabelNames))
                .supply(Select.field(TaskModifyDTO::getIndex), () -> faker.number().positive())
                .supply(Select.field(TaskModifyDTO::getTitle), () -> faker.lorem().word())
                .supply(Select.field(TaskModifyDTO::getContent), () -> faker.lorem().sentence())
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .ignore(Select.field(Label::getTasks))
                .supply(Select.field(Label::getName), () -> faker.text().text(3, 1000))
                .toModel();

        labelModifyDTOModel = Instancio.of(LabelModifyDTO.class)
                .supply(Select.field(LabelModifyDTO::getName), () -> faker.text().text(3, 1000))
                .toModel();
    }
}
