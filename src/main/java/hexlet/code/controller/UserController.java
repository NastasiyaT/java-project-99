package hexlet.code.controller;

import hexlet.code.dto.user.UserModifyDTO;
import hexlet.code.dto.user.UserDTO;
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
public final class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDTO> show(@PathVariable Long id) {
        var currentUser = userUtils.getCurrentUser();

        if (!Objects.equals(currentUser.getId(), id)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        } else {
            var user = userService.findById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(user);
        }
    }

    @GetMapping(path = "")
    public ResponseEntity<List<UserDTO>> index() {
        var users = userService.getAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @PostMapping(path = "")
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserModifyDTO data) {
        try {
            var user = userService.create(data);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<UserDTO> update(@Valid @RequestBody UserModifyDTO data, @PathVariable Long id) {
        var currentUser = userUtils.getCurrentUser();

        if (!Objects.equals(currentUser.getId(), id)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        } else {
            try {
                var user = userService.update(data, id);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(user);
            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build();
            }
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var currentUser = userUtils.getCurrentUser();

        if (Objects.equals(currentUser.getId(), id)) {
            userService.delete(id);
        }
    }
}
