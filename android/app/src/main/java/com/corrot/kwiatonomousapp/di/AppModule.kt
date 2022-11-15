package com.corrot.kwiatonomousapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.corrot.kwiatonomousapp.domain.AuthManager
import com.corrot.kwiatonomousapp.BuildConfig
import com.corrot.kwiatonomousapp.domain.NotificationsManager
import com.corrot.kwiatonomousapp.common.Constants.BASE_URL
import com.corrot.kwiatonomousapp.common.Constants.BASE_URL_DEBUG
import com.corrot.kwiatonomousapp.common.Constants.DEBUG_MODE
import com.corrot.kwiatonomousapp.common.Constants.PREFERENCES_DATA_STORE_NAME
import com.corrot.kwiatonomousapp.common.LocalTimeConverter
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.remote.DigestAuthInterceptor
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.repository.*
import com.corrot.kwiatonomousapp.domain.repository.*
import com.corrot.kwiatonomousapp.domain.workmanager.KwiatonomousWorkManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalTime
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // https://github.com/square/retrofit/issues/1554
    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit,
        ) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

            override fun convert(value: ResponseBody) =
                if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }

    @Provides
    @Singleton
    fun provideDigestAuthInterceptor(
        networkPreferencesRepository: NetworkPreferencesRepository,
    ): DigestAuthInterceptor {
        return DigestAuthInterceptor(networkPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalTime::class.java, LocalTimeConverter())
        .create()

    @Provides
    @Singleton
    fun provideKwiatonomousApi(
        digestAuthInterceptor: DigestAuthInterceptor,
    ): KwiatonomousApi {
        return Retrofit.Builder()
            .baseUrl(if (DEBUG_MODE) BASE_URL_DEBUG else BASE_URL)
            .client(OkHttpClient().newBuilder()
                .addInterceptor(digestAuthInterceptor)
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                        )
                    }
                }
                .build())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KwiatonomousApi::class.java)
    }

    @Provides
    @Singleton
    fun provideKwiatonomousDatabase(@ApplicationContext applicationContext: Context): KwiatonomousDatabase {
        return Room.databaseBuilder(
            applicationContext,
            KwiatonomousDatabase::class.java,
            "kwiatonomous_database"
        ).build()
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
    fun provideUserRepository(
        kwiatonomousApi: KwiatonomousApi,
        kwiatonomousDatabase: KwiatonomousDatabase,
    ): UserRepository {
        return UserRepositoryImpl(kwiatonomousApi, kwiatonomousDatabase)
    }

    @Provides
    @Singleton
    fun provideDeviceRepository(
        kwiatonomousApi: KwiatonomousApi,
        kwiatonomousDatabase: KwiatonomousDatabase,
    ): DeviceRepository {
        return DeviceRepositoryImpl(kwiatonomousApi, kwiatonomousDatabase)
    }

    @Provides
    @Singleton
    fun provideDeviceUpdateRepository(
        kwiatonomousApi: KwiatonomousApi,
        kwiatonomousDatabase: KwiatonomousDatabase,
    ): DeviceUpdateRepository {
        return DeviceUpdateRepositoryImpl(kwiatonomousApi, kwiatonomousDatabase)
    }

    @Provides
    @Singleton
    fun provideDeviceConfigurationRepository(
        kwiatonomousApi: KwiatonomousApi,
        kwiatonomousDatabase: KwiatonomousDatabase,
    ): DeviceConfigurationRepository {
        return DeviceConfigurationRepositoryImpl(kwiatonomousApi, kwiatonomousDatabase)
    }

    @Provides
    @Singleton
    fun provideDeviceEventRepository(
        kwiatonomousApi: KwiatonomousApi,
        kwiatonomousDatabase: KwiatonomousDatabase,
    ): DeviceEventRepository {
        return DeviceEventRepositoryImpl(kwiatonomousApi, kwiatonomousDatabase)
    }

    @Provides
    @Singleton
    fun provideAppPreferencesRepository(
        gson: Gson,
        preferencesDataStore: DataStore<Preferences>,
    ): AppPreferencesRepository {
        return AppPreferencesRepositoryImpl(gson, preferencesDataStore)
    }

    @Provides
    @Singleton
    fun provideNetworkPreferencesRepository(
        preferencesDataStore: DataStore<Preferences>,
    ): NetworkPreferencesRepository {
        return NetworkPreferencesRepositoryImpl(preferencesDataStore)
    }

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideLoginManager(
        userRepository: UserRepository,
        networkPreferencesRepository: NetworkPreferencesRepository,
    ): AuthManager {
        return AuthManager(userRepository, networkPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideNotificationsManager(): NotificationsManager {
        return NotificationsManager()
    }

    @Provides
    @Singleton
    fun provideKwiatonomousWorkManager(
        @ApplicationContext applicationContext: Context,
    ): KwiatonomousWorkManager {
        return KwiatonomousWorkManager(applicationContext)
    }
}