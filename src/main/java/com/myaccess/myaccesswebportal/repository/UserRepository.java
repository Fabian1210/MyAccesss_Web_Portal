package com.myaccess.myaccesswebportal.repository;

import com.myaccess.myaccesswebportal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByEmailContainingIgnoreCase(String emailPart);
}