package com.zabisoft.research_paper_system_project.controller;
import com.zabisoft.research_paper_system_project.dto.CreatePaperRequest;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.dto.UpdatePaperRequest;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.interfaces.PaperService;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/papers")
@PreAuthorize("hasAnyRole('RESEARCHER', 'ADMIN')")
public class ResearchController {
    private final PaperService paperService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPaper(
            @ModelAttribute @Valid CreatePaperRequest createPaperRequest
    ) {
        return ResponseEntity.status(201).body(paperService.createPaper(createPaperRequest));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PaperStatus paperStatus
            ) {

        return ResponseEntity.ok(
                paperService.getMyPapers(
                        page,
                        size,
                        paperStatus
                )
        );
    }
    @GetMapping("/{paperId}")

    public ResponseEntity<PaperResponse> getPaperById(
            @PathVariable UUID paperId
            ) {
        return ResponseEntity.status(200).body(paperService.getPaperById(paperId));
    }

    @DeleteMapping("/{paperId}")

    public ResponseEntity<ApiResponse> deletePaperById(
            @PathVariable UUID paperId
    ) {
        return ResponseEntity.status(200).body(
                paperService.deletePaper(paperId)
        );
    }
    @PutMapping("/{paperId}")
    public ResponseEntity<PaperResponse> updatePaper(
            @PathVariable UUID paperId,
            @ModelAttribute
            @Valid UpdatePaperRequest updatePaperRequest
            ) {
        return ResponseEntity.status(201).body(
                paperService.updatePaper(paperId, updatePaperRequest)
        );
    }
}
