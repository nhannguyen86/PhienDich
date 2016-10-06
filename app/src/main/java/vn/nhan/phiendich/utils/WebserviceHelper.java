package vn.nhan.phiendich.utils;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import vn.nhan.phiendich.model.AuthenticationModel;
import vn.nhan.phiendich.model.BaseModel;
import vn.nhan.phiendich.model.MessageModel;
import vn.nhan.phiendich.model.MultiContentModel;
import vn.nhan.phiendich.model.NonceModel;
import vn.nhan.phiendich.model.OnlineModel;
import vn.nhan.phiendich.model.SchedulerModel;


/**
 * Created by Nhan on 29/9/2016.
 */

public class WebserviceHelper {
    private static final String HOST = "http://www.leolink.com.vn/";
    private static final String INTRODUCE = HOST + "api/ktcgk/get_page/?code=GT";
    private static final String TEMPS = HOST + "api/ktcgk/get_page/?code=DKSD";
    private static final String CONTRIBUTE = HOST + "api/ktcgk/get_page/?code=TBDG";
    private static final String LOGIN = HOST + "api/user/user_login/";
    private static final String FORGET_PASS = HOST + "api/user/retrieve_password/";
    private static final String NONCE = HOST + "api/get_nonce/?controller=user&method=register";
    private static final String REGISTER = HOST + "api/user/register/";
    private static final String SCHEDULER = HOST + "api/ktcgk/get_gkpv/";
    private static final String READING = HOST + "api/ktcgk/get_bdtl/";
    private static final String ONLINE = HOST + "api/user/get_count_user_online/";
    private static final String OFFLINE = HOST + "api/user/notify_off/";

    private static final String TAG = WebserviceHelper.class.getName();
    private static final Gson GS = new Gson();

    private static String callWS(String url, String[][] params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            if (params != null) {
                String p = null;
                for (String[] param: params) {
                    if (p == null) {
                        p = "?";
                    } else {
                        p += "&";
                    }
                    p += param[0] + "=" + URLEncoder.encode(param[1], "UTF-8").replaceAll("\\+", "%20");
                }
                url += p;
            }
            request.setHeader("Content-Type", "text/plain; charset=utf-8");
            request.setURI(new URI(url));
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            Log.e(TAG, "callWS: " + e.getMessage(), e);
        }
        return null;
    }

    private static String callPostWS(String link, String json) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            StringEntity se = new StringEntity(json, "utf-8");
            HttpPost post = new HttpPost(link);
            post.setHeader("Content-Type", "application/json; charset=utf-8");
            post.setEntity(se);
            HttpResponse response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String resut = EntityUtils.toString(entity);
                return resut;
            } else {
                String resut = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "callWS not successful [StatusCode]: " + response.getStatusLine().getStatusCode() + " [resut]:" + resut);
            }
        } catch (Exception e) {
            Log.e(TAG, "callPostWS: " + e.getMessage(), e);
        }
        return null;
    }

    private static <T extends BaseModel> T parseData(String url, String[][] params, Class<T> classOfT) {
        String str = callWS(url, params);
        if (str == null) {
            return null;
        }
        try {
            BaseModel re = GS.fromJson(str, classOfT);
            if (re.error != null) {
                Utils.makeText(re.error);
            }
            return (T) re;
        } catch (Exception e) {
            Log.e(TAG, "parseData: " + e.getMessage(), e);
            return null;
        }
    }

    public static MultiContentModel getIntroduce() {
        return parseData(INTRODUCE, null, MultiContentModel.class);
    }

    public static MultiContentModel getContribute() {
        return parseData(CONTRIBUTE, null, MultiContentModel.class);
    }

    public static MultiContentModel getTemps() {
        return parseData(TEMPS, null, MultiContentModel.class);
    }

    public static NonceModel getNonce() {
        return parseData(NONCE, null, NonceModel.class);
    }

    public static AuthenticationModel getLogin(String user, String pass) {
        return parseData(LOGIN, new String[][]{
                new String[] {"user_login", user},
                new String[] {"user_pass", pass} },
                AuthenticationModel.class);
    }

    public static MessageModel getPassword(String user) {
        return parseData(FORGET_PASS, new String[][]{
                new String[] {"user_login", user} },
                MessageModel.class);
    }


    public static MessageModel register(List<String[]> userData) {
        return parseData(REGISTER, userData.toArray(new String[][]{}),
                MessageModel.class);
    }

    public static SchedulerModel getScheduler(Date date, boolean isScheduler) {
        return parseData(
                isScheduler ?
                        SCHEDULER :
                        READING,
                new String[][]{
                        new String[] {"date", Utils.formatSchedulerDate(date)} },
                SchedulerModel.class);
    }

    public static OnlineModel online(String imei) {
        if (imei == null) {
            return null;
        }
        return parseData(ONLINE, new String[][]{
                        new String[] {"imei", imei} },
                OnlineModel.class);
    }
    public static BaseModel offline(String imei) {
        if (imei == null) {
            return null;
        }
        return parseData(OFFLINE, new String[][]{
                        new String[] {"imei", imei} },
                BaseModel.class);
    }

}
