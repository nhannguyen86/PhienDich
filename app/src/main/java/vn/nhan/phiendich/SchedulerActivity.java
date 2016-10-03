package vn.nhan.phiendich;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.nhan.phiendich.model.SchedulerModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

public class SchedulerActivity extends BaseActive {

    private LinearLayout layout;
    public static SchedulerModel schedulerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChooseDateVisible(true);

        if (schedulerModel == null) {
            loadLesions();
        } else {
            fillLesions();
        }

    }

    private void loadLesions() {
        // load current scheduler
        new AsyncTask<Void, Void, SchedulerModel>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected SchedulerModel doInBackground(Void... params) {
                return WebserviceHelper.getScheduler(AppManager.selectedDate);
            }

            @Override
            protected void onPostExecute(SchedulerModel model) {
                super.onPostExecute(model);
                schedulerModel = model;

                fillLesions();

                showLoading(false);
            }
        }.execute();
    }

    private void fillLesions() {
        setContentView(R.layout.activity_scheduler);
        layout = (LinearLayout) findViewById(R.id.scheduler_layout);

        if (schedulerModel != null && schedulerModel.data != null && schedulerModel.data.length > 0) {
            for (int i = 0; i < schedulerModel.data.length; i++) {
                WebView title = new WebView(this);
                title.setBackgroundColor(Color.TRANSPARENT);
                title.getSettings().setSerifFontFamily("serif");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                String content = String.format("<div align=center>%s</div><div align=center>%s</div>", schedulerModel.data[i].title, schedulerModel.data[i].description);
                title.loadData(content, "text/html; charset=utf-8", "UTF-8");
                title.setLayoutParams(params);
                layout.addView(title);
            }
        } else {
            TextView title = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            title.setText("Chưa có dữ liệu cho ngày này.");
            title.setLayoutParams(params);
            layout.addView(title);
        }

    }

    @Override
    protected void onSelectedDate() {
        loadLesions();
    }
}
