package com.example.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanager.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
