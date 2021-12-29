package com.corrot.kwiatonomousapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.corrot.kwiatonomousapp.common.Constants.BASE_URL
import com.corrot.kwiatonomousapp.common.Constants.BASE_URL_DEBUG
import com.corrot.kwiatonomousapp.common.Constants.DEBUG_MODE
import com.corrot.kwiatonomousapp.common.Constants.PREFERENCES_DATA_STORE_NAME
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.repository.DeviceConfigurationRepositoryImpl
import com.corrot.kwiatonomousapp.data.repository.DeviceRepositoryImpl
import com.corrot.kwiatonomousapp.data.repository.DeviceUpdateRepositoryImpl
import com.corrot.kwiatonomousapp.data.repository.PreferencesRepositoryImpl
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideKwiatonomousApi(): KwiatonomousApi {
        return Retrofit.Builder()
            .baseUrl(
                if (DEBUG_MODE) {
                    BASE_URL_DEBUG
                } else {
                    BASE_URL
                }
            )
            .apply {
                if (DEBUG_MODE) {
                    client(
                        OkHttpClient().newBuilder()
                            .addInterceptor(
                                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                            )
                            .build()
                    )
                }
            }
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KwiatonomousApi::class.java)
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext applicationContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                applicationContext.preferencesDataStoreFile(PREFERENCES_DATA_STORE_NAME)
            }
        )
    }

    @Provides
    @Singleton
    fun provideDeviceRepository(kwiatonomousApi: KwiatonomousApi): DeviceRepository {
        return DeviceRepositoryImpl(kwiatonomousApi)
    }

    @Provides
    @Singleton
    fun provideDeviceUpdateRepository(kwiatonomousApi: KwiatonomousApi): DeviceUpdateRepository {
        return DeviceUpdateRepositoryImpl(kwiatonomousApi)
    }

    @Provides
    @Singleton
    fun provideDeviceConfigurationRepository(kwiatonomousApi: KwiatonomousApi): DeviceConfigurationRepository {
        return DeviceConfigurationRepositoryImpl(kwiatonomousApi)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(preferencesDataStore: DataStore<Preferences>): PreferencesRepository {
        return PreferencesRepositoryImpl(preferencesDataStore)
    }
}