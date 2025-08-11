package com.whodaparking.app.ocr

import com.whodaparking.app.util.Normalization
import timber.log.Timber

object PlateHeuristics {
    
    /**
     * Extract the most likely license plate from detected text blocks.
     * 
     * Heuristics:
     * 1. Must be mostly alphanumeric
     * 2. Length between 5-10 characters (after normalization)
     * 3. Prefer longer contiguous alphanumeric strings
     * 4. Look for typical license plate patterns
     * 
     * @param detectedTexts List of text blocks from OCR
     * @return The most likely license plate candidate, or null if none found
     */
    fun extractBestPlateCandidate(detectedTexts: List<String>): String? {
        if (detectedTexts.isEmpty()) return null
        
        val candidates = detectedTexts
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filter { Normalization.looksLikeRegistration(it) }
            .map { candidate ->
                val normalized = Normalization.normalizeRegistration(candidate)
                PlateCandidate(
                    original = candidate,
                    normalized = normalized,
                    score = calculatePlateScore(normalized)
                )
            }
            .filter { it.score > 0 }
            .sortedByDescending { it.score }
        
        Timber.d("Plate candidates: ${candidates.map { "${it.original} (${it.score})" }}")
        
        return candidates.firstOrNull()?.original
    }
    
    /**
     * Calculate a score for how likely a string is to be a license plate.
     * Higher score = more likely to be a plate.
     */
    private fun calculatePlateScore(normalized: String): Int {
        var score = 0
        
        // Length scoring (5-8 chars is typical)
        score += when (normalized.length) {
            in 5..6 -> 10
            in 7..8 -> 8
            in 3..4 -> 5
            in 9..10 -> 3
            else -> 0
        }
        
        // Alphanumeric content scoring
        val alphanumericCount = normalized.count { it.isLetterOrDigit() }
        val alphanumericRatio = alphanumericCount.toDouble() / normalized.length
        score += (alphanumericRatio * 10).toInt()
        
        // Pattern scoring (common license plate patterns)
        score += when {
            // Pattern like "ABC123" or "123ABC"
            hasLetterDigitPattern(normalized) -> 5
            // All letters or all digits (less common but possible)
            normalized.all { it.isLetter() } -> 2
            normalized.all { it.isDigit() } -> 2
            else -> 0
        }
        
        // Bonus for typical South African patterns (GP, WP, etc.)
        if (containsProvinceCode(normalized)) {
            score += 3
        }
        
        return score
    }
    
    /**
     * Check if the string has a typical letter-digit pattern
     */
    private fun hasLetterDigitPattern(text: String): Boolean {
        val hasLetters = text.any { it.isLetter() }
        val hasDigits = text.any { it.isDigit() }
        return hasLetters && hasDigits
    }
    
    /**
     * Check if the string contains common South African province codes
     */
    private fun containsProvinceCode(text: String): Boolean {
        val provinceCodes = listOf("GP", "WP", "KZN", "FS", "MP", "LP", "NW", "EC", "NC")
        return provinceCodes.any { text.contains(it) }
    }
    
    private data class PlateCandidate(
        val original: String,
        val normalized: String,
        val score: Int
    )
}