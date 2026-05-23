package com.zabisoft.research_paper_system_project.entities;

import com.zabisoft.research_paper_system_project.enums.Expertise;
import com.zabisoft.research_paper_system_project.enums.PaperStatus;
import com.zabisoft.research_paper_system_project.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "papers")
@EqualsAndHashCode(callSuper = true)
public class Paper extends BaseEntity{

    private String title;

    @Column(columnDefinition = "TEXT")
    private String abstractText;


    private String filePath;



    @Enumerated(EnumType.STRING)
    private PaperStatus paperStatus;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "researcher_id")
    private User researcher;



    @OneToMany(mappedBy = "paper")
    @BatchSize(size = 10)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "paper")
    @BatchSize(size = 10)
    // agar is jagah pe JOINFETCH laga dein to hoga ye k 100 papers and 50 reviews each to 50*100 = 5000 rows jo k practically possible nhn aik baar mein load karna
    // so use batchSize
    private List<Review> reviews;

    @Enumerated(EnumType.STRING)
    private Expertise domain;

    @Column(nullable = false)
    private String normalizedTitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String normalizedAbstractText;
}
