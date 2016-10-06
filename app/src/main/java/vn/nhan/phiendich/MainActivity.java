package vn.nhan.phiendich;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import vn.nhan.phiendich.model.BaseModel;
import vn.nhan.phiendich.service.StatusReceiver;
import vn.nhan.phiendich.utils.Utils;
import vn.nhan.phiendich.utils.WebserviceHelper;

public class MainActivity extends BaseActive {

    public static final int LOGIN = 99;
    private TextView tvDate, greeting, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActionBarVisible(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = (TextView) findViewById(R.id.tvDate);
        greeting = (TextView) findViewById(R.id.tvGreeting);
        username = (TextView) findViewById(R.id.tvUsername);
        tvDate.setText( getString( R.string.ngay, Utils.formatDate(new Date()) ) );

        findViewById(R.id.phungVu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToScheduler(v);
            }
        });
        findViewById(R.id.baiDoc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToScheduler(v);
            }
        });
        findViewById(R.id.tvSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetting(v);
            }
        });
        findViewById(R.id.tvIntroduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToIntroduce(v);
            }
        });
        findViewById(R.id.tvContribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToContribute(v);
            }
        });
        findViewById(R.id.tvUsername).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin(v);
            }
        });


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
        startActivitySafe(SchedulerActivity.class);
    }

    public void goToSetting(View v) {
        startActivitySafe(SettingActivity.class);
    }

    public void goToIntroduce(View v) {
        startActivitySafe(IntroduceActivity.class);
    }

    public void goToContribute(View v) {
        startActivitySafe(ContributeActivity.class);
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
            startActivityForResultSafe(LoginActivity.class, LOGIN);
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
                        new AsyncTask<Void, Void, BaseModel> () {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                finish();
                            }

                            @Override
                            protected BaseModel doInBackground(Void... params) {
                                BaseModel model = WebserviceHelper.offline(AppManager.IMEI);
                                return model;
                            }

                            @Override
                            protected void onPostExecute(BaseModel model) {
                                super.onPostExecute(model);
                                cancelStatus();
                                System.exit(0);
                            }
                        }.execute();
                    }
                }).setNegativeButton(R.string.mgs_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void cancelStatus() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), StatusReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, StatusReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
}
