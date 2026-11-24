package com.zaurh.bober.di

import com.zaurh.bober.data.repository.AuthRepoImpl
import com.zaurh.bober.data.repository.MessagesRepoImpl
import com.zaurh.bober.data.repository.TenorRepoImpl
import com.zaurh.bober.data.repository.UserRepoImpl
import com.zaurh.bober.data.repository.WebSocketRepoImpl
import com.zaurh.bober.domain.repository.AuthRepo
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.domain.repository.TenorRepo
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProvideRepositories {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepoImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepo(impl: AuthRepoImpl): AuthRepo

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessagesRepoImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindWebSocketRepository(impl: WebSocketRepoImpl): WebSocketRepository

    @Binds
    @Singleton
    abstract fun bindTenorRepo(impl: TenorRepoImpl): TenorRepo
}
