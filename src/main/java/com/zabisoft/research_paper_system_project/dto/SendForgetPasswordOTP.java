package com.zabisoft.research_paper_system_project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zabisoft.research_paper_system_project.enums.OTPType;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendForgetPasswordOTP {
    @NotBlank
    @Email(message = "Email format not supported")
    @Size(max = 30, message = "Email too long")

    private String email;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OTPType otpType = OTPType.FORGOT_PASSWORD;
}
