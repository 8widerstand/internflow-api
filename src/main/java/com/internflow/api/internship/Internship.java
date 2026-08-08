package com.internflow.api.internship;

import jakarta.persistence.*;

@Entity
@Table(name = "internships")
public class Internship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private Integer durationInMonths;

    @Enumerated(EnumType.STRING)
    private InternshipStatus status;

    protected Internship() {
    }

    public Internship(String title, String company, Integer durationInMonths) {
        this.title = title;
        this.company = company;
        this.durationInMonths = durationInMonths;
        this.status = InternshipStatus.OPEN;
    }

    public void update(String title, String company, Integer durationInMonths) {
        this.title = title;
        this.company = company;
        this.durationInMonths = durationInMonths;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public InternshipStatus getStatus() {
        return status;
    }
}