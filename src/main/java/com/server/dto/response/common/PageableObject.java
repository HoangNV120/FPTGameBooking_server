package com.server.dto.response.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageableObject<T> {

    private long totalPages;
    private long totalElements;
    private int currentPages;
    private List<T> data;

    public PageableObject(Page<T> page) {
        this.totalPages = page.getTotalPages();
        this.currentPages = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.data = page.getContent();
    }
}

