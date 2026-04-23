package com.personal_finance.service;

import com.personal_finance.dto.user.ChangePasswordDto;
import com.personal_finance.dto.user.UserRequestDto;
import com.personal_finance.dto.user.UserResponseDto;
import com.personal_finance.entity.Users;
import com.personal_finance.entity.enums.Role;
import com.personal_finance.exception.AccessForbiddenException;
import com.personal_finance.exception.EntityAlreadyExistsException;
import com.personal_finance.mapper.UserMapper;
import com.personal_finance.repository.UsersRepository;
import com.personal_finance.security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private UsersService usersService;

    @Test
    void shouldRegisterUser_WhenDataIsValid(){
        UserRequestDto dto = new UserRequestDto(
                "Rafael",
                "rafael123",
                "123456",
                "123456"
        );

        when(usersRepository.existsByUsername(dto.username())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        usersService.register(dto);

        verify(usersRepository).save(argThat(user ->
                user.getName().equals("Rafael") &&
                        user.getUsername().equals("rafael123") &&
                        user.getPassword().equals("encoded-password") &&
                        user.getRole() == Role.ROLE_CLIENT
        ));
    }

    @Test
    void shouldThrowException_WhenUserAlreadyExists(){
        UserRequestDto dto = new UserRequestDto(
                "Rafael",
                "rafael123",
                "123456",
                "123456"
        );

        when(usersRepository.existsByUsername(dto.username())).thenReturn(true);

        assertThatThrownBy(() -> usersService.register(dto))
                .isInstanceOf(EntityAlreadyExistsException.class);

        verify(usersRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenPasswordsDoNotMatch(){
        UserRequestDto dto = new UserRequestDto(
                "Rafael",
                "rafael123",
                "123456",
                "654321"
        );

        when(usersRepository.existsByUsername(dto.username())).thenReturn(false);

        assertThatThrownBy(() -> usersService.register(dto))
                .isInstanceOf(AccessForbiddenException.class);

        verify(usersRepository, never()).save(any());
    }

    @Test
    void shouldReturnUser_WhenUserExits(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        Users result = usersService.searchById(userId);

        assertThat(result.getId()).isEqualTo(userId);
        verify(usersRepository).findById(userId);
    }

    @Test
    void shouldThrowException_WhenUserDoesNotExist(){
        UUID userId = UUID.randomUUID();

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.searchById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void getMeShouldReturnUserLoggedInInformation(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        UserResponseDto dto = new UserResponseDto("Rafael", "rafaelmax", Role.ROLE_ADMIN);

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDto result = usersService.getMe();

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldChangeUserPassword_WhenPasswordsMatches(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setPassword("encoded-old-password");

        ChangePasswordDto dto = new ChangePasswordDto("1234", "4321");

        when(securityService.getUserLoggedIn()).thenReturn(user);

        when(passwordEncoder.matches("1234", "encoded-old-password")).thenReturn(true);

        when(passwordEncoder.matches("4321", "encoded-old-password")).thenReturn(false);

        when(passwordEncoder.encode("4321")).thenReturn("encoded-new-password");

        usersService.changePassword(dto);

        assertThat(user.getPassword()).isEqualTo("encoded-new-password");

        verify(usersRepository).save(user);
    }

    @Test
    void shouldThrowException_WhenCurrentPasswordIsWrong(){
        Users user = new Users();
        user.setPassword("encoded-old-password");

        ChangePasswordDto dto = new ChangePasswordDto("wrong", "4321");

        when(securityService.getUserLoggedIn()).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded-old-password")).thenReturn(false);

        assertThatThrownBy(() -> usersService.changePassword(dto))
                .isInstanceOf(AccessForbiddenException.class)
                .hasMessage("Current password is incorrect");
    }

    @Test
    void shouldThrowException_WhenNewPasswordIsSameAsCurrent(){
        Users user = new Users();
        user.setPassword("encoded-old-password");

        ChangePasswordDto dto = new ChangePasswordDto("1234", "1234");

        when(securityService.getUserLoggedIn()).thenReturn(user);

        when(passwordEncoder.matches("1234", "encoded-old-password")).thenReturn(true);

        assertThatThrownBy(() -> usersService.changePassword(dto))
                .isInstanceOf(AccessForbiddenException.class)
                .hasMessage("New password cannot be the same as the current password");
    }

    @Test
    void shouldFindAllUsers(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        List<Users> users = List.of(user);

        when(usersRepository.findAll()).thenReturn(users);

        List<Users> result = usersService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(user);

        verify(usersRepository).findAll();
    }

    @Test
    void shouldSearchByUsername_WhenUserExists(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("Rafael");

        when(usersRepository.findByUsername("Rafael")).thenReturn(Optional.of(user));

        Users result = usersService.searchByUsername("Rafael");

        assertThat(result.getUsername()).isEqualTo("Rafael");
        verify(usersRepository).findByUsername("Rafael");
    }

    @Test
    void searchByUsernameShouldThrowException_WhenUserDoesNotExist(){
        when(usersRepository.findByUsername("Rafael"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.searchByUsername("Rafael"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteUser_WhenUserExists(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        usersService.deleteUser(userId);

        verify(usersRepository).delete(user);
    }

    @Test
    void deleteUserShouldThrowException_WhenUserDoNotExists(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.deleteUser(userId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldPromoteUserToAdmin_WhenUserExists(){
        Users user = new Users();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setRole(Role.ROLE_CLIENT);

        assertThat(user.getRole()).isEqualTo(Role.ROLE_CLIENT);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        usersService.promoteToAdmin(userId);

        verify(usersRepository).findById(userId);
        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
        verify(usersRepository).save(user);
    }

    @Test
    void promoteUserShouldThrowException_WhenUserNotFound(){
        UUID userId = UUID.randomUUID();

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.promoteToAdmin(userId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(usersRepository, never()).save(any());
    }
}
