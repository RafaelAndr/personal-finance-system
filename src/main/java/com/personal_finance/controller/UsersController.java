package com.personal_finance.controller;

import com.personal_finance.dto.user.ChangePasswordDto;
import com.personal_finance.dto.user.UserResponseDto;
import com.personal_finance.entity.Users;
import com.personal_finance.mapper.UserMapper;
import com.personal_finance.service.UsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "It contains all operations related to registering, editing, and reading user information.")
public class UsersController {

    private final UsersService usersService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(){
        return ResponseEntity.ok(usersService.getMe());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id){
        Users user = usersService.searchById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDto dto) {
        usersService.changePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll(){
        List<Users> users = usersService.findAll();

        List<UserResponseDto> listResponseDto = users.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(listResponseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public void promoteToAdmin(@PathVariable UUID id) {
        usersService.promoteToAdmin(id);
    }
}