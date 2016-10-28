package vn.nhan.phiendich.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vn.nhan.phiendich.AppManager;

public class StatusReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 999;
    public static final long INTERVAL_STATUS = 5 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppManager.isOfflineMode()) {
            return;
        }
        Intent i = new Intent(context, StatusService.class);
        context.startService(i);
    }
}
