package com.zenith.dtos.responses;

import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private List<T> content;
}
