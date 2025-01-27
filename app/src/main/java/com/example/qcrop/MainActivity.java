package com.example.qcrop;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    cropping crop = new cropping();
    TextView coordtextview;
    List<List<Integer>> responses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenCVLoader.initDebug();
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.quesimageview);
        coordtextview = findViewById(R.id.coordtextview);
        final Bitmap bmp = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.p2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(crop.extractquestion(bmp));
                    coordtextview.setVisibility(View.INVISIBLE);
                    //coordtextview.setText(String.valueOf(crop.autocrop(bmp)));
                    //responses = crop.autocrop(bmp);
                    //Log.i("Rectangle Coordinates", responses.toString());
                    //coordtextview.setText(responses.toString());
                    //Log.i("Tapped","Imageview Tapped");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
