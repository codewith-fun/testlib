package investwell.client.fragment.schemes;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class FragInvestNow extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private String mCommingFrom = "";
    private String mUCC_Code = "";
    private LinearLayout mLinearTransferHolding;
    private ToolbarFragment toolbarFragment;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_invest_now, container, false);
        setInitializer();
        setUpToolBar();
        getDataFromBundle();
        setListener();

        return view;
    }

    private void setInitializer() {
        mLinearTransferHolding = view.findViewById(R.id.ll_transfer_container);
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
    }

    private void getDataFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("type")) {
                if (bundle.getString("type").equals("comming_from_prospect_dashboard")) {
                    mCommingFrom = bundle.getString("type");

                } else {
                    /*view.findViewById(R.id.relToolbar).setVisibility(View.VISIBLE);*/
                }
            }

            if (bundle.containsKey("ucc_code")) {
                mUCC_Code = bundle.getString("ucc_code");
            } else {
                mUCC_Code = mSession.getUCC_CODE();
            }
            if (bundle.containsKey("type")) {

                if (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects") && bundle.getString("type").equals("comming_from_prospect_dashboard")) {
                    mLinearTransferHolding.setVisibility(View.VISIBLE);
                } else {
                    mLinearTransferHolding.setVisibility(View.GONE);
                }
            } else {
                mLinearTransferHolding.setVisibility(View.GONE);
            }
        }
    }

    private void setListener() {
        view.findViewById(R.id.sip_btn).setOnClickListener(this);
        view.findViewById(R.id.lumpsum_btn).setOnClickListener(this);
        view.findViewById(R.id.transfer_button).setOnClickListener(this);

    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_invest_now),true, false, false, false,false,false,false,"");
        }
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        int id = view.getId();
        if (id == R.id.sip_btn) {
            if (Config.BASKET_SCHEMES_LIST.size() > 0 || mSession.getAddToCartList().length() > 0) {
                callInvestNowApi("sip");
            } else {
                bundle.putString("type", "SIP");
                bundle.putString("ucc_code", mUCC_Code);
                mActivity.displayViewOther(13, bundle);
            }
        } else if (id == R.id.lumpsum_btn) {
            if (mCommingFrom.equals("i")) {
                bundle.putString("ucc_code", mUCC_Code);
                mActivity.displayViewOther(14, bundle);
            } else {
                if (Config.BASKET_SCHEMES_LIST.size() > 0 || mSession.getAddToCartList().length() > 0) {
                    callInvestNowApi("lumsum");
                } else {
                    bundle.putString("type", "LS");
                    bundle.putString("ucc_code", mUCC_Code);
                    mActivity.displayViewOther(14, bundle);
                }
            }
        } else if (id == R.id.transfer_button) {
            mActivity.displayViewOther(52, bundle);
        } else if (id == R.id.home_btn) {
            mActivity.displayViewOther(0, null);
        }
    }


    private void callInvestNowApi(final String type) {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = "";
        if (type.equals("lumsum")) {
            url = Config.INSERT_INTO_LUMPSUM;
        } else {
            url = Config.INSERT_INTO_SIP;
        }
        JSONObject jsonParam = new JSONObject();

        try {
            String FCode = "";
            String SCode = "";
            String FolioNo = "";
            String Reinvest = "";
            String Amount = "";
            String Installment = "";
            String amountData="";
            if (Config.BASKET_SCHEMES_LIST.size() > 0) {
                for (int i = 0; i < Config.BASKET_SCHEMES_LIST.size(); i++) {
                    JSONObject jsonObject = Config.BASKET_SCHEMES_LIST.get(i);
                    for (int j = 0; j <mApplication.amountList.size() ; j++) {
                        amountData=mApplication.getAmountList().get(i).getAmount();
                    }
                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                        FolioNo = "0";
                        Amount = amountData;
                        Installment = "0";
                        Reinvest = "Z";
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                        FolioNo = FolioNo + "|" + "0";
                        Amount = Amount + "|" + amountData;
                        Installment = Installment + "|" + "0";
                        Reinvest = Reinvest + "|" + "Z";
                    }
                }

            } else {
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                        FolioNo = "0";
                        Amount = "0";
                        Installment = "0";
                        Reinvest = "Z";
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                        FolioNo = FolioNo + "|" + "0";
                        Amount = Amount + "|" + "0";
                        Installment = Installment + "|" + "0";
                        Reinvest = Reinvest + "|" + "Z";
                    }
                }
            }


            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonParam.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonParam.put("Fcode", FCode);
            jsonParam.put("Scode", SCode);
            jsonParam.put("FolioNo", FolioNo);
            jsonParam.put("Reinvest", Reinvest);
            jsonParam.put("Amount", Amount);
            jsonParam.put("Installment", Installment);
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            //jsonParam.put("BasketCode", mSession.getSelectedBasketCode());
            jsonParam.put("BasketCode", "00");
            jsonParam.put("OnlineOption", mSession.getAppType());

        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();

                try {
                    if (object.optString("Status").equals("True")) {
                        mSession.setAddToCartList("");
                        mSession.setAddToNFOCartList("");
                        // mSession.setSelectedBasketCode("");
                        Config.BASKET_SCHEMES_LIST.clear();

                        if (type.equals("lumsum")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUCC_Code);
                            mActivity.displayViewOther(14, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUCC_Code);
                            mActivity.displayViewOther(13, bundle);
                        }


                    } else {
                        if (object.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", object.optString("ServiceMSG"), "invalidPasskey",false,true);
                        }else{
                            mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", object.optString("ServiceMSG"), "message",false,true);
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


}
