package com.internflow.api.task;

import com.internflow.api.internship.Internship;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String title;
    private String description;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "internship_id")
    private Internship internship;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    public Task() {
    }

    public void assignInternship(Internship internship){
        this.internship = internship;
    }

    public void updateCompletedTasks(boolean completed) {
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Internship getInternship() {
        return internship;
    }

}
