package career.flow.owoke.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import career.flow.owoke.user.dto.request.UserCreateRequest;
import career.flow.owoke.user.dto.response.UserCreateResponse;
import career.flow.owoke.user.dto.response.UserResponse;
import career.flow.owoke.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "authenticationId", target = "authId")
    User toEntity(UserCreateRequest request);

    UserCreateResponse toCreateResponse(User user);

    UserResponse toResponse(User user);
}
