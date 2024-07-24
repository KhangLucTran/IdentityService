package com.example.SpringNe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permisstion {
    @Id
    String name;
    String description;
}