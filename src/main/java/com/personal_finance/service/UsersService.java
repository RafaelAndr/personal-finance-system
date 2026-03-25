package com.personal_finance.service;

import com.personal_finance.entity.Users;
import com.personal_finance.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Transactional
    public Users save(Users user) {
        return usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Users searchById(UUID id) {
        return usersRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    @Transactional
    public Users editPassword(UUID id, String password) {
        Users user = searchById(id);
        user.setPassword(password);
        return user;
    }

    @Transactional(readOnly = true)
    public List<Users> findAll() {
        return usersRepository.findAll();
    }
}
