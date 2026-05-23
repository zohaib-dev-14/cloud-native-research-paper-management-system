package com.zabisoft.research_paper_system_project.dto;

import com.zabisoft.research_paper_system_project.enums.ReviewDecision;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewRequest {
    private UUID assignmentId;
    private String comments;
    private Integer rating;
    private ReviewDecision reviewDecision;
}
