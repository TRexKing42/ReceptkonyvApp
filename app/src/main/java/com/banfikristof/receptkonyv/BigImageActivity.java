package com.banfikristof.receptkonyv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

import com.banfikristof.receptkonyv.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BigImageActivity extends AppCompatActivity {

    private ImageView bigImage;
    private StorageReference img;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        init();

        bigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void init() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bigImage = findViewById(R.id.bigImageIv);
        key = getIntent().getStringExtra("key");

        img = FirebaseStorage.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getUid())
                .child(key)
                .child("main_img.jpg");
        Glide.with(this).load(img).into(bigImage);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Glide.with(this).load(img).into(bigImage);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Glide.with(this).load(img).into(bigImage);
        }
    }
}
