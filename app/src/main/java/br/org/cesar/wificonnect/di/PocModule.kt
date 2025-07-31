package br.org.cesar.wificonnect.di

import android.app.KeyguardManager
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.view.WindowManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PocModule {

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.applicationContext.getSystemService(ConnectivityManager::class.java)
    }

    @Provides
    @Singleton
    fun provideKeyguardManager(@ApplicationContext context: Context): KeyguardManager {
        return context.applicationContext.getSystemService(KeyguardManager::class.java)
    }

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.applicationContext.packageManager
    }

    @Provides
    @Singleton
    fun provideRoleManager(@ApplicationContext context: Context): RoleManager {
        return context.applicationContext.getSystemService(RoleManager::class.java)
    }

    @Provides
    @Singleton
    fun provideTelecomManager(@ApplicationContext context: Context): TelecomManager {
        return context.applicationContext.getSystemService(TelecomManager::class.java)
    }

    @Provides
    @Singleton
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager {
        return context.applicationContext.getSystemService(TelephonyManager::class.java)
    }

    @Provides
    @Singleton
    fun provideWifiManager(@ApplicationContext context: Context): WifiManager {
        return context.applicationContext.getSystemService(WifiManager::class.java)
    }

    @Provides
    @Singleton
    fun provideWindowManager(@ApplicationContext context: Context): WindowManager {
        return context.applicationContext.getSystemService(WindowManager::class.java)
    }
}