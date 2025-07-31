package br.org.cesar.wificonnect.data.local.di

import br.org.cesar.wificonnect.data.local.repository.CallRepository
import br.org.cesar.wificonnect.domain.repository.ICallRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideCallRepository(): ICallRepository {
        return CallRepository()
    }
}