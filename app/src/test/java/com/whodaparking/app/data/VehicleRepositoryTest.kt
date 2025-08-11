package com.whodaparking.app.data

import com.google.common.truth.Truth.assertThat
import com.whodaparking.app.data.model.VehicleRecord
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class VehicleRepositoryTest {

    private lateinit var mockDataSource: VehicleDataSource
    private lateinit var repository: VehicleRepository

    private val testRecords = listOf(
        VehicleRecord(
            staffName = "John Doe",
            vehicleMake = "Toyota",
            vehicleColour = "White",
            vehicleReg = "ABC123GP"
        ),
        VehicleRecord(
            staffName = "Jane Smith",
            vehicleMake = "Honda",
            vehicleColour = "Blue",
            vehicleReg = "DEF456WP"
        ),
        VehicleRecord(
            staffName = "Bob Johnson",
            vehicleMake = "BMW",
            vehicleColour = null,
            vehicleReg = "GHI789GP/JKL012GP"
        )
    )

    @Before
    fun setup() {
        mockDataSource = mock()
        repository = VehicleRepository(mockDataSource)
    }

    @Test
    fun `initialize should load data successfully`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))

        val result = repository.initialize()

        assertThat(result).isTrue()
    }

    @Test
    fun `initialize should handle load failure`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(
            Result.failure(Exception("Load failed"))
        )

        val result = repository.initialize()

        assertThat(result).isFalse()
    }

    @Test
    fun `findByRegistration should return exact match`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        repository.initialize()

        val results = repository.findByRegistration("abc123gp")

        assertThat(results).hasSize(1)
        assertThat(results.first().staffName).isEqualTo("John Doe")
    }

    @Test
    fun `findByRegistration should normalize input for matching`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        repository.initialize()

        val results = repository.findByRegistration("  a b c - 1 2 3 g p  ")

        assertThat(results).hasSize(1)
        assertThat(results.first().staffName).isEqualTo("John Doe")
    }

    @Test
    fun `findByRegistration should handle multiple registrations in single field`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        repository.initialize()

        val results1 = repository.findByRegistration("GHI789GP")
        val results2 = repository.findByRegistration("JKL012GP")

        assertThat(results1).hasSize(1)
        assertThat(results1.first().staffName).isEqualTo("Bob Johnson")
        assertThat(results2).hasSize(1)
        assertThat(results2.first().staffName).isEqualTo("Bob Johnson")
    }

    @Test
    fun `findByRegistration should return empty list for no match`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        repository.initialize()

        val results = repository.findByRegistration("NOTFOUND")

        assertThat(results).isEmpty()
    }

    @Test
    fun `findByRegistration should return empty list for blank input`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        repository.initialize()

        val results = repository.findByRegistration("   ")

        assertThat(results).isEmpty()
    }

    @Test
    fun `findByRegistration should auto-initialize if not loaded`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))

        val results = repository.findByRegistration("ABC123GP")

        assertThat(results).hasSize(1)
        assertThat(results.first().staffName).isEqualTo("John Doe")
    }

    @Test
    fun `findByRegistration with fuzzy matching should find similar plates`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        repository.initialize()

        // "ABC124GP" is 1 character different from "ABC123GP"
        val results = repository.findByRegistration("ABC124GP", enableFuzzyMatch = true)

        assertThat(results).hasSize(1)
        assertThat(results.first().staffName).isEqualTo("John Doe")
    }

    @Test
    fun `findByRegistration with fuzzy matching disabled should not find similar plates`() = runTest {
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        repository.initialize()

        val results = repository.findByRegistration("ABC124GP", enableFuzzyMatch = false)

        assertThat(results).isEmpty()
    }

    @Test
    fun `retryLoad should clear cache and reload data`() = runTest {
        // First load fails
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(
            Result.failure(Exception("Initial load failed"))
        )
        repository.initialize()

        // Retry with successful load
        whenever(mockDataSource.loadVehicleRecords()).thenReturn(Result.success(testRecords))
        val result = repository.retryLoad()

        assertThat(result).isTrue()

        // Should now be able to find records
        val searchResults = repository.findByRegistration("ABC123GP")
        assertThat(searchResults).hasSize(1)
    }
}