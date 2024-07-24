package com.example.SpringNe.dto.request;

import com.example.SpringNe.validator.DobConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min=3, message = "USERNAME_INVALID")
    String username;

    @Size(min=6, message = "PASSWORD_INVALID")
    String password;

    @NotBlank(message = "FIRSTNAME_INVALID")
    String firstName;

    @NotBlank(message = "LASTNAME_INVALID")
    String lastName;

    @DobConstraint(min =16, message = "INVALID_DOB")
    LocalDate dob;
}
