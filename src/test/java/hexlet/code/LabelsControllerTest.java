package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public final class LabelsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    private User testUser;

    private Label testLabel;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @AfterEach
    public void clear() {
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testShow() throws Exception {
        var labelId = testLabel.getId();
        var request = get("/api/labels/" + labelId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/labels").with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testIndexUnauthorized() throws Exception {
        var request = get("/api/labels");
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var labelDTO = Instancio.of(modelGenerator.getLabelModel()).create();

        var request = post("/api/labels").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdate() throws Exception {
        var labelId = testLabel.getId();

        var data = new HashMap<>();
        data.put("name", "new name");

        var request = put("/api/labels/" + labelId).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        var labelId = testLabel.getId();
        var request = delete("/api/labels/" + labelId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
