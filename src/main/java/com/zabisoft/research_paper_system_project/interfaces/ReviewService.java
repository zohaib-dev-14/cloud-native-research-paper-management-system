package com.zabisoft.research_paper_system_project.interfaces;

import com.zabisoft.research_paper_system_project.dto.ReviewRequest;
import com.zabisoft.research_paper_system_project.response.*;

import java.util.UUID;


public interface ReviewService {
    PageResponse<AssignedPaperResponse> getAssignedPapers(
            int page,
            int size
    );
    AssignedPaperResponse getAssignmentById(
            UUID assignmentId
    );

    AssignedPaperResponse getAssignmentDetail(
      UUID assignmentId
    );

    void startReview(UUID assignmentId);

    ReviewResponse submitReview(
            ReviewRequest request
    );

    PageResponse<ReviewerHistoryResponse> getReviewHistory(
            int page,
            int size
    );
}
