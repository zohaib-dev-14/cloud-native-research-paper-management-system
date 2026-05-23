package com.zabisoft.research_paper_system_project.dto;

import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaperDecisionRequest {

    private PaperStatus paperStatus;
}
