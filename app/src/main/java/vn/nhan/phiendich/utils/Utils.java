package vn.nhan.phiendich.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import vn.nhan.phiendich.AppManager;
import vn.nhan.phiendich.R;

/**
 * Created by Nhan on 25/9/2016.
 */

public class Utils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return  sdf.format(date);
    }

    public static String getContent(InputStream inputStream) {
        String line, content = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = br.readLine()) != null) {
                content += " " + line;
            }
            br.close();
        } catch (Exception e) {
            Log.e(Utils.class.getName(), "getContent: " + e.getMessage(), e);
        }
        return content;
    }

    public static Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for(Object activityRecord:activities.values()){
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if(!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            Log.e(Utils.class.getName(), "getActivity: " + e.getMessage(), e);
        }
        return null;
    }

    public static void makeText(final String mgs) {
        if (mgs == null) {
            return;
        }
        final Activity cur = getActivity();
        if (cur != null) {
            cur.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(cur, mgs, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static boolean isEmpty(Spinner spinner) {
        if (spinner.getSelectedItemPosition() == 0) {
            ((TextView) spinner.getSelectedView()).setError(AppManager.sharedPreferencesActive.getString(R.string.invalid_required));
            return true;
        }
        return false;
    }

    public static boolean isEmpty(TextView textView) {
        if (textView.getText().length() == 0) {
            textView.setError(AppManager.sharedPreferencesActive.getString(R.string.invalid_required));
            return true;
        }
        return false;
    }

    private static SimpleDateFormat schedulerDf = new SimpleDateFormat("yyyy-MM-dd");
    public static String formatSchedulerDate(Date date) {
        if (date == null) {
            return null;
        }
        return schedulerDf.format(date);
//        return "2016-08-20";
    }

    private static SimpleDateFormat titleDf = new SimpleDateFormat("EEE, dd/MM");
    public static String formatTitleDate(Date date) {
        if (date == null) {
            return null;
        }
        return titleDf.format(date);
    }

    public static String twoDigit(long num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return String.valueOf(num);
        }
    }
}
