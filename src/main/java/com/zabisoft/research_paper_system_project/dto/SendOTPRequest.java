package com.zabisoft.research_paper_system_project.dto;

import com.zabisoft.research_paper_system_project.enums.OTPType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOTPRequest {
    @NotBlank
    @Email(message = "Email format not supported")
    @Size(max = 30, message = "Email too long")

    private String email;



    @Enumerated(EnumType.STRING)
    private OTPType otpType;
}
