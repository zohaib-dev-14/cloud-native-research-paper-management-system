package com.zabisoft.research_paper_system_project.service.readerServiceImplementation;

import com.zabisoft.research_paper_system_project.entities.Paper;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.interfaces.AuthenticatedUserService;
import com.zabisoft.research_paper_system_project.interfaces.ReaderService;
import com.zabisoft.research_paper_system_project.repositories.PaperRepository;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.ReaderPaperResponse;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final PaperRepository paperRepository;
    private final AuthenticatedUserService authenticatedUserService;
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReaderPaperResponse> getAcceptedPapers(int page, int size, Expertise domain) {


        Pageable pageable = PageRequest.of(page, size);

        Page<Paper> papers;

        if (domain == null) {
            papers = paperRepository.findByPaperStatus(
                    PaperStatus.ACCEPTED,
                    pageable
            );
        } else {
            papers = paperRepository.findByDomainAndPaperStatus(
                    domain,
                    PaperStatus.ACCEPTED,
                    pageable
            );
        }

        List<ReaderPaperResponse> content = papers.getContent().stream()
                .map(this::mapToReaderPaperResponse)
                                .toList();

        return mapToPageResponseRPR(content, papers);
    }


    @Override
    @Transactional(readOnly = true)
    public ReaderPaperResponse getPapersById(UUID paperId) {
        Paper paper = paperRepository.findPaperById(paperId).orElseThrow(
                () -> new RuntimeException("Paper not found")
        );

        if (paper.getPaperStatus() != PaperStatus.ACCEPTED) {
            throw new RuntimeException("Paper not available");
        }

        return mapToReaderPaperResponse(paper);
    }


    private ReaderPaperResponse mapToReaderPaperResponse(Paper paper) {
        return ReaderPaperResponse
                .builder()
                .paperId(paper.getId())
                .title(paper.getTitle())
                .abstractText(paper.getAbstractText())
                .researcherName(paper.getResearcher().getName())
                .domain(paper.getDomain())
                .filePath(paper.getFilePath())
                .build();
    }

    private PageResponse<ReaderPaperResponse> mapToPageResponseRPR(
            List<ReaderPaperResponse> content,
            Page<Paper> papers
    ) {
        return PageResponse.<ReaderPaperResponse>builder()
                .content(content)
                .page(papers.getNumber())
                .size(papers.getSize())
                .totalElements(papers.getTotalElements())
                .totalPages(papers.getTotalPages())
                .first(papers.isFirst())
                .last(papers.isLast())
                .build();
    }
}
