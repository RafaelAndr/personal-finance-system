package com.personal_finance.repository;

import com.personal_finance.entity.Account;
import com.personal_finance.entity.Income;
import com.personal_finance.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, UUID> {

    List<Income> findByUser(Users user);
    List<Income> findByAccount(Account account);
}
