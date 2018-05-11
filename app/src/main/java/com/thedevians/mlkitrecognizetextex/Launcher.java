package com.thedevians.mlkitrecognizetextex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Launcher extends AppCompatActivity {
    Spinner SelectCategs;
    Button btnSelectAction;
    String index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        SelectCategs=(Spinner)findViewById(R.id.spinnerMain);
        btnSelectAction=(Button)findViewById(R.id.btnSelectAction);
        String arr[]= new String[]{"RecognizeText","LabelImages","RecognizeLandmarks","DetectFaces","ScanBarcodes"};
        ArrayAdapter<String> stringArrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arr);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SelectCategs.setAdapter(stringArrayAdapter);
        btnSelectAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index=""+SelectCategs.getSelectedItemPosition();
                startActivity(new Intent(Launcher.this,MainActivity.class).putExtra("index",index));
            }
        });
    }
}
