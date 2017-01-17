package vn.nhan.phiendich;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import vn.nhan.phiendich.model.AuthenticationModel;
import vn.nhan.phiendich.model.BaseModel;
import vn.nhan.phiendich.utils.DataBaseManager;
import vn.nhan.phiendich.utils.Utils;
import vn.nhan.phiendich.utils.WebserviceHelper;

/**
 * Created by Nhan on 26/9/2016.
 */

public class AppManager {
    private static final String FONT_SIZE = "vn.nhan.phiendich.FontSize";
    private static final String AUTHENTICATION = "vn.nhan.phiendich.Authentication";
    public static final Gson GS = new Gson();
    public static Activity sharedPreferencesActive;
    public static AuthenticationModel authenModel;
    public static String IMEI;
    public static int ONLINE_COUNT;
    private static boolean OFFLINE_MODE;
    private static DataBaseManager dataBaseManager;


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
        // init font size
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        fontSize = sharedPref.getInt(FONT_SIZE, fontSize);
        // load login
        String auth = sharedPref.getString(AUTHENTICATION, null);
        if (auth != null) {
            authenModel = GS.fromJson(auth, AuthenticationModel.class);
            // redo login
            new AsyncTask<Void, Void, AuthenticationModel>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected AuthenticationModel doInBackground(Void... params) {
                    String pass = authenModel.pass;
                    AppManager.authenModel = WebserviceHelper.getLogin(authenModel.username, pass);
                    AppManager.authenModel.pass = pass;
                    return AppManager.authenModel;
                }

                @Override
                protected void onPostExecute(AuthenticationModel authenModel) {
                    super.onPostExecute(authenModel);
                    if (authenModel != null) {
                        if (authenModel.error != null) {
                            AppManager.authenModel = null;
                        } else {
                            AppManager.saveLogin();
                        }
                    }
                }
            }.execute();
        }
        // get IMEI
        TelephonyManager mTelephonyMgr = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = mTelephonyMgr.getDeviceId();

        // check offline mode
        OFFLINE_MODE = /*true;//*/!Utils.internetAvailable();

        // init DB
        dataBaseManager = new DataBaseManager(activity);
    }

    public static void testSim() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) sharedPreferencesActive
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imeiSIM1 = null, imeiSIM2 = null;
        try {
            imeiSIM1 = getDeviceIdBySlot(mTelephonyMgr, "getDeviceIdGemini", 0);
            imeiSIM2 = getDeviceIdBySlot(mTelephonyMgr, "getDeviceIdGemini", 1);
        } catch (Exception e) {
            try {
                imeiSIM1 = getDeviceIdBySlot(mTelephonyMgr, "getDeviceId", 0);
                imeiSIM2 = getDeviceIdBySlot(mTelephonyMgr, "getDeviceId", 1);
            } catch (Exception e1) {}
        }
        Log.d(AppManager.class.getName(), "initData: imei " + mTelephonyMgr.getDeviceId());
        Log.d(AppManager.class.getName(), "initData: sim1 " + imeiSIM1);
        Log.d(AppManager.class.getName(), "initData: sim2 " + imeiSIM2);
    }

    private static String getDeviceIdBySlot(TelephonyManager telephony, String predictedMethodName, int slotID) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String imei = null;
        try {
            Class<?> telephonyClass = TelephonyManager.class;

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                imei = ob_phone.toString();

            }
        } catch (Exception e) {
            throw e;
        }

        return imei;
    }

    public static DataBaseManager getDataBaseManager() {
        return dataBaseManager;
    }

    public static boolean isOfflineMode() {
        return OFFLINE_MODE;
    }

    public static boolean loginSuccess() {
        return authenModel != null && authenModel.status.equals(BaseModel.STATUS_OK) && authenModel.error == null;
    }

    public static boolean isDonated() {
        if (!loginSuccess()) {
            return false;
        }
        return authenModel.isDonated;
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
