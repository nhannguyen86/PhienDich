package vn.nhan.phiendich;

import android.os.Bundle;
import android.view.View;

public class ContributeTransferActivity extends ContributeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.contribute_footer).setVisibility(View.INVISIBLE);
        findViewById(R.id.contribute_footer).setVisibility(View.GONE);

        setContent(1);
    }

}
