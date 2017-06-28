package com.broadcast.chatdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.broadcast.entities.ChatDo;

public class ImagePreviewActivity extends AppCompatActivity {

    private ImageView mPreviewedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_item_image_preview_layout);
        ChatDo chatDo = getIntent().getParcelableExtra("myData");
        registerViews();
        Bitmap bitmap = chatDo.getCameraImage();
        mPreviewedImage.setImageBitmap(bitmap);

    }

    private void registerViews() {
        mPreviewedImage = (ImageView)findViewById(R.id.chatItemPreviewedImage);
    }
}
