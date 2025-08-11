package com.whodaparking.app.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NormalizationTest {

    @Test
    fun `normalizeRegistration should uppercase input`() {
        val result = Normalization.normalizeRegistration("abc123gp")
        assertThat(result).isEqualTo("ABC123GP")
    }

    @Test
    fun `normalizeRegistration should remove spaces and hyphens`() {
        val result = Normalization.normalizeRegistration("AB C-1 23GP")
        assertThat(result).isEqualTo("ABC123GP")
    }

    @Test
    fun `normalizeRegistration should trim whitespace`() {
        val result = Normalization.normalizeRegistration("  ABC123GP  ")
        assertThat(result).isEqualTo("ABC123GP")
    }

    @Test
    fun `normalizeRegistration should handle empty input`() {
        val result = Normalization.normalizeRegistration("")
        assertThat(result).isEmpty()
    }

    @Test
    fun `normalizeRegistration should handle spaces only`() {
        val result = Normalization.normalizeRegistration("   ")
        assertThat(result).isEmpty()
    }

    @Test
    fun `splitAndNormalizeRegistrations should handle single registration`() {
        val result = Normalization.splitAndNormalizeRegistrations("abc123gp")
        assertThat(result).containsExactly("ABC123GP")
    }

    @Test
    fun `splitAndNormalizeRegistrations should handle multiple registrations`() {
        val result = Normalization.splitAndNormalizeRegistrations("abc123gp/def456wp")
        assertThat(result).containsExactly("ABC123GP", "DEF456WP").inOrder()
    }

    @Test
    fun `splitAndNormalizeRegistrations should handle registrations with spaces`() {
        val result = Normalization.splitAndNormalizeRegistrations("ab c123gp / def 456wp")
        assertThat(result).containsExactly("ABC123GP", "DEF456WP").inOrder()
    }

    @Test
    fun `splitAndNormalizeRegistrations should filter empty entries`() {
        val result = Normalization.splitAndNormalizeRegistrations("abc123gp//def456wp")
        assertThat(result).containsExactly("ABC123GP", "DEF456WP").inOrder()
    }

    @Test
    fun `looksLikeRegistration should return true for typical plates`() {
        assertThat(Normalization.looksLikeRegistration("ABC123GP")).isTrue()
        assertThat(Normalization.looksLikeRegistration("123ABC")).isTrue()
        assertThat(Normalization.looksLikeRegistration("AB123CD")).isTrue()
    }

    @Test
    fun `looksLikeRegistration should return false for too short strings`() {
        assertThat(Normalization.looksLikeRegistration("AB")).isFalse()
    }

    @Test
    fun `looksLikeRegistration should return false for too long strings`() {
        assertThat(Normalization.looksLikeRegistration("ABCD123456789")).isFalse()
    }

    @Test
    fun `looksLikeRegistration should return false for non-alphanumeric strings`() {
        assertThat(Normalization.looksLikeRegistration("ABC@#$")).isFalse()
        assertThat(Normalization.looksLikeRegistration("!!!!!")).isFalse()
    }

    @Test
    fun `looksLikeRegistration should handle mixed alphanumeric ratio`() {
        // 70% alphanumeric threshold
        assertThat(Normalization.looksLikeRegistration("ABC12.")).isTrue() // 5/6 = 83%
        assertThat(Normalization.looksLikeRegistration("AB@#$")).isFalse() // 2/5 = 40%
    }
}