package com.app.imagetovideo.navigation

import android.content.Context
import android.content.Intent
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.data.model.Wallpaper
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.ui.screens.edit_screen.EditorActivity
import com.app.imagetovideo.ui.screens.main.MainActivity
import com.app.imagetovideo.ui.screens.main.profile.PrivacyPolicyActivity
import com.app.imagetovideo.ui.screens.main.template.PreviewTemplateActivity
import com.app.imagetovideo.ui.screens.preview_home.PreviewActivity
import com.app.imagetovideo.ui.screens.set_home.SetWallpaperActivity
import com.app.imagetovideo.utils.EXTRA_DATA_MODEL
import com.app.imagetovideo.utils.EXTRA_TEMPLATE
import com.app.imagetovideo.utils.EXTRA_WALLPAPER_DOWNLOADED
import javax.inject.Singleton

@Singleton
class NavigationManager (private val context: Context) {

    fun navigationToEditorScreen(template: Template? = null) {
        val intent = Intent(context, EditorActivity::class.java)
        if (template != null) intent.putExtra(EXTRA_TEMPLATE, template)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigationToPreviewScreen(dataModel: Wallpaper) {
        val intent = Intent(context, PreviewActivity::class.java)
        intent.putExtra(EXTRA_DATA_MODEL, dataModel)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigationToSetWallpaperActivity(wallpaperDownloaded: WallpaperDownloaded) {
        val intent = Intent(context, SetWallpaperActivity::class.java)
        intent.putExtra(EXTRA_WALLPAPER_DOWNLOADED, wallpaperDownloaded )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigationToHomeScreen() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigationToPrivacyScreen() {
        val intent = Intent(context, PrivacyPolicyActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigationToPreviewTemplateActivity(template: Template) {
        val intent = Intent(context, PreviewTemplateActivity::class.java)
        intent.putExtra(EXTRA_TEMPLATE, template)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}