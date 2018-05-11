package com.thedevians.mlkitrecognizetextex;

import android.graphics.Bitmap;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;

public class FirebaseVisionImageInstance {
    public FirebaseVisionImage firebaseVisionImageInstance(Bitmap bitmap){
        FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);
        return image;
    }
}
