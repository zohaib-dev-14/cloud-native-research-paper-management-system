package com.zabisoft.research_paper_system_project.service.paperServiceImplementation;

import com.zabisoft.research_paper_system_project.dto.CreatePaperRequest;
import com.zabisoft.research_paper_system_project.response.PageResponse;
import com.zabisoft.research_paper_system_project.response.PaperResponse;
import com.zabisoft.research_paper_system_project.dto.UpdatePaperRequest;
import com.zabisoft.research_paper_system_project.entities.Paper;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.enums.ProcessingStatus;
import com.zabisoft.research_paper_system_project.enums.Role;
import com.zabisoft.research_paper_system_project.interfaces.AuthenticatedUserService;
import com.zabisoft.research_paper_system_project.interfaces.FileStorageService;
import com.zabisoft.research_paper_system_project.interfaces.PaperService;
import com.zabisoft.research_paper_system_project.repositories.PaperRepository;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class PaperServiceImpl implements PaperService {
    private final FileStorageService fileStorageService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PaperRepository paperRepository;
    @Transactional
    @Override
    public PaperResponse createPaper(CreatePaperRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        // role validation
        if (currentUser.getRole() != Role.RESEARCHER) {
            throw new RuntimeException(
                    "Only researchers can upload papers"
            );
        }
        // normalize values
        String normalizedTitle = request.getTitle()
                        .trim()
                        .toLowerCase();
        String normalizedAbstract =
                request.getAbstractText()
                        .trim()
                        .toLowerCase();
        // duplicate title check
        boolean titleExists = paperRepository.existsByNormalizedTitle(normalizedTitle);
        if (titleExists) {
            throw new RuntimeException(
                    "Duplicate title detected"
            );
        }
        // duplicate abstract check
        boolean abstractExists = paperRepository.existsByNormalizedAbstractText(normalizedAbstract);
        if (abstractExists) {
            throw new RuntimeException(
                    "Duplicate abstract detected"
            );
        }
        String filePath = null;
        try {
            // store file
            filePath = fileStorageService.storeFile(request.getMultipartFile());
            // create entity
            Paper paper = new Paper();
            paper.setTitle(request.getTitle().trim());
            paper.setNormalizedTitle(normalizedTitle);
            paper.setAbstractText(request.getAbstractText().trim());
            paper.setNormalizedAbstractText(normalizedAbstract);
            paper.setDomain(request.getDomain());
            paper.setFilePath(filePath);
            paper.setResearcher(currentUser);
            paper.setPaperStatus(PaperStatus.PENDING_ADMIN_REVIEW);
            paper.setProcessingStatus(ProcessingStatus.UPLOADED);
            Paper savedPaper = paperRepository.save(paper);
            return mapToResponse(savedPaper);
        } catch (Exception e) {
            // cleanup orphan file
            if (filePath != null) {
                fileStorageService.deleteFile(filePath);
            }
            throw e;
        }
    }

    public PaperResponse mapToResponse(Paper paper) {
        return PaperResponse.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .abstractText(paper.getAbstractText())
                .filePath(paper.getFilePath())
                .researcherName(paper.getResearcher().getName())
                .domain(paper.getDomain())
                .paperStatus(paper.getPaperStatus())
                .processingStatus(paper.getProcessingStatus())
                .build();
    }


    @Override
    public PageResponse<PaperResponse> getMyPapers(int page, int size, PaperStatus status) {
        User currentUser = authenticatedUserService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Paper> papers;

        if (status == null) {

            papers =
                    paperRepository
                            .findByResearcherId(
                                    currentUser.getId(),
                                    pageable
                            );

        } else {

            papers =
                    paperRepository
                            .findByResearcherIdAndPaperStatus(
                                    currentUser.getId(),
                                    status,
                                    pageable
                            );
        }
        List<PaperResponse> content =
                papers.getContent()
                                     .stream()
                                     .map(this::mapToResponse)
                                     .toList();


        return PageResponse.<PaperResponse>builder()
                .content(content)
                .page(papers.getNumber())
                .size(papers.getSize())
                .totalElements(papers.getTotalElements())
                .totalPages(papers.getTotalPages())
                .first(papers.isFirst())
                .last(papers.isLast())
                .build();

    }

    @Override
    @Transactional(readOnly = true)
    public PaperResponse getPaperById(UUID paperId) {
        User currentUser = authenticatedUserService.getCurrentUser();

        Paper paper = paperRepository.findPaperById(paperId).orElseThrow(
                () -> new RuntimeException("No paper found")
        );

        // check whether a researcherId from paper repo is equals to id with currentUser
        boolean isOwner = paper.getResearcher()
                .getId()
                .equals(currentUser.getId());
        // check whether a role of current user should not be ADMIN
        boolean isAdmin = currentUser.getRole()
                .name()
                .equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You are not authorized to access this paper");
        }

        return mapToResponse(paper);
    }

    @Override
    @Transactional
    public ApiResponse deletePaper(UUID paperId) {
        User user = authenticatedUserService.getCurrentUser();

        Paper paper = paperRepository.findById(paperId).orElseThrow(
                () -> new RuntimeException("Paper Not Found")
        );

        boolean isOwner = paper.getResearcher().getId().equals(
                user.getId()
        );

        if (!isOwner) {
            throw new RuntimeException("You are not authorized to delete this paper");
        }

        if (paper.getPaperStatus() != PaperStatus.PENDING_ADMIN_REVIEW) {
            throw new RuntimeException("Paper cannot be deleted after workflow started");
        }

        fileStorageService.deleteFile(paper.getFilePath());

        paperRepository.delete(paper);

        return new ApiResponse(
                true,
                "Paper deleted successfully"
        );
    }

    @Transactional
    public PaperResponse updatePaper(
            UUID paperId,
            UpdatePaperRequest updatePaperRequest
    ) {

        User currentUser = authenticatedUserService.getCurrentUser();

        Paper paper = paperRepository.findById(paperId).orElseThrow(
                () -> new RuntimeException("Paper Not Found")
        );

        boolean isOwner = paper.getResearcher().getId().equals(currentUser.getId());

        if (!isOwner) {
            throw new RuntimeException("You are not authorized to update this paper");
        }

        if (paper.getPaperStatus() != PaperStatus.PENDING_ADMIN_REVIEW) {
            throw new RuntimeException("Paper cannot be updated after the workflow started");
        }

        paper.setTitle(updatePaperRequest.getTitle());
        paper.setAbstractText(updatePaperRequest.getAbstractText());
        paper.setDomain(updatePaperRequest.getDomain());


        MultipartFile file = updatePaperRequest.getMultipartFile();

        if (file != null && !file.isEmpty()) {
            fileStorageService.deleteFile(
                    paper.getFilePath()
            );

            String newFilePath = fileStorageService.storeFile(file);
            paper.setFilePath(newFilePath);
        }

        Paper updatedPaper = paperRepository.save(paper);
        return mapToResponse(updatedPaper);
    }
}
