package vn.nhan.phiendich.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Nhan on 9/10/2016.
 */

public class ExpandableHeightListView extends ListView {
    public ExpandableHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate entire height by providing a very large height hint.
        // But do not use the highest 2 bits of this integer; those are
        // reserved for the MeasureSpec mode.
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
