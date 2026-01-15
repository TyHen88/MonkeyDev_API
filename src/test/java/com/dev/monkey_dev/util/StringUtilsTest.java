package com.dev.monkey_dev.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for StringUtils class
 * 
 * This test class demonstrates fundamental unit testing concepts:
 * 
 * 1. TEST STRUCTURE:
 * - Test class naming: {ClassName}Test
 * - Test methods use descriptive names: methodName_scenario_expectedResult()
 * - Each test is independent and isolated
 * 
 * 2. AAA PATTERN (Arrange-Act-Assert):
 * - Arrange: Set up test data and conditions
 * - Act: Execute the method being tested
 * - Assert: Verify the expected outcome
 * 
 * 3. ASSERTION TYPES:
 * - assertTrue/assertFalse: For boolean results
 * - assertEquals: For comparing expected vs actual values
 * - assertNull/assertNotNull: For null checks
 * - assertThrows: For exception testing
 * 
 * 4. EDGE CASE TESTING:
 * - Null values
 * - Empty strings
 * - Boundary conditions
 * - Multiple parameters
 */
@DisplayName("StringUtils Unit Tests")
class StringUtilsTest {

    // ============================================
    // Testing isNotNullOrEmpty() method
    // ============================================

    @Test
    @DisplayName("isNotNullOrEmpty should return false when string is null")
    void isNotNullOrEmpty_shouldReturnFalse_whenStringIsNull() {
        // Arrange & Act
        boolean result = StringUtils.isNotNullOrEmpty((String) null);

        // Assert
        assertFalse(result, "Should return false for null string");
    }

    @Test
    @DisplayName("isNotNullOrEmpty should return false when string is empty")
    void isNotNullOrEmpty_shouldReturnFalse_whenStringIsEmpty() {
        // Arrange & Act
        boolean result = StringUtils.isNotNullOrEmpty("");

        // Assert
        assertFalse(result, "Should return false for empty string");
    }

    @Test
    @DisplayName("isNotNullOrEmpty should return true when string is valid")
    void isNotNullOrEmpty_shouldReturnTrue_whenStringIsValid() {
        // Arrange & Act
        boolean result = StringUtils.isNotNullOrEmpty("hello");

        // Assert
        assertTrue(result, "Should return true for valid string");
    }

    @Test
    @DisplayName("isNotNullOrEmpty should return true when all strings are valid")
    void isNotNullOrEmpty_shouldReturnTrue_whenAllStringsAreValid() {
        // Arrange & Act
        boolean result = StringUtils.isNotNullOrEmpty("hello", "world", "test");

        // Assert
        assertTrue(result, "Should return true when all strings are valid");
    }

    @Test
    @DisplayName("isNotNullOrEmpty should return false when any string is null")
    void isNotNullOrEmpty_shouldReturnFalse_whenAnyStringIsNull() {
        // Arrange & Act
        boolean result = StringUtils.isNotNullOrEmpty("hello", null, "world");

        // Assert
        assertFalse(result, "Should return false when any string is null");
    }

    @Test
    @DisplayName("isNotNullOrEmpty should return false when any string is empty")
    void isNotNullOrEmpty_shouldReturnFalse_whenAnyStringIsEmpty() {
        // Arrange & Act
        boolean result = StringUtils.isNotNullOrEmpty("hello", "", "world");

        // Assert
        assertFalse(result, "Should return false when any string is empty");
    }

    @Test
    @DisplayName("isNotNullOrEmpty should return true when no arguments provided")
    void isNotNullOrEmpty_shouldReturnTrue_whenNoArgumentsProvided() {
        // Arrange & Act
        boolean result = StringUtils.isNotNullOrEmpty();

        // Assert
        assertTrue(result, "Should return true when no arguments (all pass the check)");
    }

    // ============================================
    // Testing isNullOrEmptyOrElse() method
    // ============================================

    @Test
    @DisplayName("isNullOrEmptyOrElse should return default value when string is null")
    void isNullOrEmptyOrElse_shouldReturnDefaultValue_whenStringIsNull() {
        // Arrange
        String defaultValue = "default";

        // Act
        String result = StringUtils.isNullOrEmptyOrElse(null, defaultValue);

        // Assert
        assertEquals(defaultValue, result, "Should return default value for null string");
    }

    @Test
    @DisplayName("isNullOrEmptyOrElse should return default value when string is empty")
    void isNullOrEmptyOrElse_shouldReturnDefaultValue_whenStringIsEmpty() {
        // Arrange
        String defaultValue = "default";

        // Act
        String result = StringUtils.isNullOrEmptyOrElse("", defaultValue);

        // Assert
        assertEquals(defaultValue, result, "Should return default value for empty string");
    }

    @Test
    @DisplayName("isNullOrEmptyOrElse should return original string when string is valid")
    void isNullOrEmptyOrElse_shouldReturnOriginalString_whenStringIsValid() {
        // Arrange
        String input = "hello";
        String defaultValue = "default";

        // Act
        String result = StringUtils.isNullOrEmptyOrElse(input, defaultValue);

        // Assert
        assertEquals(input, result, "Should return original string when it's valid");
        assertNotEquals(defaultValue, result, "Should not return default value");
    }

    // ============================================
    // Testing insertZeroWidthSpaces() method
    // ============================================

    @Test
    @DisplayName("insertZeroWidthSpaces should return null when address is null")
    void insertZeroWidthSpaces_shouldReturnNull_whenAddressIsNull() {
        // Arrange & Act
        String result = StringUtils.insertZeroWidthSpaces(null);

        // Assert
        assertNull(result, "Should return null for null address");
    }

