package com.personal_finance.controller;

import com.personal_finance.dto.user.ChangePasswordDto;
import com.personal_finance.dto.user.LoginUserDto;
import com.personal_finance.dto.user.UserRequestDto;
import com.personal_finance.exception.ErrorMessage;
import com.personal_finance.security.JwtToken;
import com.personal_finance.security.JwtUserDetailsService;
import com.personal_finance.service.AuthenticationService;
import com.personal_finance.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<JwtToken> authenticate(@RequestBody LoginUserDto dto) {
        JwtToken token = authenticationService.authenticate(dto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDto userRequestDto) {
        authenticationService.register(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
