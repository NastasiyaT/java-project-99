package hexlet.code.controller;

import hexlet.code.dto.label.LabelModifyDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public final class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelService labelService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<LabelDTO> show(@PathVariable Long id) {
        var label = labelService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(label);
    }

    @GetMapping(path = "")
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelService.getAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(labels);
    }

    @PostMapping(path = "")
    public ResponseEntity<LabelDTO> create(@Valid @RequestBody LabelModifyDTO data) {
        try {
            var label = labelService.create(data);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(label);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<LabelDTO> update(@Valid @RequestBody LabelModifyDTO data, @PathVariable Long id) {
        try {
            var label = labelService.update(data, id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(label);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}
