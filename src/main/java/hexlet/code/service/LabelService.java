package hexlet.code.service;

import hexlet.code.dto.label.LabelModifyDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class LabelService {

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private LabelRepository labelRepository;

    public List<LabelDTO> getAll() {
        var labels = labelRepository.findAll();
        return labels.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with ID %s not found", id)));
        return labelMapper.map(label);
    }

    public LabelDTO create(LabelModifyDTO data) {
        var label = new Label();
        merge(label, data);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(LabelModifyDTO data, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with ID %s not found", id)));
        merge(label, data);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with ID %s not found", id)));
        if (label.getTasks().isEmpty()) {
            labelRepository.deleteById(id);
        }
    }

    private void merge(Label model, LabelModifyDTO data) {
        if (data.getName() != null) {
            model.setName(data.getName());
        }
    }
}
