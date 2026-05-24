package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.BookRequestDTO;
import com.andrey.librarymanager.dto.BookResponseDTO;
import com.andrey.librarymanager.exception.ResourceNotFoundException;
import com.andrey.librarymanager.model.Book;
import com.andrey.librarymanager.repository.AuthorRepository;
import com.andrey.librarymanager.repository.BookRepository;
import com.andrey.librarymanager.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookResponseDTO register(BookRequestDTO request) {
       if (request.getAuthorsId().isEmpty()) {
           log.warn("The list of author IDs is empty.");
            throw new ResourceNotFoundException("The list of author IDs is empty.");
       }
       log.info("Book successfully registered.");
       return toResponse(bookRepository.save(toEntity(request)));
    }

    public List<BookResponseDTO> listAllByFilter(String title, Long authorId, Boolean available){

        Specification<Book> specification = Specification.unrestricted();

        if (title != null) {
            specification = specification.and(BookSpecification.withTitle(title));
        }
        if (authorId != null){
            specification = specification.and(BookSpecification.withAuthor(authorId));
        }
        if (available != null){
            specification = specification.and(BookSpecification.withAvailable(available));
        }

        log.info("Books listed successfully");
        return bookRepository.findAll(specification).stream()
                .map(this::toResponse)
                .toList();
    }

    private Book toEntity(BookRequestDTO request){
        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publicationYear(request.getPublicationYear())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .authors(new HashSet<>(authorRepository.findAllById(request.getAuthorsId())))
                .build();
        return book;
    }

    private BookResponseDTO toResponse(Book book) {
        BookResponseDTO bookResponseDTO = new BookResponseDTO();
        bookResponseDTO.setId(book.getId());
        bookResponseDTO.setTitle(book.getTitle());
        bookResponseDTO.setIsbn(book.getIsbn());
        bookResponseDTO.setPublicationYear(book.getPublicationYear());
        bookResponseDTO.setTotalCopies(book.getTotalCopies());
        bookResponseDTO.setAvailableCopies(book.getAvailableCopies());
        return bookResponseDTO;
    }
}
