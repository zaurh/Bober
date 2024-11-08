package com.zaurh.bober.di

import android.content.Context
import com.zaurh.bober.data.message.room.MessageDatabase
import com.zaurh.bober.data.repository.AuthRepoImpl
import com.zaurh.bober.data.repository.TenorRepoImpl
import com.zaurh.bober.data.repository.UserRepoImpl
import com.zaurh.bober.data.tenor.TenorApi
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.TenorRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.preferences.BoberPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvideRepositories {

    @Provides
    @Singleton
    fun provideUserRepository(
        boberPreferences: BoberPreferences,
        api: UserApi,
        db: MessageDatabase
    ): UserRepository {
        return UserRepoImpl(api = api, boberPreferences = boberPreferences, userDao = db.userDao())
    }


    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = BoberPreferences(context = context)

    @Provides
    @Singleton
    fun provideAuthRepo(
        boberPreferences: BoberPreferences,
        api: UserApi,
        db: MessageDatabase
    ): AuthRepo {
        return AuthRepoImpl(api = api, boberPreferences = boberPreferences, userDao = db.userDao())
    }

    @Provides
    @Singleton
    fun provideTenorRepo(
        api: TenorApi
    ): TenorRepo {
        return TenorRepoImpl(api = api)
    }
}