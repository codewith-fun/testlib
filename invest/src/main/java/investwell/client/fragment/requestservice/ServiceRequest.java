package investwell.client.fragment.requestservice;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;

public class ServiceRequest extends BaseActivity {
    private AppSession appSession;
    public TextView tvToolBarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);
        appSession = AppSession.getInstance(ServiceRequest.this);
         tvToolBarTitle = findViewById(R.id.tv_title_service);
        if (savedInstanceState == null) {

            FragServiceOption serviceOption = new FragServiceOption();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, serviceOption)
                    .commit();

        }

        if (!TextUtils.isEmpty(appSession.getServiceReq())) {
            tvToolBarTitle.setText(appSession.getServiceReq());
        } else {
            tvToolBarTitle.setText(getResources().getString(R.string.toolBar_title_service_request));
        }
        findViewById(R.id.ivBack).setOnClickListener(view -> onBackPressed());

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();
        if ((fragment instanceof FragServiceOption) ) {
           finish();
        }else if (fragmentCount > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
