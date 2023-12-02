package hexlet.code.controller;

import hexlet.code.dto.UserDTO;
import hexlet.code.service.UserService;
import hexlet.code.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public final class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @GetMapping(path = "")
    public ResponseEntity<List<UserDTO>> getAll() {
        var users = userService.getAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserDTO data) {
        return userService.create(data);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@Valid @RequestBody UserDTO data, @PathVariable Long id) {
        var currentUser = userUtils.getCurrentUser();
        var user = new UserDTO();

        if (Objects.equals(currentUser.getId(), id)) {
            user = userService.update(data, id);
        }

        return user;
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
        var currentUser = userUtils.getCurrentUser();

        if (Objects.equals(currentUser.getId(), id)) {
            userService.delete(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }
}
