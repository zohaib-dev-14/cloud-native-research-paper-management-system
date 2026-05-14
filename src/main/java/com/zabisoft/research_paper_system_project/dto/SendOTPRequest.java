package com.zabisoft.research_paper_system_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOTPRequest {
    @NotBlank
    @Email(message = "Email format not supported")
    private String email;
}
