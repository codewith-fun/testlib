package investwell.client.fragment.onboarding;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

public class BankDetailFragment extends Fragment implements View.OnClickListener {
    String[] code, particulars;
    String branch, address, city, bank, bankcode, IFSC, account_number_value, brancaddress;
    private Bundle mBundle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private boolean mIsIFSCCodeVerify = false;
    private View bankDetailView;
    private ToolbarFragment fragToolBar;
    private View contentSteppers;
    //Stepper Views
    private TextView mTvStepOne, mTvStepTwo, mTvStepThree, mTvStepFour;
    private FloatingActionButton mFabStepOne, mFabStepTwo, mFabStepThree, mFabStepFour, mFabStepFive;
    //On boarding Views
    private EditText mEtIfsCode, mEtAccNo, et_micr_number;
    private Spinner account_type_spinner;
    private TextView mTvBankValue, mTvBankBranchValue, mTvAddressValue, mTvCityValue;
    private Button mBtnContinue, mBtnPrevious;
    private CardView cv_bank_detail_layout;
    private ProgressBar pbAccountTypeBar, pbIfsCodeBar;
    private TextView mTvErrIfsCode, mTvErrAcc;

    @Override
    public void onAttach(Context context) {
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
        bankDetailView = inflater.inflate(R.layout.fragment_bank_details, container, false);
        initializer();
        setUpToolBar();
        showStepper();
        setListener();
        callAccountTypeApi();
        return bankDetailView;
    }

    private void initializer() {
        mBundle = getArguments();
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        contentSteppers = bankDetailView.findViewById(R.id.content_onboarding_steppers);
        mFabStepOne = contentSteppers.findViewById(R.id.fab_step_1);
        mFabStepTwo = contentSteppers.findViewById(R.id.fab_step_2);
        mFabStepThree = contentSteppers.findViewById(R.id.fab_step_3);
        mFabStepFour = contentSteppers.findViewById(R.id.fab_step_4);
        mFabStepFive = contentSteppers.findViewById(R.id.fab_step_5);
        mTvStepOne = contentSteppers.findViewById(R.id.tv_horizontal_1);
        mTvStepTwo = contentSteppers.findViewById(R.id.tv_horizontal_2);
        mTvStepThree = contentSteppers.findViewById(R.id.tv_horizontal_3);
        mTvStepFour = contentSteppers.findViewById(R.id.tv_horizontal_4);
        View contentBank = bankDetailView.findViewById(R.id.content_onboarding_bank_detail);
        mEtIfsCode = contentBank.findViewById(R.id.et_ifs_code);
        mEtAccNo = contentBank.findViewById(R.id.et_account_number);
        et_micr_number = contentBank.findViewById(R.id.et_micr_number);
        account_type_spinner = contentBank.findViewById(R.id.et_spinner);
        cv_bank_detail_layout = contentBank.findViewById(R.id.cv_bank_detail_layout);
        mTvBankValue = contentBank.findViewById(R.id.bank);
        mTvBankBranchValue = contentBank.findViewById(R.id.branch);
        mTvAddressValue = contentBank.findViewById(R.id.address);
        mTvCityValue = contentBank.findViewById(R.id.et_city);
        pbAccountTypeBar = contentBank.findViewById(R.id.progressBar);
        pbIfsCodeBar = contentBank.findViewById(R.id.progressBar2);
        mTvErrAcc = contentBank.findViewById(R.id.tv_err_ifs);
        mTvErrIfsCode = contentBank.findViewById(R.id.tv_err_acc_no);
        mBtnContinue = contentBank.findViewById(R.id.btn_continue_nse_fatca);
        mBtnPrevious = contentBank.findViewById(R.id.btn_previous_nse_fatca);
        account_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
    }

