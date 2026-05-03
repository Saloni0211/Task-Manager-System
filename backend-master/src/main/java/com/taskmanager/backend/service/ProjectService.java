package com.taskmanager.backend.service;

import com.taskmanager.backend.entity.Project;
import com.taskmanager.backend.entity.User;
import com.taskmanager.backend.repository.ProjectRepository;
import com.taskmanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    // --- NEW: Inject the UserRepository to find members ---
    private final UserRepository userRepository;

    // 1. CREATE
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    // 2. READ ALL
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // 3. READ ONE
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    // 4. DELETE (Secured: Admin Override + Owner IDOR Check)
    public void deleteProject(Long id) {
        Project project = getProjectById(id);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Check if the current user has Admin privileges
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ADMIN"));

        // Allow deletion IF the user is an Admin OR the user is the creator of the project.
        if (!isAdmin && (project.getUser() == null || !project.getUser().getEmail().equals(currentUsername))) {
            throw new RuntimeException("Unauthorized: You do not have permission to delete this project");
        }

        projectRepository.delete(project);
    }

    // ==========================================
    // 5. MEMBER MANAGEMENT (Admin Features)
    // ==========================================

    public Project addMemberToProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId); // Reuse our existing read method

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Prevent duplicate entries in the Many-to-Many table
        if (!project.getMembers().contains(user)) {
            project.getMembers().add(user);
            return projectRepository.save(project);
        }

        return project; // User was already a member
    }

    public Project removeMemberFromProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Remove the user from the list
        project.getMembers().remove(user);
        return projectRepository.save(project);
    }
}