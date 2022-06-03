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

public class PaymentSuccess extends BaseActivity {

    private ImageView mIvivIcon;
    private TextView mTvMessage, mTvSubMessage, mTvSubmit;
    private String message;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        mIvivIcon = findViewById(R.id.ivIcon);
        mTvMessage = findViewById(R.id.tvMessage);
        mTvSubMessage = findViewById(R.id.tvSubMessage);
        mTvSubmit = findViewById(R.id.tvSubmit);
        Intent intent = getIntent();
        message = intent.getStringExtra("message");

        if (message.equalsIgnoreCase("success")){
            mTvSubmit.setText(getResources().getString(R.string.risk_profile_continue_btn_txt));
            mTvSubmit.setBackgroundResource(R.drawable.rounded_blue);
            mIvivIcon.setBackgroundResource(R.mipmap.payment_sucess);
            mTvMessage.setText(R.string.payment_success_header_txt);
            mTvSubMessage.setText(R.string.payment_success_desc_txt);
        }else{
            mTvSubmit.setText(getResources().getString(R.string.retry_btn));
            mTvSubmit.setBackgroundResource(R.drawable.rounded_red);
            mIvivIcon.setBackgroundResource(R.mipmap.payment_failed);
            mTvMessage.setText(R.string.failure_message);
            mTvSubMessage.setText(R.string.failure_sub_message);
        }


        mTvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.equalsIgnoreCase("success")){
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();

                }else{

                    finish();
                }
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
    @Override
    protected void onResume() {
        super.onResume();

    }


}
