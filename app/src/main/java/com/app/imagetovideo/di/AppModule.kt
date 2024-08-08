package com.app.imagetovideo.di

import android.content.Context
import com.app.imagetovideo.PreferencesManager
import com.app.imagetovideo.RealmManager
import com.app.imagetovideo.ads.banner.BannerAdsManager
import com.app.imagetovideo.ads.nativeads.NativeAdsInFrameSaving
import com.app.imagetovideo.ads.nativeads.NativeAdsInHomeManager
import com.app.imagetovideo.ads.nativeads.NativeAdsSetSuccessManager
import com.app.imagetovideo.ads.openapp.OpenAppAdsManager
import com.app.imagetovideo.ads.rewarded.RewardedAdsManager
import com.app.imagetovideo.base.ConnectionLiveData
import com.app.imagetovideo.base.FirebaseManager
import com.app.imagetovideo.download.DownloadWallpaperManager
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

    /**Ads*/
    @Singleton
    @Provides
    fun providerNativeAdsInHomeManager(
        @ApplicationContext context: Context
    ) = NativeAdsInHomeManager(context)

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
    fun providerBannerAds(
        @ApplicationContext context: Context
    ) = BannerAdsManager(context)

    @Singleton
    @Provides
    fun providerOpenAppAdsManager(
        @ApplicationContext context: Context
    ) = OpenAppAdsManager(context)

    @Singleton
    @Provides
    fun providerRewardedAdsManager(
        @ApplicationContext context: Context
    ) = RewardedAdsManager(context)

    @Singleton
    @Provides
    fun provideDownloadWallpaperManager(
        @ApplicationContext context: Context
    ) = DownloadWallpaperManager(context)

    @Singleton
    @Provides
    fun providerConnectionLiveData(
        @ApplicationContext context: Context
    ) = ConnectionLiveData(context)

    @Singleton
    @Provides
    fun providerFirebaseManager(
        @ApplicationContext context: Context
    ) = FirebaseManager(context)
}