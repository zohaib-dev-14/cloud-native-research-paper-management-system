package com.zabisoft.research_paper_system_project.response;

import com.zabisoft.research_paper_system_project.enums.ReviewDecision;
import com.zabisoft.research_paper_system_project.enums.ReviewStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReviewResponse {

    private UUID reviewId;

    private String reviewerName;

    private Integer rating;

    private String comments;

    private ReviewDecision reviewDecision;

    private ReviewStatus reviewStatus;
}
