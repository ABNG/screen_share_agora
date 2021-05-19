package com.example.flutter_plugin_call.externalvideosource;


import android.opengl.EGLContext;

import com.example.flutter_plugin_call.gles.ProgramTextureOES;
import com.example.flutter_plugin_call.gles.core.EglCore;


public class GLThreadContext {
    public EglCore eglCore;
    public EGLContext context;
    public ProgramTextureOES program;
}
