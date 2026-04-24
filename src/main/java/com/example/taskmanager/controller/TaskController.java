package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // ✅ Create Task
    @PostMapping
    public ResponseEntity<Task> createTask(HttpServletRequest request, @RequestBody Task task) {

        User user = (User) request.getAttribute("user");

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Task createdTask = taskService.create(task, user);
        return ResponseEntity.ok(createdTask);
    }

    // ✅ Get Tasks
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(HttpServletRequest request) {

        User user = (User) request.getAttribute("user");

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        List<Task> tasks = taskService.getTasks(user);
        return ResponseEntity.ok(tasks);
    }
@PutMapping("/{id}")
public ResponseEntity<Task> updateTask(
        @PathVariable Long id,
        @RequestBody Task task,
        HttpServletRequest request) {

    User user = (User) request.getAttribute("user");

    Task updatedTask = taskService.update(id, task, user);
    return ResponseEntity.ok(updatedTask);
}
@DeleteMapping("/{id}")
public ResponseEntity<String> deleteTask(
        @PathVariable Long id,
        HttpServletRequest request) {

    User user = (User) request.getAttribute("user");

    taskService.delete(id, user);
    return ResponseEntity.ok("Task deleted successfully");
}
}