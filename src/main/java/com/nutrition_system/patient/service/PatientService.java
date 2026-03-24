package com.nutrition_system.patient.service;

import com.nutrition_system.patient.dto.response.PatientResponseDto;
import com.nutrition_system.patient.dto.resquest.PatientRequestDto;
import com.nutrition_system.patient.entity.Patient;
import com.nutrition_system.patient.mapper.PatientMapper;
import com.nutrition_system.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto){

        Patient patient = patientMapper.toEntity(patientRequestDto);

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toDto(savedPatient);
    }
}
