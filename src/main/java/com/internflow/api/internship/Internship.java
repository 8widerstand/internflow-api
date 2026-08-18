package com.internflow.api.internship;

import com.internflow.api.mentor.Mentor;
import com.internflow.api.student.Student;
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

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    Mentor mentor;

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

    public void updateStatus(InternshipStatus status) {
        this.status = status;
    }

    public void assignStudent(Student student) {
        this.student = student;
    }

    public void assignMentor(Mentor mentor) {this.mentor = mentor; }

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

    public Student getStudent() {
        return student;
    }

    public Mentor getMentor() {
        return mentor;
    }
}