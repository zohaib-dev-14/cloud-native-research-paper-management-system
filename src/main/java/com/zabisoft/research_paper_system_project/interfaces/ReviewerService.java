package com.zabisoft.research_paper_system_project.interfaces;

import com.zabisoft.research_paper_system_project.dto.ReviewRequest;
import com.zabisoft.research_paper_system_project.response.AssignedPaperResponse;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.ReviewResponse;
import com.zabisoft.research_paper_system_project.response.ReviewerResponse;

import java.util.UUID;


public interface ReviewerService {
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
}
