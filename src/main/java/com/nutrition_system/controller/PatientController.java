package com.nutrition_system.controller;

import com.nutrition_system.dto.response.PatientResponseDto;
import com.nutrition_system.dto.resquest.PatientRequestDto;
import com.nutrition_system.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@RequestBody @Valid PatientRequestDto patientRequestDto){

        PatientResponseDto patientResponseDto = patientService.createPatient(patientRequestDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(patientResponseDto.id()).toUri();

        return ResponseEntity.created(uri).body(patientResponseDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(@RequestBody @Valid PatientRequestDto patientRequestDto, @PathVariable UUID id){

        patientService.updatePatient(patientRequestDto, id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }
}
