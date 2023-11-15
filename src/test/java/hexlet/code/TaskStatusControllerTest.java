package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
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

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    private TaskStatus testTaskStatus;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        var testUser = Instancio.of(modelGenerator.getUserModel()).create();
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
    public void testShow() throws Exception {
        var taskStatusId = testTaskStatus.getId();
        var request = get("/api/task_statuses/" + taskStatusId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testShowNonExistentId() throws Exception {
        var taskStatusId = testTaskStatus.getId() + 100500;
        var request = get("/api/task_statuses/" + taskStatusId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses").with(token);
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
        var taskStatusCreateDTO = Instancio.of(modelGenerator.getTaskStatusModifyDTOModel()).create();
        var request = post("/api/task_statuses").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var taskStatus = taskStatusRepository.findByName(taskStatusCreateDTO.getName()).get();
        assertThat(taskStatusCreateDTO.getSlug()).isEqualTo(taskStatus.getSlug());
        assertThat(taskStatus.getCreatedAt()).isNotNull();
    }

    @Test
    public void testCreateFail() throws Exception {
        var taskStatusCreateDTO = Instancio.of(modelGenerator.getTaskStatusModifyDTOModel()).create();
        taskStatusCreateDTO.setName("");
        var request = post("/api/task_statuses").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testUpdate() throws Exception {
        var taskStatusId = testTaskStatus.getId();

        var data = new HashMap<>();
        data.put("name", "ForTomorrow");

        var request = put("/api/task_statuses/" + taskStatusId).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var newTaskStatus = taskStatusRepository.findById(taskStatusId).get();
        assertThat(newTaskStatus.getName()).isEqualTo("ForTomorrow");
    }

    @Test
    public void testUpdateFail() throws Exception {
        var taskStatusId = testTaskStatus.getId();

        var data = new HashMap<>();
        data.put("name", "");

        var request = put("/api/task_statuses/" + taskStatusId).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testDelete() throws Exception {
        var taskStatusId = testTaskStatus.getId();
        var request = delete("/api/task_statuses/" + taskStatusId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
