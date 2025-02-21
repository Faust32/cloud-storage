package ru.faust.cloudstorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "Username cannot be empty.")
        @Size(min = 5, max = 25, message = "Username must be between 5 and 25 characters.")
        String username,

        @NotBlank(message = "Password cannot be empty.")
        @Size(min = 6, max = 32, message = "Password must be at least 6 characters long, but not over 32 characters.")
        String password) {
}
