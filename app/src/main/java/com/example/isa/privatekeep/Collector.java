package com.example.isa.privatekeep;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

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
    Writer log;

    @Override
    public void onServiceConnected() {
        openLogFile();
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        try {
            log.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                Log.i(TAG, "TYPE_WINDOW_STATE_CHANGED");
//                printNodeInformationRecursive(getRootInActiveWindow());
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.i(TAG, "TYPE_VIEW_CLICKED");
                printNodeInformationRecursive(getRootInActiveWindow());
                break;
            default:
                break;
        }
    }

    private void printNodeInformationRecursive(AccessibilityNodeInfo node) {
        try {
            printNodeInformationRecursiveWithHeightANDINDEX(node, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printNodeInformationRecursiveWithHeightANDINDEX(AccessibilityNodeInfo node, int height, int index) throws Exception {
        if (node == null) {
            Log.e(TAG, "get source error");
            throw new Exception();
        } else {
            write2Log(node, height, index);
            write2Log2(node, height, index);
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
        stringBuilder.append(node.getContentDescription());
        stringBuilder.append(" |||***||| ");
        stringBuilder.append(node.getText());

        Log.i(TAG, stringBuilder.toString());
    }

    private void write2Log2(AccessibilityNodeInfo node, int height, int index) {
        try {
            for (int i = 1; i < height; ++i) {
                log.write("|-");
            }
            log.write(String.format("%d : %s\n********************\n", index, node.getContentDescription()));
            for (int i = 0; i < height; ++i) {
                log.write("  ");
            }
            log.write(String.format(" : %s\n", node.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openLogFile() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "log.txt");
        try {
            FileWriter fw = new FileWriter(file);
            log = new BufferedWriter(fw);
            log = fw;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
