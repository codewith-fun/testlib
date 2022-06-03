package investwell.client.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import investwell.broker.activity.BrokerActivity;
import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextViewBold;

import static android.view.View.GONE;

public class AccountConfActivity extends BaseActivity {

    TextView mTvConfirm;
    private CustomTextViewBold mTvIIN;
    private AppSession mSession;
    private MainActivity mActivity;
    private BrokerActivity mBrokerActivity;
    private AppApplication mApplication;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_confirmed);
        mApplication = new AppApplication();
        mBrokerActivity = new BrokerActivity();
        mActivity = new MainActivity();
        mSession = AppSession.getInstance(AccountConfActivity.this);
        mTvIIN = findViewById(R.id.TvIIN);
        mTvConfirm = findViewById(R.id.TvConfirm);
        mTvIIN.setVisibility(GONE);


        if (mSession.getAppType().equalsIgnoreCase(getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase(getResources().getString(R.string.apptype_dn))) {
            if (getIntent().getExtras() != null && getIntent().hasExtra("message")) {
                mTvConfirm.setText(getIntent().getStringExtra("message"));
            } else {
                mTvConfirm.setText(getString(R.string.investment_status_bse_txt));
            }
        } else {

            mTvConfirm.setText(getString(R.string.investment_status_bse_txt));
        }

        findViewById(R.id.customTextViewRegular3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTvConfirm.getText().toString().contains("new credentials")) {
                    showLogoutDailog(getBaseContext(), getResources().getString(R.string.account_confirmed_header_txt), getString(R.string.alert_dialog_logout_desc_txt));
                } else {

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);

                    if (getIntent().hasExtra("coming_from") && (getIntent().getStringExtra("coming_from").equalsIgnoreCase("profile_list_signature")
                            || getIntent().getStringExtra("coming_from").equalsIgnoreCase("profile_list_cheque"))) {
                        intent.putExtra("comingFromActivity", getIntent().getStringExtra("coming_from"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();


                    } else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showLogoutDailog(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();




        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.msg_txt);

        tvTitle.setText(title);
        tvMessage.setText(message);

        CustomButton tvOk = dialogView.findViewById(R.id.tvOk);
        CustomButton tvcancel = dialogView.findViewById(R.id.cancel);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                mApplication.clearChacheSession();
                if (mSession.getLoginType().equalsIgnoreCase("Broker") || mSession.getLoginType().equalsIgnoreCase("SubBroker") ||
                        mSession.getLoginType().equalsIgnoreCase("RM")
                        || mSession.getLoginType().equalsIgnoreCase("Zone")
                        || mSession.getLoginType().equalsIgnoreCase("Region")
                        || mSession.getLoginType().equalsIgnoreCase("Branch")) {
                    mBrokerActivity.notificationCount();
                } else {
                    mActivity.notificationCount();
                }
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") ||
                                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                    Intent intent = new Intent(getBaseContext(), MainActivityTypeTwo.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }


            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        alertDialog.setCancelable(false);
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }
}
