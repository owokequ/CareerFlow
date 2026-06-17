package career.flow.owoke.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import career.flow.owoke.user.dto.request.UserCreateRequest;
import career.flow.owoke.user.dto.response.UserCreateResponse;
import career.flow.owoke.user.dto.response.UserResponse;
import career.flow.owoke.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "auth_id", source = "authId")
    User toEntity(UserCreateRequest request);

    @Mapping(target = "authId", source = "auth_id")
    UserCreateResponse toCreateResponse(User user);

    @Mapping(target = "authId", source = "auth_id")
    UserResponse toResponse(User user);
}
