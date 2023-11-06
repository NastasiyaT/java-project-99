package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    private String firstName;

    private String lastName;

    @Email
    private String email;

    @Size(min = 3)
    private String password;
}
