package com.personal_finance.mapper;

import com.personal_finance.dto.resquest.AddressRequestDto;
import com.personal_finance.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressRequestDto addressRequestDto);

}
