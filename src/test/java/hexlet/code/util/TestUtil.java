package hexlet.code.util;

import hexlet.code.dto.UserCreateDTO;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;

public class TestUtil {

    public static UserCreateDTO createTestUserCreateDTO() {
        Faker faker = new Faker();
        return Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password(3, 8))
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .create();
    }
}
