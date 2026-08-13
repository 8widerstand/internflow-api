package com.internflow.api.student;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String university;
    private LocalDate birthDate;

    protected Student() {
    }

    public Student(String firstName, String lastName, String university, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.university = university;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUniversity() {
        return university;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
}
