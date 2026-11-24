package com.zaurh.bober.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zaurh.bober.data.location.DefaultLocationTracker
import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.remote.AuthInterceptor
import com.zaurh.bober.data.remote.NetworkJson
import com.zaurh.bober.data.tenor.TenorApi
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.domain.repository.LocationTracker
import com.zaurh.bober.util.Constants.BASE_URL
import com.zaurh.bober.util.Constants.TENOR_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Shared by REST calls and the chat socket so both authenticate the same way. */
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            // Image uploads can be large and slow on mobile networks.
            .writeTimeout(60, TimeUnit.SECONDS)
            // Detects dead chat connections; the backend also pings every 15s.
            .pingInterval(20, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @Named("backend")
    fun provideBackendRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(NetworkJson.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @Named("tenor")
    fun provideTenorRetrofit(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(TENOR_BASE_URL)
            .build()

    @Provides
    @Singleton
    fun provideUserApi(@Named("backend") retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideMessageApi(@Named("backend") retrofit: Retrofit): MessageApi =
        retrofit.create(MessageApi::class.java)

    @Provides
    @Singleton
    fun provideTenorApi(@Named("tenor") retrofit: Retrofit): TenorApi =
        retrofit.create(TenorApi::class.java)

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences =
        app.getSharedPreferences("prefs", MODE_PRIVATE)

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    fun providesLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        application: Application
    ): LocationTracker = DefaultLocationTracker(
        fusedLocationProviderClient = fusedLocationProviderClient,
        application = application
    )
}
