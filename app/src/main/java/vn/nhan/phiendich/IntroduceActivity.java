package vn.nhan.phiendich;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import vn.nhan.phiendich.model.MultiContentModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

public class IntroduceActivity extends BaseActive {

    private WebView content;
    private static MultiContentModel contentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_introduce);
        super.onCreate(savedInstanceState);

        content = (WebView) findViewById(R.id.introduce_content);
        content.setBackgroundColor(Color.TRANSPARENT);

        setContent(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && contentModel == null) {
            new AsyncTask<Void, Void, MultiContentModel>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showLoading(true);
                }

                @Override
                protected MultiContentModel doInBackground(Void... params) {
                    contentModel = WebserviceHelper.getIntroduce();
                    return contentModel;
                }

                @Override
                protected void onPostExecute(MultiContentModel contentModel) {
                    super.onPostExecute(contentModel);
                    setContent(0);
                    showLoading(false);
                }
            }.execute();
        }
    }

    private void setContent(int index) {
        if (contentModel != null && contentModel.data != null && contentModel.data.length > index) {
            String c = String.format("<div align=justify>%s</div>", contentModel.data[index].content);
            content.loadData(c, "text/html; charset=utf-8", "UTF-8");
            content.loadData(c, "text/html; charset=utf-8", "UTF-8");
        }
    }

    public void selectTab(View view) {
        switch (view.getId()) {
            case R.id.tv_ktcmn:
                resetTabBackground();
                view.setBackgroundResource(R.drawable.introduce_left_selected);
                setContent(0);
                break;
            case R.id.tv_kpv:
                resetTabBackground();
                view.setBackgroundResource(R.drawable.introduce_center_selected);
                setContent(1);
                break;
            case R.id.tv_bdtl:
                resetTabBackground();
                view.setBackgroundResource(R.drawable.introduce_right_selected);
                setContent(2);
                break;
        }
    }

    private void resetTabBackground() {
        findViewById(R.id.tv_ktcmn).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.tv_kpv).setBackgroundResource(R.drawable.introduce_center_border);
        findViewById(R.id.tv_bdtl).setBackgroundColor(Color.TRANSPARENT);
    }
}