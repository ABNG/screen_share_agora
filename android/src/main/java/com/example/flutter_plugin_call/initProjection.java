package com.example.flutter_plugin_call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.flutter_plugin_call.externalvideosource.ExternalVideoInputManager;

public class initProjection extends Activity {
    private static final int PROJECTION_REQ_CODE = 1 << 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initProj();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initProj(){
        MediaProjectionManager mpm = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mpm.createScreenCaptureIntent();
        startActivityForResult(intent, PROJECTION_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROJECTION_REQ_CODE && resultCode == RESULT_OK) {

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                float percent = 0.f;
                float hp = ((float) metrics.heightPixels) - 1920.f;
                float wp = ((float) metrics.widthPixels) - 1080.f;

                if (hp < wp) {
                    percent = (((float) metrics.widthPixels) - 1080.f) / ((float) metrics.widthPixels);
                } else {
                    percent = (((float) metrics.heightPixels) - 1920.f) / ((float) metrics.heightPixels);
                }
                metrics.heightPixels = (int) (((float) metrics.heightPixels) - (metrics.heightPixels * percent));
                metrics.widthPixels = (int) (((float) metrics.widthPixels) - (metrics.widthPixels * percent));

                Middleware.getInstance(this).setVideoSource(data,metrics);
                finish();

        }
    }
}
