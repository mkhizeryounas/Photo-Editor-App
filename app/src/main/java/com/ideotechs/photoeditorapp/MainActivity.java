package com.ideotechs.photoeditorapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.util.List;

import ly.img.android.ui.activities.CameraPreviewActivity;
import ly.img.android.ui.activities.CameraPreviewIntent;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.activities.PhotoEditorIntent;

public class MainActivity extends AppCompatActivity {

    public static int CAMERA_PREVIEW_RESULT = 1;
    private ImageButton cameraBtn,liveCameraBtn,editorBtn;
    private  String path;
    private File dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //storage paths
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PhotoEditorApp";
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        cameraBtn = (ImageButton)findViewById(R.id.camera);
        //liveCameraBtn = (ImageButton)findViewById(R.id.livecamera);
        editorBtn = (ImageButton)findViewById(R.id.editor);
        cameraBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        // Your action here on button click
                        new CameraPreviewIntent(MainActivity.this)
                            .setExportDir(dir.getPath())
                            .setExportPrefix("PhotoEditorApp_")
                            .setEditorIntent(
                                    new PhotoEditorIntent(MainActivity.this)
                                            .setExportDir(dir.getPath())
                                            .setExportPrefix("PhotoEditorApp_result_")
                                            .destroySourceAfterSave(true)
                            )
                            .startActivityForResult(CAMERA_PREVIEW_RESULT);

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
        editorBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        new ImagePicker.Builder(MainActivity.this)
                                .mode(ImagePicker.Mode.GALLERY)
                                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                                .directory(ImagePicker.Directory.DEFAULT)
                                .extension(ImagePicker.Extension.PNG)
                                .scale(600, 600)
                                .allowMultipleImages(false)
                                .enableDebuggingMode(true)
                                .build();

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
            String path = data.getStringExtra(CameraPreviewActivity.RESULT_IMAGE_PATH);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
            Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();

        }
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
            new PhotoEditorIntent(MainActivity.this)
                .setSourceImagePath(mPaths.get(0))
                .setExportDir(dir.getPath())
                .setExportPrefix("PhotoEditorApp_result_")
                .destroySourceAfterSave(true)
                .startActivityForResult(CAMERA_PREVIEW_RESULT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
