package com.example.whatsappclone.ImageViewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.whatsappclone.R;
import com.squareup.picasso.Picasso;

public class ImageviewerActivity extends AppCompatActivity {
    private ImageView  imageView;
    private String imageurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);
                imageView=findViewById(R.id.image_viewer);
                imageurl=getIntent().getStringExtra("imageurl");
        Picasso.get().load(imageurl).into(imageView);
    }
}