package com.zabisoft.research_paper_system_project.response;

import com.zabisoft.research_paper_system_project.enums.Expertise;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewerResponse {
    private UUID id;
    private String name;
    private String email;
    private Set<Expertise> expertises;
}
