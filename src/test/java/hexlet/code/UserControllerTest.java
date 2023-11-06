package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public final class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper om;

    private User testUser;

    @BeforeEach
    public void saveTestUser() {
        var testUserCreateDTO = TestUtil.createTestUserCreateDTO();
        testUser = userMapper.map(testUserCreateDTO);
        var passwordDigest = passwordEncoder.encode(testUserCreateDTO.getPassword());
        testUser.setPasswordDigest(passwordDigest);
        userRepository.save(testUser);
    }

    @Test
    public void testShow() throws Exception {
        var userId = testUser.getId();
        var request = get("/api/users/" + userId);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testShowFail() throws Exception {
        var userId = testUser.getId() + 100500;
        var request = get("/api/users/" + userId);
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/users");
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        var userDTO = TestUtil.createTestUserCreateDTO();
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByFirstName(userDTO.getFirstName()).get();
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    public void testCreateFail() throws Exception {
        var userDTO = TestUtil.createTestUserCreateDTO();
        userDTO.setPassword("no");
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userDTO));

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testUpdate() throws Exception {
        var userId = testUser.getId();

        var data = new HashMap<>();
        data.put("firstName", "Gregory");

        var request = put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var newUser = userRepository.findById(userId).get();
        assertThat(newUser.getFirstName()).isEqualTo("Gregory");
    }

    @Test
    public void testUpdateFail() throws Exception {
        var userId = testUser.getId();

        var data = new HashMap<>();
        data.put("email", "абракадабра");

        var request = put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testDelete() throws Exception {
        var userId = testUser.getId();
        var request = delete("/api/users/" + userId);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
