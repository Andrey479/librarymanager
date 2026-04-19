package com.andrey.librarymanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequestDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String nationality;

    @NotNull
    @Past
    private LocalDate birthDate;
}
