package com.whodaparking.app.ocr

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlateHeuristicsTest {

    @Test
    fun `extractBestPlateCandidate should return null for empty list`() {
        val result = PlateHeuristics.extractBestPlateCandidate(emptyList())
        assertThat(result).isNull()
    }

    @Test
    fun `extractBestPlateCandidate should return null for list with no valid plates`() {
        val detectedTexts = listOf("!!!", "@@", "AB", "TOOLONGREGISTRATION")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isNull()
    }

    @Test
    fun `extractBestPlateCandidate should return single valid plate`() {
        val detectedTexts = listOf("ABC123GP")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isEqualTo("ABC123GP")
    }

    @Test
    fun `extractBestPlateCandidate should prefer typical length plates`() {
        val detectedTexts = listOf("AB123", "ABC123GP", "ABCD")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isEqualTo("ABC123GP") // Should prefer 8-char over 5-char or 4-char
    }

    @Test
    fun `extractBestPlateCandidate should prefer mixed alphanumeric`() {
        val detectedTexts = listOf("ABCDEFGH", "ABC123GP")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isEqualTo("ABC123GP") // Should prefer mixed over all letters
    }

    @Test
    fun `extractBestPlateCandidate should handle plates with province codes`() {
        val detectedTexts = listOf("ABC123", "XYZ456GP")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isEqualTo("XYZ456GP") // Should prefer one with GP province code
    }

    @Test
    fun `extractBestPlateCandidate should ignore whitespace and formatting`() {
        val detectedTexts = listOf("  ABC 123 GP  ", "XYZ456")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isEqualTo("  ABC 123 GP  ") // Should return original format
    }

    @Test
    fun `extractBestPlateCandidate should handle mixed quality OCR results`() {
        val detectedTexts = listOf(
            "PARKIN6",         // Low quality
            "ABC123GP",        // Perfect plate
            "RESERVED",        // Not a plate
            "12345",          // Too short, all digits
            "ENTRANCE"        // Not a plate
        )
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isEqualTo("ABC123GP")
    }

    @Test
    fun `extractBestPlateCandidate should handle similar scored candidates consistently`() {
        val detectedTexts = listOf("ABC123WP", "XYZ456GP")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        // Should return one of them consistently (first highest scorer)
        assertThat(result).isIn(listOf("ABC123WP", "XYZ456GP"))
    }

    @Test
    fun `extractBestPlateCandidate should reject non-alphanumeric heavy strings`() {
        val detectedTexts = listOf("ABC@#$%", "XYZ!@#", "123***")
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        assertThat(result).isNull()
    }

    @Test
    fun `extractBestPlateCandidate should handle various South African province codes`() {
        val detectedTexts = listOf(
            "ABC123GP", // Gauteng
            "DEF456WP", // Western Cape
            "GHI789EC", // Eastern Cape
            "JKL012KZN" // KwaZulu-Natal
        )
        val result = PlateHeuristics.extractBestPlateCandidate(detectedTexts)
        // All should be valid, should return one of them
        assertThat(result).isIn(detectedTexts)
    }
}