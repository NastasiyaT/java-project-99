package hexlet.code.util;

import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.UserDTO;
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
    private Model<UserDTO> userDTOModel;

    private Model<TaskStatus> taskStatusModel;

    private Model<Task> taskModel;
    private Model<TaskDTO> taskDTOModel;

    private Model<Label> labelModel;

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

        userDTOModel = Instancio.of(UserDTO.class)
                .ignore(Select.field(UserDTO::getId))
                .ignore(Select.field(UserDTO::getCreatedAt))
                .supply(Select.field(UserDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserDTO::getPassword), () -> faker.internet().password(3, 8))
                .supply(Select.field(UserDTO::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .ignore(Select.field(TaskStatus::getTasks))
                .supply(Select.field(TaskStatus::getName), () -> faker.lorem().word())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
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

        taskDTOModel = Instancio.of(TaskDTO.class)
                .ignore(Select.field(TaskDTO::getAssigneeId))
                .ignore(Select.field(TaskDTO::getStatus))
                .ignore(Select.field(TaskDTO::getTaskLabelIds))
                .supply(Select.field(TaskDTO::getIndex), () -> faker.number().positive())
                .supply(Select.field(TaskDTO::getTitle), () -> faker.lorem().word())
                .supply(Select.field(TaskDTO::getContent), () -> faker.lorem().sentence())
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .ignore(Select.field(Label::getTasks))
                .supply(Select.field(Label::getName), () -> faker.text().text(3, 1000))
                .toModel();
    }
}
