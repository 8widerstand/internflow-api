package com.internflow.api.internship;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    Page<Internship> findByStatus(InternshipStatus status, Pageable pageable);

    Page<Internship> findByStatusAndCompanyContainingIgnoreCase(InternshipStatus status, String company, Pageable pageable);

    Page<Internship> findByCompanyContainingIgnoreCase(String company, Pageable pageable);
}
