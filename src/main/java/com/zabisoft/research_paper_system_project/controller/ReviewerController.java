package com.zabisoft.research_paper_system_project.controller;

import com.zabisoft.research_paper_system_project.dto.ReviewRequest;
import com.zabisoft.research_paper_system_project.interfaces.ReviewService;
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
public class ReviewerController {

    private final ReviewService reviewService;
    @GetMapping("/papers")
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
    public ResponseEntity<?> getAssignmentsById(
            @PathVariable UUID assignmentId
            ) {
        return ResponseEntity.ok(
                reviewService.getAssignmentById(assignmentId)
        );
    }
    @PatchMapping("/assignments/{assignmentId}/start")
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
    public ResponseEntity<?> submitReview(
            @RequestBody
            ReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.submitReview(request));
    }


    @GetMapping("/history")
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
