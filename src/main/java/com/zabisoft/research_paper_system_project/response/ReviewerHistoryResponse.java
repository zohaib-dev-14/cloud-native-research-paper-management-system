package com.zabisoft.research_paper_system_project.response;

import com.zabisoft.research_paper_system_project.enums.ReviewDecision;
import com.zabisoft.research_paper_system_project.enums.ReviewStatus;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewerHistoryResponse {
    private UUID reviewId;
    private String paperTitle;
    private Integer rating;
    private String comments;
    private ReviewDecision reviewDecision;
    private ReviewStatus reviewStatus;
}

