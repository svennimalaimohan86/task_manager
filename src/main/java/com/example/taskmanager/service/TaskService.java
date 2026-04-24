package com.example.taskmanager.service;

import com.example.taskmanager.entity.AuditLog;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.AuditLogRepository;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AuditLogRepository auditLogRepository;

    // ✅ Create Task
    public Task create(Task task, User user) {

        task.setCreatedBy(user.getId());
        task.setOrganization(user.getOrganization());

        Task saved = taskRepository.save(task);

        saveAudit("CREATE", user, saved);

        return saved;
    }

    // ✅ Get Tasks (Tenant Isolation + RBAC)
    public List<Task> getTasks(User user) {

        List<Task> tasks = taskRepository.findByOrganizationId(
                user.getOrganization().getId()
        );

        // 👥 RBAC: MEMBER → only own tasks
        if (!user.getRole().equals("ADMIN")) {
            tasks = tasks.stream()
                    .filter(task -> task.getCreatedBy().equals(user.getId()))
                    .toList();
        }

        return tasks;
    }

    // ✅ Update Task (RBAC enforced)
    public Task update(Long taskId, Task updatedTask, User user) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 🔒 Tenant check
        if (!task.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new RuntimeException("Access denied");
        }

        // 🔒 RBAC check
        if (!user.getRole().equals("ADMIN") &&
            !task.getCreatedBy().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());

        Task saved = taskRepository.save(task);

        saveAudit("UPDATE", user, saved);

        return saved;
    }

    // ✅ Delete Task (RBAC enforced)
    public void delete(Long taskId, User user) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 🔒 Tenant check
        if (!task.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new RuntimeException("Access denied");
        }

        // 🔒 RBAC check
        if (!user.getRole().equals("ADMIN") &&
            !task.getCreatedBy().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        taskRepository.delete(task);

        saveAudit("DELETE", user, task);
    }

    // 📜 Audit helper
    private void saveAudit(String action, User user, Task task) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setUserId(user.getId());
        log.setTaskId(task.getId());
        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}