package vn.nhan.phiendich.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.nhan.phiendich.AppManager;
import vn.nhan.phiendich.model.MultiContentModel;
import vn.nhan.phiendich.model.SchedulerModel;

/**
 * Created by Nhan on 7/10/2016.
 */

public class DataBaseManager  extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "KTCGK";

    private static final int INTRODUCE_ID = -3;
    private static final int CONTRIBUTE_ID = -1;
    private static final int TEMPS_ID = -2;

    public DataBaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(getClass().getName(), "onCreate: init database");
        String history = "CREATE TABLE HISTORY ("
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,  CREATED_DATE NUMBER,"
                + " IS_SCHEDULER INTEGER, CONTENT BLOB )";
        db.execSQL(history);

        db.execSQL("CREATE INDEX date_idx ON HISTORY (CREATED_DATE ASC)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(getClass().getName(), "onUpgrade: update new version");
        db.execSQL("DROP TABLE IF EXISTS HISTORY");

        onCreate(db);
    }

    public boolean saveHistory(int id, Date date, boolean isScheduler, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{String.valueOf(id)};
        String existSql = "ID = ?";
        if (date != null) {
            args = new String[]{String.valueOf(date.getTime())};
            existSql = "CREATED_DATE = ?";
        }
        try {
            Cursor cursor = db.rawQuery("SELECT id FROM HISTORY WHERE " + existSql, args);
            ContentValues cv = new ContentValues();
            cv.put("CONTENT", content);
            if (cursor.moveToFirst()) {
                db.update("HISTORY", cv, existSql, args);
            } else {
                cv.put("ID", id);
                if (date != null) {
                    cv.put("CREATED_DATE", date.getTime());
                    cv.put("IS_SCHEDULER", isScheduler);
                }
                db.insert("HISTORY", null, cv);
            }
            return true;
        } catch (Exception e) {
            Log.d(getClass().getName(), "saveHistory: " + e.getMessage(), e);
        } finally {
            db.close();
        }

        return false;
    }

    private <T> T getHistoryModel(int id, Class<T> cls) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT CONTENT FROM HISTORY WHERE ID=?", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                String content = cursor.getString(0);
                if (content != null) {
                    return AppManager.GS.fromJson(content, cls);
                }
            }
        } catch (JsonSyntaxException e) {
            Log.d(getClass().getName(), "getHistoryModel(int): " + e.getMessage(), e);
        } finally {
            db.close();
        }
        return null;
    }

    private <T> T getHistoryModel(Date date, boolean isScheduler, Class<T> cls) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT CONTENT FROM HISTORY WHERE CREATED_DATE=? AND IS_SCHEDULER=?",
                    new String[]{String.valueOf(date.getTime()), String.valueOf(isScheduler?1:0)});
            if (cursor.moveToFirst()) {
                String content = cursor.getString(0);
                if (content != null) {
                    return AppManager.GS.fromJson(content, cls);
                }
            }
        } catch (JsonSyntaxException e) {
            Log.d(getClass().getName(), "getHistoryModel(Date, boolean): " + e.getMessage(), e);
        } finally {
            db.close();
        }
        return null;
    }

    public boolean saveIntroduce(MultiContentModel model) {
        if (model != null) {
            return saveHistory(INTRODUCE_ID, null, false, AppManager.GS.toJson(model));
        }
        return false;
    }

    public MultiContentModel getIntroduce() {
        return getHistoryModel(INTRODUCE_ID, MultiContentModel.class);
    }

    public boolean saveContribute(MultiContentModel model) {
        if (model != null) {
            return saveHistory(CONTRIBUTE_ID, null, false, AppManager.GS.toJson(model));
        }
        return false;
    }

    public MultiContentModel getContribute() {
        return getHistoryModel(CONTRIBUTE_ID, MultiContentModel.class);
    }

    public boolean saveTemps(MultiContentModel model) {
        if (model != null) {
            return saveHistory(TEMPS_ID, null, false, AppManager.GS.toJson(model));
        }
        return false;
    }

    public MultiContentModel getTemps() {
        return getHistoryModel(TEMPS_ID, MultiContentModel.class);
    }

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    public boolean saveScheduler(SchedulerModel model, boolean isScheduler) {
        if (model != null && model.data != null && model.data.length > 0) {
            try {
                Date date = df.parse(model.data[0].readingDate);
                return saveHistory(model.data[0].id, date, isScheduler, AppManager.GS.toJson(model));
            } catch (ParseException e) {
                Log.d(getClass().getName(), "saveScheduler: can't parse date - " + model.data[0].readingDate, e);
            }
        }
        return false;
    }

    public SchedulerModel getScheduler(int id) {
        return getHistoryModel(id, SchedulerModel.class);
    }

    public SchedulerModel getScheduler(Date date, boolean isScheduler) {
        return getHistoryModel(date, isScheduler, SchedulerModel.class);
    }

    public List<String[]> getSchedulers(boolean isScheduler) {
        List<String[]> re = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT ID, CREATED_DATE FROM HISTORY WHERE IS_SCHEDULER=? AND CREATED_DATE <> 0",
                    new String[]{String.valueOf(isScheduler?1:0)});
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String date = Utils.formatDate(new Date(cursor.getLong(1)));
                    re.add(new String[] {id, date});
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(getClass().getName(), "getSchedulers: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        return re;
    }
}
