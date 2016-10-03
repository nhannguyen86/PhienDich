package vn.nhan.phiendich;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.nhan.phiendich.model.MessageModel;
import vn.nhan.phiendich.model.NonceModel;
import vn.nhan.phiendich.utils.WebserviceHelper;

import static vn.nhan.phiendich.utils.Utils.isEmailValid;
import static vn.nhan.phiendich.utils.Utils.isEmpty;

public class RegisterActivity extends BaseActive {

    private TextView username, email, pass, pass2, lastName, firstName;
    private Spinner gender, age, job, major;
    private TextView phone, address, province, state, zipcode, national;
    private Button submit;
    private String nonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.register_username);
        email = (EditText) findViewById(R.id.register_email);
        pass = (EditText) findViewById(R.id.register_pass);
        pass2 = (EditText) findViewById(R.id.register_pass2);
        lastName = (EditText) findViewById(R.id.register_lastname);
        firstName = (EditText) findViewById(R.id.register_firstname);
        gender = (Spinner) findViewById(R.id.register_gender);
        age = (Spinner) findViewById(R.id.register_age);
        job = (Spinner) findViewById(R.id.register_job);
        major = (Spinner) findViewById(R.id.register_major);
        phone = (EditText) findViewById(R.id.register_phone);
        address = (EditText) findViewById(R.id.register_address);
        province = (EditText) findViewById(R.id.register_province);
        state = (EditText) findViewById(R.id.register_state);
        zipcode = (EditText) findViewById(R.id.register_zipcode);
        national = (EditText) findViewById(R.id.register_national);
        submit = (Button) findViewById(R.id.register_submit);
        // load nonce
        new AsyncTask<Void, Void, NonceModel>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected NonceModel doInBackground(Void... params) {
                return WebserviceHelper.getNonce();
            }

            @Override
            protected void onPostExecute(NonceModel model) {
                super.onPostExecute(model);

                if (model != null) {
                    nonce = model.nonce;
                }

                showLoading(false);
            }
        }.execute();
    }

    public void accept(View view) {
        submit.setEnabled( ((CheckBox) view).isChecked() );
    }

    public void goToTemps(View view) {
        Intent i = new Intent(this, TempsActivity.class);
        startActivity(i);
    }

    public void submit(View view) {
        boolean error = false;
        if (isEmpty(username)) {
            error = true;
        }
        if (isEmpty(email)) {
            error = true;
        } else if (!isEmailValid(email.getText())) {
            email.setError(getString(R.string.invalid_email));
            error = true;
        }
        if (isEmpty(pass)) {
            error = true;
        } else if (isEmpty(pass2)) {
            error = true;
        } else if (!pass.getText().toString().equals(pass2.getText().toString())) {
            pass2.setError(getString(R.string.invalid_pass2));
        }
        if (isEmpty(lastName)) {
            error = true;
        }
        if (isEmpty(firstName)) {
            error = true;
        }
        if (isEmpty(phone)) {
            error = true;
        }
        if (error) {
            return;
        }

        final List<String[]> userData = new ArrayList<>();
        userData.add(new String[] { "nonce", nonce });
        userData.add(new String[] { "username", username.getText().toString() });
        userData.add(new String[] { "email", email.getText().toString() });
        userData.add(new String[] { "user_pass", pass.getText().toString() });
        userData.add(new String[] { "last_name", lastName.getText().toString() });
        userData.add(new String[] { "first_name", firstName.getText().toString() });
        userData.add(new String[] { "phone_number", phone.getText().toString() });
        if (gender.getSelectedItemPosition() != 0) {// Male: 1; Female: 2
            /*
            0 : Nữ
            1 : Nam
            */
            userData.add(new String[] { "sex", String.valueOf(
                    gender.getSelectedItemPosition() == 1 ? 1 : 0
            ) });
        }
        if (age.getSelectedItemPosition() != 0) {
            /*
            1 : Dưới 18 tuổi
            2 : 18-35 tuổi
            3 : 35-50 tuổi
            4 : Trên 50 tuổi
            */
            userData.add(new String[] { "age", String.valueOf(
                    age.getSelectedItemPosition()
            ) });
        }
        if (job.getSelectedItemPosition() != 0) {
            /*
            1 : Học sinh - sinh viên
            2 : Giáo viên - giáo lý viên
            3 : Giáo dục
            4 : Linh mục
            5 : Giáo dân
            6 : Công nhân
            7 : Nghiên cứu
            */
            userData.add(new String[] { "job", String.valueOf(
                    age.getSelectedItemPosition()
            ) });
        }
        if (major.getSelectedItemPosition() != 0) {
            /*
            1 : Kinh thánh học hỏi, chú giải
            2 : Kinh thánh trong Phụng Vụ
            3 : Kinh thánh trong cuộc sống
            */
            userData.add(new String[] { "field", String.valueOf(
                    major.getSelectedItemPosition()
            ) });
        }
        if (address.getText().length() > 0) {
            userData.add(new String[] { "address", address.getText().toString() });
        }
        if (province.getText().length() > 0) {
            userData.add(new String[] { "city", province.getText().toString() });
        }
        if (state.getText().length() > 0) {
            userData.add(new String[] { "state", state.getText().toString() });
        }
        if (zipcode.getText().length() > 0) {
            userData.add(new String[] { "zipcode", zipcode.getText().toString() });
        }
        if (national.getText().length() > 0) {
            userData.add(new String[] { "country", national.getText().toString() });
        }

        new AsyncTask<Void, Void, MessageModel>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(true);
            }

            @Override
            protected MessageModel doInBackground(Void... params) {
                return WebserviceHelper.register(userData);
            }

            @Override
            protected void onPostExecute(MessageModel model) {
                super.onPostExecute(model);

                if (model != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setCancelable(false);
                    if (model.error != null) {
                        builder.setMessage(model.error).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        builder.setMessage(model.mgs).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                RegisterActivity.this.finish();
                            }
                        });
                    }
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                showLoading(false);
            }
        }.execute();
    }
}
