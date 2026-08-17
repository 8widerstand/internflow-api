package com.internflow.api.task;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
        Optional<TaskResponse> task = this.service.createTask(request, internshipId);
        if (task.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        URI location = URI.create("/internships/" + internshipId + "/tasks");

        return ResponseEntity.created(location).body(task.get());
    }

    @GetMapping("/internships/{internshipId}/tasks")
    ResponseEntity<List<TaskResponse>> findInternshipTasks(@PathVariable Long internshipId) {
        return service.findInternshipTasks(internshipId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/tasks/{taskId}/completed")
    ResponseEntity<TaskResponse> updateTaskCompleted(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskCompletedRequest completed
    ){
        return this.service.updateCompletedTask(taskId, completed)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
}
