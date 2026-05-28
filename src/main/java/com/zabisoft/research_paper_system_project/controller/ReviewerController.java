package com.zabisoft.research_paper_system_project.controller;

import com.zabisoft.research_paper_system_project.dto.ReviewRequest;
import com.zabisoft.research_paper_system_project.interfaces.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@AllArgsConstructor
@PreAuthorize(
        "hasAnyRole('ADMIN', 'REVIEWER')"
)
@Tag(
        name = "4. Reviewer APIs",
        description = "APIs for reviewer workflow and review management."
)
public class ReviewerController {

    private final ReviewService reviewService;
    @GetMapping("/papers")
    @Operation(
            summary = "Get Assigned Papers",
            description = "Returns papers assigned to reviewer."
    )
    public ResponseEntity<?> getAssignedPapers(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "5")
            int size
    ) {
        return ResponseEntity.ok(
                reviewService.getAssignedPapers(page, size)
        );
    }

    @GetMapping("/assignments/{assignmentId}")
    @Operation(
            summary = "Get Assignment Details",
            description = "Returns assignment details."
    )
    public ResponseEntity<?> getAssignmentsById(
            @PathVariable UUID assignmentId
            ) {
        return ResponseEntity.ok(
                reviewService.getAssignmentById(assignmentId)
        );
    }
    @PatchMapping("/assignments/{assignmentId}/start")
    @Operation(
            summary = "Start Review Workflow",
            description = "Starts assigned review workflow."
    )
    public ResponseEntity<?> startReview(
            @PathVariable
            UUID assignmentId
    ) {
        reviewService.startReview(assignmentId);
        return ResponseEntity.ok(
                "Review started successfully"
        );
    }

    @PostMapping
    @Operation(
            summary = "Submit Review",
            description = "Submits paper review and comments."
    )
    public ResponseEntity<?> submitReview(
            @RequestBody
            ReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.submitReview(request));
    }


    @GetMapping("/history")
    @Operation(
            summary = "Get Review History",
            description = "Returns reviewer history."
    )
    public ResponseEntity<?> getReviewHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                reviewService.getReviewHistory(
                        page,
                        size
                )
        );
    }

}
