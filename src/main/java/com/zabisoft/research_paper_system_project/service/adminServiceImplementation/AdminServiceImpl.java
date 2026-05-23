package com.zabisoft.research_paper_system_project.service.adminServiceImplementation;

import com.zabisoft.research_paper_system_project.dto.AssignReviewerRequest;
import com.zabisoft.research_paper_system_project.dto.PaperDecisionRequest;
import com.zabisoft.research_paper_system_project.entities.Assignment;
import com.zabisoft.research_paper_system_project.entities.Review;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.AssignmentStatus;
import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.enums.Role;
import com.zabisoft.research_paper_system_project.repositories.ReviewRepository;
import com.zabisoft.research_paper_system_project.response.AdminReviewResponse;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.entities.Paper;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.interfaces.AdminService;
import com.zabisoft.research_paper_system_project.interfaces.AuthenticatedUserService;
import com.zabisoft.research_paper_system_project.interfaces.FileStorageService;
import com.zabisoft.research_paper_system_project.repositories.AssignmentRepository;
import com.zabisoft.research_paper_system_project.repositories.PaperRepository;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import com.zabisoft.research_paper_system_project.response.ReviewerResponse;
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
public class AdminServiceImpl implements AdminService {

    private final PaperRepository paperRepository;
    private final AssignmentRepository assignmentRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;


    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaperResponse> getPapers(int page, int size, PaperStatus status) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Paper> paperPage = paperRepository.findByPaperStatus(
                status,
                pageable
        );

        // For every paper convert it into PaperResponse
        List<PaperResponse> content = paperPage.stream().map(this::mapToResponse).toList();

