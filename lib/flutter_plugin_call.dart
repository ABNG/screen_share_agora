import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPluginCall {
  static const MethodChannel _channel =
      const MethodChannel('flutter_plugin_call');

  // static Future<String> get platformVersion async {
  //   final String version = await _channel.invokeMethod('getPlatformVersion');
  //   return version;
  // }

  static Future<void> joinChannel(
      {required String name,required String id,required String token}) async {
    await _channel
        .invokeMethod('join', {"name": name, "id": id, "token": token});
  }

  static Future<void> get leaveChannel async {
    await _channel.invokeMethod('leave');
  }

  // static Future<void> get openCamera async {
  //   await _channel.invokeMethod('camera');
  // }
  //
  // static Future<void> get openRender async {
  //   await _channel.invokeMethod('render');
  // }
  //
  // static Future<void> get openScreenShare async {
  //   await _channel.invokeMethod('screenshare');
  // }
  //
  // static Future<void> get openSwitchCamera async {
  //   await _channel.invokeMethod('switchcamera');
  // }

  static Future<void> get openMute async {
    await _channel.invokeMethod('mute');
  }
}
