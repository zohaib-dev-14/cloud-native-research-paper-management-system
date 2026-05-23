package com.zabisoft.research_paper_system_project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignReviewerRequest {
    @NotNull
    private UUID paperId;
    @NotNull
    private UUID reviewerId;
}
