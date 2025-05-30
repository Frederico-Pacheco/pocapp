package br.org.cesar.wificonnect.domain.di

import android.content.Context
import android.net.wifi.WifiManager
import br.org.cesar.wificonnect.domain.usecase.network.NetworkScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideNetworkScanner(
        @ApplicationContext context: Context,
        wifiManager: WifiManager
    ): NetworkScanner {
        return NetworkScanner(context, wifiManager)
    }
}