package vn.nhan.phiendich;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.nhan.phiendich.model.Detail;
import vn.nhan.phiendich.model.Scheduler;
import vn.nhan.phiendich.model.SchedulerModel;
import vn.nhan.phiendich.utils.Utils;
import vn.nhan.phiendich.utils.WebserviceHelper;

public class SchedulerActivity extends BaseActive {

    private LayoutInflater mInflater;
    private static SchedulerModel schedulerModel;
    private static boolean isScheduler = false;
    private boolean loading = false;// fixed bug of android 4.x

    public static void setScheduler(SchedulerModel schedulerModel) {
        SchedulerActivity.schedulerModel = schedulerModel;
    }
    public static void selectScheduler(boolean isScheduler) {
        if (SchedulerActivity.isScheduler != isScheduler) {
            SchedulerActivity.isScheduler = isScheduler;
            schedulerModel = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChooseDateVisible(true);
        this.mInflater = LayoutInflater.from(this);

        if (schedulerModel == null) {
            loadLesions();
        } else {
            fillLesions();
        }

    }

    private void loadLesions() {
        if (loading) {
            return;
        }
        loading = true;
        // load current scheduler
        new AsyncTask<Void, Void, SchedulerModel>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected SchedulerModel doInBackground(Void... params) {

                return WebserviceHelper.getScheduler(AppManager.selectedDate, isScheduler);
            }

            @Override
            protected void onPostExecute(SchedulerModel model) {
                super.onPostExecute(model);
                schedulerModel = model;

                fillLesions();

                showLoading(false);

                loading = false;
            }
        }.execute();
    }

    private void fillLesions() {
        setContentView(R.layout.activity_scheduler);
        LinearLayout layout = (LinearLayout) findViewById(R.id.scheduler_layout);
        //set title
        String t = "";
        if (isScheduler) {
            t = getString(R.string.scheduler);
        } else {
            t = getString(R.string.reading);
        }
        t += " - " + Utils.formatTitleDate(AppManager.selectedDate);
        setTitle(t);

        if (schedulerModel != null && schedulerModel.data != null && schedulerModel.data.length > 0) {
            for (int i = 0; i < schedulerModel.data.length; i++) {
                Scheduler sch = schedulerModel.data[i];
                WebView title = new WebView(this);
                title.setBackgroundColor(Color.TRANSPARENT);
                title.getSettings().setSerifFontFamily("serif");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                String content = String.format("<div align=center>%s</div><div align=center>%s</div>", sch.title, sch.description);
                title.loadData(content, "text/html; charset=utf-8", "UTF-8");
                title.setLayoutParams(params);
                layout.addView(title);

                LinearLayout menu = new LinearLayout(this);
                menu.setOrientation(LinearLayout.VERTICAL);
                menu.setLayoutParams(params);
                menu.setBackgroundResource(R.drawable.main_border);
                layout.addView(menu);

                for (int j = 0; j < sch.details.length; j++) {
                    View view = mInflater.inflate(R.layout.item_scheduler, null);
                    TextView tv = (TextView) view.findViewById(R.id.item_scheduler);
                    tv.setText(sch.details[j].typename);
                    menu.addView(view);
                    if (j == sch.details.length - 1) {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                    final Detail m = sch.details[j];
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DetailActivity.setModel(m);
                            startActivitySafe(DetailActivity.class);
                        }
                    });
                }
            }
            View save = mInflater.inflate(R.layout.save_scheduler, null);
            layout.addView(save);
            Button s = (Button) save.findViewById(R.id.save_scheduler),
                    l = (Button) save.findViewById(R.id.list_scheduler);
            s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppManager.isDonated()) {
                        Utils.makeText(getString(R.string.not_donated));
                        return;
                    }
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showLoading(true);
                        }

                        @Override
                        protected Boolean doInBackground(Void... params) {

                            return AppManager.getDataBaseManager().saveScheduler(schedulerModel, isScheduler);
                        }

                        @Override
                        protected void onPostExecute(Boolean re) {
                            super.onPostExecute(re);
                            showLoading(false);
                            if (re) {
                                Utils.makeText(getString(R.string.save_success));
                            }
                        }
                    }.execute();
                }
            });
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppManager.isDonated()) {
                        Utils.makeText(getString(R.string.not_donated));
                        return;
                    }
                    HistoryActivity.selectScheduler(isScheduler);
                    startActivitySafe(HistoryActivity.class);
                }
            });
            if (!isScheduler) {
                l.setText("Các Bài Đọc Đã Lưu");
            }

        } else {
            TextView title = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            title.setText("Chưa có dữ liệu cho ngày này.");
            title.setLayoutParams(params);
            title.setTextSize(20);
            title.setGravity(Gravity.CENTER);
            layout.addView(title);
        }

    }

    @Override
    protected void onSelectedDate() {
        loadLesions();
    }
}
