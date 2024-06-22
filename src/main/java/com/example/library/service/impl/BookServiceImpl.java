package com.example.library.service.impl;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Author;
import com.example.library.entity.Book;
import com.example.library.entity.BookAuthor;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookAuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {


    private BookRepository bookRepository;


    private AuthorRepository authorRepository;


    private BookAuthorRepository bookAuthorRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository,AuthorRepository authorRepository, BookAuthorRepository bookAuthorRepository){
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookAuthorRepository = bookAuthorRepository;
    }

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book = bookRepository.save(book);

        List<BookAuthor> bookAuthors = new ArrayList<>();
        for (Long authorId : bookDTO.getAuthorIds()) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBook(book);
            bookAuthor.setAuthor(author);
            bookAuthors.add(bookAuthor);
        }

        bookAuthorRepository.saveAll(bookAuthors);
        book.setBookAuthors(bookAuthors);

        List<Long> authorIds = bookAuthors.stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getId())
                .collect(Collectors.toList());

        return new BookDTO(book.getId(), book.getTitle(), authorIds);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        book.setTitle(bookDTO.getTitle());

        bookAuthorRepository.deleteAll(book.getBookAuthors());

        List<BookAuthor> bookAuthors = new ArrayList<>();
        for (Long authorId : bookDTO.getAuthorIds()) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBook(book);
            bookAuthor.setAuthor(author);
            bookAuthors.add(bookAuthor);
        }

        bookAuthorRepository.saveAll(bookAuthors);
        book.setBookAuthors(bookAuthors);

        List<Long> authorIds = bookAuthors.stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getId())
                .collect(Collectors.toList());

        return new BookDTO(book.getId(), book.getTitle(), authorIds);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        bookRepository.delete(book);
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        List<Long> authorIds = book.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getId())
                .collect(Collectors.toList());

        return new BookDTO(book.getId(), book.getTitle(), authorIds);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> {
                    List<Long> authorIds = book.getBookAuthors().stream()
                            .map(bookAuthor -> bookAuthor.getAuthor().getId())
                            .collect(Collectors.toList());
                    return new BookDTO(book.getId(), book.getTitle(), authorIds);
                })
                .collect(Collectors.toList());
    }
}
