package com.zabisoft.research_paper_system_project.dto;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter

public class UpdatePaperRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String abstractText;

    @NotNull
    private Expertise domain;

    private MultipartFile multipartFile;
}
