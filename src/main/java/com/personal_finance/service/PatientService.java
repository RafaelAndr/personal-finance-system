package com.personal_finance.service;

import com.personal_finance.dto.response.PatientResponseDto;
import com.personal_finance.dto.resquest.PatientRequestDto;
import com.personal_finance.entity.Address;
import com.personal_finance.entity.Patient;
import com.personal_finance.exception.DuplicatedCpfException;
import com.personal_finance.mapper.AddressMapper;
import com.personal_finance.mapper.PatientMapper;
import com.personal_finance.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public PatientResponseDto getDetails(UUID id){
        Optional<Patient> patientOptional =  patientRepository.findById(id);

        return patientOptional.map(patientMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

    private void checkDuplicatedCpf(String cpf){
        if (patientRepository.existsByCpf(cpf)) {
            throw new DuplicatedCpfException("CPF: " + cpf + " is already registered");
        }
    }
}
