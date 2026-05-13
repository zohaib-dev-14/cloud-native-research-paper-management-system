package com.zabisoft.research_paper_system_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOTPRequest {
    @Email
    private String email;

    @Size(min = 6, max = 6)
    private String otp;
}
