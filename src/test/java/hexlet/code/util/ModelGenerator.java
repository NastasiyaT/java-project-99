package hexlet.code.util;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.UserCreateDTO;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    private Model<UserCreateDTO> userCreateDTOModel;
    private Model<TaskStatusCreateDTO> taskStatusCreateDTOModel;

    @PostConstruct
    private void init() {
        Faker faker = new Faker();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password(3, 8))
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        taskStatusCreateDTOModel = Instancio.of(TaskStatusCreateDTO.class)
                .supply(Select.field(TaskStatusCreateDTO::getName), () -> faker.lorem().word())
                .supply(Select.field(TaskStatusCreateDTO::getSlug), () -> faker.internet().slug())
                .toModel();
    }
}
