package com.myaccess.myaccesswebportal.repository;

import com.myaccess.myaccesswebportal.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}