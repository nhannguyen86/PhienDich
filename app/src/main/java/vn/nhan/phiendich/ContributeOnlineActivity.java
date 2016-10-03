package vn.nhan.phiendich;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static vn.nhan.phiendich.utils.Utils.*;

public class ContributeOnlineActivity extends BaseActive {

    private Spinner amountSelector;
    private EditText customAmount;
    private View customLayout;
    private TextView description;
    private TextView email;
    private TextView firstname;
    private TextView lastname;
    private TextView phone;
    private TextView cardNumber;
    private TextView pin;
    private Spinner month;
    private Spinner year;

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
        email = (TextView) findViewById(R.id.contribute_online_email);
        phone = (TextView) findViewById(R.id.contribute_online_phone);
        cardNumber = (TextView) findViewById(R.id.contribute_online_credit_card);
        pin = (TextView) findViewById(R.id.contribute_online_pin);
        month = (Spinner) findViewById(R.id.contribute_online_month);
        year = (Spinner) findViewById(R.id.contribute_online_year);

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
        List<String> months = new ArrayList<>();
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
        year.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years));
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
        if (isEmpty(lastname)) {
            error = true;
        }
        if (isEmpty(firstname)) {
            error = true;
        }
        if (isEmpty(phone)) {
            error = true;
        }
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
        }

    }

}
