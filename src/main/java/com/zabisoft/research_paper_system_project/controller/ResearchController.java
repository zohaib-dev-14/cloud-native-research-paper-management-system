package com.zabisoft.research_paper_system_project.controller;
import com.zabisoft.research_paper_system_project.dto.CreatePaperRequest;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.dto.UpdatePaperRequest;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.interfaces.PaperService;
import com.zabisoft.research_paper_system_project.response.GenericApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "3. Research Paper APIs",
        description = "APIs for research paper management."
)
public class ResearchController {
    private final PaperService paperService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload Research Paper",
            description = "Uploads a new research paper."
    )
    public ResponseEntity<?> createPaper(
            @ModelAttribute @Valid CreatePaperRequest createPaperRequest
    ) {
        return ResponseEntity.status(201).body(paperService.createPaper(createPaperRequest));
    }

    @GetMapping("/my")
    @Operation(
            summary = "Get My Research Papers",
            description = "Returns papers uploaded by authenticated user."
    )
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

    @Operation(
            summary = "Get Research Paper",
            description = "Returns research paper details."
    )
    public ResponseEntity<PaperResponse> getPaperById(
            @PathVariable UUID paperId
            ) {
        return ResponseEntity.status(200).body(paperService.getPaperById(paperId));
    }

    @DeleteMapping("/{paperId}")

    @Operation(
            summary = "Delete Research Paper",
            description = "Deletes research paper."
    )
    public ResponseEntity<GenericApiResponse> deletePaperById(
            @PathVariable UUID paperId
    ) {
        return ResponseEntity.status(200).body(
                paperService.deletePaper(paperId)
        );
    }
    @PutMapping(value = "/{paperId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update Research Paper",
            description = "Updates existing research paper via multi-part form submission.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UpdatePaperRequest.class)
                    )
            )
    )
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
