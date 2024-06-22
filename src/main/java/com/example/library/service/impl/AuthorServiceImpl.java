package com.example.library.service.impl;

import com.example.library.dto.AuthorDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.BookAuthor;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookAuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {


    private AuthorRepository authorRepository;


    private BookRepository bookRepository;


    private BookAuthorRepository bookAuthorRepository;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository, BookAuthorRepository bookAuthorRepository){
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookAuthorRepository = bookAuthorRepository;
    }

    @Override
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        author.setName(authorDTO.getName());
        author = authorRepository.save(author);

        List<BookAuthor> bookAuthors = new ArrayList<>();
        for (Long bookId : authorDTO.getBookIds()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBook(book);
            bookAuthor.setAuthor(author);
            bookAuthors.add(bookAuthor);
        }

        bookAuthorRepository.saveAll(bookAuthors);
        author.setBookAuthors(bookAuthors);

        List<Long> bookIds = bookAuthors.stream()
                .map(bookAuthor -> bookAuthor.getBook().getId())
                .collect(Collectors.toList());

        return new AuthorDTO(author.getId(), author.getName(), bookIds);
    }

    @Override
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        author.setName(authorDTO.getName());

        bookAuthorRepository.deleteAll(author.getBookAuthors());

        List<BookAuthor> bookAuthors = new ArrayList<>();
        for (Long bookId : authorDTO.getBookIds()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBook(book);
            bookAuthor.setAuthor(author);
            bookAuthors.add(bookAuthor);
        }

        bookAuthorRepository.saveAll(bookAuthors);
        author.setBookAuthors(bookAuthors);

        List<Long> bookIds = bookAuthors.stream()
                .map(bookAuthor -> bookAuthor.getBook().getId())
                .collect(Collectors.toList());

        return new AuthorDTO(author.getId(), author.getName(), bookIds);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        authorRepository.delete(author);
    }

    @Override
    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        List<Long> bookIds = author.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getBook().getId())
                .collect(Collectors.toList());

        return new AuthorDTO(author.getId(), author.getName(), bookIds);
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(author -> {
                    List<Long> bookIds = author.getBookAuthors().stream()
                            .map(bookAuthor -> bookAuthor.getBook().getId())
                            .collect(Collectors.toList());
                    return new AuthorDTO(author.getId(), author.getName(), bookIds);
                })
                .collect(Collectors.toList());
    }
}
