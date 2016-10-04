package vn.nhan.phiendich;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import vn.nhan.phiendich.utils.Utils;

public class MainActivity extends Activity {

    public static final int LOGIN = 99;
    private TextView tvDate, greeting, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = (TextView) findViewById(R.id.tvDate);
        greeting = (TextView) findViewById(R.id.tvGreeting);
        username = (TextView) findViewById(R.id.tvUsername);
        tvDate.setText( getString( R.string.ngay, Utils.formatDate(new Date()) ) );

        checkLogin();
    }

    private void checkLogin() {
        if (AppManager.loginSuccess()) {
            greeting.setText(R.string.hello);
            username.setText(getString(R.string.username, AppManager.authenModel.displayname));
        } else {
            greeting.setText(R.string.msg_request_login);
            username.setText(R.string.login);
        }
    }

    public void goToScheduler(View view) {
        SchedulerActivity.selectScheduler(view.getId() == R.id.phungVu);
        Intent i = new Intent(this, SchedulerActivity.class);
        startActivity(i);
    }

    public void goToSetting(View v) {
        Intent i = new Intent(this, SettingActivity.class);
        startActivity(i);
    }

    public void goToIntroduce(View v) {
        Intent i = new Intent(this, IntroduceActivity.class);
        startActivity(i);
    }

    public void goToContribute(View v) {
        Intent i = new Intent(this, ContributeActivity.class);
        startActivity(i);
    }

    public void goToLogin(View v) {
        if (AppManager.loginSuccess()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.mgs_logout).setCancelable(false)
                    .setPositiveButton(R.string.mgs_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppManager.logout();
                            checkLogin();
                        }
                    }).setNegativeButton(R.string.mgs_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case LOGIN:
                    checkLogin();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.mgs_exist).setCancelable(false)
                .setPositiveButton(R.string.mgs_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                }).setNegativeButton(R.string.mgs_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
