package com.dev.monkey_dev.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Criteria filter for pagination, sorting, and searching.
 * Used as a base class or parameter for filtering and pagination requests.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CriteriaFilter {

    /**
     * Search keyword for filtering records.
     * Can be used to search across multiple fields.
     */
    private String search;

    /**
     * Sort field and direction.
     * Format: "field,direction" (e.g., "createdAt,desc" or "name,asc")
     * If only field is provided, defaults to ascending order.
     * If null or empty, no sorting is applied.
     */
    private String sort;

    /**
     * Page number (0-based index).
     * Default: 0 (first page)
     */
    @Builder.Default
    private Integer page = 0;

    /**
     * Page size (number of records per page).
     * Default: 10
     */
    @Builder.Default
    private Integer size = 10;

    /**
     * Converts this CriteriaFilter to a Spring Data Pageable object.
     * 
     * @return Pageable object with pagination and sorting applied
     */
    public Pageable toPageable() {
        return toPageable(null);
    }

    /**
     * Converts this CriteriaFilter to a Spring Data Pageable object with default
     * sort.
     * 
     * @param defaultSortField Default sort field if no sort is specified
     * @param defaultDirection Default sort direction (ASC or DESC)
     * @return Pageable object with pagination and sorting applied
     */
    public Pageable toPageable(String defaultSortField, Sort.Direction defaultDirection) {
        Sort sortObj = parseSort(defaultSortField, defaultDirection);
        return PageRequest.of(getPage(), getSize(), sortObj);
    }

    /**
     * Converts this CriteriaFilter to a Spring Data Pageable object with default
     * sort.
     * 
     * @param defaultSort Default Sort object if no sort is specified
     * @return Pageable object with pagination and sorting applied
     */
    public Pageable toPageable(Sort defaultSort) {
        Sort sortObj = parseSort(defaultSort);
        return PageRequest.of(getPage(), getSize(), sortObj);
    }

    /**
     * Parses the sort string and returns a Sort object.
     * Format: "field,direction" or just "field" (defaults to ASC)
     * 
     * @return Sort object, or Sort.unsorted() if no sort is specified
     */
    public Sort parseSort() {
        return parseSort((Sort) null);
    }

    /**
     * Parses the sort string and returns a Sort object with default fallback.
     * 
     * @param defaultSort Default Sort object if no sort is specified
     * @return Sort object
     */
    public Sort parseSort(Sort defaultSort) {
        if (sort == null || sort.trim().isEmpty()) {
            return defaultSort != null ? defaultSort : Sort.unsorted();
        }

        String[] parts = sort.split(",");
        String field = parts[0].trim();

        // Validate that field is not a direction keyword
        if (field.equalsIgnoreCase("ASC") || field.equalsIgnoreCase("DESC")) {
            return defaultSort != null ? defaultSort : Sort.unsorted();
        }

        if (parts.length > 1) {
            String directionStr = parts[1].trim().toUpperCase();
            Sort.Direction direction = directionStr.equals("DESC")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return Sort.by(direction, field);
        }

        return Sort.by(Sort.Direction.ASC, field);
    }

    /**
     * Parses the sort string and returns a Sort object with default field and
     * direction.
     * 
     * @param defaultSortField Default sort field if no sort is specified
     * @param defaultDirection Default sort direction if no sort is specified
     * @return Sort object
     */
    public Sort parseSort(String defaultSortField, Sort.Direction defaultDirection) {
        if (sort == null || sort.trim().isEmpty()) {
            if (defaultSortField != null && !defaultSortField.trim().isEmpty()) {
                return Sort.by(defaultDirection != null ? defaultDirection : Sort.Direction.ASC, defaultSortField);
            }
            return Sort.unsorted();
        }

        String[] parts = sort.split(",");
        String field = parts[0].trim();

        // Validate that field is not a direction keyword
        if (field.equalsIgnoreCase("ASC") || field.equalsIgnoreCase("DESC")) {
            if (defaultSortField != null && !defaultSortField.trim().isEmpty()) {
                return Sort.by(defaultDirection != null ? defaultDirection : Sort.Direction.ASC, defaultSortField);
            }
            return Sort.unsorted();
        }

        if (parts.length > 1) {
            String directionStr = parts[1].trim().toUpperCase();
            Sort.Direction direction = directionStr.equals("DESC")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return Sort.by(direction, field);
        }

        return Sort.by(Sort.Direction.ASC, field);
    }

    /**
     * Gets the page number, ensuring it's not null and non-negative.
     * 
     * @return Page number (0-based)
     */
    public Integer getPage() {
        return page != null && page >= 0 ? page : 0;
    }

    /**
     * Gets the page size, ensuring it's not null and positive.
     * 
     * @return Page size
     */
    public Integer getSize() {
        return size != null && size > 0 ? size : 10;
    }

    /**
     * Checks if search criteria is provided.
     * 
     * @return true if search is not null and not empty
     */
    public boolean hasSearch() {
        return search != null && !search.trim().isEmpty();
    }

    /**
     * Gets the search string trimmed, or null if empty.
     * 
     * @return Trimmed search string or null
     */
    public String getSearch() {
        return search != null && !search.trim().isEmpty() ? search.trim() : null;
    }
}
