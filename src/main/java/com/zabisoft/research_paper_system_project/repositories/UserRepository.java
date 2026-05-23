package com.zabisoft.research_paper_system_project.repositories;

import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
     SELECT u FROM User u
     JOIN u.expertises e
     WHERE u.role = :role
     AND e = :expertise
     """)
    Page<User> findReviewerByExpertises(
            @Param("role")
            Role role,
            @Param("expertise")
            Expertise expertise,
            Pageable pageable
    );

    Page<User> findReviewerByRole(Role role, Pageable pageable);

    Optional<User> findReviewerById(UUID reviewerId);
}
