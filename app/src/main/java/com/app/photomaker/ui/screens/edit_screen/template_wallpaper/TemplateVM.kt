package com.app.photomaker.ui.screens.edit_screen.template_wallpaper

import androidx.lifecycle.ViewModel
import com.app.photomaker.repository.EditorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TemplateVM @Inject constructor(editorRepository: EditorRepository) : ViewModel() {
}