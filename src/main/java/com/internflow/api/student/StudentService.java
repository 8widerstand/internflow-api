package com.internflow.api.student;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Optional<StudentResponse> findStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::toResponse);
    }

    public List<StudentResponse> findAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentResponse create(CreateStudentRequest request) {
        Student student = new Student(
                request.firstName(),
                request.lastName(),
                request.university(),
                request.birthDate()
        );
        Student savedStudent = studentRepository.save(student);
        return toResponse(savedStudent);
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getUniversity(),
                student.getBirthDate()
        );
    }
}
