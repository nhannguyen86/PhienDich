package vn.nhan.phiendich;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends BaseActive {

    private ListView font_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        super.onCreate(savedInstanceState);

        font_list = (ListView) findViewById(R.id.font_list);
        List<Integer> fontSizes = new ArrayList<>();
        for (int i = 13; i <= 20; i++) {
            fontSizes.add(i);
        }
        font_list.setAdapter(new FontAdapter(this, fontSizes));
    }

    public void rollbackDefault(View view) {
        changeFontSize(15);
    }

    public void changeFontSize(int fontSize) {
        AppManager.changeFontSize(fontSize);
        ((FontAdapter) font_list.getAdapter()).notifyDataSetChanged();
    }
}
