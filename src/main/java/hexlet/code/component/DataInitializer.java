package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public final class DataInitializer implements ApplicationRunner {

    private final Map<String, String> admin = Map.of(
            "email", "hexlet@example.com",
            "password", "qwerty");

    private final Map<String, String> taskStatuses = Map.of(
            "Draft", "draft",
            "ToReview", "to_review",
            "ToBeFixed", "to_be_fixed",
            "ToPublish", "to_publish",
            "Published", "published");

    private final List<String> labels = List.of("feature", "bug");

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new User();
        userData.setEmail(admin.get("email"));
        userData.setPasswordDigest(admin.get("password"));
        userService.createUser(userData);

        var taskStatusNames = taskStatuses.keySet();
        for (String name : taskStatusNames) {
            var slug = taskStatuses.get(name);
            var data = new TaskStatus();
            data.setName(name);
            data.setSlug(slug);
            taskStatusRepository.save(data);
        }

        for (String labelName : labels) {
            var label = new Label();
            label.setName(labelName);
            labelRepository.save(label);
        }
    }
}
