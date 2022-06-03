package investwell.client.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.Objects;

import investwell.client.flavourtypetwo.activity.HelpTypeTwoBActivity;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomTextViewRegular;

public class OtpSuccessActivity extends BaseActivity implements View.OnClickListener {
    private AppSession mSession;
    private Bundle mBundle;
    private TextView tvContact;
    private ImageView ivContactUs;
    private String callBack = "", email = "";


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify_done);
        initializer();
        getDataFromAppConfig();
        getDataFromBundle();
        setListener();
    }

    private void initializer() {
        if (!Utils.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSession = AppSession.getInstance(this);
        tvContact = findViewById(R.id.tv_please_contact);
        ivContactUs = findViewById(R.id.iv_contact);
    }

    private void setListener() {
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.tv_request_video_kyc).setOnClickListener(this);
        tvContact.setOnClickListener(this);
        ivContactUs.setOnClickListener(this);
    }

    private void getDataFromBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            mBundle = intent.getExtras();
        }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button4) {
            LoginWithoutPassword(mBundle.getString("email"), "onboard");
        } else if (id == R.id.tv_request_video_kyc) {
            LoginWithoutPassword(mBundle.getString("email"), "skip");
        } else if (id == R.id.iv_contact || id == R.id.tv_please_contact) {
            showHelpDialog();
        }
    }


    private void LoginWithoutPassword(final String email, final String condition) {

        String url = Config.LOGIN_WITHOUT_PASSWORD;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Username", email);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject objDa) {
                    if (objDa.optString("Status").equalsIgnoreCase("True")) {
                        String userType = objDa.optString("LoginCategory");

                        mSession.set_login_detail(objDa.toString());
                        mSession.setHasFirstTimeCompleted(true);
                        mSession.setEmail(objDa.optString("Email"));
                        mSession.setUCC_CODE(objDa.optString("UCC"));
                        mSession.setPAN(objDa.optString("PAN"));
                        mSession.setDOB(objDa.optString("DOB"));
                        mSession.settaxStatus(objDa.optString("TaxStatus"));
                        mSession.setOccupation(objDa.optString("Occupation"));
                        mSession.setTransactionPermission(objDa.optJSONArray("TransactionPermission").toString());
                        mSession.setLoginType(userType);

                        mSession.setHasLoging(true);
                        mSession.setUserType("1");
                        mSession.setAppType(objDa.optString("OnlineOption"));

                        mSession.setHasMendate(objDa.optString("MandateStatus").equals("Y"));
                        mSession.setHasFatca(objDa.optString("FATCAStatus").equals("Y"));
                        mSession.setHasCAFStatus(objDa.optString("CAFStatus").equals("Y"));
                        mSession.setHasSignature(objDa.optString("DocUploadStatus").equals("Y"));
                        mSession.setPANKYC(objDa.optString("PANKYCStatus").equals("Y"));

                        mSession.setRMmobile(objDa.optString("RMMobile"));
                        mSession.setRMemail(objDa.optString("RMEmail"));
                        mSession.setRM(objDa.optString("RM"));
                        mSession.setUname(email);

                        if (userType.equals("RM") || userType.equals("SubBroker")) {
                            mSession.setSecondryCID(objDa.optString("CID"));
                        } else {
                            mSession.setCID(objDa.optString("CID"));
                            mSession.setSecondryCID(objDa.optString("CID"));
                        }

                        if ((mSession.getLoginType().equals("Broker")
                                || mSession.getLoginType().equals("SubBroker")
                                || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                                || mSession.getLoginType().equalsIgnoreCase("Region")
                                || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
                            mSession.setBrokerFullName(objDa.optString("Name"));
                        } else {
                            mSession.setFullName(objDa.optString("Name"));
                        }


                        if (condition.equalsIgnoreCase("onboard")) {
                            Done();
                        } else {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            mBundle.putString(AppConstants.COME_FROM, "skipOption");
                            intent.putExtras(mBundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }


                    } else {
                        Toast.makeText(OtpSuccessActivity.this, objDa.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

                    }

                }
            }, error -> {

            });

            RequestQueue requestQueue = Volley.newRequestQueue(OtpSuccessActivity.this);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {

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
                Intent intent = new Intent(OtpSuccessActivity.this, HelpTypeTwoBActivity.class);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.setCancelable(false);
        alertDialog.show();
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

    public void Done() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        mBundle.putString(AppConstants.COME_FROM, "instruction");
        intent.putExtras(mBundle);
        startActivity(intent);
        finish();
    }
}
