package vn.nhan.phiendich;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

import vn.nhan.phiendich.model.MultiContentModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

public class TempsActivity extends BaseActive {

    private WebView content;
    private static MultiContentModel contentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temps);

        content = (WebView) findViewById(R.id.temps_content);
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
                    if (AppManager.isOfflineMode()) {
                        contentModel = AppManager.getDataBaseManager().getTemps();
                    } else {
                        contentModel = WebserviceHelper.getTemps();
                        if (contentModel != null) {
                            AppManager.getDataBaseManager().saveTemps(contentModel);
                        }
                    }
                    return contentModel;
                }

                @Override
                protected void onPostExecute(MultiContentModel model) {
                    super.onPostExecute(model);

                    setContent();

                    showLoading(false);
                }
            }.execute();
        } else {
            setContent();
        }
    }

    private void setContent() {
        if (contentModel != null && contentModel.data != null && contentModel.data.length > 0) {
            content.loadData(
                    String.format("<div align=\"justify\">%s</div>", contentModel.data[0].content),
                    "text/html; charset=utf-8", "UTF-8");
        }
    }
}
