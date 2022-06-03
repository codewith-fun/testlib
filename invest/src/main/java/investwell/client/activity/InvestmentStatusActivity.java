package investwell.client.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.iw.acceleratordemo.R;

import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;

public class InvestmentStatusActivity extends BaseActivity {
    private TextView mTvIIN,mTvConfirm;
    private AppSession mSession;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;


    @Override
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(),MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_confirmed);

        mSession = AppSession.getInstance(InvestmentStatusActivity.this);
        mTvIIN= findViewById(R.id.TvIIN);
        mTvConfirm = findViewById(R.id.TvConfirm);

        if (mSession.getAppType().equals(getResources().getString(R.string.apptype_n))||mSession.getAppType().equalsIgnoreCase("DN")){
            mTvIIN.setVisibility(View.GONE);
            mTvConfirm.setText(getString(R.string.investment_status_nse_txt));
        }else{
            mTvIIN.setVisibility(View.GONE);
            mTvConfirm.setText(getString(R.string.investment_status_bse_txt));
        }

        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }
    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }
    //Display Network Error Content
    private void displayNoInternetMessage() {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }

}
