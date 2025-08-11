package com.whodaparking.app.util

import kotlin.math.min

object Levenshtein {
    
    /**
     * Calculate the Levenshtein distance between two strings.
     * This is the minimum number of single-character edits (insertions, deletions, or substitutions)
     * required to change one string into another.
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return The Levenshtein distance
     */
    fun distance(s1: String, s2: String): Int {
        if (s1 == s2) return 0
        if (s1.isEmpty()) return s2.length
        if (s2.isEmpty()) return s1.length
        
        val len1 = s1.length
        val len2 = s2.length
        
        // Create a matrix to store distances
        val matrix = Array(len1 + 1) { IntArray(len2 + 1) }
        
        // Initialize first row and column
        for (i in 0..len1) {
            matrix[i][0] = i
        }
        for (j in 0..len2) {
            matrix[0][j] = j
        }
        
        // Fill the matrix
        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                
                matrix[i][j] = min(
                    min(
                        matrix[i - 1][j] + 1,      // deletion
                        matrix[i][j - 1] + 1       // insertion
                    ),
                    matrix[i - 1][j - 1] + cost    // substitution
                )
            }
        }
        
        return matrix[len1][len2]
    }
    
    /**
     * Check if two strings are similar within a given distance threshold.
     * 
     * @param s1 First string
     * @param s2 Second string
     * @param threshold Maximum allowed distance
     * @return True if the strings are within the threshold distance
     */
    fun isSimilar(s1: String, s2: String, threshold: Int = 1): Boolean {
        return distance(s1, s2) <= threshold
    }
    
    /**
     * Calculate similarity ratio between two strings (0.0 to 1.0).
     * 1.0 means identical, 0.0 means completely different.
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return Similarity ratio
     */
    fun similarity(s1: String, s2: String): Double {
        val maxLen = kotlin.math.max(s1.length, s2.length)
        if (maxLen == 0) return 1.0
        
        val dist = distance(s1, s2)
        return 1.0 - (dist.toDouble() / maxLen)
    }
}