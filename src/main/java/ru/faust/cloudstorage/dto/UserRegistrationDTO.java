package ru.faust.cloudstorage.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegistrationDTO(

        UserDTO userDTO,

        @NotBlank(message = "You did not typed you ")
        String repeatPassword) {
}
