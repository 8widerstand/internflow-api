package com.internflow.api.task;

import com.internflow.api.common.error.ResourceNotFoundException;
import com.internflow.api.internship.Internship;
import com.internflow.api.internship.InternshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository tasksRepository;
    private final InternshipRepository internshipRepository;

    public TaskService(TaskRepository tasksRepository, InternshipRepository internshipRepository) {
        this.tasksRepository = tasksRepository;
        this.internshipRepository = internshipRepository;
    }

    /**
     * Keeps the persistence context open while the lazy tasks collection is read.
     * Calling getTasks() returns the lazy collection, while stream() triggers its
     * loading from the database before the tasks are mapped to DTOs.
     */
    @Transactional(readOnly = true)
    public Optional<List<TaskResponse>> findInternshipTasks(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);
        if (internship == null) {
            return Optional.empty();
        }

        List<Task> tasks = internship.getTasks();
        List<TaskResponse> taskResponses = tasks.stream().map(this::toTasksResponse).toList();
        return Optional.of(taskResponses);
    }

    @Transactional
    public Optional<TaskResponse> createTask(CreateTaskRequest task, Long internshipId) {
        Task newTask = new Task(
                task.title(),
                task.description()
        );
        Internship internship = internshipRepository.findById(internshipId).orElse(null);
        if (internship == null) {
            return Optional.empty();
        }
        internship.addTask(newTask);
        Task savedTask = tasksRepository.save(newTask);
        return Optional.of(toTasksResponse(savedTask));
    }

    public TaskResponse updateCompletedTask(Long taskId, UpdateTaskCompletedRequest request) {
        Task task = tasksRepository.findById(taskId)
                        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.updateCompletedTasks(request.completed());
        Task savedTask = tasksRepository.save(task);
        return toTasksResponse(savedTask);
    }

    private TaskResponse toTasksResponse(Task task) {
        Internship internship = task.getInternship();
        Long internshipId = null;
        if (internship != null) {
            internshipId = internship.getId();
        }
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                internshipId
        );
    }
}
