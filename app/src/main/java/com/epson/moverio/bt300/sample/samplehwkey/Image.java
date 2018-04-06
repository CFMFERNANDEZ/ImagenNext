package com.epson.moverio.bt300.sample.samplehwkey;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by CellFusion on 4/6/2018.
 */

public class Image {

    private String image_1;

    public String getImage_1() {
        return image_1;
    }

    public Bitmap getImage(){
        if( image_1 != null){
            String imageString = image_1;
            imageString = imageString.replace("data:image/jpeg;base64,","");
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT );
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
    }
}