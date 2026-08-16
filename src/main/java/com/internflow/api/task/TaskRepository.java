package com.internflow.api.task;

import com.internflow.api.internship.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByInternship(Internship internship);
}
