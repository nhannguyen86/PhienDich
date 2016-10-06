package vn.nhan.phiendich.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import vn.nhan.phiendich.AppManager;
import vn.nhan.phiendich.model.OnlineModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StatusService extends IntentService {

    public StatusService() {
        super("StatusService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            new StatusLoader().execute();
            Log.d(getClass().getName(), "StatusService: get status online.");
        }
    }

    public static class StatusLoader extends AsyncTask<Void, Void, OnlineModel> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected OnlineModel doInBackground(Void... params) {
            OnlineModel model = WebserviceHelper.online(AppManager.IMEI);
            return model;
        }

        @Override
        protected void onPostExecute(OnlineModel model) {
            super.onPostExecute(model);
            if (model != null) {
                AppManager.ONLINE_COUNT = model.useronline;
            }
        }
    };
}
