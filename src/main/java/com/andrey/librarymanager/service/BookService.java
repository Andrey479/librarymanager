package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.BookRequestDTO;
import com.andrey.librarymanager.dto.BookResponseDTO;
import com.andrey.librarymanager.model.Book;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.repository.AuthorRepository;
import com.andrey.librarymanager.repository.BookRepository;
import com.andrey.librarymanager.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LoanRepository loanRepository;

    public BookResponseDTO register(BookRequestDTO request){
        return toResponse(bookRepository.save(toEntity(request)));
    }

    public List<BookResponseDTO> listAll(){
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        boolean haveLoan = loanRepository.existsByBookIdAndStatus(id, LoanStatus.ACTIVE);
        if (haveLoan) {
            throw new RuntimeException("O livro tem emprestimo ativo");
        } else {
            bookRepository.deleteById(id);
        }
    }



    private Book toEntity(BookRequestDTO request){
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        book.setTotalCopies(request.getTotalCopies());
        book.setAuthors(new HashSet<>(authorRepository.findAllById(request.getAuthorsId())));
        book.setAvailableCopies(request.getTotalCopies());
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
