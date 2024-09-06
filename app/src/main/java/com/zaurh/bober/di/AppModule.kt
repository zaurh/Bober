package com.zaurh.bober.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.zaurh.bober.data.location.DefaultLocationTracker
import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.repository.MessagesRepoImpl
import com.zaurh.bober.data.repository.WebSocketRepoImpl
import com.zaurh.bober.data.tenor.TenorApi
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.domain.repository.LocationTracker
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.util.Constants.BASE_URL
import com.zaurh.bober.util.Constants.TENOR_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        api: MessageApi,
        sharedPreferences: SharedPreferences,
    ): MessageRepository {
        return MessagesRepoImpl(api, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideWebsocketRepository(
        sharedPreferences: SharedPreferences,
        client: HttpClient
    ): WebSocketRepository {
        return WebSocketRepoImpl(client, sharedPreferences)
    }


    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    //Retrofit
    @Singleton
    @Provides
    @Named("base")
    fun provideBaseRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }


    @Singleton
    @Provides
    @Named("message")
    fun provideMessageRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @Named("tenor")
    fun provideTenorRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(TENOR_BASE_URL)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofitApi(@Named("base") retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }


    @Provides
    @Singleton
    fun provideMessageApi(@Named("message") retrofit: Retrofit): MessageApi {
        return retrofit.create(MessageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTenorApi(@Named("tenor") retrofit: Retrofit): TenorApi {
        return retrofit.create(TenorApi::class.java)
    }


    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("prefs", MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(
        application: Application
    ): FusedLocationProviderClient =
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