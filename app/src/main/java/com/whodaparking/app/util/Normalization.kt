package com.whodaparking.app.util

import java.util.Locale

object Normalization {
    
    /**
     * Normalize a registration plate for consistent comparison.
     * 
     * Rules:
     * 1. Convert to uppercase
     * 2. Remove all spaces and hyphens
     * 3. Trim whitespace
     * 
     * @param registration The raw registration string
     * @return The normalized registration string
     */
    fun normalizeRegistration(registration: String): String {
        return registration
            .trim()
            .uppercase(Locale.ROOT)
            .replace(Regex("[\\s-]"), "")
    }
    
    /**
     * Split multiple registrations from a single field and normalize each.
     * Handles cases like "ABC123/DEF456"
     * 
     * @param registrationField The field that may contain multiple registrations
     * @return List of normalized registration strings
     */
    fun splitAndNormalizeRegistrations(registrationField: String): List<String> {
        return registrationField
            .split("/")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { normalizeRegistration(it) }
    }
    
    /**
     * Check if a string looks like a vehicle registration plate.
     * Basic heuristic: should be mostly alphanumeric and of reasonable length.
     * 
     * @param text The text to check
     * @return True if it looks like a registration plate
     */
    fun looksLikeRegistration(text: String): Boolean {
        val normalized = normalizeRegistration(text)
        
        // Should be between 3 and 10 characters (reasonable for most registration formats)
        if (normalized.length < 3 || normalized.length > 10) return false
        
        // Should be mostly alphanumeric (allow some special chars but not too many)
        val alphanumericCount = normalized.count { it.isLetterOrDigit() }
        val alphanumericRatio = alphanumericCount.toDouble() / normalized.length
        
        return alphanumericRatio >= 0.7 // At least 70% alphanumeric
    }
}