package com.personal_finance.repository;

import com.personal_finance.entity.Account;
import com.personal_finance.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUser(Users userLoggedIn);
}
