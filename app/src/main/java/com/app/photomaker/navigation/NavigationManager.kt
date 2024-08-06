package com.app.photomaker.navigation

import android.content.Context
import android.content.Intent
import com.app.photomaker.data.model.Template
import com.app.photomaker.data.model.Wallpaper
import com.app.photomaker.data.realm_model.WallpaperDownloaded
import com.app.photomaker.ui.screens.edit_screen.EditorActivity
import com.app.photomaker.ui.screens.main.MainActivity
import com.app.photomaker.ui.screens.main.profile.PrivacyPolicyActivity
import com.app.photomaker.ui.screens.main.template.PreviewTemplateActivity
import com.app.photomaker.ui.screens.preview_home.PreviewActivity
import com.app.photomaker.ui.screens.set_home.SetWallpaperActivity
import com.app.photomaker.utils.EXTRA_DATA_MODEL
import com.app.photomaker.utils.EXTRA_TEMPLATE
import com.app.photomaker.utils.EXTRA_WALLPAPER_DOWNLOADED
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