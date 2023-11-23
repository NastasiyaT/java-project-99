package hexlet.code.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModifyDTO {
    private String firstName;
    private String lastName;
    private String email;

    @Size(min = 3)
    private String password;
}