    //Show Personal Details  Stepper
    private void showStepper() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            mFabStepOne.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepOne.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv1 = contentSteppers.findViewById(R.id.tv_step_1);
            tv1.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepTwo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepTwo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv2 = contentSteppers.findViewById(R.id.tv_step_2);
            tv2.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));


            mFabStepThree.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepThree.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv3 = contentSteppers.findViewById(R.id.tv_step_3);
            tv3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.darkPrimaryBtnBg));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
        }else
        {
            mFabStepOne.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepOne.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv1 = contentSteppers.findViewById(R.id.tv_step_1);
            tv1.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepTwo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepTwo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv2 = contentSteppers.findViewById(R.id.tv_step_2);
            tv2.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));


            mFabStepThree.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepThree.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv3 = contentSteppers.findViewById(R.id.tv_step_3);
            tv3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorPrimary)));
            mTvStepFour.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TextView tv4 = contentSteppers.findViewById(R.id.tv_step_4);
            tv4.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            mFabStepFive.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorWhite)));
            TextView tv5 = contentSteppers.findViewById(R.id.tv_step_5);
            tv5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
        }

    }

    private void setUpToolBar() {
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_bank_details_form), true, false, false, false, false, false, false, "");
        }
    }

    private void setListener() {
        mEtIfsCode.addTextChangedListener(textWatcher);
        mBtnContinue.setOnClickListener(this);
        mBtnPrevious.setOnClickListener(this);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mEtIfsCode.getText().toString().length() == 11) {
                callBankDetailApi(mEtIfsCode.getText().toString());
            }
        }
    };

    private void showErrorDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.document_alert, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView content = dialogView.findViewById(R.id.content);
        investwell.utils.customView.CustomButton ready_btn = dialogView.findViewById(R.id.ready_btn);
        content.setText(getResources().getString(R.string.personal_details_error_invalid_ifsc));
        ready_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private void setAccountType() {
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, particulars);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_type_spinner.setAdapter(spinner_value);
        account_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String account_type_code = code[i];
                mBundle.putString("account_type_code", account_type_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void callAccountTypeApi() {
        pbAccountTypeBar.setVisibility(View.VISIBLE);
        String url = Config.BANK_ACCOUNT_TYPE;


        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    pbAccountTypeBar.setVisibility(View.GONE);
                    try {

                        if (jsonObject.optString("Status").equals("True")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("BankTypeDetail");
                            code = new String[jsonArray.length()];
                            particulars = new String[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject values = jsonArray.getJSONObject(i);
                                code[i] = values.getString("Code");
                                particulars[i] = values.getString("Particular");
                            }
                            setAccountType();
                        } else {
                            mApplication.showSnackBar(mEtAccNo, getResources().getString(R.string.error_try_again));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    pbAccountTypeBar.setVisibility(View.GONE);
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callBankDetailApi(String IFSC_Code) {
        pbIfsCodeBar.setVisibility(View.VISIBLE);
        String url = Config.BANK_DETAIL;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("IFSCCode", IFSC_Code);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject response) {
                    pbIfsCodeBar.setVisibility(View.GONE);
                    try {

                        JSONArray jsonArray = response.optJSONArray("IFSCBankDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(0);

                            bank = jsonObject.optString("Bank");
                            branch = jsonObject.optString("Branch");
                            IFSC = jsonObject.optString("IFSC");
                            address = jsonObject.getString("Address");
                            city = jsonObject.optString("City");
                            bankcode = jsonObject.optString("BankCode");
                            brancaddress = jsonObject.optString("Address");

                            cv_bank_detail_layout.setVisibility(View.VISIBLE);
                            if (bank.equalsIgnoreCase("") || bank == null) {
                                showErrorDialog();
                            } else {
                                mTvBankValue.setText("Bank: " + bank);
                                mTvBankBranchValue.setText("Branch: " + branch);
                                mTvAddressValue.setText("Address: " + address);
                                mTvCityValue.setText("City: " + city);
                                et_micr_number.setText(jsonObject.optString("MICR"));

                            }
                            mIsIFSCCodeVerify = true;
                            mBundle.putString("ifsc_code", IFSC);
                            mBundle.putString("bank", bank);
                            mBundle.putString("branch", branch);
                            mBundle.putString("bankcode", bankcode);
                            mBundle.putString("branch_address", brancaddress);
                            mBundle.putString("micr", jsonObject.optString("MICR"));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        mIsIFSCCodeVerify = false;
                        cv_bank_detail_layout.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    pbIfsCodeBar.setVisibility(View.GONE);
                    mIsIFSCCodeVerify = false;
                    cv_bank_detail_layout.setVisibility(View.GONE);
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError volleyError) {

                }
            });


            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_continue_nse_fatca) {
            goToNext();
        } else if (id == R.id.btn_previous_nse_fatca) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

    private void goToNext() {
        account_number_value = mEtAccNo.getText().toString();
        IFSC = mEtIfsCode.getText().toString();
        if (IFSC.equals("")) {
            mTvErrIfsCode.setText(getResources().getString(R.string.personal_details_error_empty_ifsc));
            mTvErrIfsCode.setVisibility(View.VISIBLE);
            mTvErrAcc.setVisibility(View.INVISIBLE);
        } else if (IFSC.length() < 11) {
            mTvErrIfsCode.setText(getResources().getString(R.string.personal_details_error_invalid_ifsc));
            mTvErrIfsCode.setVisibility(View.VISIBLE);
            mTvErrAcc.setVisibility(View.INVISIBLE);
        } else if (!mIsIFSCCodeVerify) {
            mTvErrIfsCode.setText(getResources().getString(R.string.personal_details_error_invalid_ifsc));
            mTvErrIfsCode.setVisibility(View.VISIBLE);
            mTvErrAcc.setVisibility(View.INVISIBLE);
        } else if (account_number_value.equals("")) {
            mTvErrAcc.setText(getResources().getString(R.string.personal_details_error_acount_no));
            mTvErrAcc.setVisibility(View.VISIBLE);
            mTvErrIfsCode.setVisibility(View.INVISIBLE);
        } else if (account_number_value.length() < 9) {
            mTvErrAcc.setText(getResources().getString(R.string.personal_details_error_invalid_acount_no));
            mTvErrAcc.setVisibility(View.VISIBLE);
            mTvErrIfsCode.setVisibility(View.INVISIBLE);
        } else {
            mBundle.putString("mEtAccNum", account_number_value);
            mBundle.putString("IFSC", IFSC);
            mActivity.displayViewOther(9, mBundle);
        }
    }
}
