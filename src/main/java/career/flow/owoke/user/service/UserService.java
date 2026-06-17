package career.flow.owoke.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import career.flow.owoke.common.exception.userExceptions.EmailAlreadyUsedException;
import career.flow.owoke.common.exception.userExceptions.UserAlreadyExistsException;
import career.flow.owoke.common.exception.userExceptions.UserNotFoundException;
import career.flow.owoke.user.dto.request.UserCreateRequest;
import career.flow.owoke.user.dto.request.UserUpdateRequest;
import career.flow.owoke.user.dto.response.UserCreateResponse;
import career.flow.owoke.user.dto.response.UserListResponse;
import career.flow.owoke.user.dto.response.UserResponse;
import career.flow.owoke.user.entity.User;
import career.flow.owoke.user.mapper.UserMapper;
import career.flow.owoke.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request) {
        log.info("Creating new user");

        if (userRepository.existsByAuthId(request.authId())) {
            log.warn("User already exists");
            throw new UserAlreadyExistsException(request.email());
        }

        User savedUser = userRepository.save(userMapper.toEntity(request));
        log.debug("User created successfully with id: {}", savedUser.getId());

        return userMapper.toCreateResponse(savedUser);
    }

    public UserListResponse getAllUsers() {
        log.info("Fetching all users");
        var users = userRepository.findAll();
        log.info("Found {} users", users.size());

        return new UserListResponse(
                users.stream()
                        .map(userMapper::toResponse)
                        .toList());
    }

    public UserResponse getUserById(String id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found with id: {}", id);
            return new UserNotFoundException(id);
        });
        log.debug("User found");
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found for update with id: {}", id);
            return new UserNotFoundException(id);
        });

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email already exists");
            throw new EmailAlreadyUsedException(request.email());
        }
        user.setEmail(request.email());

        user.setName(request.name());

        log.debug("User updated successfully with id: {}", user.getId());

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(String id) {
        log.info("Deleting user with id: {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found for deletion with id: {}", id);
            return new UserNotFoundException(id);
        });

        userRepository.delete(user);
        log.debug("User deleted successfully with id: {}", id);
    }
}