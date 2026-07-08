package career.flow.owoke.user.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import career.flow.owoke.common.exception.userExceptions.EmailAlreadyUsedException;
import career.flow.owoke.common.exception.userExceptions.UserAlreadyExistsException;
import career.flow.owoke.common.exception.userExceptions.UserNotFoundException;
import career.flow.owoke.common.util.EmailUtils;
import career.flow.owoke.messaging.event.AuthUserCreatedEvent;
import career.flow.owoke.user.dto.request.UserUpdateRequest;
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

    @KafkaListener(topics = "auth", groupId = "user-service")
    @Transactional
    public void createUser(AuthUserCreatedEvent event) {
        log.info("Received auth user created event");

        if (userRepository.existsByAuthId(event.authId())) {
            log.warn("User already exists");
            throw new UserAlreadyExistsException(event.email());
        }

        User savedUser = userRepository.save(userMapper.toEntity(event));
        log.debug("User created successfully with id: {}", savedUser.getId());

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
        String normalizedEmail = EmailUtils.normalize(request.email());

        if (userRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            log.warn("Email already exists");
            throw new EmailAlreadyUsedException(request.email());
        }
        user.setEmail(normalizedEmail);

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

    public UserResponse getCurrentUser(String authId) {
        User user = userRepository.findByAuthId(authId).orElseThrow(() -> new UserNotFoundException(authId));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(String authId, UserUpdateRequest request) {
        User user = userRepository.findByAuthId(authId).orElseThrow(() -> new UserNotFoundException(authId));
        String normalizedEmail = EmailUtils.normalize(request.email());
        if (userRepository.existsByEmailAndAuthIdNot(normalizedEmail, authId)) {
            throw new EmailAlreadyUsedException(request.email());
        }

        user.setEmail(normalizedEmail);
        user.setName(request.name());
        return userMapper.toResponse(userRepository.save(user));
    }
}
