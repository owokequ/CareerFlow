package career.flow.owoke.user.mapper;

import org.mapstruct.Mapper;

import career.flow.owoke.messaging.event.AuthUserCreatedEvent;
import career.flow.owoke.user.dto.response.UserResponse;
import career.flow.owoke.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User toEntity(AuthUserCreatedEvent event);
}
