package com.example.isa.privatekeep;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
 * Created by isa on 2016/3/30.
 */

public abstract class Analyzer {
    protected Handler handler;

    public abstract String getPackageName();

    public abstract void start(Looper looper);

    public abstract void quit();

    public abstract void sendMessage(int eventType, AccessibilityNodeInfo node);
}

class Facebook extends Analyzer {
    static final String TAG = "FB_Anaylizer";
    static final String packageName = "com.facebook.katana";

    private AnalyzerTool tool;

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void start(Looper looper) {
        handler = new FacebookHandler(looper);
        tool = new AnalyzerTool(packageName);
//        tool.openLogFile(TAG+".json");
    }

    @Override
    public void quit() {
        tool.closeLogFile();
    }

    @Override
    public void sendMessage(int eventType, AccessibilityNodeInfo node) {
        Message msg = handler.obtainMessage();
        msg.what = eventType;
        msg.obj = node;
        handler.sendMessage(msg);
    }

    class FacebookHandler extends Handler {
        FacebookHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int eventType = msg.what;
            AccessibilityNodeInfo node = (AccessibilityNodeInfo) msg.obj;

            switch (eventType) {
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    tool.printNodeInformationRecursive(node);
                    break;
            }
        }
    }
}

class AnalyzerTool {
    Writer jsonLog = null;
    String TAG = "AnalyzerTool";
    String packageName = null;

    AnalyzerTool(String packageName) {
        this.packageName = packageName;
    }

    public void printNodeInformationRecursive(AccessibilityNodeInfo node) {
        try {
            printNodeInformationRecursiveWithHeightANDINDEX(node, 0, 0);
        } catch (Exception e) {
            Log.e(TAG, "get source error");
        }
    }

    private void printNodeInformationRecursiveWithHeightANDINDEX(AccessibilityNodeInfo node, int height, int index) throws Exception {
        if (node == null) {
            throw new Exception();
        } else {
            //choose write
            if (jsonLog == null) {
                write2Log(node, height, index);
            } else {
                write2json(node);
            }

            if (node.getChildCount() != 0) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    printNodeInformationRecursiveWithHeightANDINDEX(node.getChild(i), height + 1, i);
                }
            } else {
                return;
            }
        }
    }

    private void write2Log(AccessibilityNodeInfo node, int height, int index) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < height; ++i) {
            stringBuilder.append("|-");
        }

        stringBuilder.append(index);
        stringBuilder.append(" : ");
        stringBuilder.append(node.toString());
//        stringBuilder.append("Text:");
//        stringBuilder.append(node.getText());
//        stringBuilder.append(" ; ");
//        stringBuilder.append("ContentDescription : ");
//        stringBuilder.append(node.getContentDescription());
        Log.i(TAG, stringBuilder.toString());
    }

    private void write2json(AccessibilityNodeInfo node) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Text", node.getText());
            obj.put("ContentDescription", node.getContentDescription());
            if (obj.has("Text") || obj.has("ContentDescription")) {
                Log.i(TAG, obj.toString());
                jsonLog.write(",");
                jsonLog.write(obj.toString());
            } else {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openLogFile(String filename) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try {
            FileWriter fw = new FileWriter(file);
            jsonLog = new BufferedWriter(fw);
            jsonLog = fw;
            jsonLog.write("[{}");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeLogFile() {
        if (jsonLog == null)
            return;
        try {
            jsonLog.write("]");
            jsonLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}