package investwell.client.fragment.transferholding;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class FragTransferHolding extends Fragment implements View.OnClickListener {
    String category = "E", scheme_type = "G", Reinvest = "Z";
    Spinner mSpinerAMC, mSpinnerScheme, mscheme_fromSpinner;
    private AppSession mSession;
    private AppApplication mApplication;
    private EditText folio_number,mFtNumber;
    private ArrayList<JSONObject> mListAmc, mListSchemes, mListFromSchme;
    private String mSelectedAmcCode = "", mType = "", mSelectedFolio = "0", mfromlistCode, tolistCode;
    private JSONObject mSelectedSchemeObject;
    private JsonObjectRequest jsonObjectRequest;
    private String mUCC_Code = "", switch_type_value = "Amount", reedmption_type_value;
    private MainActivity mActivity;
    private ToolbarFragment fragToolBar;
    private TextView mTvName,mTvHolding,mTvUcc;
    private LinearLayout mll_ft_number;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_transfer_holding, container, false);

        setUpToolBar();
        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        view.findViewById(R.id.transaction_btn).setOnClickListener(this);
        mSpinerAMC = view.findViewById(R.id.amc_spinner);
        folio_number = view.findViewById(R.id.folio_number);
        mFtNumber = view.findViewById(R.id.ft_number);
        mSpinnerScheme = view.findViewById(R.id.scheme_spinner);
        mscheme_fromSpinner = view.findViewById(R.id.scheme_from);
        mTvName = view.findViewById(R.id.tvName);
        mTvHolding = view.findViewById(R.id.tvHolding);
        mTvUcc = view.findViewById(R.id.tvUcc);
        RadioGroup scheme_category_grp = view.findViewById(R.id.scheme_category);
        RadioGroup scheme_type_grp = view.findViewById(R.id.scheme_type);
        mll_ft_number = view.findViewById(R.id.ll_ft_number);

        Bundle bundle = getArguments();

        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
            try{
                JSONObject jsonObject = new JSONObject(bundle.getString("allData"));
                mTvName.setText(jsonObject.optString("InvestorName"));
                mTvHolding.setText(jsonObject.optString("HoldingType"));
                mTvUcc.setText(bundle.getString("ucc_code"));

            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            mUCC_Code = mSession.getUCC_CODE();
        }


        scheme_category_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.equity) {
                    category = "E";
                } else if (i == R.id.debt) {
                    category = "D";
                } else if (i == R.id.hybrid) {
                    category = "H";
                }

                getAllSchemesWithDetails();
            }
        });

        scheme_type_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.growth) {
                    scheme_type = "G";
                    Reinvest = "Z";
                } else if (i == R.id.devident) {
                    scheme_type = "D";
                    Reinvest = "D";
                }
                getAllSchemesWithDetails();

            }
        });

        getAllAMC();
        return view;

    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_transfer_holding), true, false, false, false, false, false, false,"");
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();/* case R.id.back_arrow:
                mActivity.getSupportFragmentManager().popBackStack();
                break;*/
        if (id == R.id.cancel_btn) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.transaction_btn) {
            if (folio_number.getText().toString().isEmpty()) {
                folio_number.requestFocus();
                folio_number.setError("Error");
            } else if (mll_ft_number.getVisibility() == View.VISIBLE && mFtNumber.getText().toString().isEmpty()) {
                mFtNumber.requestFocus();
                mFtNumber.setError("");
            } else {
                ShowDialog();
            }
        }

    }


    private void setSpinnerAMC() {
        ArrayList<String> stringArray = new ArrayList<String>();
        for (int i = 0, count = mListAmc.size(); i < count; i++) {
            JSONObject jsonObject = mListAmc.get(i);
            stringArray.add(jsonObject.optString("AMCName"));
        }
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, stringArray);
        spinner_value.setDropDownViewResource(R.layout.spinner_dropdown_items);
        mSpinerAMC.setAdapter(spinner_value);
        mSpinerAMC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonObject = mListAmc.get(mSpinerAMC.getSelectedItemPosition());
                mSelectedAmcCode = jsonObject.optString("AMCCode");
                if (mSelectedAmcCode.contains("F0032")||mSelectedAmcCode.contains("FTI")){
                    mll_ft_number.setVisibility(View.VISIBLE);
                }else{
                    mll_ft_number.setVisibility(View.GONE);
                }
                getAllSchemesWithDetails();
                getSchemeFromDta();
                // getFolioList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSpinnerScheme() {
        ArrayList<String> stringArray = new ArrayList<String>();
        for (int i = 0, count = mListSchemes.size(); i < count; i++) {
            JSONObject jsonObject = mListSchemes.get(i);
            stringArray.add(jsonObject.optString("SchemeName"));
        }
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, stringArray);
        spinner_value.setDropDownViewResource(R.layout.spinner_dropdown_items);
        mSpinnerScheme.setAdapter(spinner_value);
        mSpinnerScheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedSchemeObject = mListSchemes.get(mSpinnerScheme.getSelectedItemPosition());
                tolistCode = mSelectedSchemeObject.optString("SchemeCode");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSpinnerFromScheme() {
        ArrayList<String> stringArray = new ArrayList<String>();
        for (int i = 0, count = mListFromSchme.size(); i < count; i++) {
            JSONObject jsonObject = mListFromSchme.get(i);
            stringArray.add(jsonObject.optString("SchemeName"));
        }
        ArrayAdapter spinner_value = new ArrayAdapter(mActivity, R.layout.spinner_item, stringArray);
        spinner_value.setDropDownViewResource(R.layout.spinner_dropdown_items);
        mscheme_fromSpinner.setAdapter(spinner_value);
        mscheme_fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject jsonObject = mListFromSchme.get(mscheme_fromSpinner.getSelectedItemPosition());
                mfromlistCode = jsonObject.optString("SchemeCode");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void getAllAMC() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.GET_ALL_AMC;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        mListAmc = new ArrayList<>();
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("AMCList");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mListAmc.add(object);
                            }
                            setSpinnerAMC();

                        } else {
                            mApplication.showSnackBar(mscheme_fromSpinner,getResources().getString(R.string.error_try_again));
                        }


                    } catch (JSONException e) {
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


    private void getAllSchemesWithDetails() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//

        String url = Config.GET_SCHMES_LIST;
        try {

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Fcode", mSelectedAmcCode);
            jsonParam.put("FolioNo", mSelectedFolio);
            jsonParam.put("SchemeType", "All");
            jsonParam.put("Option", "All");
            jsonParam.put("MyScheme", "N");
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("OnlineOption", mSession.getAppType());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    mBar.dismiss();
                    try {
                        mListSchemes = new ArrayList<>();

                        if (response.optBoolean("Status")) {
                            JSONArray araArray = response.getJSONArray("SchemeListDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mListSchemes.add(object);
                            }
                            setSpinnerScheme();
                        } else {

                            mApplication.showSnackBar(mscheme_fromSpinner,getResources().getString(R.string.error_try_again));

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mBar.dismiss();
                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (error instanceof NoConnectionError)
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

    private void getSchemeFromDta() {

        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.GET_SCHMES_LIST;

        try {

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Fcode", mSelectedAmcCode);
            jsonParam.put("FolioNo", mSelectedFolio);
            jsonParam.put("SchemeType", "All");
            jsonParam.put("Option", "All");
            jsonParam.put("MyScheme", "All");
            jsonParam.put("Passkey", mSession.getPassKey());
            jsonParam.put("OnlineOption", mSession.getAppType());


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    System.out.println("response" + response);

                    mBar.dismiss();
                    try {
                        mListFromSchme = new ArrayList<>();

                        if (response.optBoolean("Status")) {
                            JSONArray araArray = response.getJSONArray("SchemeListDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mListFromSchme.add(object);
                            }
                            setSpinnerFromScheme();

                        } else {
                            mApplication.showSnackBar(mscheme_fromSpinner,getResources().getString(R.string.error_try_again));

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mBar.dismiss();
                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (error instanceof NoConnectionError)
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

    private void transact() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.Exceptional_Switch;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Fcode", mSelectedAmcCode);
            jsonObject.put("FromScode", mfromlistCode);
            jsonObject.put("ToScode", tolistCode);
            jsonObject.put("FolioNo", folio_number.getText().toString());
            jsonObject.put("Amount", "0");
            jsonObject.put("SwitchType", "AllUnit");
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("DividendOption", "Z");
            jsonObject.put("FTAccountNo",mFtNumber.getText().toString());

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    /*           mActivity.showCommonDailog(mActivity, "Status", response.optString("ServiceMSG"));*/
                    //showDialog(response.optString("Status"),response.optString("ServiceMSG"));
                    mApplication.showCommonDailog(mActivity, mActivity, true, "Status", response.optString("ServiceMSG"), "Error",false,true);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mBar.dismiss();
                    if (error.networkResponse != null && error.networkResponse.data != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (error instanceof NoConnectionError)
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


    private void ShowDialog() {
        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_notification);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvHeading = dialog.findViewById(R.id.heading);
        Button yes_btn = dialog.findViewById(R.id.yes_btn);
        Button no_btn = dialog.findViewById(R.id.no_btn);

        tvTitle.setText(getResources().getString(R.string.transfer_holding_note_txt));
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("BrokerName"))) {
            tvHeading.setText(getString(R.string.transact_txt)+" "+getString(R.string.app_name)+"\n\n"+getString(R.string.transact_txt_1)+" "+ Utils.getConfigData(mSession).optString("BrokerName"));
        }
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                transact();

            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);

        dialog.show();


    }
}


