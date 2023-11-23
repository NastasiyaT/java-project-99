package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import jakarta.servlet.ServletException;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    private User testUser;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();    }

    @Test
    public void testShow() throws Exception {
        var userId = testUser.getId();
        var request = get("/api/users/" + userId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testShowFail() throws Exception {
        var userId = testUser.getId() + 100500;
        var request = get("/api/users/" + userId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/users").with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        var userCreateDTO = Instancio.of(modelGenerator.getUserCreateDTOModel()).create();
        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByFirstName(userCreateDTO.getFirstName()).get();
        assertThat(userCreateDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    public void testCreateFail() throws Exception {
        var userDTO = Instancio.of(modelGenerator.getUserCreateDTOModel()).create();
        userDTO.setPassword("no");
        var request = post("/api/users").with(token)
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

        var request = put("/api/users/" + userId).with(token)
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

        var request = put("/api/users/" + userId).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testDelete() throws Exception {
        var userId = testUser.getId();
        var request = delete("/api/users/" + userId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteFail() throws Exception {
        var userId = testUser.getId();

        var taskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var task = Instancio.of(modelGenerator.getTaskModel()).create();
        task.setTaskStatus(taskStatus);
        taskStatus.addTask(task);
        taskStatusRepository.save(taskStatus);

        testUser.addTask(task);
        userRepository.save(testUser);

        var request = delete("/api/users/" + userId).with(token);
        var exception = assertThrows(ServletException.class,
                () -> mockMvc.perform(request));
        assertThat(exception.getMessage()).contains("User has active tasks");
    }
}
