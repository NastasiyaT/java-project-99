package hexlet.code.util;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.model.User;
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

    @PostConstruct
    private void init() {
        Faker faker = new Faker();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password(3, 8))
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .toModel();
    }
}
