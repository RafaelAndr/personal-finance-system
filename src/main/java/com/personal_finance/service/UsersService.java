package com.personal_finance.service;

import com.personal_finance.entity.Users;
import com.personal_finance.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public Users save(Users user) {
        return usersRepository.save(user);
    }
}
