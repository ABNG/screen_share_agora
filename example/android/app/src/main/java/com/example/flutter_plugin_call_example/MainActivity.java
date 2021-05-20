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

public class MainActivity extends FlutterActivity{

}
