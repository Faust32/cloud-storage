package ru.faust.cloudstorage.contoller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.faust.cloudstorage.dto.UserRegistrationDTO;
import ru.faust.cloudstorage.service.AuthService;

import java.util.Objects;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String signIn() {
        return "sign-in";
    }

    @GetMapping("/registration")
    public String signUp() {
        return "sign-up";
    }

    @PostMapping("/registration")
    public String signUp(@Valid @ModelAttribute UserRegistrationDTO userDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            String errorMessage = Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/registration";
        }
        authService.register(userDTO);
        return "redirect:/login";
    }
}