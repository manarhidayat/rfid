package com.example.uhf_bt.di

import com.example.uhf_bt.opname.OpnameRepository
import com.example.uhf_bt.opname.OpnameRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindOpnameRepository(
        opnameRepositoryImpl: OpnameRepositoryImpl
    ): OpnameRepository
}