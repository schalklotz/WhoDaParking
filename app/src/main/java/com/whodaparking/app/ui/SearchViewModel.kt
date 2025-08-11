package com.whodaparking.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whodaparking.app.data.VehicleRepository
import com.whodaparking.app.data.model.VehicleRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<VehicleRecord>>(emptyList())
    val searchResults: StateFlow<List<VehicleRecord>> = _searchResults.asStateFlow()

    init {
        initializeRepository()
    }

    private fun initializeRepository() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val success = vehicleRepository.initialize()
            if (!success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load vehicle data"
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun searchByRegistration(registration: String, enableFuzzyMatch: Boolean = false) {
        if (registration.isBlank()) {
            clearSearch()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, error = null)
            
            try {
                val results = vehicleRepository.findByRegistration(registration, enableFuzzyMatch)
                _searchResults.value = results
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    hasSearched = true,
                    lastSearchQuery = registration
                )
                
                Timber.d("Search for '$registration' returned ${results.size} results")
            } catch (e: Exception) {
                Timber.e(e, "Search failed")
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = "Search failed: ${e.message}"
                )
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _uiState.value = _uiState.value.copy(
            hasSearched = false,
            lastSearchQuery = "",
            error = null
        )
    }

    fun retryDataLoad() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val success = vehicleRepository.retryLoad()
            if (!success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load vehicle data"
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val lastSearchQuery: String = "",
    val error: String? = null
)