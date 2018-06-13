package com.junda.user.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    Button mButton, mButton1;
    ImageView mImageView;
    /**
     * 相册选择
     **/
    public static final int PHOTO_REQUEST_PHOTO = 1;
    /**
     * 相机拍照
     **/
    public static final int PHOTO_REQUEST_CAMERA = 2;
    private File file;
    private String photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.mButton);
        mButton1 = findViewById(R.id.mButton1);
        mImageView = findViewById(R.id.mImageView);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromCamera();
            }
        });
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromPhoto();
            }
        });
    }

    /**
     * 从相册选择照片
     */
    private void fromPhoto() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_PHOTO);
    }

    private void fromCamera() {
        photoName = String.valueOf(System.currentTimeMillis());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + photoName + ".jpg");
        file.getParentFile().mkdirs();
        //老哥 注意修改mainfests文件的配置和这个一样
        Uri uri = FileProvider.getUriForFile(this, "com.junda.user.myapplication.fileprovider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_PHOTO:
                    if (data.getData() != null) {
                        Log.d("相册返回", "onActivityResult: " + data.getData());
                        File mfile = new File(getPath(data.getData()));
                        UpServerPhoto(mfile.getAbsolutePath());
                    }
                    break;
                case PHOTO_REQUEST_CAMERA:
                    Log.d("拍照返回", "onActivityResult: " + file.getAbsolutePath());
                    UpServerPhoto(file.getAbsolutePath());
                    break;
            }
        } else {
            Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
        }
    }


    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



}
