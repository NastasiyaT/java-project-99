package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found", id)));
        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO data) {
        var passwordDigest = passwordEncoder.encode(data.getPassword());
        var user = userMapper.map(data);
        user.setPasswordDigest(passwordDigest);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO update(UserUpdateDTO data, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found", id)));
        userMapper.update(data, user);

        if (data.getPassword() != null) {
            var passwordDigest = passwordEncoder.encode((CharSequence) data.getPassword());
            user.setPasswordDigest(passwordDigest);
        }

        userRepository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
