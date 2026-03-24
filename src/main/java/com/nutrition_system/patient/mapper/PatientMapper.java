package com.nutrition_system.patient.mapper;

import com.nutrition_system.patient.dto.response.PatientResponseDto;
import com.nutrition_system.patient.dto.resquest.PatientRequestDto;
import com.nutrition_system.patient.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toEntity(PatientRequestDto patientRequestDto);

    PatientResponseDto toDto(Patient patient);
}
