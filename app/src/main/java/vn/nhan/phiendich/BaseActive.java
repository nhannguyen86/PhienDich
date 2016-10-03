package vn.nhan.phiendich;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DecorToolbar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by Nhan on 26/9/2016.
 */

public class BaseActive extends AppCompatActivity {
    private boolean menuChooseDateVisible;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        try {
            alignTitle(actionBar);
        } catch (Exception e) {
            Log.d(getClass().getName(), "Can't align title: " + e.getMessage());
        }
    }

    protected void setChooseDateVisible(boolean visible) {
        menuChooseDateVisible = visible;
    }

    protected void showLoading(boolean visible) {
        if (visible) {
            loading = new ProgressDialog(this);
            loading.setTitle(getString(R.string.wating));
            loading.setCancelable(false);
            loading.show();
        } else {
            loading.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cusmenu, menu);

        MenuItem chooseDate = menu.findItem(R.id.menu_choose_date);
        chooseDate.setVisible(menuChooseDateVisible);

        return true;
    }

    private void alignTitle(Object obj) throws IllegalAccessException, NoSuchFieldException {
        Field f = obj.getClass().getDeclaredField("mDecorToolbar");
        f.setAccessible(true);
        DecorToolbar de = (DecorToolbar) f.get(obj);
        f = de.getClass().getDeclaredField("mToolbar");
        f.setAccessible(true);
        Toolbar tb = (Toolbar) f.get(de);
        f = tb.getClass().getDeclaredField("mTitleTextView");
        f.setAccessible(true);
        TextView title = (TextView) f.get(tb);
        f = tb.getClass().getDeclaredField("mNavButtonView");
        f.setAccessible(true);
        ImageView nav = (ImageView) f.get(tb);
        int navW = nav.getDrawable().getMinimumWidth();
        navW += nav.getMinimumWidth() * 2;
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        Toolbar.LayoutParams txvPars = (Toolbar.LayoutParams) title.getLayoutParams();
        txvPars.gravity = Gravity.CENTER_HORIZONTAL;
        txvPars.width = metrics.widthPixels - navW;
        title.setLayoutParams(txvPars);
        title.setGravity(Gravity.CENTER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menu_choose_date:
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setBaseActive(this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        BaseActive baseActive;

        public void setBaseActive(BaseActive baseActive) {
            this.baseActive = baseActive;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            if (AppManager.selectedDate != null) {
                c.setTime(AppManager.selectedDate);
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.YEAR, year);
            ca.set(Calendar.MONTH, month);
            ca.set(Calendar.DAY_OF_MONTH, day);
            AppManager.selectedDate = ca.getTime();
            if (baseActive != null) {
                baseActive.onSelectedDate();
            }
        }
    }

    protected void onSelectedDate() {

    }
}
