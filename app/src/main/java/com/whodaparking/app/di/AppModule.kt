package com.whodaparking.app.di

import android.content.Context
import com.whodaparking.app.data.VehicleDataSource
import com.whodaparking.app.data.VehicleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideVehicleDataSource(
        @ApplicationContext context: Context
    ): VehicleDataSource {
        return VehicleDataSource(context)
    }
    
    @Provides
    @Singleton
    fun provideVehicleRepository(
        dataSource: VehicleDataSource
    ): VehicleRepository {
        return VehicleRepository(dataSource)
    }
}