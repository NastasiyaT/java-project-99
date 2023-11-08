package hexlet.code.controller.api;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public final class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) throws Exception {
        var currentUser = userUtils.getCurrentUser();

        if (Objects.equals(currentUser.getId(), id)) {
            throw new AccessDeniedException("Access forbidden");
        } else {
            return userService.findById(id);
        }
    }

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> index() throws Exception{
        var currentUser = userUtils.getCurrentUser();

        if (!userRepository.existsById(currentUser.getId())) {
            throw new AccessDeniedException("Access forbidden");
        } else {
            return userService.getAll();
        }
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO data) throws Exception {
        var currentUser = userUtils.getCurrentUser();

        if (!userRepository.existsById(currentUser.getId())) {
            throw new AccessDeniedException("Access forbidden");
        } else {
            return userService.create(data);
        }
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@Valid @RequestBody UserUpdateDTO data, @PathVariable Long id) throws Exception {
        var currentUser = userUtils.getCurrentUser();

        if (!Objects.equals(currentUser.getId(), id)) {
            throw new AccessDeniedException("Access forbidden");
        } else {
            return userService.update(data, id);
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) throws Exception {
        var currentUser = userUtils.getCurrentUser();

        if (!Objects.equals(currentUser.getId(), id)) {
            throw new AccessDeniedException("Access forbidden");
        } else {
            userService.delete(id);
        }
    }
}
