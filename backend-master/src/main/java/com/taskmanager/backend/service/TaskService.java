package com.taskmanager.backend.service;

import com.taskmanager.backend.entity.Project;
import com.taskmanager.backend.entity.Task;
import com.taskmanager.backend.entity.User;
import com.taskmanager.backend.enums.TaskStatus;
import com.taskmanager.backend.repository.ProjectRepository;
import com.taskmanager.backend.repository.TaskRepository;
import com.taskmanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 1. ADMIN ONLY: Create a task and assign it to a specific user
    public Task createTask(Long projectId, Long assignedToId, Task taskDetails) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User assignee = userRepository.findById(assignedToId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        taskDetails.setProject(project);
        taskDetails.setAssignedTo(assignee);
        taskDetails.setStatus(TaskStatus.PENDING); // Default status for new tasks

        return taskRepository.save(taskDetails);
    }

    // 2. EVERYONE: Get all tasks for a specific project
    public List<Task> getTasksByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return project.getTasks();
    }

    // 3. MEMBER/ADMIN: Update task status (e.g., Pending -> Completed)
    public Task updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Grab the currently logged-in user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        // SECURITY CHECK: Block if not an Admin AND not the person assigned to the task
        if (!isAdmin && (task.getAssignedTo() == null || !task.getAssignedTo().getEmail().equals(currentUsername))) {
            throw new RuntimeException("Access Denied: You can only update tasks assigned to you.");
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    // 4. ADMIN ONLY: Delete a task (Incorporating your fix securely!)
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // SECURITY CHECK: Only Admins should be able to delete tasks permanently
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Access Denied: Only Admins can delete tasks.");
        }

        taskRepository.delete(task);
    }
}