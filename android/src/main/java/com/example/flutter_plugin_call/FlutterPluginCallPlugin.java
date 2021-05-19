package com.example.flutter_plugin_call;

import android.app.Activity;

import android.content.Context;

import android.util.Log;


import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


/** FlutterPluginCallPlugin */
public class FlutterPluginCallPlugin implements FlutterPlugin, MethodCallHandler , ActivityAware {
  private MethodChannel channel;
  private Context context;
  private Activity activity;
  private static final String TAG = FlutterPluginCallPlugin.class.getSimpleName();
  private String name="";
  private String id="";
  private String token="";
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_plugin_call");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }
    else if(call.method.equals("join")){
      name= call.argument("name");
      id= call.argument("id");
      token= call.argument("token");
      Log.i(TAG,name);
      Log.i(TAG,id);
      Log.i(TAG,token);
      Middleware.getInstance().joinChannel(id,name,token);

    }
    else if(call.method.equals("leave")){
      Middleware.getInstance().leaveChannel();
    }
//    else if(call.method.equals("camera")){
////      unbindVideoService();
////      handler.postDelayed(new Runnable() {
////        @Override
////        public void run() {
////          ENGINE.setVideoSource(new AgoraDefaultSource());
////          FlutterPluginCallPlugin.this.addLocalPreview();
////
////
////        }
////      }, 1000);
//    }
//    else if(call.method.equals("render")){
////      if (remoteUid == -1) {
////        return;
////      }
////      if (curRenderMode == RENDER_MODE_HIDDEN) {
////        curRenderMode = RENDER_MODE_FIT;
////
////      } else if (curRenderMode == RENDER_MODE_FIT) {
////        curRenderMode = RENDER_MODE_HIDDEN;
////
////      }
//
//    }
//    else if(call.method.equals("screenshare")){
//
//
//    }
//    else if(call.method.equals("switchcamera")){
////      ENGINE.switchCamera();
//
//    }
    else if(call.method.equals("mute")){
      Middleware.getInstance().muteChannel();
    }
    else {
      result.notImplemented();
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {

  }

}
