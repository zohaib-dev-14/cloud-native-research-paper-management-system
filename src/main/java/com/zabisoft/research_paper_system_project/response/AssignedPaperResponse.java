package com.zabisoft.research_paper_system_project.response;

import com.zabisoft.research_paper_system_project.enums.AssignmentStatus;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedPaperResponse {

    private UUID assignmentId;

    private UUID paperId;

    private String paperTitle;

    private String researcherName;

    private String assignedBy;

    private AssignmentStatus assignmentStatus;

    private PaperStatus paperStatus;
}
