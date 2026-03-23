package com.nutrition_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "test")
public class Test {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;
}
