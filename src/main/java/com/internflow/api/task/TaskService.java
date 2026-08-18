package com.internflow.api.task;

import com.internflow.api.common.error.ResourceNotFoundException;
import com.internflow.api.internship.Internship;
import com.internflow.api.internship.InternshipRepository;
import org.springframework.stereotype.Service;

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

    public Optional<List<TaskResponse>> findInternshipTasks(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);
        if (internship == null) {
            return Optional.empty();
        }

        List<Task> tasks = tasksRepository.findByInternship(internship);
        List<TaskResponse> taskResponses = tasks.stream().map(this::toTasksResponse).toList();
        return Optional.of(taskResponses);
    }

    public Optional<TaskResponse> createTask(CreateTaskRequest task, Long internshipId) {
        Task newTask = new Task(
                task.title(),
                task.description()
        );
        Internship internship = internshipRepository.findById(internshipId).orElse(null);
        if (internship == null) {
            return Optional.empty();
        }
        newTask.assignInternship(internship);
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
