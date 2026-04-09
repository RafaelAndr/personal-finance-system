package com.personal_finance.service;

import com.personal_finance.dto.user.LoginUserDto;
import com.personal_finance.dto.user.UserRequestDto;
import com.personal_finance.entity.Users;
import com.personal_finance.exception.UserAlreadyExistsException;
import com.personal_finance.mapper.UserMapper;
import com.personal_finance.repository.UsersRepository;
import com.personal_finance.security.JwtToken;
import com.personal_finance.security.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final UsersService usersService;

    public JwtToken authenticate(LoginUserDto loginUserDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.username(), loginUserDto.password());

        authenticationManager.authenticate(authenticationToken);

        return jwtUserDetailsService.getTokenAuthenticated(loginUserDto.username());
    }

    @Transactional
    public void register(UserRequestDto userRequestDto) {
        usersService.register(userRequestDto);
    }


}
