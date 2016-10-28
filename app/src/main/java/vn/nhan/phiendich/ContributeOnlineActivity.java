package vn.nhan.phiendich;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import vn.nhan.phiendich.model.BaseModel;
import vn.nhan.phiendich.utils.Utils;
import vn.nhan.phiendich.utils.WebserviceHelper;

import static vn.nhan.phiendich.utils.Utils.isEmpty;

public class ContributeOnlineActivity extends BaseActive {

    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("ASGECBGVEYxsi98c-0PTL1fSDKh61v7lLhgfZUdD0lIoDi_slXQUHMfeEmePopPEvoUBTRQ6UQn3tLUw");

    private Spinner amountSelector;
    private EditText customAmount;
    private View customLayout;
    private TextView description;
//    private TextView email;
    private TextView firstname;
    private TextView lastname;
    private TextView phone;
    /*private TextView cardNumber;
    private TextView pin;
    private Spinner month;
    private Spinner year;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute_online);
        
        amountSelector = (Spinner) findViewById(R.id.contribute_online_amount_spinner);

        customLayout = findViewById(R.id.contribute_online_custom_amount_layout);
        customLayout.setVisibility(View.INVISIBLE);
        customLayout.setVisibility(View.GONE);
        customAmount = (EditText) findViewById(R.id.contribute_online_custom_amount);
        description = (TextView) findViewById(R.id.contribute_online_description);

        lastname = (TextView) findViewById(R.id.contribute_online_lastname);
        firstname = (TextView) findViewById(R.id.contribute_online_firstname);
//        email = (TextView) findViewById(R.id.contribute_online_email);
        phone = (TextView) findViewById(R.id.contribute_online_phone);
        /*cardNumber = (TextView) findViewById(R.id.contribute_online_credit_card);
        pin = (TextView) findViewById(R.id.contribute_online_pin);
        month = (Spinner) findViewById(R.id.contribute_online_month);
        year = (Spinner) findViewById(R.id.contribute_online_year);*/

        customAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    return;
                }
                try {
                    int n = Integer.parseInt(s.toString());
                    if (n >= 15) {
                        description.setText(getString(R.string.contribute_online_des, n));
                    }
                } catch (Exception e) {}

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        amountSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectAmount(15);
                        break;
                    case 1:
                        selectAmount(25);
                        break;
                    case 2:
                        selectCustomerAmount();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*List<String> months = new ArrayList<>();
        months.add("Chọn tháng");
        for (int i = 1; i < 13; i ++) {
            months.add(String.format("Tháng %s", i));
        }
        month.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months));
        int y = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        years.add("Chọn năm");
        for (int i = 0; i <= 5; i ++) {
            years.add(String.valueOf(y + i));
        }
        year.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years));*/

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                new AsyncTask<Void, Void, BaseModel>() {
                    @Override
                    protected void onPreExecute() {
                        showLoading(true);
                        super.onPreExecute();
                    }

                    @Override
                    protected BaseModel doInBackground(Void... params) {
                        JSONObject js = confirm.toJSONObject();
                        String amt = ((TextView) customAmount).getText().toString();
                        long userId = AppManager.loginSuccess() ? AppManager.authenModel.id : 1;
                        try {
                            return WebserviceHelper.donate(userId, js.getJSONObject("response").getString("id"), amt, js.toString());
                        } catch (JSONException e) {
                            Log.e("payment", "an extremely unlikely failure occurred: ", e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(BaseModel baseModel) {
                        showLoading(false);
                        if (baseModel != null) {
                            if (baseModel.error == null) {
                                finish();
                                Utils.makeText("Quý vị đã đóng góp thành công. Xin chân thành cám ơn!");
                                if (AppManager.loginSuccess()) {
                                    AppManager.authenModel.isDonated = true;
                                    AppManager.saveLogin();
                                }
                            } else {
                                Utils.makeText(baseModel.error);
                            }
                        }
                        super.onPostExecute(baseModel);
                    }
                }.execute();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    private void selectCustomerAmount() {
        customLayout.setVisibility(View.VISIBLE);
        customAmount.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(customAmount, InputMethodManager.SHOW_FORCED);
    }

    private void selectAmount(int amount) {
        customLayout.setVisibility(View.INVISIBLE);
        customLayout.setVisibility(View.GONE);
        description.setText(getString(R.string.contribute_online_des, amount));
        customAmount.setText(String.valueOf(amount));
    }

    public void submit(View v) {
        boolean error = false;
        if (isEmpty(customAmount)) {
            error = true;
        }
        String amt = ((TextView) customAmount).getText().toString();
        if (Integer.parseInt(amt) < 15) {
            customAmount.setError("Mức thấp nhất là 15$");
            error = true;
        }
        if (isEmpty(lastname)) {
            error = true;
        }
        if (isEmpty(firstname)) {
            error = true;
        }
        if (isEmpty(phone)) {
            error = true;
        }
        if (error) {
            return;
        }

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.

        PayPalPayment payment = new PayPalPayment(new BigDecimal(amt), "USD",
                "Thanh toán trực tuyến số tiền đóng góp",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);


        /*
        if (isEmpty(cardNumber)) {
            error = true;
        }
        if (isEmpty(pin)) {
            error = true;
        }
        if (isEmpty(month)) {
            error = true;
        }
        if (isEmpty(year)) {
            error = true;
        }
        if (isEmpty(pin)) {
            error = true;
        }
        if (isEmpty(email)) {
            error = true;
        } else if (!isEmailValid(email.getText())) {
            email.setError(getString(R.string.invalid_email));
            error = true;
        }
        if (error) {
            return;
        }*/

    }

}
