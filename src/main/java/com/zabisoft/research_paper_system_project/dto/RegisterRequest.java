package com.zabisoft.research_paper_system_project.dto;

import com.zabisoft.research_paper_system_project.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 30, message = "Email too long")
    private String email;
    @NotBlank(message = "Password is required")

    @Size(min = 8, message = "Password must be at least 8 characters")

    @Pattern(
            regexp = "^(?=.*[A-Z]).*$",
            message = "Password must contain at least one uppercase letter"
    )
    private String password;
    @NotBlank(message = "Password is required")

    @Size(min = 8, message = "Password must be at least 8 characters")

    @Pattern(
            regexp = "^(?=.*[A-Z]).*$",
            message = "Password must contain at least one uppercase letter"
    )
    private String confirmPassword;
    @Enumerated(EnumType.STRING)
    private Role role;
}
