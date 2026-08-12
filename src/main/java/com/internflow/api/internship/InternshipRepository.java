package com.internflow.api.internship;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByStatus(InternshipStatus status);
    List<Internship> findByCompany(String company);
    List<Internship> findByStatusAndCompany(InternshipStatus status, String company);
}
