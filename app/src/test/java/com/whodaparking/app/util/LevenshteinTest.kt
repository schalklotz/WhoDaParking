package com.whodaparking.app.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LevenshteinTest {

    @Test
    fun `distance should return 0 for identical strings`() {
        val distance = Levenshtein.distance("ABC123GP", "ABC123GP")
        assertThat(distance).isEqualTo(0)
    }

    @Test
    fun `distance should return string length for empty string comparison`() {
        assertThat(Levenshtein.distance("", "ABC")).isEqualTo(3)
        assertThat(Levenshtein.distance("ABC", "")).isEqualTo(3)
    }

    @Test
    fun `distance should calculate single character substitution`() {
        val distance = Levenshtein.distance("ABC123GP", "ABC124GP")
        assertThat(distance).isEqualTo(1)
    }

    @Test
    fun `distance should calculate single character insertion`() {
        val distance = Levenshtein.distance("ABC23GP", "ABC123GP")
        assertThat(distance).isEqualTo(1)
    }

    @Test
    fun `distance should calculate single character deletion`() {
        val distance = Levenshtein.distance("ABC123GP", "ABC23GP")
        assertThat(distance).isEqualTo(1)
    }

    @Test
    fun `distance should calculate multiple edits`() {
        val distance = Levenshtein.distance("ABC123GP", "XYZ456WP")
        assertThat(distance).isEqualTo(8) // All characters different
    }

    @Test
    fun `distance should handle case sensitivity`() {
        val distance = Levenshtein.distance("abc123gp", "ABC123GP")
        assertThat(distance).isEqualTo(8) // All characters different due to case
    }

    @Test
    fun `isSimilar should return true for identical strings`() {
        assertThat(Levenshtein.isSimilar("ABC123GP", "ABC123GP")).isTrue()
    }

    @Test
    fun `isSimilar should return true for strings within threshold`() {
        assertThat(Levenshtein.isSimilar("ABC123GP", "ABC124GP", 1)).isTrue()
        assertThat(Levenshtein.isSimilar("ABC123GP", "ABC125GP", 1)).isFalse()
    }

    @Test
    fun `isSimilar should use default threshold of 1`() {
        assertThat(Levenshtein.isSimilar("ABC123GP", "ABC124GP")).isTrue()
        assertThat(Levenshtein.isSimilar("ABC123GP", "ABC125GP")).isFalse()
    }

    @Test
    fun `similarity should return 1_0 for identical strings`() {
        val similarity = Levenshtein.similarity("ABC123GP", "ABC123GP")
        assertThat(similarity).isEqualTo(1.0)
    }

    @Test
    fun `similarity should return 0_0 for completely different strings of same length`() {
        val similarity = Levenshtein.similarity("ABCDEFGH", "12345678")
        assertThat(similarity).isEqualTo(0.0)
    }

    @Test
    fun `similarity should calculate ratio correctly`() {
        // One character different in 8-character string = 1 - (1/8) = 0.875
        val similarity = Levenshtein.similarity("ABC123GP", "ABC124GP")
        assertThat(similarity).isEqualTo(0.875)
    }

    @Test
    fun `similarity should handle empty strings`() {
        assertThat(Levenshtein.similarity("", "")).isEqualTo(1.0)
        assertThat(Levenshtein.similarity("", "ABC")).isEqualTo(0.0)
        assertThat(Levenshtein.similarity("ABC", "")).isEqualTo(0.0)
    }

    @Test
    fun `similarity should use max length for calculation`() {
        // "ABC" vs "ABCD" - distance is 1, max length is 4
        val similarity = Levenshtein.similarity("ABC", "ABCD")
        assertThat(similarity).isEqualTo(0.75) // 1 - (1/4)
    }
}