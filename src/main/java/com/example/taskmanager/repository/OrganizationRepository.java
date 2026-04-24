package com.example.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanager.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {}
