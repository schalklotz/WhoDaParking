package com.whodaparking.app.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VehicleRecordTest {

    @Test
    fun `getNormalizedRegistrations should handle single registration`() {
        val record = VehicleRecord(
            staffName = "John Doe",
            vehicleMake = "Toyota",
            vehicleColour = "White",
            vehicleReg = "ABC123GP"
        )

        val result = record.getNormalizedRegistrations()

        assertThat(result).containsExactly("ABC123GP")
    }

    @Test
    fun `getNormalizedRegistrations should handle multiple registrations`() {
        val record = VehicleRecord(
            staffName = "Bob Johnson",
            vehicleMake = "BMW",
            vehicleColour = "Black",
            vehicleReg = "ABC123GP/DEF456WP"
        )

        val result = record.getNormalizedRegistrations()

        assertThat(result).containsExactly("ABC123GP", "DEF456WP").inOrder()
    }

    @Test
    fun `getNormalizedRegistrations should normalize each registration`() {
        val record = VehicleRecord(
            staffName = "Alice Brown",
            vehicleMake = "Mercedes",
            vehicleColour = "Silver",
            vehicleReg = "abc 123-gp / def 456 wp"
        )

        val result = record.getNormalizedRegistrations()

        assertThat(result).containsExactly("ABC123GP", "DEF456WP").inOrder()
    }

    @Test
    fun `getNormalizedRegistrations should filter blank entries`() {
        val record = VehicleRecord(
            staffName = "Charlie Wilson",
            vehicleMake = "Audi",
            vehicleColour = "Red",
            vehicleReg = "ABC123GP//DEF456WP/"
        )

        val result = record.getNormalizedRegistrations()

        assertThat(result).containsExactly("ABC123GP", "DEF456WP").inOrder()
    }

    @Test
    fun `getNormalizedRegistrations should handle whitespace around slashes`() {
        val record = VehicleRecord(
            staffName = "Diana Prince",
            vehicleMake = "Ford",
            vehicleColour = "Blue",
            vehicleReg = "ABC123GP / DEF456WP"
        )

        val result = record.getNormalizedRegistrations()

        assertThat(result).containsExactly("ABC123GP", "DEF456WP").inOrder()
    }

    @Test
    fun `getDisplayColour should return actual colour when valid`() {
        val record = VehicleRecord(
            staffName = "John Doe",
            vehicleMake = "Toyota",
            vehicleColour = "White",
            vehicleReg = "ABC123GP"
        )

        val result = record.getDisplayColour()

        assertThat(result).isEqualTo("White")
    }

    @Test
    fun `getDisplayColour should return Unknown for null colour`() {
        val record = VehicleRecord(
            staffName = "Jane Smith",
            vehicleMake = "Honda",
            vehicleColour = null,
            vehicleReg = "DEF456WP"
        )

        val result = record.getDisplayColour()

        assertThat(result).isEqualTo("Unknown")
    }

    @Test
    fun `getDisplayColour should return Unknown for blank colour`() {
        val record = VehicleRecord(
            staffName = "Bob Johnson",
            vehicleMake = "BMW",
            vehicleColour = "",
            vehicleReg = "GHI789GP"
        )

        val result = record.getDisplayColour()

        assertThat(result).isEqualTo("Unknown")
    }

    @Test
    fun `getDisplayColour should return Unknown for NaN colour`() {
        val record = VehicleRecord(
            staffName = "Alice Brown",
            vehicleMake = "Mercedes",
            vehicleColour = "NaN",
            vehicleReg = "JKL012GP"
        )

        val result = record.getDisplayColour()

        assertThat(result).isEqualTo("Unknown")
    }

    @Test
    fun `getDisplayColour should handle whitespace-only colour`() {
        val record = VehicleRecord(
            staffName = "Charlie Wilson",
            vehicleMake = "Audi",
            vehicleColour = "   ",
            vehicleReg = "MNO345GP"
        )

        val result = record.getDisplayColour()

        assertThat(result).isEqualTo("Unknown")
    }
}