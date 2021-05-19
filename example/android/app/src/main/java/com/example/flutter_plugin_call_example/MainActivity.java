package com.example.flutter_plugin_call_example;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.flutter_plugin_call.Communicator;
import com.example.flutter_plugin_call.FlutterPluginCallPlugin;
import com.example.flutter_plugin_call.Middleware;
import com.example.flutter_plugin_call.externalvideosource.ExternalVideoInputManager;
import com.example.flutter_plugin_call.externalvideosource.ExternalVideoInputService;

import io.agora.advancedvideo.externvideosource.IExternalVideoInputService;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.mediaio.AgoraDefaultSource;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.flutter.embedding.android.FlutterActivity;

import static com.example.flutter_plugin_call.externalvideosource.Constant.ENGINE;
import static com.example.flutter_plugin_call.externalvideosource.Constant.TEXTUREVIEW;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_STARTING;
import static io.agora.rtc.Constants.RENDER_MODE_HIDDEN;

public class MainActivity extends FlutterActivity implements Communicator {
    private static final String TAG = MainActivity.class.getSimpleName();
    public boolean mMuted=false;
    private int  myUid,remoteUid = -1;
    private boolean joined = false;
    private static final int PROJECTION_REQ_CODE = 1 << 2;
    private static final int DEFAULT_SHARE_FRAME_RATE = 15;
    protected Handler handler;
    private IExternalVideoInputService mService;
    private VideoInputServiceConnection mServiceConnection;
    private int curRenderMode = RENDER_MODE_HIDDEN;
    private VideoEncoderConfiguration.ORIENTATION_MODE curMirrorMode =
            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Middleware.getInstance().setCallback(this);
    }
    @Override
    public void joinChanel(String id, String name, String token) {
        try {
            handler = new Handler(Looper.getMainLooper());
            ENGINE = RtcEngine.create(this, id, iRtcEngineEventHandler);
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        TEXTUREVIEW = new TextureView(this);
        joinChannel(name,token);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            bindVideoService();
        } else {
            showAlert("The system version is too low to use this feature; please use Android 5.0 System.");
        }
    }

    @Override
    public void leaveChannel() {
        ENGINE.leaveChannel();
        TEXTUREVIEW = null;
        unbindVideoService();

    }

    @Override
    public void Mute() {
      mMuted=!mMuted;
      ENGINE.muteLocalAudioStream(mMuted);
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROJECTION_REQ_CODE && resultCode == RESULT_OK) {
            try {
                DisplayMetrics metrics = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

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

                data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_WIDTH, metrics.widthPixels);
                data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_HEIGHT, metrics.heightPixels);
                data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_DPI, (int) metrics.density);
                data.putExtra(ExternalVideoInputManager.FLAG_FRAME_RATE, DEFAULT_SHARE_FRAME_RATE);
                setVideoConfig(ExternalVideoInputManager.TYPE_SCREEN_SHARE, metrics.widthPixels, metrics.heightPixels);
                mService.setExternalVideoInput(ExternalVideoInputManager.TYPE_SCREEN_SHARE, data);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        unbindVideoService();
        TEXTUREVIEW = null;

        if (ENGINE != null) {
            ENGINE.leaveChannel();
        }
        handler.post(RtcEngine::destroy);
        ENGINE = null;
        super.onDestroy();
    }
    private void setVideoConfig(int sourceType, int width, int height) {
        switch (sourceType) {
            case ExternalVideoInputManager.TYPE_LOCAL_VIDEO:
            case ExternalVideoInputManager.TYPE_SCREEN_SHARE:
                curMirrorMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
                break;
            default:
                curMirrorMode = VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
                break;
        }

        Log.e(TAG, "SDK encoding ->width:" + width + ",height:" + height);

        ENGINE.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                new VideoEncoderConfiguration.VideoDimensions(width, height),
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE, curMirrorMode
        ));

    }

    private void addLocalPreview() {

      
        // Create render view by RtcEngine
        SurfaceView surfaceView = RtcEngine.CreateRendererView(this);
        // Setup local video to render your local camera preview
        ENGINE.setupLocalVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, 0));
    }

    private void setRemotePreview(Context context) {

        SurfaceView remoteSurfaceView = RtcEngine.CreateRendererView(context);
        remoteSurfaceView.setZOrderMediaOverlay(true);

        ENGINE.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, curRenderMode, remoteUid));
    }
    private void joinChannel(String name,String token) {
        addLocalPreview();

        ENGINE.setParameters("{\"che.video.mobile_1080p\":true}");

        ENGINE.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);

        ENGINE.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

        ENGINE.enableVideo();
        ENGINE.setVideoSource(new AgoraDefaultSource());

        ENGINE.setDefaultAudioRoutetoSpeakerphone(false);
        ENGINE.setEnableSpeakerphone(false);


        ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        option.autoSubscribeVideo = true;
        int res = ENGINE.joinChannel(token, name, "Extra Optional Data", 0, option);
        if (res != 0) {
            showAlert(RtcEngine.getErrorDescription(Math.abs(res)));
            return;
        }

    }

    private void bindVideoService() {
        Intent intent = new Intent();
        intent.setClass(this, ExternalVideoInputService.class);
        mServiceConnection = new VideoInputServiceConnection();
        this.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindVideoService() {
        if (mServiceConnection != null) {
            this.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }



    private class VideoInputServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = (IExternalVideoInputService) iBinder;
            /**Start the screen recording service of the system*/
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                MediaProjectionManager mpm = (MediaProjectionManager)
                        getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Intent intent = mpm.createScreenCaptureIntent();
                startActivityForResult(intent, PROJECTION_REQ_CODE);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    }
    protected void showAlert(String message)
    {

        showLongToast(message);
    }

    protected final void showLongToast(final String msg)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    private final IRtcEngineEventHandler iRtcEngineEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onWarning(int warn) {
            Log.w(TAG, String.format("onWarning code %d message %s", warn, RtcEngine.getErrorDescription(warn)));
        }

        @Override
        public void onError(int err) {
            Log.e(TAG, String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err)));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.i(TAG, String.format("onJoinChannelSuccess channel %s uid %d", channel, uid));
            showLongToast(String.format("onJoinChannelSuccess channel %s uid %d", channel, uid));
            myUid = uid;
            joined = true;
            handler.post(() -> {

            });
        }

        @Override
        public void onLocalVideoStateChanged(int localVideoState, int error) {
            super.onLocalVideoStateChanged(localVideoState, error);
            if (localVideoState == 1) {
                Log.e(TAG, "launch successfully");
            }
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            Log.i(TAG, "onRemoteVideoStateChanged:uid->" + uid + ", state->" + state);
            if (state == REMOTE_VIDEO_STATE_STARTING) {
                /**Check if the context is correct*/

                handler.post(() ->
                {
                    remoteUid = uid;
                    curRenderMode = RENDER_MODE_HIDDEN;
                    setRemotePreview(getApplicationContext());
                });
            }
        }

        @Override
        public void onRemoteVideoStats(RemoteVideoStats stats) {
            super.onRemoteVideoStats(stats);
            Log.d(TAG, "onRemoteVideoStats: width:" + stats.width + " x height:" + stats.height);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.i(TAG, "onUserJoined->" + uid);
            showLongToast(String.format("user %d joined!", uid));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.i(TAG, String.format("user %d offline! reason:%d", uid, reason));
            showLongToast(String.format("user %d offline! reason:%d", uid, reason));
            handler.post(() -> {
                ENGINE.setupRemoteVideo(new VideoCanvas(null, RENDER_MODE_HIDDEN, uid));

            });
        }
    };

}
