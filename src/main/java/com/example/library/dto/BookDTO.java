package com.example.library.dto;

import java.util.List;

public class BookDTO {
    private Long id;
    private String title;
    private List<Long> authorIds;

    public BookDTO(Long id, String title, List<Long> authorIds) {
        this.id = id;
        this.title = title;
        this.authorIds = authorIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Long> authorIds) {
        this.authorIds = authorIds;
    }
}