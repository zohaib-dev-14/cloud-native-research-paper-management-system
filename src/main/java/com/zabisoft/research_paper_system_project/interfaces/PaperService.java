package com.zabisoft.research_paper_system_project.interfaces;

import com.zabisoft.research_paper_system_project.dto.CreatePaperRequest;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.dto.UpdatePaperRequest;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.response.ApiResponse;

import java.util.UUID;


public interface PaperService {
    PageResponse<PaperResponse> getMyPapers(int page, int size, PaperStatus paperStatus);
    PaperResponse createPaper(CreatePaperRequest request);
    PaperResponse getPaperById(UUID paperId);
    ApiResponse deletePaper(UUID paperId);
    PaperResponse updatePaper(UUID paperId, UpdatePaperRequest updatePaperRequest);
}
