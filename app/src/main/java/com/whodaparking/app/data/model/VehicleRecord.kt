package com.whodaparking.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleRecord(
    @SerialName("Derivco Staff")
    val staffName: String,
    @SerialName("Vehicle MAKE")
    val vehicleMake: String,
    @SerialName("Vehicle COLOUR")
    val vehicleColour: String? = null,
    @SerialName("Vehicle REG")
    val vehicleReg: String
) {
    /**
     * Get all normalized registration plates for this record.
     * Handles multiple plates separated by '/' and normalizes each one.
     */
    fun getNormalizedRegistrations(): List<String> {
        return vehicleReg.split("/")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { normalizeRegistration(it) }
    }

    /**
     * Get the display color, handling null values gracefully.
     */
    fun getDisplayColour(): String {
        return vehicleColour?.takeIf { it.isNotBlank() && it != "NaN" } ?: "Unknown"
    }

    private fun normalizeRegistration(reg: String): String {
        return reg.uppercase()
            .replace(Regex("[\\s-]"), "")
    }
}