package com.personal_finance.controller;

import com.personal_finance.dto.response.UserResponseDto;
import com.personal_finance.dto.resquest.UserRequestDto;
import com.personal_finance.entity.Users;
import com.personal_finance.mapper.UserMapper;
import com.personal_finance.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    @Operation(summary = "Save", description = "Register new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered."),
            @ApiResponse(responseCode = "422", description = "Validation Error"),
            @ApiResponse(responseCode = "409", description = "User already registered"),
    })
    public ResponseEntity<UserResponseDto> create(@RequestBody UserRequestDto userRequestDto){
        Users userCreated = usersService.save(userMapper.toEntity(userRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(userCreated));
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id){
        Users user = usersService.searchById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable UUID id, @RequestBody Users user){
        Users userToUpdatePassword = usersService.editPassword(id, user.getPassword());
        return ResponseEntity.ok(userMapper.toDto(userToUpdatePassword));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(){
        List<Users> users = usersService.findAll();

        List<UserResponseDto> listResponseDto = users.stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(listResponseDto);
    }
}