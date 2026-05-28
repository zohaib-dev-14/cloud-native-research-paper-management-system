package com.zabisoft.research_paper_system_project.controller;

import com.zabisoft.research_paper_system_project.dto.AssignReviewerRequest;
import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.interfaces.AdminService;
import com.zabisoft.research_paper_system_project.response.GenericApiResponse;
import com.zabisoft.research_paper_system_project.response.ReviewerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/admin")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
@Tag(
        name = "5. Admin APIs",
        description = "APIs for administrative operations and workflow management."
)
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/papers/pending")
    @Operation(
            summary = "Get Pending Papers",
            description = "Returns papers pending review."
    )
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
    @Operation(
            summary = "Get Paper Details",
            description = "Returns paper details for admin."
    )
    public ResponseEntity<PaperResponse> getPaperById(
            @PathVariable UUID paperId
    ) {
        return ResponseEntity.status(200).body(
                adminService.getPaperById(paperId)
        );
    }

    @PostMapping("/papers/{paperId}/approve")
    @Operation(
            summary = "Approve Research Paper",
            description = "Approves submitted research paper."
    )
    public ResponseEntity<GenericApiResponse> approvePaperById(
            @PathVariable
            UUID paperId
    ) {
       adminService.approvePaper(paperId);
       return ResponseEntity.status(201).body(
               new GenericApiResponse(
                       true,
                       "Paper approved successfully"
               )
       );
    }

    @PostMapping("/papers/{paperId}/reject")
    @Operation(
            summary = "Reject Research Paper",
            description = "Rejects submitted research paper."
    )
    public ResponseEntity<GenericApiResponse> rejectPaperById(
            @PathVariable UUID paperId
    ) {
        adminService.rejectPaper(paperId);
        return ResponseEntity.status(201).body(
                new GenericApiResponse(
                        true,
                        "Paper rejected successfully"
                )
        );
    }

    @GetMapping("/reviewers")
    @Operation(
            summary = "Get All Reviewers",
            description = "Returns all reviewers."
    )
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
    @Operation(
            summary = "Get Reviewer Details",
            description = "Returns reviewer details."
    )
    public ResponseEntity<ReviewerResponse> getReviewerById(
            @PathVariable UUID reviewerId
    ) {
        return ResponseEntity.status(200).body(
                adminService.getReviewerById(reviewerId)
        );
    }
    @PostMapping("/assign-reviewer")
    @Operation(
            summary = "Assign Reviewer",
            description = "Assigns reviewer to research paper."
    )
    public ResponseEntity<?> assignReviewer(
            @RequestBody AssignReviewerRequest request
    ) {
        adminService.assignReviewer(request);
        return ResponseEntity.ok("Reviewer assigned successfully");
    }
    @GetMapping("/papers/{paperId}/reviews")
    @Operation(
            summary = "Get Paper Reviews",
            description = "Returns reviews of a research paper."
    )
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
    @Operation(
            summary = "Finalize Paper Decision",
            description = "Sets final paper decision."
    )
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
