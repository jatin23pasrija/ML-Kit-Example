package com.thedevians.mlkitrecognizetextex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.FirebaseVision.*;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector;
import com.google.firebase.ml.vision.common.*;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.text.*;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888; // field
    private Button btnCameraCall, btnGetText;
    Bitmap bitmap;
    ImageView img;
    TextView txtView;
    String resultIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        getDataIntent();
    }

    private void initialize() {
        txtView = (TextView) findViewById(R.id.textViewData);
        btnCameraCall = (Button) findViewById(R.id.btnCamera);
        btnGetText = (Button) findViewById(R.id.btntext);
        img = (ImageView) findViewById(R.id.image_f);
        btnCameraCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCamera();
            }
        });
        btnGetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getBitmapFromImageView().hasMipMap()){
                    switch (resultIndex){
                        case "0":
                            TextRecognization(getBitmapFromImageView());
                            break;
                        case "1":
                            LabelImages(getBitmapFromImageView());
                            break;
                        case "2":
                            RecognizeLandmarks(getBitmapFromImageView());
                            break;
                        case "3":
                            DetectFaces(getBitmapFromImageView());
                            break;
                        case "4":
                            ScanBarcodes(getBitmapFromImageView());
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Index Value Not Found :"+resultIndex, Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(MainActivity.this, "Start Camera First to get Image in ImageView", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //return bitmap from imageview
    private Bitmap getBitmapFromImageView(){
        BitmapDrawable drawable=(BitmapDrawable)img.getDrawable();
        Bitmap bitmap1 = drawable.getBitmap();
        return bitmap1;
    }
    private void getDataIntent(){
        Intent intent=getIntent();
        resultIndex=intent.getStringExtra("index");
    }
    //Call Camera
    private void callCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");//this is your bitmap image
            img.setImageBitmap(bitmap);
        }
    }

    //Text Recogition Processor
    private void TextRecognization(Bitmap bitmap){
        FirebaseVisionTextDetector detector=FirebaseVision.getInstance().getVisionTextDetector();
        Task<FirebaseVisionText> firebaseVisionTextTask=
                detector.detectInImage(new FirebaseVisionImageInstance().firebaseVisionImageInstance(bitmap))//FirebaseVisionImage Instance Created
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        for(FirebaseVisionText.Block block:firebaseVisionText.getBlocks()){
                            txtView.setText(block.getText());
                            //Get Those Lines Adjusted Here :)
//                            for (FirebaseVisionText.Line line: block.getLines()) {
//                                // ...
//                                for (FirebaseVisionText.Element element: line.getElements()) {
//                                    // ...
//                                }
//                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Handle Errors
                    }
                });

    }
    //Scan Barcodes Processor
    private void ScanBarcodes(Bitmap bitmap){
        FirebaseVisionBarcodeDetector detector=FirebaseVision.getInstance().getVisionBarcodeDetector();
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(new FirebaseVisionImageInstance().firebaseVisionImageInstance(bitmap))
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        // Task completed successfully
                        for (FirebaseVisionBarcode barcode: barcodes) {

                            String rawValue = barcode.getRawValue();
                            txtView.setText(rawValue);
                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case FirebaseVisionBarcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    txtView.setText(ssid+"\n"+password+"\n"+type);
                                    break;
                                case FirebaseVisionBarcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    txtView.setText(url);
                                    break;
                            }
                        }                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
    }
    //Detect Faces Processor
    private void DetectFaces(Bitmap bitmap){
        FirebaseVisionFaceDetector detector=FirebaseVision.getInstance().getVisionFaceDetector();
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(new FirebaseVisionImageInstance().firebaseVisionImageInstance(bitmap))
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // ...
                                        txtView.setText(""+faces.size());

                                        for (FirebaseVisionFace face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                            // nose available):
                                            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                            if (leftEar != null) {
                                                FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                                //txtView.setText("LftearPos: "+leftEarPos);

                                            }

                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float smileProb = face.getSmilingProbability();
                                                //txtView.setText("Smile Probability: "+smileProb);
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                //txtView.setText("righteyeopenprob: "+rightEyeOpenProb);

                                            }

                                            // If face tracking was enabled:
                                            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                int id = face.getTrackingId();
                                                //txtView.setText("id: "+id);
                                            }
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        txtView.append(""+e);
                                        Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                                    }
                                });
    }
    //RecognizeLandmarks Processor
    private void RecognizeLandmarks(Bitmap bitmap){
        FirebaseVisionCloudLandmarkDetector detector = FirebaseVision.getInstance()
                .getVisionCloudLandmarkDetector();
        Task<List<FirebaseVisionCloudLandmark>> result = detector.detectInImage(new FirebaseVisionImageInstance().firebaseVisionImageInstance(bitmap))
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLandmark>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
                        // Task completed successfully
                        // ...
                        for (FirebaseVisionCloudLandmark landmark: firebaseVisionCloudLandmarks) {

                            Rect bounds = landmark.getBoundingBox();
                            String landmarkName = landmark.getLandmark();
                            String entityId = landmark.getEntityId();
                            float confidence = landmark.getConfidence();
                            txtView.append(landmarkName);
                            // Multiple locations are possible, e.g., the location of the depicted
                            // landmark and the location the picture was taken.
                            for (FirebaseVisionLatLng loc: landmark.getLocations()) {
                                double latitude = loc.getLatitude();
                                double longitude = loc.getLongitude();
                                txtView.append(latitude+"\n"+longitude);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }
                });

    }
    //Label Images Processor
    private void LabelImages(Bitmap bitmap){
        FirebaseVisionLabelDetector detector=FirebaseVision.getInstance().getVisionLabelDetector();
        Task<List<FirebaseVisionLabel>> result =
                detector.detectInImage(new FirebaseVisionImageInstance().firebaseVisionImageInstance(bitmap))
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionLabel> labels) {
                                        // Task completed successfully
                                        // ...
                                        for (FirebaseVisionLabel label: labels) {
                                            String text = label.getLabel();
                                            String entityId = label.getEntityId();
                                            float confidence = label.getConfidence();
                                            txtView.append(text +"//"+entityId+"//"+confidence);
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                                        txtView.append("//"+e);
                                    }
                                });
    }

}
