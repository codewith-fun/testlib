package investwell.client.fragment.videoKyc;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import java.util.Calendar;
import java.util.Date;

import investwell.client.activity.AccountConfActivity;
import investwell.client.activity.KycActivity;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextInputLayout;


public class frag_kyc_Form extends Fragment implements View.OnClickListener {
    private ToolbarFragment fragToolBar;
    private TextView mTvVerify;
    private CustomTextInputEditText mEtPanNumber, mEtName, mEtMobile, mEtEmail, mEtRefrlCode;
    private CustomTextInputLayout tilPan, tilName, tilEmail, tilMobile, tilRefralCode;
    private CustomButton mBtnContinue;
    private AppSession mSession;
    private MainActivity mActivity;
    private String mCid;
    private Bundle bundle;
    private CardView panCard;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_kyc_form, container, false);
        bundle = getArguments();
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(getActivity());
        setUpToolBar();
        setInitializer(view);
        intialCheck();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.kyc_toolbar_secondary_title), true, false, false, false, false, false, false
                    , "");
        }
    }

    private void setInitializer(View view) {
        tilPan = view.findViewById(R.id.til_pan);
        tilName = view.findViewById(R.id.til_name);
        tilEmail = view.findViewById(R.id.til_mail);
        tilMobile = view.findViewById(R.id.til_mobile);
        tilRefralCode = view.findViewById(R.id.til_referal);
        mEtPanNumber = view.findViewById(R.id.et_pan);
        mEtName = view.findViewById(R.id.et_name);
        mEtMobile = view.findViewById(R.id.et_mobile);
        mEtEmail = view.findViewById(R.id.et_mail);
        mEtRefrlCode = view.findViewById(R.id.et_referal);
        mTvVerify = view.findViewById(R.id.tvverify);
        panCard = view.findViewById(R.id.panCard);

        mBtnContinue = view.findViewById(R.id.btn_continue);
        setListeners();

        getDataFromBundle();

        panCheck();

    }

    private void setListeners() {
        mBtnContinue.setOnClickListener(this);
        mTvVerify.setOnClickListener(this);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else if (mSession.getCID().isEmpty() || mSession.getCID().equalsIgnoreCase("NA")) {
            mCid = mEtPanNumber.getText().toString();
        } else {
            mCid = mSession.getCID();
        }
    }

    private void intialCheck() {

        if (mSession.getHasLoging()) {
            if (bundle != null && bundle.containsKey("Name")) {

                if ((!bundle.getString("Name").isEmpty()) || (!bundle.getString("Name").equals("_"))) {
                    mEtName.setText(bundle.getString("Name"));
                    mEtPanNumber.setEnabled(true);
                    panCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));

                }

                if ((!bundle.getString("MobileNO").isEmpty()) || (!bundle.getString("MobileNO").equals("_"))) {
                    mEtMobile.setText(bundle.getString("MobileNO"));
                }

                if ((!bundle.getString("Email").isEmpty()) || (!bundle.getString("Email").equals("_"))) {
                    mEtEmail.setText(bundle.getString("Email"));
                }


            } else {
                mEtEmail.setText(mSession.getEmail());
                mEtMobile.setText(mSession.getMobileNumber());
                mEtName.setText(mSession.getFullName());
                mEtPanNumber.setEnabled(true);
                panCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));

            }
        }

        mEtRefrlCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtRefrlCode.getText().toString().length() > 2) {
                    mTvVerify.setVisibility(View.VISIBLE);
                } else {
                    mTvVerify.setVisibility(View.INVISIBLE);
                    tilRefralCode.setError("");
                }

                if (mEtRefrlCode.getText().toString().length() == 0) {
                    mBtnContinue.setEnabled(true);
                }

            }
        });


    }

    public void onClick(View view) {
        String email_value = mEtEmail.getText().toString().trim();

        int id = view.getId();
        if (id == R.id.btn_continue) {
            if (mEtPanNumber.getText().toString().isEmpty()) {
                mEtPanNumber.requestFocus();
                mEtPanNumber.setError(getResources().getString(R.string.personal_details_error_pan_card));
            } else if (mEtName.getText().toString().isEmpty()) {
                tilName.setError(getResources().getString(R.string.personal_details_error_empty_username));
                mEtName.requestFocus();
            } else if (mEtMobile.getText().toString().isEmpty()) {
                mEtMobile.setError(getResources().getString(R.string.personal_details_invalid_mobile));
                mEtMobile.requestFocus();
            } else if (mEtEmail.getText().toString().isEmpty()) {
                mEtEmail.setError("");
                mEtEmail.requestFocus();
            } else if (email_value.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                mEtEmail.setError("");
                mEtEmail.requestFocus();
            } else if (tilPan.getError().toString().equals("KYC Verified")) {
                bundle.putString("coming_from", "pan_kyc");
                bundle.putString("name", mEtName.getText().toString());
                bundle.putString("email", mEtEmail.getText().toString());
                bundle.putString("mobile", mEtMobile.getText().toString());
                bundle.putString("pan", mEtPanNumber.getText().toString());
                mActivity.displayViewOther(5, bundle);


            } else {


                showKYCDialog();
            }
        } else if (id == R.id.tvverify) {
            checkrfrlcode();
        }
    }

    private void panCheck() {

        mEtPanNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tilPan.setError("");

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtPanNumber.getText().toString().length() > 9) {
                    verifyPAN(mEtPanNumber.getText().toString());
                }
            }
        });


    }

    public void checkrfrlcode() {

        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.Refrl_Code;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("SBCode", mEtRefrlCode.getText().toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {

                        tilRefralCode.setError("(" + jsonObject.optString("ServiceMSG") + ")");
                        if (jsonObject.optString("status").equalsIgnoreCase("true")) {
                            mBtnContinue.setEnabled(true);
                        } else {
                            mBtnContinue.setEnabled(false);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBar.dismiss();
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

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void verifyPAN(String panNumber) {
        tilPan.setError("");
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.PAN_VERIFICATION;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("PAN", panNumber);
            if (Config.COMMON_URL.equalsIgnoreCase("https://nativeapi.my-portfolio.in")) {
                jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            } else {
                jsonObject.put(AppConstants.PASSKEY, "jdfjdf7474jcfjh");
            }
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("ReferCode", mEtRefrlCode.getText().toString());
            jsonObject.put("Cid", mCid);
            jsonObject.put("applicant_name", mEtName.getText().toString());
            jsonObject.put("location_cord", "");
            jsonObject.put("location_details", "");


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();

                    tilPan.setError(jsonObject.optString("ServiceMSG"));

                    if (jsonObject.optString("KYCResponseMSG").equals("KYC Verified")) {
                        // mEtName.setText(jsonObject.optString("Name"));
                        tilPan.setError(jsonObject.optString("ServiceMSG"));
                        tilPan.setErrorTextColor(ColorStateList.valueOf(Color.GREEN));

                    }

                    if (jsonObject.optString("KYCResponseMSG").contains("new credentials")) {

                        Intent intent = new Intent(getActivity(), AccountConfActivity.class);
                        intent.putExtra("message", jsonObject.optString("KYCResponseMSG"));
                        startActivity(intent);
                    }

                    if (jsonObject.optString("KYCResponseMSG").contains("Name & Email mismatched")) {
                        mBtnContinue.setEnabled(false);
                        mBtnContinue.setBackgroundResource(R.drawable.rounded_red);
                    } else {
                        mBtnContinue.setEnabled(true);
                        mBtnContinue.setBackgroundResource(R.drawable.btn_bg_primary);
                    }
                    if (jsonObject.optString("KYCResponseMSG").contains("IIN is ACTIVE")) {
                        String IIN = jsonObject.optString("IIN");
                        alertdialog(IIN, 100, jsonObject.optString("KYCResponseMSG"));


                    } else if (jsonObject.optString("KYCResponseMSG").contains("IIN is INACTIVE")) {
                        String IIN = jsonObject.optString("IIN");
                        alertdialog(IIN, 200, jsonObject.optString("KYCResponseMSG"));

                    } else {

                        tilPan.setError(jsonObject.optString("ServiceMSG"));
                        tilPan.setErrorTextColor(ColorStateList.valueOf(Color.RED));


                    }



/*



                    if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("KYC Not Verified")) {
                        tilPan.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        mBtnContinue.setEnabled(true);
                        mBtnContinue.setBackgroundResource(R.drawable.btn_bg_primary);
                        // mEtName.setText(jsonObject.optString("Name"));
                    } else {
                        tilPan.setErrorTextColor(ColorStateList.valueOf(Color.RED));
                        mBtnContinue.setEnabled(false);
                        mBtnContinue.setBackgroundResource(R.drawable.rounded_grey_fill);

                    }*/

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mBar.dismiss();
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            //  Toast.makeText(mActivity, mDataList.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError) {
                        // Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    }
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

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    public void alertdialog(final String value, final int code, String message) {

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (code == 100) {

            Intent intent = new Intent(getActivity(), AccountConfActivity.class);
            intent.putExtra("message", message);
            startActivity(intent);
            // startActivity(new Intent(mActivity, AccountConfirmed.class));
        } else {

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("UCC", value);
                jsonObject.put("chequeRequired", Utils.getConfigData(mSession).optString("ChequeRequired"));
                jsonObject.put("ServiceMSG", message);
                if (Utils.getConfigData(mSession).optString("SignRequired").equalsIgnoreCase("Y")) {
                    bundle.putString("AllData", jsonObject.toString());
                    bundle.putString("comingFrom", "kycForm");
                    mActivity.displayViewOther(95, bundle);
                    mEtPanNumber.setText("");
                } else {
                    startActivity(new Intent(getActivity(), AccountConfActivity.class));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private void showKYCDialog() {

        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Light);
        dialog.setContentView(R.layout.videokyc_dialog);
        TextView mTvEmail = dialog.findViewById(R.id.TvEmail);
        TextView mTvMobile = dialog.findViewById(R.id.TvMobile);
        TextView mInfo = dialog.findViewById(R.id.info);
        dialog.findViewById(R.id.later_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mEtPanNumber.setText("");
            }
        });

        dialog.findViewById(R.id.proceed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                VideoKyc();
            }
        });
        mInfo.setText("You can also contact " + getString(R.string.app_name) + " for KYC at:");
        mTvEmail.setText(Utils.getConfigData(mSession).optString("Email"));
        mTvMobile.setText(Utils.getConfigData(mSession).optString("CallBack"));
        dialog.setCancelable(true);


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mEtPanNumber.setText("");
            }
        });
        dialog.show();
    }

    private void VideoKyc() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int mSec = calendar.get(Calendar.MILLISECOND);

        String url = Config.Video_KYC;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Mobile", mEtMobile.getText().toString());
            jsonObject.put("Name", mEtName.getText().toString());
            jsonObject.put("Email", mEtEmail.getText().toString());
            jsonObject.put("PAN", mEtPanNumber.getText().toString());
            jsonObject.put("KYCUserName", mEtName.getText().toString().replace(" ", "").toLowerCase() + mSec);
            jsonObject.put("ClientID", mCid);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        mBar.dismiss();
                        Intent intent = new Intent(mActivity, KycActivity.class);
                        intent.putExtra("title", "Video KYC");
                        intent.putExtra("video_kyc_url", response.optString("ServiceMSG"));
                        startActivity(intent);
                        mActivity.finish();
                    } else {
                        mBar.dismiss();
                        Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBar.dismiss();
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
