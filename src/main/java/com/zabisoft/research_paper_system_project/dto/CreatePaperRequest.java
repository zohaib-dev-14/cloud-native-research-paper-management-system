package com.zabisoft.research_paper_system_project.dto;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaperRequest {
    @NotBlank
    @NotNull
    private String title;
    @NotBlank
    @NotNull
    private String abstractText;
    @NotNull
    private Expertise domain;
    @NotNull
    private MultipartFile multipartFile;

}
