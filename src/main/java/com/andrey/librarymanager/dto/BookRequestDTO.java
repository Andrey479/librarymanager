package com.andrey.librarymanager.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String isbn;

    @NotNull
    private Integer publicationYear;

    @Positive
    private Integer totalCopies;

    @NotEmpty
    private Set<Long> authorsId = new HashSet<>();
}
