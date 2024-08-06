package com.app.photomaker.tracking

import com.alo.ringo.tracking.BaseEventDefinition
import com.alo.ringo.tracking.EventDefinitionManager

class MakerEventDefinition : BaseEventDefinition() {
    private val eventNameList = arrayListOf(
        EVENT_EV2_G2_CLICK_TEMPLATE,
        EVENT_EV2_G2_CLICK_CREATE_VIDEO,
        EVENT_EV2_G2_SAVE_VIDEO,
        EVENT_EV2_G2_SET_VIDEO,
        EVENT_EV2_G2_LOAD_PREVIEW,
        EVENT_EV2_G8_CLICK_BTN_ROTATE,
        EVENT_EV2_G8_CLICK_BTN_FILTER,
        EVENT_EV2_G8_CLICK_BTN_CHANGE_ANIMATION,
        EVENT_EV2_G5_LOAD_PREVIEW
    )

    init {
        val eventDefinition: BaseEventDefinition = MakerEventDefinition()
        EventDefinitionManager.register(eventDefinition)
    }

    override fun getEventNameList(): List<String> = eventNameList

    companion object {

        //Content - GROUP 2
        const val EVENT_EV2_G2_CLICK_TEMPLATE = "ev2_g2_click_template"
        const val EVENT_EV2_G2_CLICK_CREATE_VIDEO = "ev2_g2_click_create_video"
        const val EVENT_EV2_G2_SAVE_VIDEO = "ev2_g2_save_video"
        const val EVENT_EV2_G2_SET_VIDEO = "ev2_g2_set_video"

        //Configs - Group 5
        const val EVENT_EV2_G2_LOAD_PREVIEW = "ev2_g2_load_preview"

        //Other - GROUP 8
        const val EVENT_EV2_G8_CLICK_BTN_ROTATE = "ev2_g8_click_btn_rotate"
        const val EVENT_EV2_G8_CLICK_BTN_FILTER = "ev2_g8_click_btn_filter"
        const val EVENT_EV2_G8_CLICK_BTN_CHANGE_ANIMATION = "ev2_g8_click_btn_change_animation"

        //CONFIGS - GROUP 5
        const val EVENT_EV2_G5_LOAD_PREVIEW = "ev2_g5_load_preview"
    }
}