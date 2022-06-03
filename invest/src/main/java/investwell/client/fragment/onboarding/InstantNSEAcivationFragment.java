package investwell.client.fragment.onboarding;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;

public class InstantNSEAcivationFragment extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack {

    String fatca_dob, Email;
    private CustomDialog customDialog;
    private Bundle mBundle;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private View fatcaView;

    private Button mBtnInstant, mBtnlater, mBtnDone;
    private ToolbarFragment fragToolBar;
    private ProgressDialog mBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fatcaView = inflater.inflate(R.layout.fragment_nse_activation, container, false);
        initializer();
        setUpToolBar();
        getDataFromBundle();
        setListeners();
        return fatcaView;
    }

    private void getDataFromBundle() {
        if (mBundle != null) {
            if (mBundle.containsKey("email_value")) {

                Email = mBundle.getString("email_value");
            } else {
                Email = mSession.getEmail();
            }
        }
    }

    private void setListeners() {
      mBtnInstant.setOnClickListener(this);
      mBtnlater.setOnClickListener(this);
      mBtnDone.setOnClickListener(this);

    }

    private void initializer() {
        mBundle = getArguments();
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        customDialog = new CustomDialog(this);
        mBtnInstant = fatcaView.findViewById(R.id.instant);
        mBtnlater = fatcaView.findViewById(R.id.later);
        mBtnDone = fatcaView.findViewById(R.id.doneActivation);

    }

    private void setUpToolBar() {
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar("Profile Activation", true, false, false, false, false, false, false, "");
            fragToolBar.setCallback(this);
        }
    }

    private void completeRegistration(String IINConfLink) {
        String dob_value = mBundle.getString("dateofbirth_value");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("dd/MM/yy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            Date oneWayTripDate = input.parse(dob_value);                 // parse input
            fatca_dob = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        saveInformation(IINConfLink);
    }

    private void saveInformation(String IINConfLink) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                imm.hideSoftInputFromWindow(Objects.requireNonNull(mActivity.getCurrentFocus()).getWindowToken(), 0);
            }
        }
        mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        Map<String, String> jsonParam = new HashMap<String, String>();

        try {
            String nominee_birth;

            String birth = mBundle.getString("dateofbirth_value").replace("-", "/");
            if (mBundle.getString("NomineeDob").equalsIgnoreCase("DD-MM-YYYY")) {
                nominee_birth = "";
            } else {
                nominee_birth = mBundle.getString("NomineeDob").replace("-", "/");
            }

            jsonParam.put("Holding", mBundle.getString("holding_nature_code"));
            //jsonParam.put("TaxStatus", mBundle.getString("tax_status"));
            jsonParam.put("TaxStatus", "01");
            jsonParam.put("Occupation", mBundle.getString("occupation_code"));
            jsonParam.put("Name", mBundle.getString("name_value"));
            jsonParam.put("DOB", birth);
            jsonParam.put("Gender", mBundle.getString("radiobtn_value"));
            jsonParam.put("PAN", mBundle.getString("PAN_value"));
            jsonParam.put("Address1", mBundle.getString("address_one_value"));
            jsonParam.put("Address2", mBundle.getString("address_second_value"));
            //jsonParam.put("Address3", mBundle.getString("address_third_value"));
            jsonParam.put("City", mBundle.getString("city_value"));
            jsonParam.put("State", mBundle.getString("state_code"));
            jsonParam.put("PinCode", mBundle.getString("pin_value"));
            jsonParam.put("Country", "IND");
            jsonParam.put("Email", Email);
            jsonParam.put("Mobile", mBundle.getString("mobile_value"));
            jsonParam.put("SecondApplicant", mBundle.getString("second_name_vlaue"));
            jsonParam.put("SecondApplicantPAN", mBundle.getString("second_pan_value"));
            jsonParam.put("ThirdApplicant", "");
            jsonParam.put("ThirdApplicantPAN", "");

            if (!mBundle.containsKey("gaurdian_name_value") || mBundle.getString("gaurdian_name_value").equals("null")) {
                jsonParam.put("GuardianName", "");
            } else {
                jsonParam.put("GuardianName", mBundle.getString("gaurdian_name_value"));
            }

            if (!mBundle.containsKey("gaurdian_pan_value") || mBundle.getString("gaurdian_pan_value").equals("null")) {
                jsonParam.put("GuardianPAN", "");
            } else {
                jsonParam.put("GuardianPAN", mBundle.getString("gaurdian_pan_value"));
            }

            jsonParam.put("NomineeName", mBundle.getString("nominee_name_value"));
            jsonParam.put("NomineeRelation", mBundle.getString("nominee_relation_value"));
            jsonParam.put("AcType", mBundle.getString("account_type_code"));
            jsonParam.put("AcNo", mBundle.getString("mEtAccNum"));
            if (mBundle.containsKey("micr"))
                jsonParam.put("MICRNo", mBundle.getString("micr"));
            else
                jsonParam.put("MICRNo", "");
            jsonParam.put("IFSCCode", mBundle.getString("IFSC"));
            jsonParam.put("BankName", mBundle.getString("bank"));
            jsonParam.put("BranchName", mBundle.getString("branch"));
            jsonParam.put("ForeignAddress", mBundle.getString("foreign_address_value"));
            jsonParam.put("ForeignCity", mBundle.getString("foreign_city_value"));
            jsonParam.put("ForeignPinCode", mBundle.getString("foreign_pin_value"));
            jsonParam.put("ForeignState", mBundle.getString("foreign_state_value"));
            jsonParam.put("ForeignCountry", mBundle.getString("foreign_country_value"));
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("Bid", AppConstants.APP_BID);

            jsonParam.put("FATCACountry", mBundle.getString("FATCACountry"));
            // jsonParam.put("FATCABirthState", mBundle.getString("state_code"));
            jsonParam.put("FATCABirthState", mBundle.getString("FATCABirthState"));
            jsonParam.put("FATCAAddressType", mBundle.getString("FATCAAddressType"));
            jsonParam.put("FATCABirthplace", mBundle.getString("FATCABirthplace"));
            jsonParam.put("FATCAWealthsource", mBundle.getString("FATCAWealthsource"));
            jsonParam.put("FATCAPEP", mBundle.getString("FATCAPEP"));
            jsonParam.put("FATCADOB", birth);

            jsonParam.put("ClientID", mSession.getCID());
            jsonParam.put("OnlineOption", mSession.getAppType());
            jsonParam.put("ProcessMode", "P");
            jsonParam.put("BankBranchAddress", mBundle.getString("branch_address"));
            jsonParam.put("BankCity", "");
            jsonParam.put("SecondApplicantDOB", "");
            jsonParam.put("ThirdApplicantDOB", "");
            jsonParam.put("NomineeGuardianName1", "");
            jsonParam.put("NomineeGuardianPAN1", "");
            jsonParam.put("NomineeName2", "");
            jsonParam.put("NomineeRelation2", "");
            jsonParam.put("NomineeDOB2", "");
            jsonParam.put("NomineeAddress2", "");
            jsonParam.put("NomineePercentage2", "");
            jsonParam.put("NomineeCity2", "");
            jsonParam.put("NomineeGuardianName2", "");
            jsonParam.put("NomineeGuardianPAN2", "");
            jsonParam.put("NomineeName3", "");
            jsonParam.put("NomineeRelation3", "");
            jsonParam.put("NomineeDOB3", "");
            jsonParam.put("NomineeAddress3", "");
            jsonParam.put("NomineePercentage3", "");
            jsonParam.put("NomineeCity3", "");
            jsonParam.put("NomineeGuardianName3", "");
            jsonParam.put("NomineeGuardianPAN3", "");
            jsonParam.put("GuardianDOB", "");
            jsonParam.put("NumberOfNominee", "1");

            jsonParam.put("NomineeAddress1", mBundle.getString("NomineeAddress"));
            jsonParam.put("NomineeCity1", mBundle.getString("NomineeCity"));
            jsonParam.put("ReferCode", mBundle.getString("refrl_code"));

            // new changes 15 Jun 2021
            if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
                jsonParam.put("NomineePercentage1", "100");
                jsonParam.put("NomineeDOB1", nominee_birth);
            } else {
                jsonParam.put("NewUCCFormat", "Y");
                jsonParam.put("NomineePercentage1", "100");
                if (mBundle.containsKey("isMinor") && mBundle.getString("isMinor").equals("true")) {
                    jsonParam.put("NomineeMinorFlag", "Y");
                    jsonParam.put("NomineeDOB1", mBundle.getString("nominee_minorBOD"));
                    jsonParam.put("NomineeGuardianName1", mBundle.getString("nominee_minor_gaurdain"));
                } else {
                    jsonParam.put("NomineeMinorFlag", "N");
                }
            }

            jsonParam.put("FATCAIncomeSlab", mBundle.getString("FATCAIncomeSlab"));
            jsonParam.put("IINConfLink", IINConfLink);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("==Request Param===="+(new JSONObject(jsonParam)));
        String url = Config.COMPLETE_REGISTRATION;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonParam), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        mActivity.removeAllStack();

                        if (mSession.getUCC_CODE().length() == 0 || mSession.getUCC_CODE().equalsIgnoreCase("NA")) {
                            mSession.setUCC_CODE(object.optString("UCC"));
                        }

                        mBundle.putString("AllData", object.toString());
                        if (IINConfLink.equals("Y")) {
                            String authanticationUrl = object.optString("NSEConfirmationLink");
                            if(authanticationUrl != null && !authanticationUrl.equals("")) {
                                loadAuthanticationUrl(authanticationUrl);
                            }
                        } else {
                            mActivity.displayViewOther(95, mBundle);
                        }

                        // Toast.makeText(mActivity, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        // Intent intent = new Intent(mActivity, SignatureActivity.class);
                        // intent.putExtra("ucc_code", object.optString("UCC"));
                        // intent.putExtra("chequeRequired", object.optString("ChequRequired"));
                        // startActivityForResult(intent, 100);

                    } else {
                        String message = object.optString("ServiceMSG");
                        if (message.contains(":")) {
                            String[] errorMessage = message.split(":", 2);
                            Toast.makeText(mActivity, errorMessage[1], Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mBar.dismiss();

                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        Toast.makeText(mActivity, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);

    }

    private void loadAuthanticationUrl(String authanticationUrl){
        fatcaView.findViewById(R.id.activationArea).setVisibility(View.GONE);
        fatcaView.findViewById(R.id.webviewArea).setVisibility(View.VISIBLE);
        WebView myWebView = (WebView) fatcaView.findViewById(R.id.activationWebview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        myWebView.loadUrl(authanticationUrl);
    }

    public void showConfirmationDialog(String msg) {
        // Have you done authantication?
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.do_confirm_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView editText = (TextView) dialogView.findViewById(R.id.textMsg);
        editText.setText(msg);
        AlertDialog alertDialog = dialogBuilder.create();
        TextView tvYes = (TextView) dialogView.findViewById(R.id.textYes);
        TextView tvNo = (TextView) dialogView.findViewById(R.id.textNo);
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mActivity, "No Clicked", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mActivity.displayViewOther(93, mBundle);

                mBundle.putString("ucc_code", mSession.getUCC_CODE());
                mActivity.displayViewOther(124, mBundle);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.instant) {
            completeRegistration("Y");
        } else if (id == R.id.later) {
            completeRegistration("N");
        } else if (id == R.id.doneActivation) {
            showConfirmationDialog("Have you done authantication?");
        }
    }

    @Override
    public void onToolbarItemClick(View view) {
    }

    private void testDialog(String title, String msg) {
        customDialog.showDialog(mActivity, title, msg,
                getResources().getString(R.string.text_ok), "", true, false);
    }

    @Override
    public void onDialogBtnClick(View view) {
        view.getId();//Todo nothing
    }
}
