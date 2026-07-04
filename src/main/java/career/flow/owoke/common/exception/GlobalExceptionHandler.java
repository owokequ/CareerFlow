package career.flow.owoke.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import career.flow.owoke.common.exception.userExceptions.EmailAlreadyUsedException;
import career.flow.owoke.common.exception.userExceptions.InvalidUserRequestException;
import career.flow.owoke.common.exception.userExceptions.InvalidVerificationTokenException;
import career.flow.owoke.common.exception.userExceptions.UserAlreadyExistsException;
import career.flow.owoke.common.exception.userExceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex,
                        HttpServletRequest request) {
                log.error("UserNotFoundException: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                ex.getMessage(),
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
                        HttpServletRequest request) {
                log.error("UserAlreadyExistsException: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                ex.getMessage(),
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        @ExceptionHandler(EmailAlreadyUsedException.class)
        public ResponseEntity<ErrorResponse> handleEmailAlreadyUsedException(EmailAlreadyUsedException ex,
                        HttpServletRequest request) {
                log.error("EmailAlreadyUsedException: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                ex.getMessage(),
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        @ExceptionHandler(InvalidUserRequestException.class)
        public ResponseEntity<ErrorResponse> handleInvalidUserRequestException(InvalidUserRequestException ex,
                        HttpServletRequest request) {
                log.error("InvalidUserRequestException: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                ex.getMessage(),
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(InvalidVerificationTokenException.class)
        public ResponseEntity<ErrorResponse> InvalidVerificationTokenException(InvalidVerificationTokenException ex,
                        HttpServletRequest request) {
                log.error("Invalid verification token: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                ex.getMessage(),
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(
                        BadCredentialsException ex,
                        HttpServletRequest request) {

                log.error("Bad credentials: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                "Invalid email or password",
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                var details = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .toList();

                ErrorResponse response = new ErrorResponse(
                                "Validation failed",
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                details);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUsernameNotFound(
                        UsernameNotFoundException ex,
                        HttpServletRequest request) {

                log.error("User not found: {}", ex.getMessage());

                ErrorResponse response = new ErrorResponse(
                                "Invalid credentials",
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
                log.error("Unexpected error: {}", ex.getMessage(), ex);

                ErrorResponse response = new ErrorResponse(
                                "An unexpected error occurred",
                                request.getRequestURI(),
                                request.getMethod(),
                                LocalDateTime.now(),
                                null);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
}
