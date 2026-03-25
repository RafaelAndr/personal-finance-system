package com.personal_finance.controller;

import com.personal_finance.entity.Users;
import com.personal_finance.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping
    public ResponseEntity<Users> create(@RequestBody Users user){
        Users userCreated = usersService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getById(@PathVariable UUID id){
        Users user = usersService.searchById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Users> updatePassword(@PathVariable UUID id, @RequestBody Users user){
        Users userToUpdatePassword = usersService.editPassword(id, user.getPassword());
        return ResponseEntity.ok(userToUpdatePassword);
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAll(){
        List<Users> users = usersService.findAll();
        return ResponseEntity.ok(users);
    }
}
