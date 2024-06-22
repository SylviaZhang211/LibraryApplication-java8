package com.example.library.service;

import com.example.library.dto.AuthorDTO;

import java.util.List;

public interface AuthorService {
    AuthorDTO createAuthor(AuthorDTO authorDTO);
    AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO);
    void deleteAuthor(Long id);
    AuthorDTO getAuthorById(Long id);
    List<AuthorDTO> getAllAuthors();
}
