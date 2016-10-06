package vn.nhan.phiendich;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import vn.nhan.phiendich.model.MultiContentModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

public class ContributeActivity extends BaseActive {

    private WebView content;
    private static MultiContentModel contentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

        content = (WebView) findViewById(R.id.contribute_content);
        content.setBackgroundColor(Color.TRANSPARENT);

        if (contentModel == null) {
            new AsyncTask<Void, Void, MultiContentModel>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showLoading(true);
                }

                @Override
                protected MultiContentModel doInBackground(Void... params) {
                    contentModel = WebserviceHelper.getContribute();
                    return contentModel;
                }

                @Override
                protected void onPostExecute(MultiContentModel contentModel) {
                    super.onPostExecute(contentModel);
                    setContent(0);
                    showLoading(false);
                }
            }.execute();
        } else {
            setContent(0);
        }
    }

    protected void setContent(int index) {
        if (contentModel != null && contentModel.data != null && contentModel.data.length > index) {
            content.loadData(
                    String.format("<div>%s</div>", contentModel.data[index].content),
                    "text/html; charset=utf-8", "UTF-8");
        }
    }

    public void goToTransferContribute(View v) {
        Intent i = new Intent(this, ContributeTransferActivity.class);
        startActivity(i);
    }

    public void goToOnlineContribute(View v) {
        Intent i = new Intent(this, ContributeOnlineActivity.class);
        startActivity(i);
    }
}
