package com.dev.monkey_dev.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

/**
 * Utility component for creating standardized paginated responses.
 * Provides static methods to wrap Spring Data Page objects into a consistent response format.
 * 
 * Usage example:
 * <pre>
 * Page&lt;UserResponseDto&gt; usersPage = userService.getAllUsers(criteriaFilter);
 * Map&lt;String, Object&gt; response = PaginatedResponse.of(usersPage);
 * return success(response);
 * </pre>
 */
public class PaginatedResponse {

    /**
     * Creates a paginated response map from a Spring Data Page object.
     * The response contains:
     * - "data": List of content from the page
     * - "pagination": Pagination metadata object
     * 
     * @param page Spring Data Page object
     * @param <T> Type of the content in the page
     * @return Map containing "data" and "pagination" keys
     */
    public static <T> Map<String, Object> of(Page<T> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", page.getContent());
        response.put("pagination", new Pagination(page));
        return response;
    }

    /**
     * Creates a paginated response map with custom data key.
     * 
     * @param page Spring Data Page object
     * @param dataKey Custom key for the data (default: "data")
     * @param <T> Type of the content in the page
     * @return Map containing custom data key and "pagination" key
     */
    public static <T> Map<String, Object> of(Page<T> page, String dataKey) {
        Map<String, Object> response = new HashMap<>();
        response.put(dataKey, page.getContent());
        response.put("pagination", new Pagination(page));
        return response;
    }

    /**
     * Creates a paginated response map with custom data and pagination keys.
     * 
     * @param page Spring Data Page object
     * @param dataKey Custom key for the data (default: "data")
     * @param paginationKey Custom key for pagination (default: "pagination")
     * @param <T> Type of the content in the page
     * @return Map containing custom data and pagination keys
     */
    public static <T> Map<String, Object> of(Page<T> page, String dataKey, String paginationKey) {
        Map<String, Object> response = new HashMap<>();
        response.put(dataKey, page.getContent());
        response.put(paginationKey, new Pagination(page));
        return response;
    }
}
