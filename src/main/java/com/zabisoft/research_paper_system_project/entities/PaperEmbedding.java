package com.zabisoft.research_paper_system_project.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "paper_embeddings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaperEmbedding extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @Column(columnDefinition = "TEXT")
    private String chunkText;

    private Integer chunkIndex;

    private Integer pageNumber;
}
