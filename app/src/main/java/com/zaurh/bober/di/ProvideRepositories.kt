package com.zaurh.bober.di

import android.content.SharedPreferences
import com.zaurh.bober.data.repository.AuthRepoImpl
import com.zaurh.bober.data.repository.TenorRepoImpl
import com.zaurh.bober.data.repository.UserRepoImpl
import com.zaurh.bober.data.tenor.TenorApi
import com.zaurh.bober.data.user.UserApi
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.TenorRepo
import com.zaurh.bober.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvideRepositories {

    @Provides
    @Singleton
    fun provideUserRepository(
        sharedPreferences: SharedPreferences,
        api: UserApi
    ): UserRepository {
        return UserRepoImpl(api = api, prefs = sharedPreferences)
    }


    @Provides
    @Singleton
    fun provideAuthRepo(
        sharedPreferences: SharedPreferences,
        api: UserApi
    ): AuthRepo {
        return AuthRepoImpl(api = api, prefs = sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideTenorRepo(
        api: TenorApi
    ): TenorRepo {
        return TenorRepoImpl(api = api)
    }
}