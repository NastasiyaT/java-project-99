package hexlet.code.mapper;

import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserModifyDTO;
import hexlet.code.model.User;
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
public abstract class UserMapper {
    public abstract UserDTO map(User model);
    public abstract User map(UserModifyDTO data);
    public abstract void update(UserModifyDTO data, @MappingTarget User model);
}
