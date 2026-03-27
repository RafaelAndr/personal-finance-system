package com.personal_finance.service;

import com.personal_finance.dto.response.UserResponseDto;
import com.personal_finance.dto.resquest.UserRequestDto;
import com.personal_finance.entity.Users;
import com.personal_finance.entity.enums.Role;
import com.personal_finance.mapper.UserMapper;
import com.personal_finance.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional //só precisa usar transaction quando há mais de uma operação no metodo
    public Users save(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    @Transactional(readOnly = true)
    public Users searchByUsername(String username) {
        return usersRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Username '%s' not found", username))
        );
    }

    @Transactional(readOnly = true)
    public Role searchRoleByUsername(String username) {
        return usersRepository.findRoleByUsername(username);
    }
}
