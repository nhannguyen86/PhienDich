package vn.nhan.phiendich;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends BaseActive {

    private ListView fontList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        super.onCreate(savedInstanceState);

        fontList = (ListView) findViewById(R.id.font_list);
        List<Integer> fontSizes = new ArrayList<>();
        for (int i = 13; i <= 20; i++) {
            fontSizes.add(i);
        }
        fontList.setAdapter(new FontAdapter(this, fontSizes));
    }

    public void rollbackDefault(View view) {
        changeFontSize(15);
    }

    public void changeFontSize(int fontSize) {
        AppManager.changeFontSize(fontSize);
        ((FontAdapter) fontList.getAdapter()).notifyDataSetChanged();
    }
}
