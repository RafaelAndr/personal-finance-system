package com.personal_finance.service;

import com.personal_finance.entity.Users;
import com.personal_finance.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Transactional
    public Users save(Users user) {
        return usersRepository.save(user);
    }
}
