package com.zabisoft.research_paper_system_project.controller;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.interfaces.ReaderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/show-papers")
@AllArgsConstructor
public class ReaderController {
    private final ReaderService readerService;
    @GetMapping("/view")
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
    public ResponseEntity<?> getAcceptedPaperById(
            @PathVariable UUID paperId
    ) {
        return ResponseEntity.ok(
                readerService.getPapersById(paperId)
        );
    }
}
