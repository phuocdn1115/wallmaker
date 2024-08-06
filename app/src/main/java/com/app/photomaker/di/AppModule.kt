package com.app.photomaker.di

import android.content.Context
import com.app.photomaker.PreferencesManager
import com.app.photomaker.RealmManager
import com.app.photomaker.ads.banner.BannerAdsManager
import com.app.photomaker.ads.nativeads.NativeAdsInFrameSaving
import com.app.photomaker.ads.nativeads.NativeAdsInHomeManager
import com.app.photomaker.ads.nativeads.NativeAdsSetSuccessManager
import com.app.photomaker.ads.openapp.OpenAppAdsManager
import com.app.photomaker.ads.rewarded.RewardedAdsManager
import com.app.photomaker.base.ConnectionLiveData
import com.app.photomaker.base.FirebaseManager
import com.app.photomaker.download.DownloadWallpaperManager
import com.app.photomaker.navigation.NavigationManager
import com.app.photomaker.tracking.EventTrackingManager
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