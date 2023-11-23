package hexlet.code.mapper;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelModifyDTO;
import hexlet.code.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {
    public abstract LabelDTO map(Label model);
    public abstract Label map(LabelModifyDTO data);
    public abstract void update(LabelModifyDTO data, @MappingTarget Label model);
}
