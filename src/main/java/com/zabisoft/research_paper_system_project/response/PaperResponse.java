package com.zabisoft.research_paper_system_project.response;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.enums.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PaperResponse {
    private UUID id;
    private String title;
    private String abstractText;
    private String filePath;
    private String researcherName;
    private Expertise domain;
    private PaperStatus paperStatus;
    private ProcessingStatus processingStatus;
}
