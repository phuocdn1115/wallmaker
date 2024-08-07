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
import com.app.imagetovideo.tracking.EventTrackingManager
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
        @ApplicationContext context: Context,
        eventTrackingManager: EventTrackingManager
    ) = NativeAdsInHomeManager(context, eventTrackingManager)

    @Singleton
    @Provides
    fun providerNativeAdsSetSuccessManager(
        @ApplicationContext context: Context,
        eventTrackingManager: EventTrackingManager
    ) = NativeAdsSetSuccessManager(context, eventTrackingManager)

    @Singleton
    @Provides
    fun providerNativeAdsInFrameSaving(
        @ApplicationContext context: Context,
        eventTrackingManager: EventTrackingManager
    ) = NativeAdsInFrameSaving(context, eventTrackingManager)

    @Singleton
    @Provides
    fun providerBannerAds(
        @ApplicationContext context: Context,
        eventTrackingManager: EventTrackingManager
    ) = BannerAdsManager(context, eventTrackingManager)

    @Singleton
    @Provides
    fun providerOpenAppAdsManager(
        @ApplicationContext context: Context,
        eventTrackingManager: EventTrackingManager
    ) = OpenAppAdsManager(context, eventTrackingManager)

    @Singleton
    @Provides
    fun providerRewardedAdsManager(
        @ApplicationContext context: Context,
        eventTrackingManager: EventTrackingManager
    ) = RewardedAdsManager(context, eventTrackingManager)

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

    @Singleton
    @Provides
    fun providerEventTrackingManager(
        @ApplicationContext context: Context
    ) = EventTrackingManager(context)
}