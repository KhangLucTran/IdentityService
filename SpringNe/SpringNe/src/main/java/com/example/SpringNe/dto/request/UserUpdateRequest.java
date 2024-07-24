package com.example.SpringNe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min=3, message = "PASSWORD_INVALID")
    String password;

    @NotBlank(message = "FIRSTNAME_INVALID")
    String firstName;

    @NotBlank(message = "LASTNAME_INVALID")
    String lastName;

    @NotNull(message = "DOB_INVALID")
    LocalDate dob;

    List<String> roles;
}
