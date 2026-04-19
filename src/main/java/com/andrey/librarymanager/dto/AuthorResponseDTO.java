package com.andrey.librarymanager.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponseDTO {
    private Long id;

    private String name;

    private String nationality;

    private LocalDate birthDate;
}
