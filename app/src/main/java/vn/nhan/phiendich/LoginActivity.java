package vn.nhan.phiendich;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import vn.nhan.phiendich.model.AuthenticationModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

import static vn.nhan.phiendich.utils.Utils.isEmpty;

public class LoginActivity extends BaseActive {

    private TextView username, pass, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.login_username);
        pass = (EditText) findViewById(R.id.login_pass);
        status = (TextView) findViewById(R.id.login_status);

    }

    public void submit(View view) {
        boolean error = false;
        if (isEmpty(username)) {
            error = true;
        }
        if (isEmpty(pass)) {
            error = true;
        }
        if (error) {
            return;
        }
        final String user = username.getText().toString();
        final String pass = this.pass.getText().toString();
        new AsyncTask<Void, Void, AuthenticationModel>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected AuthenticationModel doInBackground(Void... params) {
                AppManager.authenModel = WebserviceHelper.getLogin(user, pass);
                return AppManager.authenModel;
            }

            @Override
            protected void onPostExecute(AuthenticationModel authenModel) {
                super.onPostExecute(authenModel);
                if (authenModel != null) {
                    if (authenModel.error != null) {
                        status.setText(getString(R.string.error_login, authenModel.error));
                    } else {
                        AppManager.saveLogin();
                        status.setText("");
                        Intent i = new Intent();
                        LoginActivity.this.setResult(RESULT_OK, i);
                        LoginActivity.this.finish();
                    }
                }
                showLoading(false);
            }
        }.execute();
    }

    public void forgetPass(View view) {
        Intent i = new Intent(this, ForgetPassActivity.class);
        startActivity(i);
    }

    public void register(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
}
