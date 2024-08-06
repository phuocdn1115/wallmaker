package com.app.photomaker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PhotoEditorUtil {
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]{contrast, 0, 0, 0,
                brightness, 0, contrast, 0, 0, brightness, 0, 0, contrast,
                0, brightness, 0, 0, 0, 1, 0});

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
                bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    private void saveImageToCache(@NonNull Bitmap croppedBitmap, @NonNull Context context, @NonNull String pathOutput) throws FileNotFoundException {
        //path save cache tham khao 130 cropimagefragment
        int compressQuality = 90;
        OutputStream outputStream = null;
        ByteArrayOutputStream outStream = null;
        try {
            outputStream = new FileOutputStream(pathOutput, false);
            outStream = new ByteArrayOutputStream();
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outStream);
            outputStream.write(outStream.toByteArray());
            croppedBitmap.recycle();
        } catch (IOException exc) {

        } finally {
            BitmapLoadUtils.close(outputStream);
            BitmapLoadUtils.close(outStream);
        }
    }
}
