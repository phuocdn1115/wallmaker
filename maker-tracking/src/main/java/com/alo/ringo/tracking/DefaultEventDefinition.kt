package com.alo.ringo.tracking

class DefaultEventDefinition : BaseEventDefinition() {
    private val eventNameList = arrayListOf(
        EVENT_EV2_G1_ADS_LOAD,
        EVENT_EV2_G1_ADS_SHOW,
        EVENT_EV2_G1_REWARD,
        EVENT_EV2_G5_LOAD_CONFIG
    )

    override fun getEventNameList(): List<String> = eventNameList

    companion object {
        //ADS - GROUP 1
        const val EVENT_EV2_G1_ADS_LOAD = "ev2_g1_ads_load"
        const val EVENT_EV2_G1_ADS_SHOW = "ev2_g1_ads_show"
        const val EVENT_EV2_G1_REWARD = "ev2_g1_reward"

        //CONFIGS - GROUP 5
        const val EVENT_EV2_G5_LOAD_CONFIG = "ev2_g5_load_config"
    }
}