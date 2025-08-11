package com.whodaparking.app.data

import com.whodaparking.app.data.model.VehicleRecord
import com.whodaparking.app.util.Normalization
import com.whodaparking.app.util.Levenshtein
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor(
    private val dataSource: VehicleDataSource
) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    // In-memory cache indexed by normalized registration
    private val registrationIndex = mutableMapOf<String, MutableList<VehicleRecord>>()
    private var isDataLoaded = false

    /**
     * Initialize the repository by loading data from assets
     */
    suspend fun initialize(): Boolean {
        if (isDataLoaded) return true

        _isLoading.value = true
        _loadError.value = null

        return try {
            val result = dataSource.loadVehicleRecords()
            if (result.isSuccess) {
                val records = result.getOrThrow()
                buildIndex(records)
                isDataLoaded = true
                Timber.i("Vehicle repository initialized with ${records.size} records")
                true
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                _loadError.value = error
                Timber.e("Failed to initialize vehicle repository: $error")
                false
            }
        } catch (e: Exception) {
            _loadError.value = e.message ?: "Unknown error"
            Timber.e(e, "Exception during repository initialization")
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Find vehicles by registration plate with exact and fuzzy matching
     */
    suspend fun findByRegistration(
        input: String,
        enableFuzzyMatch: Boolean = false
    ): List<VehicleRecord> {
        if (!isDataLoaded) {
            val initialized = initialize()
            if (!initialized) return emptyList()
        }

        val normalizedInput = Normalization.normalizeRegistration(input)
        if (normalizedInput.isBlank()) return emptyList()

        // First try exact match
        val exactMatches = registrationIndex[normalizedInput] ?: emptyList()
        if (exactMatches.isNotEmpty()) {
            Timber.d("Found ${exactMatches.size} exact matches for '$normalizedInput'")
            return exactMatches
        }

        // If no exact match and fuzzy matching is enabled, try fuzzy
        if (enableFuzzyMatch) {
            val fuzzyMatches = findFuzzyMatches(normalizedInput)
            if (fuzzyMatches.isNotEmpty()) {
                Timber.d("Found ${fuzzyMatches.size} fuzzy matches for '$normalizedInput'")
                return fuzzyMatches
            }
        }

        Timber.d("No matches found for '$normalizedInput'")
        return emptyList()
    }

    /**
     * Retry loading data after an error
     */
    suspend fun retryLoad(): Boolean {
        isDataLoaded = false
        registrationIndex.clear()
        return initialize()
    }

    private fun buildIndex(records: List<VehicleRecord>) {
        registrationIndex.clear()
        
        records.forEach { record ->
            record.getNormalizedRegistrations().forEach { normalizedReg ->
                registrationIndex.getOrPut(normalizedReg) { mutableListOf() }.add(record)
            }
        }
        
        Timber.d("Built index with ${registrationIndex.size} unique registrations")
    }

    private fun findFuzzyMatches(normalizedInput: String): List<VehicleRecord> {
        val fuzzyMatches = mutableListOf<VehicleRecord>()
        val maxDistance = 1 // Allow single character difference
        
        registrationIndex.forEach { (registeredPlate, records) ->
            if (Levenshtein.distance(normalizedInput, registeredPlate) <= maxDistance) {
                fuzzyMatches.addAll(records)
            }
        }
        
        return fuzzyMatches.distinctBy { "${it.staffName}_${it.vehicleReg}" }
    }
}