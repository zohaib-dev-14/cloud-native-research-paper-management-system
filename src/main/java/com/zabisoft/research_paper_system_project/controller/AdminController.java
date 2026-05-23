package com.zabisoft.research_paper_system_project.controller;

import com.zabisoft.research_paper_system_project.dto.AssignReviewerRequest;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.interfaces.AdminService;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import com.zabisoft.research_paper_system_project.response.ReviewerResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/admin")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/papers/pending")
    public ResponseEntity<PageResponse<PaperResponse>> getPendingPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) PaperStatus paperStatus
            ) {
        return ResponseEntity.status(200).body(
                adminService.getPapers(page, size, paperStatus)
        );
    }

    @GetMapping("/papers/{paperId}")
    public ResponseEntity<PaperResponse> getPaperById(
            @PathVariable UUID paperId
    ) {
        return ResponseEntity.status(200).body(
                adminService.getPaperById(paperId)
        );
    }

    @PostMapping("/papers/{paperId}/approve")
    public ResponseEntity<ApiResponse> approvePaperById(
            @PathVariable
            UUID paperId
    ) {
       adminService.approvePaper(paperId);
       return ResponseEntity.status(201).body(
               new ApiResponse(
                       true,
                       "Paper approved successfully"
               )
       );
    }

    @PostMapping("/papers/{paperId}/reject")

    public ResponseEntity<ApiResponse> rejectPaperById(
            @PathVariable UUID paperId
    ) {
        adminService.rejectPaper(paperId);
        return ResponseEntity.status(201).body(
                new ApiResponse(
                        true,
                        "Paper rejected successfully"
                )
        );
    }

    @GetMapping("/reviewers")
    public ResponseEntity<PageResponse<ReviewerResponse>> getReviewers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Expertise expertise
            ) {
        return ResponseEntity.status(200).body(
                adminService.getReviewers(page, size, expertise)
        );
    }

    @GetMapping("/reviewers/{reviewerId}")
    public ResponseEntity<ReviewerResponse> getReviewerById(
            @PathVariable UUID reviewerId
    ) {
        return ResponseEntity.status(200).body(
                adminService.getReviewerById(reviewerId)
        );
    }
    @PostMapping("/assign-reviewer")
    public ResponseEntity<?> assignReviewer(
            @RequestBody AssignReviewerRequest request
    ) {
        adminService.assignReviewer(request);
        return ResponseEntity.ok("Reviewer assigned successfully");
    }
    @GetMapping("/papers/{paperId}/reviews")
    public ResponseEntity<?> getPaperReviews(
            @PathVariable UUID paperId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                adminService.getPaperReviews(
                paperId,
                page,
                size
        )
        );
    }

    @PatchMapping("/papers/{paperId}/final-decision")
    public ResponseEntity<?> finalizePaperDecision(
            @PathVariable
            UUID paperId
    ) {
        adminService.makeFinalDecision(paperId);
        return ResponseEntity.ok(
                "Paper decision finalized"
        );
    }



}
