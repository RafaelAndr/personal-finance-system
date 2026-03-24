package com.nutrition_system.service;

import com.nutrition_system.dto.response.PatientResponseDto;
import com.nutrition_system.dto.resquest.PatientRequestDto;
import com.nutrition_system.entity.Patient;
import com.nutrition_system.exception.DuplicatedCpfException;
import com.nutrition_system.mapper.PatientMapper;
import com.nutrition_system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto){

        checkDuplicatedCpf(patientRequestDto.cpf());

        Patient patient = patientMapper.toEntity(patientRequestDto);

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toDto(savedPatient);
    }

    private void checkDuplicatedCpf(String cpf){
        if (patientRepository.existsByCpf(cpf)) {
            throw new DuplicatedCpfException("CPF: " + cpf + " is already registered");
        }
    }
}
