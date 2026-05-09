package com.zabisoft.research_paper_system_project.repositories;

import com.zabisoft.research_paper_system_project.entities.Paper;
import com.zabisoft.research_paper_system_project.entities.Review;
import com.zabisoft.research_paper_system_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    // Paper specific Reviews
    @Query("""
       SELECT r FROM Review r
       JOIN FETCH r.reviewer
       JOIN FETCH r.paper
       WHERE r.paper = :paper
    """)
    List<Review> findByPaper(
            @Param("paper") Paper paper
    );


    // Reviewer specific reviews (needed for reviewer dashboard)
    @Query("""
       SELECT r FROM Review r
       JOIN FETCH r.reviewer
       JOIN FETCH r.paper
       WHERE r.reviewer = :reviewer
    """)
    List<Review> findByReviewer(@Param("reviewer") User reviewer);
}
