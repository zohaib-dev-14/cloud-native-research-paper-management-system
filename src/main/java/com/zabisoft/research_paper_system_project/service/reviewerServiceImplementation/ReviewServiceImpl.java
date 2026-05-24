package com.zabisoft.research_paper_system_project.service.reviewerServiceImplementation;

import com.zabisoft.research_paper_system_project.dto.ReviewRequest;
import com.zabisoft.research_paper_system_project.entities.Assignment;
import com.zabisoft.research_paper_system_project.entities.Paper;
import com.zabisoft.research_paper_system_project.entities.Review;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.AssignmentStatus;
import com.zabisoft.research_paper_system_project.enums.ReviewStatus;
import com.zabisoft.research_paper_system_project.interfaces.AuthenticatedUserService;
import com.zabisoft.research_paper_system_project.interfaces.ReviewService;
import com.zabisoft.research_paper_system_project.repositories.AssignmentRepository;
import com.zabisoft.research_paper_system_project.repositories.ReviewRepository;
import com.zabisoft.research_paper_system_project.response.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final AuthenticatedUserService authenticatedUserService;
    private final AssignmentRepository assignmentRepository;
    private final ReviewRepository reviewRepository;
    @Override
    @Transactional(readOnly = true)
    public PageResponse<AssignedPaperResponse> getAssignedPapers(int page, int size) {
        User reviewer = authenticatedUserService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Assignment> assignments = assignmentRepository.findByReviewer(reviewer, pageable);

        //? this::mapToAssignedPaperResponse can be written as
        //* (assignment -> mapToAssignedPaperResponse(assignment))

        List<AssignedPaperResponse> content = assignments.getContent().stream().map(
                this::mapToAssignedPaperResponse).toList();

        return PageResponse.<AssignedPaperResponse>builder()
                .content(content)
                .page(assignments.getNumber())
                .totalElements(assignments.getTotalElements())
                .size(assignments.getSize())
                .totalPages(assignments.getTotalPages())
                .first(assignments.isFirst())
                .last(assignments.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AssignedPaperResponse getAssignmentById(UUID assignmentId) {
        User currentReviewer = authenticatedUserService.getCurrentUser();

        Assignment assignment = assignmentRepository.findAssignmentDetail(assignmentId).orElseThrow(
                () -> new RuntimeException("Assignment not found")
        );

        if (!assignment.getReviewer().getId().equals(currentReviewer.getId())) {
            throw new RuntimeException("You are not assigned to this paper");
        }

        return mapToAssignedPaperResponse(assignment);

    }

    @Override
    @Transactional
    public AssignedPaperResponse getAssignmentDetail(UUID assignmentId) {
        User currentReviewer = authenticatedUserService.getCurrentUser();

        Assignment assignment = assignmentRepository.findAssignmentDetail(assignmentId).orElseThrow(
                () -> new RuntimeException("Assignment not found")
        );

        if (!assignment.getReviewer().getId().equals(currentReviewer.getId())) {
            throw new RuntimeException("You are not assigned to this paper");
        }

        return mapToAssignedPaperResponse(assignment);
    }

    @Override
    @Transactional
    public void startReview(UUID assignmentId) {
        User currentReviewer = authenticatedUserService.getCurrentUser();

        Assignment assignment = assignmentRepository.findAssignmentDetail(assignmentId).orElseThrow(
                () -> new RuntimeException("Assignment not found")
        );

        // ownership validation
        if (!assignment.getReviewer().getId().equals(currentReviewer.getId())) {
            throw new RuntimeException("You are not assigned to this paper");
        }

        // workflow validation
        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new RuntimeException("Review already started");
        }

        assignment.setStatus(AssignmentStatus.IN_PROGRESS);
    }

    @Override
    @Transactional
    public ReviewResponse submitReview(ReviewRequest request) {
       User currentReviewer = authenticatedUserService.getCurrentUser();

       Assignment assignment = assignmentRepository.findAssignmentDetail(request.getAssignmentId()).orElseThrow(
               () -> new RuntimeException("Assignment not found")
       );

       if (!assignment.getReviewer().getId().equals(currentReviewer.getId())) {
           throw new RuntimeException("You are not assigned to this paper");
       }

       if (assignment.getStatus() != AssignmentStatus.IN_PROGRESS) {
           throw new RuntimeException("Review not started yet");
       }

        Paper paper = assignment.getPaper();

       boolean alreadyReviewed = reviewRepository.existsByPaperAndReviewer(paper, currentReviewer);

       if (alreadyReviewed) {
           throw new RuntimeException("Review already submitted");
       }

        Review review = new Review();

        review.setPaper(paper);
        review.setReviewer(currentReviewer);
        review.setComments(request.getComments());
        review.setRating(request.getRating());
        review.setReviewDecision(request.getReviewDecision());
        review.setStatus(ReviewStatus.SUBMITTED);

        Review savedReview = reviewRepository.save(review);
        // assignment complete
        assignment.setStatus(AssignmentStatus.COMPLETED);

        return ReviewResponse
                .builder()
                .reviewId(savedReview.getId())
                .paperTitle(paper.getTitle())
                .reviewerName(currentReviewer.getName())
                .rating(savedReview.getRating())
                .comments(savedReview.getComments())
                .reviewDecision(savedReview.getReviewDecision())
                .status(savedReview.getStatus())
                .build();
     }

    @Override
    public PageResponse<ReviewerHistoryResponse> getReviewHistory(int page, int size) {
        User currentReviewer = authenticatedUserService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Review> reviews = reviewRepository.findByReviewer(currentReviewer, pageable);

        List<ReviewerHistoryResponse> content = reviews.getContent().stream()
                .map(this::mapToReviewerHistoryResponse).toList();

        return mapToPageResponseRHR(content, reviews);
    }

    //  private PaperResponse mapToResponse(Paper paper) {
//        return PaperResponse.builder()
//                .id(paper.getId())
//                .title(paper.getTitle())
//                .abstractText(paper.getAbstractText())
//                .filePath(paper.getFilePath())
//                .researcherName(paper.getResearcher().getName())
//                .domain(paper.getDomain())
//                .paperStatus(paper.getPaperStatus())
//                .processingStatus(paper.getProcessingStatus())
//                .build();
//    }

    public AssignedPaperResponse mapToAssignedPaperResponse(
            Assignment assignment
    ) {
       return AssignedPaperResponse.builder()
               .assignmentId(assignment.getId())
               .paperId(assignment.getPaper().getId())
               .paperTitle(assignment.getPaper().getTitle())
               .researcherName(assignment.getPaper().getResearcher().getName())
               .assignedBy(assignment.getAssignedBy().getName())
               .assignmentStatus(assignment.getStatus())
               .paperStatus(assignment.getPaper().getPaperStatus())
               .build();
    }

    public ReviewerHistoryResponse mapToReviewerHistoryResponse(Review review) {
        return ReviewerHistoryResponse
                .builder()
                .reviewId(review.getId())
                .paperTitle(review.getPaper().getTitle())
                .rating(review.getRating())
                .comments(review.getComments())
                .reviewDecision(review.getReviewDecision())
                .reviewStatus(review.getStatus())
                .build();

    }

    public PageResponse<ReviewerHistoryResponse> mapToPageResponseRHR(List<ReviewerHistoryResponse> content, Page<Review> reviews) {
        return PageResponse.<ReviewerHistoryResponse>builder()
                .content(content)
                .page(reviews.getNumber())
                .size(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .first(reviews.isFirst())
                .last(reviews.isLast())
                .build();
    }

}
