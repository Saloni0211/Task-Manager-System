package com.taskmanager.backend.controller;

import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.enums.TaskStatus;
import com.taskmanager.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 1. CREATE: Admin Only - Creates a task inside a project and assigns it to a user
    @PostMapping("/project/{projectId}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTask(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestBody Task task) {
        try {
            Task savedTask = taskService.createTask(projectId, userId, task);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating task: " + e.getMessage());
        }
    }

    // 2. READ: Fetches all tasks for a specific project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getTasksByProject(@PathVariable Long projectId) {
        try {
            List<Task> tasks = taskService.getTasksByProject(projectId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching tasks: " + e.getMessage());
        }
    }

    // 3. UPDATE: Service layer checks if the user is an Admin or the Task Owner
    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam String status) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            Task updatedTask = taskService.updateTaskStatus(taskId, taskStatus);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status. Use PENDING, IN_PROGRESS, or COMPLETED.");
        } catch (Exception e) {
            // If the Service throws an "Access Denied" error, it gets caught here!
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // 4. DELETE: Removes a task (SECURED)
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN')") // <--- REMAINS SECURE: Only Admins can delete
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting task: " + e.getMessage());
        }
    }
}