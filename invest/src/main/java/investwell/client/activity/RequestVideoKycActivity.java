package investwell.client.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.Objects;

import investwell.client.flavourtypetwo.activity.HelpTypeTwoBActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextViewRegular;

public class RequestVideoKycActivity extends AppCompatActivity implements View.OnClickListener {
    private AppApplication mApplication;
    private TextView tvDescription, tvRequestVideoKyc;
    private ImageView ivContactUs, ivBackPress;
    private String callBack = "", email = "", clientEmail = "";
    private AppSession mSession;
    private Bundle mBundle;
    private LinearLayout llRequest;
    private ProgressBar pbRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_video_kyc);
        initializer();
        getDataFromAppConfig();
        getDataFromBundle();
        setListener();
    }

    private void initializer() {
        if (!Utils.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSession = AppSession.getInstance(this);
        tvDescription = findViewById(R.id.textView12);
        llRequest = findViewById(R.id.ll_request);
        pbRequest = findViewById(R.id.pb_request);
        tvRequestVideoKyc = findViewById(R.id.tv_request_video_kyc);
        ivContactUs = findViewById(R.id.iv_contact);
        ivBackPress = findViewById(R.id.iv_back);
    }

    private void getDataFromBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mBundle = intent.getExtras();
            if (mBundle != null && mBundle.containsKey("messageKyc")) {
                tvDescription.setText(mBundle.getString("messageKyc"));
                clientEmail = mBundle.getString("Email");
            }
        }
    }

    public void showHelpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_contact_us, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        ImageView ivCloseDialog = dialogView.findViewById(R.id.iv_close_dialog);
        View viewHelpSelection = dialogView.findViewById(R.id.content_help_dialog);
        CardView cvCall = viewHelpSelection.findViewById(R.id.cv_call);
        CardView cvE_mail = viewHelpSelection.findViewById(R.id.cv_mail);
        CustomTextViewRegular tvBtnMore = dialogView.findViewById(R.id.btn_more);
        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        cvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(callBack)) {
                    onCallClick();
                } else {
                    Toast.makeText(getApplicationContext(), "No Callback Number  Found", Toast.LENGTH_SHORT).show();
                }
                alertDialog.cancel();
            }
        });
        cvE_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(email)) {
                    onEmailClick();
                } else {
                    Toast.makeText(getApplicationContext(), "No Email Id Found", Toast.LENGTH_SHORT).show();
                }
                alertDialog.cancel();
            }
        });
        tvBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                Intent intent = new Intent(RequestVideoKycActivity.this, HelpTypeTwoBActivity.class);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void setListener() {
        llRequest.setOnClickListener(this);
        ivContactUs.setOnClickListener(this);
        ivBackPress.setOnClickListener(this);
    }

    private void getDataFromAppConfig() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CallBack"))) {
            callBack = Utils.getConfigData(mSession).optString("CallBack");

        } else {
            callBack = "";


        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Email"))) {
            email = Utils.getConfigData(mSession).optString("Email");
        } else {
            email = "";
        }
    }

    private void onEmailClick() {
        String brokerName = "";
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("BrokerName"))) {
            brokerName = Utils.getConfigData(mSession).optString("BrokerName");
        }
        String recepientEmail = email; // either set to destination email or leave empty
        String subject = "Query From " + brokerName;
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] strTo = {recepientEmail};
        intent.putExtra(Intent.EXTRA_EMAIL, strTo);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setType("message/rfc822");
        intent.setPackage("com.google.android.gm");
        startActivity(intent);
    }

    private void onCallClick() {
        Intent inten = new Intent(Intent.ACTION_DIAL);
        inten.setData(Uri.parse("tel:" + callBack));
        startActivity(inten);
    }

    private void onSuccessResponse() {
        Intent intent = new Intent(RequestVideoKycActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void showLogOutAlert(String message, final String status) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_common_application, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        CustomButton btDone = dialogView.findViewById(R.id.btDone);
        btDone.setText(R.string.text_ok);
        CustomButton btCalcel = dialogView.findViewById(R.id.btCalcel);
        btCalcel.setVisibility(View.GONE);
        btCalcel.setText("Cancel");
        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);

        tvTitle.setText(R.string.message_txt);
        tvMessage.setText(message);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        btDone.setVisibility(View.VISIBLE);


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (status.equalsIgnoreCase("True")) {
                    onSuccessResponse();
                } else {
                    alertDialog.dismiss();
                }
            }
        });


        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void onLoadingStart() {
        pbRequest.setVisibility(View.VISIBLE);
        llRequest.setEnabled(false);
        llRequest.setClickable(false);
        llRequest.setBackgroundColor(ContextCompat.getColor(this, R.color.btnDisableColor));
    }

    private void onLoadingEnd() {
        pbRequest.setVisibility(View.GONE);
        llRequest.setEnabled(true);
        llRequest.setClickable(true);
        llRequest.setBackgroundColor(ContextCompat.getColor(RequestVideoKycActivity.this, R.color.btnPrimaryBackgroundColor));

    }

    private void callVideoKycLimitApi() {
        onLoadingStart();
        String url = Config.VIDEO_KYC_LIMIT;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Email", clientEmail);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    onLoadingEnd();
                    try {
                        showLogOutAlert(jsonObject.optString("ServiceMSG"), jsonObject.optString("Status"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onLoadingEnd();
                    showLogOutAlert(error.getMessage(), "False");
                }
            });
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 1;
                }

                @Override
                public void retry(VolleyError error) {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_request) {
            callVideoKycLimitApi();
        } else if (id == R.id.iv_contact) {
            showHelpDialog();
        } else if (id == R.id.iv_back) {
            finish();
        }
    }

}