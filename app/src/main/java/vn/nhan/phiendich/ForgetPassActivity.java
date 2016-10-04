package vn.nhan.phiendich;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import vn.nhan.phiendich.model.MessageModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

import static vn.nhan.phiendich.utils.Utils.isEmailValid;
import static vn.nhan.phiendich.utils.Utils.isEmpty;

public class ForgetPassActivity extends BaseActive {

    private TextView status, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        email = (EditText) findViewById(R.id.forget_email);
        status = (TextView) findViewById(R.id.forget_status);

    }

    public void submit(View view) {
        boolean error = false;
        if (isEmpty(email)) {
            error = true;
        } else if (email.getText().toString().indexOf('@') != -1 && !isEmailValid(email.getText())) {
            email.setError(getString(R.string.invalid_email));
            error = true;
        }
        if (error) {
            return;
        }
        final String user = this.email.getText().toString();
        new AsyncTask<Void, Void, MessageModel>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected MessageModel doInBackground(Void... params) {
                MessageModel model = WebserviceHelper.getPassword(user);
                return model;
            }

            @Override
            protected void onPostExecute(MessageModel model) {
                super.onPostExecute(model);
                if (model != null) {
                    if (model.error != null) {
                        status.setText(String.format("Lỗi khi lấy mật khẩu: %s", model.error));
                    } else {
                        status.setText("");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPassActivity.this);
                        builder.setMessage(model.msg).setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Intent i = new Intent(ForgetPassActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        ForgetPassActivity.this.finish();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                showLoading(false);
            }
        }.execute();
    }
}
