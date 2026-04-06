package com.personal_finance.mapper;

import com.personal_finance.dto.user.UserResponseDto;
import com.personal_finance.dto.user.UserRequestDto;
import com.personal_finance.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users toEntity(UserRequestDto userRequestDto);

    UserResponseDto toDto(Users user);
}
