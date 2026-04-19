package com.andrey.librarymanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
    @PastOrPresent
    private Integer publicationYear;

    @Positive
    private Integer totalCopies;

    private Set<Long> authorsId = new HashSet<>();
}
