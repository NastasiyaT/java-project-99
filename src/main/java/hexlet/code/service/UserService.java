package hexlet.code.service;

import hexlet.code.dto.user.UserModifyDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class UserService implements UserDetailsManager {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUtils userUtils;

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
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found", id)));
        return userMapper.map(user);
    }

    public UserDTO create(UserModifyDTO data) {
        var user = new User();
        merge(user, data);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO update(UserModifyDTO data, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found", id)));
        merge(user, data);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void delete(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with ID %s not found", id)));
        var tasks = user.getTasks();

        if (tasks.isEmpty()) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void createUser(UserDetails userData) {
        var user = new User();
        user.setEmail(userData.getUsername());
        var hashedPassword = passwordEncoder.encode(userData.getPassword());
        user.setPasswordDigest(hashedPassword);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public boolean userExists(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
    }

    private void merge(User model, UserModifyDTO data) {
        if (data.getFirstName() != null) {
            model.setFirstName(data.getFirstName());
        }
        if (data.getLastName() != null) {
            model.setLastName(data.getLastName());
        }
        if (data.getEmail() != null) {
            model.setEmail(data.getEmail());
        }
        if (data.getPassword() != null) {
            var passwordDigest = passwordEncoder.encode(data.getPassword());
            model.setPasswordDigest(passwordDigest);
        }
    }
}
