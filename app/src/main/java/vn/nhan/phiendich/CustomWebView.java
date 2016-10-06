package vn.nhan.phiendich;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by Nhan on 5/10/2016.
 */

public class CustomWebView extends WebView {
    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getScrollRangeHeight() {

        return computeVerticalScrollRange();
    }
}
