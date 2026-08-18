package com.internflow.api.internship;

import com.internflow.api.common.error.ResourceNotFoundException;
import com.internflow.api.mentor.Mentor;
import com.internflow.api.mentor.MentorRepository;
import com.internflow.api.student.Student;
import com.internflow.api.student.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InternshipService {
    private final MentorRepository mentorRepository;
    private final InternshipRepository internshipRepository;
    private final StudentRepository studentRepository;

    public InternshipService(MentorRepository mentorRepository, InternshipRepository internshipRepository, StudentRepository studentRepository) {
        this.mentorRepository = mentorRepository;
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

    public InternshipResponse findInternshipById(Long id) {
        return internshipRepository.findById(id)
                .map(this::toInternshipResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + id));
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

    public void delete(Long id) {
        if (!internshipRepository.existsById(id)) {
           throw new ResourceNotFoundException("Internship not found with id: " + id);
        }
        internshipRepository.deleteById(id);
    }

    public Optional<InternshipResponse> assignMentorToInternship(Long internshipId, Long mentorId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);
        if (internship == null) return Optional.empty();

        Mentor mentor = mentorRepository.findById(mentorId).orElse(null);
        if (mentor == null) return Optional.empty();

        internship.assignMentor(mentor);
        Internship savedInternship = internshipRepository.save(internship);
        return Optional.of(toInternshipResponse(savedInternship));
    }

    public InternshipResponse toInternshipResponse(Internship internship) {
        Student student = internship.getStudent();
        Mentor mentor = internship.getMentor();
        Long mentorId = null;
        Long studentId = null;

        if (student != null) studentId = student.getId();
        if (mentor != null) mentorId = mentor.getId();

        return new InternshipResponse(
                internship.getId(),
                internship.getTitle(),
                internship.getCompany(),
                internship.getDurationInMonths(),
                internship.getStatus(),
                studentId,
                mentorId
        );
    }
}
