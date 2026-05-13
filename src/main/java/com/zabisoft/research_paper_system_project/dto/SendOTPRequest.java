package com.zabisoft.research_paper_system_project.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOTPRequest {
    @Email
    private String email;
}
