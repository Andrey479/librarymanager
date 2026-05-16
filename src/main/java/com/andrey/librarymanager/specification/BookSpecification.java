package com.andrey.librarymanager.specification;

import com.andrey.librarymanager.model.Author;
import com.andrey.librarymanager.model.Book;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withTitle(String title){

        if (title == null) return null;

        return (root, query, criteriaBuilder) -> {
            String busca = "%" + title + "%";
            return criteriaBuilder.like(root.get("title"), busca);
        };
    }

    public static Specification<Book> withAuthor(Long authorId){

        if (authorId == null) return null;

        return (root, query, criteriaBuilder) -> {
            Join<Book, Author> authors = root.join("authors");
            return criteriaBuilder.equal(authors.get("id"), authorId);
        };
    }

    public static Specification<Book> withAvailable(Boolean available){
        if (available == null) return null;

        if (available) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("availableCopies"), 0);
        } else {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("availableCopies"), 0);
        }
    }
}
