package com.complyai.common.util;

import com.complyai.common.dto.PagedResponse;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageMapper {

    public <S, T> PagedResponse<T> toResponse(Page<S> page, Function<S, T> mapper) {
        List<T> items = page.stream().map(mapper).toList();
        return new PagedResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