    @Test
    @DisplayName("insertZeroWidthSpaces should insert zero-width spaces in address")
    void insertZeroWidthSpaces_shouldInsertZeroWidthSpaces_whenAddressIsValid() {
        // Arrange
        String address = "123 Main Street";

        // Act
        String result = StringUtils.insertZeroWidthSpaces(address);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertNotEquals(address, result, "Result should be different from input (contains zero-width spaces)");
        // The result should contain zero-width space characters
        assertTrue(result.contains("\u200B"), "Result should contain zero-width space character");
    }

    @Test
    @DisplayName("insertZeroWidthSpaces should handle addresses with numbers and dots")
    void insertZeroWidthSpaces_shouldHandleAddressesWithNumbersAndDots() {
        // Arrange
        String address = "B.0022 Building";

        // Act
        String result = StringUtils.insertZeroWidthSpaces(address);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("\u200B"), "Result should contain zero-width space");
    }

    @Test
    @DisplayName("insertZeroWidthSpaces should handle hyphenated addresses")
    void insertZeroWidthSpaces_shouldHandleHyphenatedAddresses() {
        // Arrange
        String address = "123-A Main Street";

        // Act
        String result = StringUtils.insertZeroWidthSpaces(address);

        // Assert
        assertNotNull(result, "Result should not be null");
    }

    @Test
    @DisplayName("insertZeroWidthSpaces should handle addresses with underscores")
    void insertZeroWidthSpaces_shouldHandleAddressesWithUnderscores() {
        // Arrange
        String address = "123_Main_Street";

        // Act
        String result = StringUtils.insertZeroWidthSpaces(address);

        // Assert
        assertNotNull(result, "Result should not be null");
    }

    // ============================================
    // Testing cleanupAddress() method
    // ============================================

    @Test
    @DisplayName("cleanupAddress should return null when address is null")
    void cleanupAddress_shouldReturnNull_whenAddressIsNull() {
        // Arrange & Act
        String result = StringUtils.cleanupAddress(null);

        // Assert
        assertNull(result, "Should return null for null address");
    }

    @Test
    @DisplayName("cleanupAddress should normalize multiple spaces to single space")
    void cleanupAddress_shouldNormalizeMultipleSpaces_toSingleSpace() {
        // Arrange
        String address = "123    Main     Street";

        // Act
        String result = StringUtils.cleanupAddress(address);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.contains("    "), "Should not contain multiple consecutive spaces");
        assertTrue(result.contains(" "), "Should contain single spaces");
    }

    @Test
    @DisplayName("cleanupAddress should trim leading and trailing whitespace")
    void cleanupAddress_shouldTrimLeadingAndTrailingWhitespace() {
        // Arrange
        String address = "  123 Main Street  ";

        // Act
        String result = StringUtils.cleanupAddress(address);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.startsWith(" "), "Should not start with space");
        assertFalse(result.endsWith(" "), "Should not end with space");
        assertEquals("123 Main Street", result, "Should match expected cleaned address");
    }

    @Test
    @DisplayName("cleanupAddress should handle address with tabs and newlines")
    void cleanupAddress_shouldHandleAddressWithTabsAndNewlines() {
        // Arrange
        String address = "123\tMain\nStreet";

        // Act
        String result = StringUtils.cleanupAddress(address);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.contains("\t"), "Should not contain tabs");
        assertFalse(result.contains("\n"), "Should not contain newlines");
    }

    @Test
    @DisplayName("cleanupAddress should return same string when already clean")
    void cleanupAddress_shouldReturnSameString_whenAlreadyClean() {
        // Arrange
        String address = "123 Main Street";

        // Act
        String result = StringUtils.cleanupAddress(address);

        // Assert
        assertEquals(address, result, "Should return same string when already clean");
    }

    // ============================================
    // Testing removeDiacritics() method
    // ============================================

    @Test
    @DisplayName("removeDiacritics should remove accented characters")
    void removeDiacritics_shouldRemoveAccentedCharacters() {
        // Arrange
        String textWithAccents = "café résumé naïve";

        // Act
        String result = StringUtils.removeDiacritics(textWithAccents);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("cafe resume naive", result, "Should remove all diacritical marks");
    }

    @Test
    @DisplayName("removeDiacritics should handle Vietnamese characters")
    void removeDiacritics_shouldHandleVietnameseCharacters() {
        // Arrange
        String vietnameseText = "Nguyễn Văn An";

        // Act
        String result = StringUtils.removeDiacritics(vietnameseText);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.contains("ễ"), "Should remove Vietnamese diacritics");
        assertFalse(result.contains("ă"), "Should remove Vietnamese diacritics");
    }

    @Test
    @DisplayName("removeDiacritics should return same string when no diacritics")
    void removeDiacritics_shouldReturnSameString_whenNoDiacritics() {
        // Arrange
        String textWithoutAccents = "hello world";

        // Act
        String result = StringUtils.removeDiacritics(textWithoutAccents);

        // Assert
        assertEquals(textWithoutAccents, result, "Should return same string when no diacritics");
    }

    @Test
    @DisplayName("removeDiacritics should handle empty string")
    void removeDiacritics_shouldHandleEmptyString() {
        // Arrange
        String emptyString = "";

        // Act
        String result = StringUtils.removeDiacritics(emptyString);

        // Assert
        assertEquals(emptyString, result, "Should return empty string");
    }

    @Test
    @DisplayName("removeDiacritics should handle special characters")
    void removeDiacritics_shouldHandleSpecialCharacters() {
        // Arrange
        String textWithSpecialChars = "hello@world#123";

        // Act
        String result = StringUtils.removeDiacritics(textWithSpecialChars);

        // Assert
        assertEquals(textWithSpecialChars, result, "Should preserve special characters");
    }
}