        return new PageResponse<>(
                content,
                paperPage.getNumber(),
                paperPage.getSize(),
                paperPage.getTotalElements(),
                paperPage.getTotalPages(),
                paperPage.isFirst(),
                paperPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaperResponse getPaperById(UUID paperId) {
        Paper paper = paperRepository.findPaperById(paperId).orElseThrow(
                () -> new RuntimeException("Paper Not Found")
        );

        return mapToResponse(paper);
    }

    @Override
    @Transactional
    public void approvePaper(UUID paperId) {
        Paper paper = paperRepository.findById(paperId).orElseThrow(
                () -> new RuntimeException("Paper not found")
        );

        if (paper.getPaperStatus() != PaperStatus.PENDING_ADMIN_REVIEW) {
            throw new RuntimeException("Only pending papers can be approved");
        }

        paper.setPaperStatus(PaperStatus.SUBMITTED);
        paperRepository.save(paper);
    }

    @Override
    public void rejectPaper(UUID paperId) {
        Paper paper = paperRepository.findById(paperId).orElseThrow(
                () -> new RuntimeException("Paper not found")
        );

        if (paper.getPaperStatus() != PaperStatus.PENDING_ADMIN_REVIEW) {
            throw new RuntimeException("Only pending papers can be removed");
        }

        // delete physical file

        fileStorageService.deleteFile(paper.getFilePath());

        // update workflow state
        paper.setPaperStatus(PaperStatus.REJECTED);

        paperRepository.save(paper);
    }

    @Override
    public PageResponse<ReviewerResponse> getReviewers(int page, int size, Expertise expertise) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> reviewers;
        if ( expertise == null) {
            reviewers = userRepository.findReviewerByRole(
                    Role.REVIEWER,
                    pageable
            );
        } else {
            reviewers = userRepository.findReviewerByExpertises(
                    Role.REVIEWER,
                    expertise,
                    pageable
            );
        }

        List<ReviewerResponse> content =
                reviewers.getContent()
                        .stream()
                        .map(reviewer ->
                                ReviewerResponse.builder()
                                        .id(reviewer.getId())
                                        .name(reviewer.getName())
                                        .email(reviewer.getEmail())
                                        .expertises(reviewer.getExpertises())
                                        .build()
                        )
                        .toList();

        return PageResponse.<ReviewerResponse>builder()
                .content(content)
                .page(reviewers.getNumber())
                .size(reviewers.getSize())
                .totalElements(reviewers.getTotalElements())
                .totalPages(reviewers.getTotalPages())
                .first(reviewers.isFirst())
                .last(reviewers.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewerResponse getReviewerById(UUID reviewerId) {

        User reviewer = userRepository.findReviewerById(reviewerId).orElseThrow(
                () -> new RuntimeException("Reviewer not found")
        );

        ReviewerResponse response = new ReviewerResponse();

        response.setId(reviewer.getId());
        response.setName(reviewer.getName());
        response.setEmail(reviewer.getEmail());
        response.setExpertises(reviewer.getExpertises());

        return response;
    }

    @Override
    @Transactional
    public void assignReviewer(AssignReviewerRequest assignReviewerRequest) {
        Paper paper = paperRepository.findPaperById(assignReviewerRequest.getPaperId()).orElseThrow(
                () -> new RuntimeException("Paper not found")
        );

        User reviewer = userRepository.findById(assignReviewerRequest.getReviewerId())
                .orElseThrow(
                        () -> new RuntimeException("Reviewer not found")
                );

        User admin = authenticatedUserService.getCurrentUser();

        // check if ROLE is reviewer

        if (reviewer.getRole() != Role.REVIEWER) {
            throw new RuntimeException("User is not a reviewer");
        }


        // check if domain is correct
        if (!reviewer.getExpertises().contains(paper.getDomain())) {
            throw new RuntimeException("Paper and Reviewer domain mismatch");
        }

        // check if workflow works properly
        if (paper.getPaperStatus() != PaperStatus.SUBMITTED) {
            throw new RuntimeException("Only Submitted papers can be assigned");
        }

        List<Assignment> assignments = assignmentRepository.findByPaper(paper);

        boolean alreadyAssigned = assignments.stream().anyMatch(
                assignment ->
                        assignment
                                .getReviewer()
                                .getId()
                                .equals(
                                        reviewer.getId()
                                )
        );

        if (alreadyAssigned) {
            throw new RuntimeException("Reviewer already assigned");
        }

        Assignment assignment = new Assignment();

        assignment.setPaper(paper);
        assignment.setReviewer(reviewer);
        assignment.setAssignedBy(admin);
        assignment.setStatus(AssignmentStatus.ASSIGNED);

        assignmentRepository.save(assignment);
        // move paper into review stage
        paper.setPaperStatus(PaperStatus.UNDER_REVIEW);
        paperRepository.save(paper);

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminReviewResponse> getPaperReviews(UUID paperId, int page, int size) {
        Paper paper = paperRepository.findById(paperId).orElseThrow(
                () -> new RuntimeException("Paper not found")
        );
        Pageable pageable = PageRequest.of(page, size);

        Page<Review> reviewPage = reviewRepository.findByPaper(paper, pageable);
        List<AdminReviewResponse> content = reviewPage.getContent()
                        .stream()
                        .map(this::mapToAdminReviewResponse)
                        .toList();

        return PageResponse.<AdminReviewResponse>builder()
                .content(content)
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .first(reviewPage.isFirst())
                .last(reviewPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void makeFinalDecision(UUID paperId) {
        Paper paper = paperRepository.findById(paperId).orElseThrow(
                () -> new RuntimeException("Paper not found")
        );

        List<Review> reviews = reviewRepository.findAllByPaper(paper);
        if (reviews.isEmpty()) {
            throw new RuntimeException("No reviews found");
        }
        double averageRating = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0);

        // automated decision
        if (averageRating >= 4) {
            paper.setPaperStatus(PaperStatus.ACCEPTED);
        } else if (averageRating >= 2) {
            paper.setPaperStatus(PaperStatus.REVISION);
        } else {
            paper.setPaperStatus(PaperStatus.REJECTED);
        }
    }

    private PaperResponse mapToResponse(Paper paper) {
        return PaperResponse.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .abstractText(paper.getAbstractText())
                .filePath(paper.getFilePath())
                .researcherName(paper.getResearcher().getName())
                .domain(paper.getDomain())
                .paperStatus(paper.getPaperStatus())
                .processingStatus(paper.getProcessingStatus())
                .build();
    }

    private AdminReviewResponse mapToAdminReviewResponse(Review review) {
        return AdminReviewResponse.builder()
                .reviewId(review.getId())
                .reviewerName(review.getReviewer().getName())
                .rating(review.getRating())
                .comments(review.getComments())
                .reviewDecision(review.getReviewDecision())
                .reviewStatus(review.getStatus())
                .build();
    }
}
