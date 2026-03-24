package com.nutrition_system.service;

import com.nutrition_system.dto.response.PatientResponseDto;
import com.nutrition_system.dto.resquest.PatientRequestDto;
import com.nutrition_system.entity.Address;
import com.nutrition_system.entity.Patient;
import com.nutrition_system.exception.DuplicatedCpfException;
import com.nutrition_system.mapper.AddressMapper;
import com.nutrition_system.mapper.PatientMapper;
import com.nutrition_system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AddressMapper addressMapper;

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto){

        checkDuplicatedCpf(patientRequestDto.cpf());

        Patient patient = patientMapper.toEntity(patientRequestDto);

        Patient savedPatient = patientRepository.save(patient);

        return patientMapper.toDto(savedPatient);
    }

    public void updatePatient(PatientRequestDto patientRequestDto, UUID id){

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Address address = addressMapper.toEntity(patientRequestDto.address());

        if (patientRequestDto.name() != null) {
            patient.setName(patientRequestDto.name());
        }

        if (patientRequestDto.dateOfBirth() != null) {
            patient.setDateOfBirth(patientRequestDto.dateOfBirth());
        }

        if (patientRequestDto.address() != null) {
            patient.setAddress(address);
        }

        patientRepository.save(patient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }


    private void checkDuplicatedCpf(String cpf){
        if (patientRepository.existsByCpf(cpf)) {
            throw new DuplicatedCpfException("CPF: " + cpf + " is already registered");
        }
    }
}
