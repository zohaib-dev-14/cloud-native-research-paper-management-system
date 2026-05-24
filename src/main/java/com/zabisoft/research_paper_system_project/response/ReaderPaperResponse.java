package com.zabisoft.research_paper_system_project.response;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class ReaderPaperResponse {
    private UUID paperId;
    private String title;
    private String abstractText;
    private String researcherName;
    private Expertise domain;
    private String filePath;
}
