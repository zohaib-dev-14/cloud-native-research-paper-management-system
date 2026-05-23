package com.zabisoft.research_paper_system_project.entities;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;


    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean isVerified = false;
    private boolean isActive = true;

    @OneToMany(mappedBy = "researcher")
    @BatchSize(size = 10)
    private List<Paper> papers;


    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<Expertise> expertises = new HashSet<>();
}
