package com.example.isa.privatekeep;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * Created by isa on 2016/3/23.
 */
public class Collector extends AccessibilityService {
    static final String TAG = "Collector";
    static final String[] packages = {
            "com.example.isa.privatekeep",
            "com.facebook.katana",
            "com.facebook.orca",
    };

    HandlerThread handlerThread;
    Facebook facebook;

    @Override
    public void onServiceConnected() {
        // create thread
        handlerThread = new HandlerThread("analyzer");
        handlerThread.start();

        // add analyzer
        facebook = new Facebook();
        facebook.start(handlerThread.getLooper());

        // set packages
        AccessibilityServiceInfo info = getServiceInfo();
        info.packageNames = packages;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // close analyzer
        facebook.quit();
        // close thread
        handlerThread.quit();

        return true;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        String packageName = event.getPackageName().toString();
        if (facebook.getPackageName().equals(packageName)) {
            if(eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
                Log.i(TAG, String.format("X:%d , Y:%d", record.getScrollX(), record.getScrollY()) );
//                Log.i(TAG, String.format("X:%d , Y:%d", event.getScrollX(), event.getScrollY()) );
                facebook.sendMessage(eventType, event.getSource());
            }

        }
//        switch(eventType){
//            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
//                Log.i(TAG, "TYPE_VIEW_LONG_CLICKED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SELECTED:

//                Log.i(TAG, "TYPE_VIEW_SELECTED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                Log.i(TAG, "TYPE_VIEW_TEXT_CHANGED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                Log.i(TAG, "TYPE_VIEW_TEXT_SELECTION_CHANGED");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
//                Log.i(TAG, "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                Log.i(TAG, "TYPE_VIEW_CLICKED");
//                break;
//        }
    }
}
