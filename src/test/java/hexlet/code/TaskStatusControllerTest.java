package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public final class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    private User testUser;

    private TaskStatus testTaskStatus;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @Test
    public void testGetAll() throws Exception {
        var request = get("/api/task_statuses").with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testGetById() throws Exception {
        var taskStatusId = testTaskStatus.getId();
        var request = get("/api/task_statuses/" + taskStatusId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testIndexUnauthorized() throws Exception {
        var request = get("/api/task_statuses");
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var taskStatusDTO = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var request = post("/api/task_statuses").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var taskStatus = taskStatusRepository.findBySlug(taskStatusDTO.getSlug()).get();
        assertThat(taskStatus.getId()).isNotNull();
        assertThat(taskStatus.getCreatedAt()).isBeforeOrEqualTo(LocalDate.now());
        assertThat(taskStatus.getName()).isEqualTo(taskStatusDTO.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        var taskStatusId = testTaskStatus.getId();

        var data = new TaskStatusDTO();
        data.setName("ForTomorrow");

        var request = put("/api/task_statuses/" + taskStatusId).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var taskStatus = taskStatusRepository.getReferenceById(taskStatusId);
        assertThat(taskStatus.getName()).isEqualTo("ForTomorrow");
    }

    @Test
    public void testDelete() throws Exception {
        var taskStatusId = testTaskStatus.getId();
        var request = delete("/api/task_statuses/" + taskStatusId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
