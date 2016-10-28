package vn.nhan.phiendich;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vn.nhan.phiendich.model.SchedulerModel;
import vn.nhan.phiendich.utils.Utils;

public class HistoryActivity extends BaseActive {
    private static boolean isScheduler = false;

    public static void selectScheduler(boolean isScheduler) {
        if (HistoryActivity.isScheduler != isScheduler) {
            HistoryActivity.isScheduler = isScheduler;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        LayoutInflater mInflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) findViewById(R.id.history_layout);
        //set title
        String t = "";
        if (isScheduler) {
            t = getString(R.string.scheduler);
        } else {
            t = getString(R.string.reading);
        }
        ((TextView) findViewById(R.id.history_title)).setText(t);
        // history list
        List<String[]> histories = AppManager.getDataBaseManager().getSchedulers(isScheduler);
        if (histories != null && !histories.isEmpty()) {
            for (int i = 0; i < histories.size(); i++) {
                final String[] item = histories.get(i);
                View view = mInflater.inflate(R.layout.item_scheduler, null);
                TextView tv = (TextView) view.findViewById(R.id.item_scheduler);
                tv.setText((i + 1) + ". Ngày " + item[1]);
                tv.setId(Integer.parseInt(item[0]));
                layout.addView(view);
                if (i == histories.size() - 1) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SchedulerModel schedulerModel = AppManager.getDataBaseManager().getScheduler(v.getId());
                        AppManager.selectedDate = Utils.parseDate(item[1]);
                        SchedulerActivity.selectScheduler(isScheduler);
                        SchedulerActivity.setScheduler(schedulerModel);
                        startActivitySafe(SchedulerActivity.class);
                    }
                });
            }
        }
    }
}
