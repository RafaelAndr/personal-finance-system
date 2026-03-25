package com.personal_finance.mapper;

import com.personal_finance.dto.response.PatientResponseDto;
import com.personal_finance.dto.resquest.PatientRequestDto;
import com.personal_finance.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toEntity(PatientRequestDto patientRequestDto);

    PatientResponseDto toDto(Patient patient);
}
