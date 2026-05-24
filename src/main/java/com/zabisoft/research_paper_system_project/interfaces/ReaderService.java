package com.zabisoft.research_paper_system_project.interfaces;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.ReaderPaperResponse;

import java.util.UUID;

public interface ReaderService {
    PageResponse<ReaderPaperResponse> getAcceptedPapers(
            int page,
            int size,
            Expertise domain
    );

    ReaderPaperResponse getPapersById(
            UUID paperId
    );
}
