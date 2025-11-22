package com.myaccess.myaccesswebportal.repository;

import com.myaccess.myaccesswebportal.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}