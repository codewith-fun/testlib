package investwell.client.fragment.bank;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.crop.CropImage;
import investwell.utils.crop.CropImageView;

public class FragAddBank extends Fragment implements View.OnClickListener {
    String[] code, particulars;
    String branch = "", address = "", city = "", bank = "", bankcode = "", IFSC = "", account_number_value = "", brancaddress = "", PIN = "";
    private Bundle mBundle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private boolean mIsIFSCCodeVerify = false;
    private View bankDetailView;
    private ToolbarFragment fragToolBar;
    //Stepper Views
    //On boarding Views
    private EditText mEtIfsCode, mEtAccNo, et_micr_number, et_BranchName,
            et_Address1, et_Address2, et_Address3, et_City, et_PINCODe;
    private Spinner account_type_spinner, spinner_proof;
    private TextView mTvBankValue, mTvBankBranchValue, mTvAddressValue, mTvCityValue, tvBankName;
    private Button mBtnContinue, mBtnPrevious;
    private CardView cv_bank_detail_layout;
    private ProgressBar pbAccountTypeBar, pbIfsCodeBar;
    private TextView mTvErrIfsCode, mTvErrAcc;
    private String mUCC_Code = "";
    private String mSelectedAccountType = "", mBankName = "", mBankCode = "";

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
        bankDetailView = inflater.inflate(R.layout.frag_add_bank_form, container, false);
        initializer();
        setUpToolBar();
        setListener();
        callAccountTypeApi();
        setAccountProofType();
        return bankDetailView;
    }

    private void initializer() {
        mBundle = getArguments();

        if (mBundle != null && mBundle.containsKey("ucc_code")) {
            mUCC_Code = mBundle.getString("ucc_code");
            mBankName = mBundle.getString("bankName");
            mBankCode = mBundle.getString("bankCode");
        } else {
            mUCC_Code = mSession.getUCC_CODE();
        }

        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);

        tvBankName = bankDetailView.findViewById(R.id.tvBankName);
        et_BranchName = bankDetailView.findViewById(R.id.et_BranchName);
        et_Address1 = bankDetailView.findViewById(R.id.et_Address1);
        et_Address2 = bankDetailView.findViewById(R.id.et_Address2);
        et_Address3 = bankDetailView.findViewById(R.id.et_Address3);
        et_City = bankDetailView.findViewById(R.id.et_City);
        et_PINCODe = bankDetailView.findViewById(R.id.et_PINCODe);
        mEtIfsCode = bankDetailView.findViewById(R.id.et_ifs_code);
        mEtAccNo = bankDetailView.findViewById(R.id.et_account_number);
        et_micr_number = bankDetailView.findViewById(R.id.et_micr_number);
        account_type_spinner = bankDetailView.findViewById(R.id.et_spinner);
        spinner_proof = bankDetailView.findViewById(R.id.et_spinner_proof);
        cv_bank_detail_layout = bankDetailView.findViewById(R.id.cv_bank_detail_layout);
        mTvBankValue = bankDetailView.findViewById(R.id.bank);
        mTvBankBranchValue = bankDetailView.findViewById(R.id.branch);
        mTvAddressValue = bankDetailView.findViewById(R.id.address);
        mTvCityValue = bankDetailView.findViewById(R.id.et_city);
        pbAccountTypeBar = bankDetailView.findViewById(R.id.progressBar);
        pbIfsCodeBar = bankDetailView.findViewById(R.id.progressBar2);
        mTvErrAcc = bankDetailView.findViewById(R.id.tv_err_acc_no);
        mTvErrIfsCode = bankDetailView.findViewById(R.id.tv_err_ifs);
        mBtnContinue = bankDetailView.findViewById(R.id.btn_continue_nse_fatca);
        mBtnPrevious = bankDetailView.findViewById(R.id.btn_previous_nse_fatca);
        account_type_spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);
        spinner_proof.getBackground().setColorFilter(getResources().getColor(R.color.colorGrey_600), PorterDuff.Mode.SRC_ATOP);

        tvBankName.setText(mBankName);
    }


    private void setUpToolBar() {
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.add_bank_text), true, false, false, false, false, false, false, "");
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
            addBankMethod();
        }
    }


    private void addBankMethod() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//{PASSKEY}/{BID}/{UCC}
        String url = Config.ADD_NSE_BANK;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("IIN", mUCC_Code);
            jsonObject.put("AccountNo", mEtAccNo.getText().toString().trim());

            int selectedPosition = account_type_spinner.getSelectedItemPosition();
            mSelectedAccountType = code[selectedPosition];

            jsonObject.put("AccountType", mSelectedAccountType);
            jsonObject.put("IFSCCode", mEtIfsCode.getText().toString());
            jsonObject.put("BankCode", mBankCode);
            jsonObject.put("Branch", et_BranchName.getText().toString());
            jsonObject.put("Address1", et_Address1.getText().toString());
            jsonObject.put("Address2", et_Address2.getText().toString());
            jsonObject.put("Address3", et_Address3.getText().toString());
            jsonObject.put("City", et_City.getText().toString());
            jsonObject.put("Pin", et_PINCODe.getText().toString());
            String selctedProof = spinner_proof.getSelectedItem().toString();
            jsonObject.put("ProofOfAccount", selctedProof);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    if (jsonObject.optBoolean("Status")) {
                        //Toast.makeText(getActivity(), jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(getActivity(), 109);


                    } else {
                        Toast.makeText(getActivity(), jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 109) {
            CropImage.ActivityResult imageResult = CropImage.getActivityResult(data);
            try {
                Uri selectedImage = imageResult.getUri();
                if (selectedImage != null) {
                    Bitmap gallery_bit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    gallery_bit.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] byteArray = baos.toByteArray();
                    String b64String = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    String dataUri = "data:image/jpeg;base64," + b64String;
                    uploadFile( dataUri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void uploadFile(String dataUri) {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.UPLOAD_FILE_NSE_BANK;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("BankCode", bankcode);
            jsonObject.put("AccountNo", mEtAccNo.getText().toString().trim());
            jsonObject.put("IIN", mUCC_Code);
            jsonObject.put("ImageString", dataUri);


        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        Toast.makeText(getActivity(), object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mActivity, jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(mActivity, mApplication.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);

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
                            mTvErrIfsCode.setVisibility(View.INVISIBLE);
                            bank = jsonObject.optString("Bank");
                            branch = jsonObject.optString("Branch");
                            IFSC = jsonObject.optString("IFSC");
                            address = jsonObject.getString("Address");
                            city = jsonObject.optString("City");
                            bankcode = jsonObject.optString("BankCode");
                            brancaddress = jsonObject.optString("Address");
                            PIN = extractDigits(brancaddress);
                            et_PINCODe.setText(PIN);
                            et_Address1.setText(address);
                            et_City.setText(city);
                            et_BranchName.setText(branch);

                            //PIN = brancaddress.replaceAll("(\\d{6})","");

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

    public String extractDigits(final String in) {
        final Pattern p = Pattern.compile("(\\d{6})");
        final Matcher m = p.matcher(in);
        if (m.find()) {
            return m.group(0);
        }
        return "";
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

    private void setAccountProofType() {
        List list = new ArrayList();
        list.add("Original cancelled cheque");
        list.add("Attested copy of bank passbook frontpage");
        list.add("Attested copy of bank statement");
        list.add("Attested copy of bank letter");


        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, list);
        spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_proof.setAdapter(spinner_value);
        spinner_proof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

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
}
