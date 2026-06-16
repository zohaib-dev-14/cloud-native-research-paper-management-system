package com.zabisoft.research_paper_system_project.controller;


import com.zabisoft.research_paper_system_project.interfaces.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        String filePath = fileStorageService.storeFile(file);
        return ResponseEntity.ok(
                Map.of (
                        "success", true,
                        "filePath", filePath
                )
        );
    }
}