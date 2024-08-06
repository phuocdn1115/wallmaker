package com.app.photomaker.base.handler

import com.app.photomaker.data.model.Wallpaper
import com.app.photomaker.enums.DataType
import com.app.photomaker.model.Data
import com.app.photomaker.model.NativeAds

object DataResponseHandler {
    fun process(dataResponse: List<Wallpaper>, listVideoData: ArrayList<Data>){
        for(position in dataResponse.indices){
            if(dataResponse[position].type == DataType.NATIVE_ADS.type)
                listVideoData.add(NativeAds())
            else if(dataResponse[position].type == DataType.VIDEO_TEMPLATE_TYPE.type){
                listVideoData.add(dataResponse[position].convertToTemplateObject())
            }
            else{
                val checkPoint = listVideoData.firstOrNull { data: Data -> data is Wallpaper && data.id == dataResponse[position].id}
                if(checkPoint != null)
                    continue
                listVideoData.add(dataResponse[position])
            }
        }
    }
}