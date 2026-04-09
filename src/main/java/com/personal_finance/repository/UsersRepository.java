package com.personal_finance.repository;

import com.personal_finance.entity.Users;
import com.personal_finance.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByUsername(String username);

    @Query("select u.role from Users u where u.username like :username")
    Role findRoleByUsername(String username);

    boolean existsByUsername(String username);
}
