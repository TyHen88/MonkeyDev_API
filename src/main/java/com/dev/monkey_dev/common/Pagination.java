package com.dev.monkey_dev.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Pagination {

    @JsonProperty("is_first")
    private boolean first;

    @JsonProperty("is_last")
    private boolean last;

    @JsonProperty("page_size")
    private int size;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("current_total_elements")
    private int currentTotalElements;

    @JsonProperty("total_elements")
    private long totalElements;

    @JsonProperty("is_empty")
    private boolean empty;

    @JsonProperty("has_next")
    private boolean hasNext;

    @JsonProperty("has_previous")
    private boolean hasPrevious;

    @JsonProperty("next_page")
    private Integer nextPage;

    @JsonProperty("previous_page")
    private Integer previousPage;

    public Pagination(Page<?> page) {
        this.first = page.isFirst();
        this.last = page.isLast();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber() + 1; // 1-based index for API consumers
        this.currentTotalElements = page.getNumberOfElements();
        this.totalElements = page.getTotalElements();
        this.empty = page.isEmpty();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.nextPage = page.hasNext() ? this.currentPage + 1 : null;
        this.previousPage = page.hasPrevious() ? this.currentPage - 1 : null;
    }

    /**
     * Builder for manual construction (optional).
     */
    public static Pagination of(int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean isFirst = page <= 1;
        boolean isLast = page >= totalPages;
        boolean isEmpty = totalElements == 0;
        int currentTotalElements = isLast && !isEmpty
                ? (int) (totalElements - (long) (page - 1) * size)
                : (isEmpty ? 0 : size);

        return new Pagination(
                isFirst,
                isLast,
                size,
                totalPages,
                page,
                currentTotalElements,
                totalElements,
                isEmpty,
                !isLast && !isEmpty,
                !isFirst && !isEmpty,
                !isLast && !isEmpty ? page + 1 : null,
                !isFirst && !isEmpty ? page - 1 : null);
    }
}
