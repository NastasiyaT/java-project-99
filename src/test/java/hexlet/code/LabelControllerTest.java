package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelModifyDTO;
import hexlet.code.model.Label;
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

import java.time.LocalDate;
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
public final class LabelControllerTest {

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

    private Label testLabel;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        var testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    public void testShow() throws Exception {
        var labelId = testLabel.getId();
        var request = get("/api/labels/" + labelId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testShowNonExistentId() throws Exception {
        var labelId = testLabel.getId() + 100500;
        var request = get("/api/labels/" + labelId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
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
        var labelCreateDTO = Instancio.of(modelGenerator.getLabelModifyDTOModel()).create();

        var request = post("/api/labels").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var label = labelRepository.findByName(labelCreateDTO.getName()).get();
        assertThat(label.getCreatedAt()).isBeforeOrEqualTo(LocalDate.now());
    }

    @Test
    public void testCreateFail() throws Exception {
        var labelCreateDTO = new LabelModifyDTO();
        labelCreateDTO.setName("no");
        var request = post("/api/labels").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
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

        var newLabel = labelRepository.findById(labelId).get();
        assertThat(newLabel.getName()).isEqualTo("new name");
    }

    @Test
    public void testUpdateFail() throws Exception {
        var labelId = testLabel.getId();

        var data = new HashMap<>();
        data.put("name", "");

        var request = put("/api/labels/" + labelId).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testDelete() throws Exception {
        var labelId = testLabel.getId();
        var request = delete("/api/labels/" + labelId).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
