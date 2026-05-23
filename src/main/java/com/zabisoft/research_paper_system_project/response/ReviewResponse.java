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
public class ReviewResponse {

    private UUID reviewId;

    private String paperTitle;

    private String reviewerName;

    private Integer rating;

    private String comments;

    private ReviewStatus status;

    private ReviewDecision reviewDecision;
}
