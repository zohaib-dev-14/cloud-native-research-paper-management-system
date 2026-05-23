package com.zabisoft.research_paper_system_project.repositories;

import com.zabisoft.research_paper_system_project.entities.Assignment;
import com.zabisoft.research_paper_system_project.entities.Paper;
import com.zabisoft.research_paper_system_project.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {


    List<Assignment> findByPaper(Paper paper);

    @Query("""
     SELECT a FROM Assignment a
     JOIN FETCH a.paper p
     JOIN FETCH p.researcher
     JOIN FETCH a.reviewer
     WHERE a.reviewer = :reviewer
     """)

    //* bts of query
    /*
    SELECT a.*, p.*, researcher.*, reviewer.*
FROM assignments a
JOIN papers p
ON a.paper_id = p.id
JOIN users researcher
ON p.researcher_id = researcher.id
JOIN users reviewer
ON a.reviewer_id = reviewer.id
WHERE reviewer.id = ?
  */
    Page<Assignment> findByReviewer(
            @Param("reviewer") User reviewer,
            Pageable pageable
    );


    Optional<Assignment> getAssignmentsById(UUID assignmentId);



    @Query("""
    SELECT a FROM Assignment a
    JOIN FETCH a.paper p
    JOIN FETCH p.researcher
    JOIN FETCH a.assignedBy
    JOIN FETCH a.reviewer
    WHERE a.id = :assignmentId
    """)
    Optional<Assignment> findAssignmentDetail(
            @Param("assignmentId") UUID assignmentId
    );
}
