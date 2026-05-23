package com.zabisoft.research_paper_system_project.interfaces;

import com.zabisoft.research_paper_system_project.dto.AssignReviewerRequest;
import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.response.AdminReviewResponse;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.response.ReviewerResponse;

import java.util.UUID;

public interface AdminService {
    PageResponse<PaperResponse> getPapers(int page, int size, PaperStatus paperStatus);
    PaperResponse getPaperById(UUID paperId);
    void approvePaper(UUID paperId);
    void rejectPaper(UUID paperId);
    PageResponse<ReviewerResponse> getReviewers(
            int page,
            int size,
            Expertise expertise
    );
    ReviewerResponse getReviewerById(UUID reviewerId);
    void assignReviewer(AssignReviewerRequest assignReviewerRequest);
    PageResponse<AdminReviewResponse> getPaperReviews(
            UUID paperId,
            int page,
            int size
    );

    void makeFinalDecision(
            UUID paperId
    );
}
