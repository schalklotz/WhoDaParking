package com.whodaparking.app.data

import android.content.Context
import com.whodaparking.app.data.model.VehicleRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleDataSource @Inject constructor(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Load vehicle records from assets/vehicle_records.json
     */
    suspend fun loadVehicleRecords(): Result<List<VehicleRecord>> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("vehicle_records.json").use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            }
            
            val records = json.decodeFromString<List<VehicleRecord>>(jsonString)
            Timber.d("Loaded ${records.size} vehicle records")
            Result.success(records)
        } catch (e: Exception) {
            Timber.e(e, "Failed to load vehicle records")
            Result.failure(e)
        }
    }
}