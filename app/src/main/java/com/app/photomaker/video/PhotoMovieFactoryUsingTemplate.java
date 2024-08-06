package com.app.photomaker.video;

import com.app.photomaker.data.model.Template;
import com.app.photomaker.enums.SegmentType;
import com.app.photomaker.video.template.TemplateDefault;
import com.app.photomaker.video.template.TemplateEight;
import com.app.photomaker.video.template.TemplateFour;
import com.app.photomaker.video.template.TemplateTen;
import com.app.photomaker.video.template.TemplateOne;
import com.hw.photomovie.PhotoMovie;
import com.hw.photomovie.model.PhotoData;
import com.hw.photomovie.segment.FitCenterScaleSegment;
import com.hw.photomovie.segment.FitCenterSegment;
import com.hw.photomovie.segment.GradientTransferSegment;
import com.hw.photomovie.segment.MoveTransitionSegment;
import com.hw.photomovie.segment.MovieSegment;
import com.hw.photomovie.segment.ScaleSegment;
import com.hw.photomovie.segment.ScaleTransSegment;
import com.hw.photomovie.segment.SingleBitmapSegment;
import com.hw.photomovie.segment.ThawSegment;
import com.hw.photomovie.segment.WindowSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoMovieFactoryUsingTemplate {
    public static Template preProcess(Template template, List<PhotoData> photoList) {

        Template myTemplate = template.clone();
        //2: number of segment each photo
        //each Photo 2 effects:
        // 1: display
        // 1: transition
        int limitIndex = photoList.size() - 2;
        myTemplate.getScript().removeIf(segmentObjectData -> segmentObjectData.getIndex() > limitIndex);
        return myTemplate;
    }

    public static PhotoMovie generatePhotoMovies(Template template, ArrayList<PhotoData> photoList){
        ArrayList<PhotoData> myCurrentPhotoDataList = new ArrayList<>();
        myCurrentPhotoDataList.addAll(photoList);
        Template myCurrentTemplate = preProcess(template, myCurrentPhotoDataList);
        switch (Objects.requireNonNull(template.getId())) {
            case "4":{
                /**
                 * Start with scale segment
                 */
                TemplateFour templateFour = new TemplateFour(myCurrentTemplate, myCurrentPhotoDataList);
                templateFour.handleEndSegmentObjectData();
                return templateFour.process();
            }

            case "8": {
                TemplateEight templateEight = new TemplateEight(myCurrentTemplate, myCurrentPhotoDataList);
                templateEight.handleEndSegmentObjectData();
                return templateEight.process();
            }
            case "10": {
                TemplateTen templateTen = new TemplateTen(myCurrentTemplate, myCurrentPhotoDataList);
                templateTen.handleEndSegmentObjectData();
                return templateTen.process();
            }
            case "1": {
                TemplateOne templateOne = new TemplateOne(myCurrentTemplate, myCurrentPhotoDataList);
                templateOne.handleEndSegmentObjectData();
                return templateOne.process();
            }
            // handle: 2,3,5,6,7,9,11
            default: {
                TemplateDefault templateDefault = new TemplateDefault(myCurrentTemplate, myCurrentPhotoDataList);
                templateDefault.handleEndSegmentObjectData();
                return templateDefault.process();
            }
        }
    }

    public static MovieSegment generateSegment(String type, int duration){
        SegmentType segmentType = SegmentType.valueOf(type);
        switch (segmentType){
            case FIT_CENTER_SEGMENT: {
                return new FitCenterSegment(duration).setBackgroundColor(0xFF323232);
            }
            case MOVE_TRANSITION_VERTICAL_SEGMENT: {
                return new MoveTransitionSegment(MoveTransitionSegment.DIRECTION_VERTICAL, duration);
            }
            case MOVE_TRANSITION_HORIZONTAL_SEGMENT:{
                return new MoveTransitionSegment(MoveTransitionSegment.DIRECTION_HORIZON, duration);
            }
            case SCALE_SEGMENT:{
                return new ScaleSegment(duration, 6, 1);
            }
            case SCALE_TRANS_SEGMENT: {
                return new ScaleTransSegment();
            }
            case SINGLE_BITMAP_SEGMENT:{
                return new SingleBitmapSegment(duration);
            }
            case FIT_CENTER_SCALE_SEGMENT:{
                return new FitCenterScaleSegment(duration, 1.05f, 1.1f);
            }
            case GRADIENT_TRANSFER_SEGMENT:{
                return new GradientTransferSegment(duration, 1.1f, 1.15f, 1.05f, 1.05f);
            }
            case WINDOW_SEGMENT:{
                return new WindowSegment(2.1f, 1f, 2.1f, -1f, -1.1f).removeFirstAnim();
            }
            case THAW_SEGMENT:{
                return new ThawSegment(duration, 0);
            }
        }
        return null;
    }
}