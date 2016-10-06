package vn.nhan.phiendich;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

import java.util.Date;

import vn.nhan.phiendich.model.AuthenticationModel;
import vn.nhan.phiendich.model.BaseModel;

/**
 * Created by Nhan on 26/9/2016.
 */

public class AppManager {
    private static final String FONT_SIZE = "vn.nhan.phiendich.FontSize";
    private static final String AUTHENTICATION = "vn.nhan.phiendich.Authentication";
    private static final Gson GS = new Gson();
    public static Activity sharedPreferencesActive;
    public static AuthenticationModel authenModel;
    public static String IMEI;
    public static int ONLINE_COUNT;


    public static Date selectedDate = new Date();
    public static int fontSize = 15;

    public static void changeFontSize(int fontSize) {
        if (sharedPreferencesActive == null) {
            return;
        }
        AppManager.fontSize = fontSize;
        SharedPreferences sharedPref = sharedPreferencesActive.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(FONT_SIZE, AppManager.fontSize);
        editor.commit();
    }

    public static void initData(Activity activity) {
        if (activity == null) {
            return;
        }
        sharedPreferencesActive = activity;
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        fontSize = sharedPref.getInt(FONT_SIZE, fontSize);
        String auth = sharedPref.getString(AUTHENTICATION, null);
        if (auth != null) {
            authenModel = GS.fromJson(auth, AuthenticationModel.class);
        }
        TelephonyManager mTelephonyMgr = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = mTelephonyMgr.getDeviceId();
    }

    public static boolean loginSuccess() {
        return authenModel != null && authenModel.status.equals(BaseModel.STATUS_OK);
    }

    public static void logout() {
        if (sharedPreferencesActive == null || !loginSuccess()) {
            return;
        }
        authenModel = null;
        SharedPreferences sharedPref = sharedPreferencesActive.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(AUTHENTICATION);
        editor.commit();
    }

    public static void saveLogin() {
        if (sharedPreferencesActive == null || !loginSuccess()) {
            return;
        }
        SharedPreferences sharedPref = sharedPreferencesActive.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AUTHENTICATION, GS.toJson(authenModel));
        editor.commit();
    }
}
