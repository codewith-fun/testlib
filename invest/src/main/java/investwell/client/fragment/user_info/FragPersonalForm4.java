package investwell.client.fragment.user_info;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextViewRegular;

public class FragPersonalForm4 extends Fragment implements View.OnClickListener {

    Spinner account_type_spinner;
    String[] code, particulars;
    CustomTextInputEditText mEtIfscCode, mEtAccNum;
    String branch, address, contact, city, district, state, rtgs, bank, bankcode, IFSC, account_number_value, brancaddress;
    CustomTextViewRegular bank_value, branch_value, address_value, city_value;
    private CardView cv_bank_detail_layout;
    private Bundle mBundle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private boolean mIsIFSCCodeVerify = false;
    private ToolbarFragment fragToolBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_personal_form4, container, false);
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        mSession = AppSession.getInstance(mActivity);
        setUpToolBar();
        setInitializer(view);
        setListener(view);

        account_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        mEtIfscCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtIfscCode.getText().toString().length() == 11) {


                    getBankDetail(mEtIfscCode.getText().toString());
                }
            }
        });

        getBankAccountType();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_bank_details_form), true, false, false, false, false, false, false,"");
        }
    }

    private void setInitializer(View view) {
        mBundle = getArguments();

        account_type_spinner = (Spinner) view.findViewById(R.id.et_spinner);
        mEtIfscCode = view.findViewById(R.id.et_ifsc_code);
        cv_bank_detail_layout = view.findViewById(R.id.cv_bank_detail_layout);

        bank_value = view.findViewById(R.id.bank);
        branch_value = view.findViewById(R.id.branch);
        address_value = view.findViewById(R.id.address);
        city_value = view.findViewById(R.id.et_city);

        mEtAccNum = view.findViewById(R.id.et_account_number);
    }

    private void setListener(View view) {
        view.findViewById(R.id.btn_continue_nse_fatca).setOnClickListener(this);
        view.findViewById(R.id.btn_previous_nse_fatca).setOnClickListener(this);
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
        account_number_value = mEtAccNum.getText().toString();
        IFSC = mEtIfscCode.getText().toString();

        if (IFSC.equals("")) {
            mApplication.showSnackBar(mEtIfscCode,getResources().getString(R.string.personal_details_error_empty_ifsc));
        } else if (IFSC.length() < 11) {
            mApplication.showSnackBar(mEtIfscCode,getResources().getString(R.string.personal_details_error_invalid_ifsc));
        } else if (!mIsIFSCCodeVerify) {
            mApplication.showSnackBar(mEtIfscCode,getResources().getString(R.string.personal_details_error_invalid_ifsc));
        } else if (account_number_value.equals("")) {
            mApplication.showSnackBar(mEtIfscCode,getResources().getString(R.string.personal_details_error_acount_no));
        } else if (account_number_value.length() < 9) {
            mApplication.showSnackBar(mEtIfscCode,getResources().getString(R.string.personal_details_error_invalid_acount_no));
        } else {
            mBundle.putString("mEtAccNum", account_number_value);
            mBundle.putString("IFSC", IFSC);
            mActivity.displayViewOther(9, mBundle);
        }
    }


    private void getBankDetail(String IFSC_Code) {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.BANK_DETAIL;
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid",AppConstants.APP_BID);
            jsonObject.put("IFSCCode",IFSC_Code);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    try {

                        JSONArray jsonArray = response.optJSONArray("IFSCBankDetail");
                        for (int i = 0; i <jsonArray.length() ; i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(0);

                            bank = jsonObject.optString("Bank");
                            branch = jsonObject.optString("Branch");
                            IFSC = jsonObject.optString("IFSC");
                            address = jsonObject.getString("Address");
                            city = jsonObject.optString("City");
                            bankcode = jsonObject.optString("BankCode");
                            brancaddress = jsonObject.optString("Address");

                            cv_bank_detail_layout.setVisibility(View.VISIBLE);
                            if (bank.equalsIgnoreCase("") || bank.equals(null)) {
                                showErrorDailog();
                            } else {
                                bank_value.setText("Bank: " + bank);
                                branch_value.setText("Branch: " + branch);
                                address_value.setText("Address: " + address);
                                city_value.setText("City: " + city);

                            }
                            mIsIFSCCodeVerify = true;
                            mBundle.putString("ifsc_code", IFSC);
                            mBundle.putString("bank", bank);
                            mBundle.putString("branch", branch);
                            mBundle.putString("bankcode", bankcode);
                            mBundle.putString("branch_address",brancaddress);
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
                    mBar.dismiss();
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
                public void retry(VolleyError volleyError) throws VolleyError {

                }
            });


            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    private void showErrorDailog() {
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


    private void getBankAccountType() {

        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.BANK_ACCOUNT_TYPE;


        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    mBar.hide();
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
                            mApplication.showSnackBar(mEtAccNum,getResources().getString(R.string.error_try_again));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mBar.hide();
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
}
