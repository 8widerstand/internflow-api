package com.internflow.api.task;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping("/internships/{internshipId}/tasks")
    ResponseEntity<TaskResponse> createTask(
            @Valid @PathVariable Long internshipId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        TaskResponse task = this.service.createTask(request, internshipId);
        URI location = URI.create("/internships/" + internshipId + "/tasks");

        return ResponseEntity.created(location).body(task);
    }

    @GetMapping("/internships/{internshipId}/tasks")
    List<TaskResponse> findInternshipTasks(@PathVariable Long internshipId) {
        return service.findInternshipTasks(internshipId);
    }

    @PatchMapping("/tasks/{taskId}/completed")
    TaskResponse  updateTaskCompleted(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskCompletedRequest completed
    ){
        return this.service.updateCompletedTask(taskId, completed);

    }
}
