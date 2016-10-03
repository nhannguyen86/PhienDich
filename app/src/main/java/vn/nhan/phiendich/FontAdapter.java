package vn.nhan.phiendich;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nhan on 26/9/2016.
 */

public class FontAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<Integer> fontSizes;
    private SettingActivity context;

    public FontAdapter(SettingActivity context, List<Integer> fontSizes) {
        this.fontSizes = fontSizes;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return fontSizes.size();
    }

    @Override
    public Integer getItem(int position) {
        return fontSizes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return fontSizes.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        TextView tv;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_setting, null);
            tv = (TextView) view.findViewById(R.id.item_setting);
            final View fv = view;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.changeFontSize((int) v.getTag());
                }
            });
            view.setTag(tv);
        } else {
            tv = (TextView) view.getTag();
        }
        tv.setText(String.format("%dpx text", getItem(position)));
        tv.setTextSize(getItem(position));
        tv.setTag(getItem(position));
        int bg;
        if (getItem(position) == AppManager.fontSize) {
            bg = R.drawable.setting_border_selected;
        } else {
            bg = R.drawable.setting_border;
        }
        view.setBackgroundResource(bg);

        return view;
    }
}
