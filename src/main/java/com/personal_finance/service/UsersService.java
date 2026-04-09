package com.personal_finance.service;

import com.personal_finance.dto.user.ChangePasswordDto;
import com.personal_finance.dto.user.UserRequestDto;
import com.personal_finance.dto.user.UserResponseDto;
import com.personal_finance.entity.Users;
import com.personal_finance.entity.enums.Role;
import com.personal_finance.exception.AccessForbiddenException;
import com.personal_finance.exception.UserAlreadyExistsException;
import com.personal_finance.mapper.UserMapper;
import com.personal_finance.repository.UsersRepository;
import com.personal_finance.security.SecurityService;
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
    private final SecurityService securityService;

    @Transactional
    public Users register(UserRequestDto userRequestDto) {

        if (usersRepository.existsByUsername(userRequestDto.username())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        if (!userRequestDto.password().equals(userRequestDto.confirmPassword())) {
            throw new AccessForbiddenException("Passwords do not match");
        }

        Users user = new Users();
        user.setName(userRequestDto.name());
        user.setUsername(userRequestDto.username());
        user.setPassword(passwordEncoder.encode(userRequestDto.password()));
        user.setRole(userRequestDto.role());

        return usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Users searchById(UUID id) {
        return usersRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDto getMe(){
        Users userLoggedIn = securityService.getUserLoggedIn();
        return userMapper.toDto(usersRepository.findById(userLoggedIn.getId()).orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    @Transactional
    public Users editPassword(UUID id, String password) {
        Users user = searchById(id);
        user.setPassword(password);
        return user;
    }

    @Transactional
    public void changePassword(ChangePasswordDto dto) {

        Users user = securityService.getUserLoggedIn();

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new AccessForbiddenException("Current password is incorrect");
        }

        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new AccessForbiddenException("New password cannot be the same as the current password");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));

        usersRepository.save(user);
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

    public void deleteUser(UUID id) {
        usersRepository.deleteById(id);
    }
}
