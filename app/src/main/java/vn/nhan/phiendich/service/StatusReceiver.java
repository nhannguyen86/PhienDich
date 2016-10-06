package vn.nhan.phiendich.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StatusReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 999;
    public static final long INTERVAL_STATUS = 5 * 60 * 1000;

    public StatusReceiver() {
        Log.d(getClass().getName(), "StatusReceiver: starting...");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, StatusService.class);
        context.startService(i);
    }
}
