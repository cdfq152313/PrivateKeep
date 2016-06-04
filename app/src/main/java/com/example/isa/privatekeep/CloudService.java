package com.example.isa.privatekeep;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CloudService extends Service {
    private final IBinder mBinder = new LocalBinder();

    public CloudService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // check connection
        return mBinder;
    }

    public class LocalBinder extends Binder {
        CloudService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CloudService.this;
        }
    }

}
