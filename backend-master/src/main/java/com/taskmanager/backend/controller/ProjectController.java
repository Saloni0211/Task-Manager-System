package com.taskmanager.backend.controller;

import com.taskmanager.backend.entity.Project;
import com.taskmanager.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Ensure this matches your Vercel URL in production
public class ProjectController {

    private final ProjectService projectService;

    // 1. CREATE
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        try {
            projectService.createProject(project);
            return ResponseEntity.status(HttpStatus.CREATED).body("Project created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Backend Error: " + e.getMessage());
        }
    }

    // 2. READ ALL
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    // 3. READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        try {
            Project project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found with id: " + id);
        }
    }

    // 4. DELETE: Restricted to ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting project: " + e.getMessage());
        }
    }
}