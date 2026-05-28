package com.zabisoft.research_paper_system_project.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GenericApiResponse {
    private boolean responseCheck;
    private String message;
}
