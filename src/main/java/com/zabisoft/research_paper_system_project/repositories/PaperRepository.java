package com.zabisoft.research_paper_system_project.repositories;

import com.zabisoft.research_paper_system_project.entities.Paper;
import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaperRepository extends JpaRepository<Paper, UUID> {

    // preload paper + researcher
    // to avoid N+1 problem in lazy fetching
    @Query("""
       SELECT p FROM Paper p
       JOIN FETCH p.researcher
       WHERE p.domain = :domain
       AND p.paperStatus = :paperStatus
    """)
     Page<Paper> findByDomainAndPaperStatus(
            @Param("domain") Expertise domain,
            @Param("paperStatus") PaperStatus paperStatus,
            Pageable pageable
    );

    @Query("""
       SELECT p FROM Paper p
       JOIN FETCH p.researcher r
       WHERE r.id = :researcherId
    """)
    Page<Paper> findByResearcherId(
            @Param("researcherId") UUID researcherId,
            Pageable pageable
    );
}
