package com.server.service.common;


import com.server.dto.response.common.PageableObject;
import jakarta.validation.Valid;

public interface BaseService<T, C, U, F> {
    PageableObject<T> findAll(F request);

    T add(@Valid C dto);

    T update(U dto);

    T getById(String id);
}
