package com.personal_finance.controller;

import com.personal_finance.dto.user.LoginUserDto;
import com.personal_finance.exception.ErrorMessage;
import com.personal_finance.security.JwtToken;
import com.personal_finance.security.JwtUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto, HttpServletRequest request) {
        log.info("Process of authentication by login {}", loginUserDto.username());

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUserDto.username(), loginUserDto.password());
            authenticationManager.authenticate(authenticationToken);
            JwtToken jwtToken = jwtUserDetailsService.getTokenAuthenticated(loginUserDto.username());
            return ResponseEntity.ok(jwtToken);

        } catch (AuthenticationException e) {
            log.warn("Bad credentials from username {} ", loginUserDto.username());
        }
        return ResponseEntity.badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credenciais Inválidas"));
    }
}
