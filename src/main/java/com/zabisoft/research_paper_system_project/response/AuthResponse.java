package com.zabisoft.research_paper_system_project.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}