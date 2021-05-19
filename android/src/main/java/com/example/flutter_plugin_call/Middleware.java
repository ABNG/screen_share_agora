package com.example.flutter_plugin_call;

import android.annotation.SuppressLint;
import android.media.projection.MediaProjection;

import io.flutter.Log;

public class Middleware {

    @SuppressLint("StaticFieldLeak")
    private static Middleware instance;
    private Communicator communicator;
    public static Middleware getInstance() {
        if (instance == null) {
            synchronized (Middleware.class) {
                if (instance == null) {
                    instance = new Middleware();
                }
            }
        }
        return instance;
    }
    public void setCallback(Communicator callback) {
        this.communicator = callback;
    }
    public void joinChannel(String id, String name,String token) {
        if (communicator != null) {
            communicator.joinChanel(id, name,token);
        }

    }
    public void leaveChannel() {
        if (communicator != null) {
            communicator.leaveChannel();
        }

    }
    public void muteChannel() {
        if (communicator != null) {
            communicator.Mute();
        }

    }
}
