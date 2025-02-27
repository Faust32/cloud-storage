package ru.faust.cloudstorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.faust.cloudstorage.dto.UserDTO;
import ru.faust.cloudstorage.dto.UserRegistrationDTO;
import ru.faust.cloudstorage.exception.AlreadyExistsException;
import ru.faust.cloudstorage.exception.InvalidParameterException;
import ru.faust.cloudstorage.model.User;
import ru.faust.cloudstorage.repository.UserRepository;
import ru.faust.cloudstorage.service.AuthService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserRegistrationDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationDTO(
                new UserDTO("test", "test"),
                "test");
    }

    @Test
    void register_success() {
        when(userRepository.findByUsername(dto.userDTO().username()))
                .thenReturn(Optional.empty());

        try (MockedStatic<PasswordEncoder> passwordEncoderMock = mockStatic(PasswordEncoder.class)) {
            passwordEncoderMock.when(() -> passwordEncoder.encode(dto.userDTO().password()))
                    .thenReturn("hashedPassword");

            authService.register(dto);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            assertEquals("hashedPassword", userCaptor.getValue().getPassword());
        }
    }

    @Test
    void register_userExists() {
        when(userRepository.findByUsername(dto.userDTO().username()))
                .thenReturn(Optional.of(new User()));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> authService.register(dto));

        assertEquals("User with this username already exists.", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_wrongPassword() {
        UserRegistrationDTO dto = new UserRegistrationDTO(
                new UserDTO("test", "test"),
                "test1");

        when(userRepository.findByUsername(dto.userDTO().username()))
                .thenReturn(Optional.empty());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> authService.register(dto));

        assertEquals("Passwords do not match.", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}
