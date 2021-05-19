package com.example.flutter_plugin_call;

public interface Communicator {


     void joinChanel(String id, String name,String token);
     void leaveChannel();
     void Mute();
}
