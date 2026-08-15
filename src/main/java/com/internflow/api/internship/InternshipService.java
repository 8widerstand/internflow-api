package com.internflow.api.internship;

import com.internflow.api.student.Student;
import com.internflow.api.student.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final StudentRepository studentRepository;

    public InternshipService(InternshipRepository internshipRepository, StudentRepository studentRepository) {
        this.internshipRepository = internshipRepository;
        this.studentRepository = studentRepository;
    }

    public Page<InternshipResponse> findAllInternships(
            InternshipStatus status,
            String company,
            Pageable pageable
    ) {
        Page<Internship> internships;
        if (status == null && company == null) {
            internships = internshipRepository.findAll(pageable);
        } else if (status != null && company == null) {
            internships = internshipRepository.findByStatus(status, pageable);
        } else if (status == null && company != null) {
            internships = internshipRepository.findByCompanyContainingIgnoreCase(company, pageable);
        } else {
            internships = internshipRepository.findByStatusAndCompanyContainingIgnoreCase(status, company, pageable);
        }
        return internships.map(this::toInternshipResponse);
    }

    public Optional<InternshipResponse> findInternshipById(Long id) {
        return internshipRepository.findById(id)
                .map(this::toInternshipResponse);
    }

    public InternshipResponse create(CreateInternshipRequest request) {
        Internship internship = new Internship(
                request.title(),
                request.company(),
                request.durationInMonths()
        );

        Internship savedInternship = internshipRepository.save(internship);

        return toInternshipResponse(savedInternship);

    }

    public Optional<InternshipResponse> update(Long id, CreateInternshipRequest request) {
        Optional<Internship> internshipOptional = internshipRepository.findById(id);
        if (internshipOptional.isEmpty()) {
            return Optional.empty();
        }
        Internship internship = internshipOptional.get();
        internship.update(request.title(), request.company(), request.durationInMonths());
        Internship savedInternship = internshipRepository.save(internship);
        return Optional.of(toInternshipResponse(savedInternship));
    }

    public Optional<InternshipResponse> assignInternship(Long internshipId, Long studentId) {
        Optional<Internship> internshipOptional = internshipRepository.findById(internshipId);
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (internshipOptional.isEmpty() || studentOptional.isEmpty()) {
            return Optional.empty();
        }
        Internship internship = internshipOptional.get();
        Student student = studentOptional.get();
        internship.assignStudent(student);
        Internship savedInternship = internshipRepository.save(internship);
        return Optional.of(toInternshipResponse(savedInternship));
    }

    public Optional<InternshipResponse> updateStatus(Long id, InternshipStatus status) {
        Optional<Internship> internshipOptional = internshipRepository.findById(id);
        if (internshipOptional.isEmpty()) {
            return Optional.empty();
        }
        Internship internship = internshipOptional.get();
        internship.updateStatus(status);
        Internship savedInternship = internshipRepository.save(internship);
        return Optional.of(toInternshipResponse(savedInternship));
    }

    public boolean delete(Long id) {
        if (!internshipRepository.existsById(id)) {
            return false;
        }

        internshipRepository.deleteById(id);
        return true;
    }


    InternshipResponse toInternshipResponse(Internship internship) {
        Student student = internship.getStudent();
        Long studentId = null;
        if (student != null) {
            studentId = student.getId();
        }
        return new InternshipResponse(
                internship.getId(),
                internship.getTitle(),
                internship.getCompany(),
                internship.getDurationInMonths(),
                internship.getStatus(),
                studentId
        );
    }
}
