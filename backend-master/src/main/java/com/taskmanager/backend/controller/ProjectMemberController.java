package com.taskmanager.backend.controller;

import com.taskmanager.backend.entity.Project;
import com.taskmanager.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectService projectService;

    // Only Admins can add members
    @PostMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> addMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {

        Project updatedProject = projectService.addMemberToProject(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }

    // Only Admins can remove members
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {

        Project updatedProject = projectService.removeMemberFromProject(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }
}