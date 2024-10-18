package com.app.imagetovideo.di

import android.content.Context
import com.app.imagetovideo.PreferencesManager
import com.app.imagetovideo.RealmManager
import com.app.imagetovideo.ads.nativeads.NativeAdsInFrameSaving
import com.app.imagetovideo.ads.nativeads.NativeAdsSetSuccessManager
import com.app.imagetovideo.ads.rewarded.RewardedAdsManager
import com.app.imagetovideo.base.ConnectionLiveData
import com.app.imagetovideo.navigation.NavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**Database local*/
    @Singleton
    @Provides
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ) = PreferencesManager(context)

    @Singleton
    @Provides
    fun provideRealmManager(
        @ApplicationContext context: Context
    ) = RealmManager(context)

    @Singleton
    @Provides
    fun provideNavigationManager(
        @ApplicationContext context: Context
    ) = NavigationManager(context)

    @Singleton
    @Provides
    fun providerNativeAdsSetSuccessManager(
        @ApplicationContext context: Context
    ) = NativeAdsSetSuccessManager(context)

    @Singleton
    @Provides
    fun providerNativeAdsInFrameSaving(
        @ApplicationContext context: Context
    ) = NativeAdsInFrameSaving(context)

    @Singleton
    @Provides
    fun providerRewardedAdsManager(
        @ApplicationContext context: Context
    ) = RewardedAdsManager(context)

    @Singleton
    @Provides
    fun providerConnectionLiveData(
        @ApplicationContext context: Context
    ) = ConnectionLiveData(context)

}