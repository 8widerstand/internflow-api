package com.internflow.api.task;

import com.internflow.api.common.error.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(
        name = "Tasks",
        description = "Create, retrieve and update tasks"
)
@RestController
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(summary = "Create a task for an internship")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "400", description = "Invalid task data",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Internship not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
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
