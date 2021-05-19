import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_plugin_call/flutter_plugin_call.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  TextEditingController channelName = TextEditingController();
  TextEditingController agoraID = TextEditingController();
  TextEditingController agoraToken = TextEditingController();
  Map<Permission, PermissionStatus> statuses;

  @override
  void initState() {
    super.initState();
  }

  Future<bool> checkPermission() async {
    statuses = await [
      Permission.storage,
      Permission.microphone,
      Permission.camera
    ].request();
    return statuses[Permission.storage] == PermissionStatus.granted &&
        statuses[Permission.microphone] == PermissionStatus.granted &&
        statuses[Permission.camera] == PermissionStatus.granted;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: [
            TextField(
              controller: channelName,
              decoration: InputDecoration(
                hintText: "channel name",
              ),
            ),
            TextField(
              controller: agoraID,
              decoration: InputDecoration(
                hintText: "agora app id",
              ),
            ),
            TextField(
              controller: agoraToken,
              decoration: InputDecoration(
                hintText: "agora access token",
              ),
            ),
            ElevatedButton(
                onPressed: () async {
                  bool val = await checkPermission();
                  if (val) {
                    await FlutterPluginCall.joinChannel(
                        name: channelName.text,
                        id: agoraID.text,
                        token: agoraToken.text);
                  } else {
                    debugPrint("please allow permission");
                  }
                },
                child: Text("Join")),
            // ElevatedButton(
            //     onPressed: () async {
            //       await FlutterPluginCall.openCamera;
            //     },
            //     child: Text("Camera")),
            // ElevatedButton(
            //     onPressed: () async {
            //       await FlutterPluginCall.openRender;
            //     },
            //     child: Text("Render")),
            // ElevatedButton(
            //     onPressed: () async {
            //       await FlutterPluginCall.openScreenShare;
            //     },
            //     child: Text("ScreenShare")),
            // ElevatedButton(
            //     onPressed: () async {
            //       await FlutterPluginCall.openSwitchCamera;
            //     },
            //     child: Text("SwitchCamera")),
            ElevatedButton(
                onPressed: () async {
                  await FlutterPluginCall.openMute;
                },
                child: Text("Mute")),
            ElevatedButton(
                onPressed: () async {
                  await FlutterPluginCall.leaveChannel;
                },
                child: Text("Leave")),
          ],
        ),
      ),
    );
  }
}
