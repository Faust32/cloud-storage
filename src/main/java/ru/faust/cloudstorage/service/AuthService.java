package ru.faust.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.faust.cloudstorage.dto.UserRegistrationDTO;
import ru.faust.cloudstorage.exception.AlreadyExistsException;
import ru.faust.cloudstorage.exception.InvalidParameterException;
import ru.faust.cloudstorage.model.User;
import ru.faust.cloudstorage.repository.UserRepository;
import ru.faust.cloudstorage.validation.UserValidation;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void register(UserRegistrationDTO userRegistrationDTO) {
        if (userRepository.findByUsername(userRegistrationDTO.userDTO().username()).isPresent()) {
            throw new AlreadyExistsException("User with this username already exists.", "register");
        }
        UserValidation.validate(userRegistrationDTO.userDTO());
        if (!userRegistrationDTO.userDTO().password().equals(userRegistrationDTO.repeatPassword())) {
            throw new InvalidParameterException("Passwords do not match.", "register");
        }
        User user = new User();
        user.setUsername(userRegistrationDTO.userDTO().username());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.userDTO().password()));
        userRepository.save(user);
    }
}
