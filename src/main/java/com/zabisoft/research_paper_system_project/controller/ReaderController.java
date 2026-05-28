package com.zabisoft.research_paper_system_project.controller;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.interfaces.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/show-papers")
@AllArgsConstructor
@Tag(
        name = "6. Reader APIs",
        description = "APIs for viewing published research papers."
)
public class ReaderController {
    private final ReaderService readerService;
    @GetMapping("/view")
    @Operation(
            summary = "View Published Papers",
            description = "Returns all published research papers."
    )
    public ResponseEntity<?> getAcceptedPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Expertise domain
    ) {
        return ResponseEntity.ok(
                readerService.getAcceptedPapers(
                        page,
                        size,
                        domain
                )
        );
    }

    @GetMapping("/view/{paperId}")
    @Operation(
            summary = "View Research Paper Details",
            description = "Returns published research paper details."
    )
    public ResponseEntity<?> getAcceptedPaperById(
            @PathVariable UUID paperId
    ) {
        return ResponseEntity.ok(
                readerService.getPapersById(paperId)
        );
    }
}
