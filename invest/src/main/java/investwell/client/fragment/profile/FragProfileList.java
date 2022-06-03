package investwell.client.fragment.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.SignUpActivity;
import investwell.client.adapter.RecyCleProfilesAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

public class FragProfileList extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback {
    private ProgressDialog mBar;
    private RecyCleProfilesAdapter mAdapter;
    private AppSession mSession;
    private MainActivity mActivity;
    private BrokerActivity mBrokerActivity;

    private String mPaymentType = "", mUCC_Code = "", mCommingFrom = "", mCid;
    public static String mType = "";
    private Button mCreateButton;
    private String investOption = "";
    private String AlreadyUser = "false";
    private ToolbarFragment fragmentToolBar;
    private AppApplication mApplication;
    private View view;
    private ShimmerFrameLayout mShimmerViewContainer;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
        } else if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mBrokerActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_profile_list, container, false);


        setUpToolBar();
        errorContentInitializer(view);
        /* mCreateButton = view.findViewById(R.id.create_acnt_btn);
         */
        /*mCreateButton.setOnClickListener(this);*/

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        final Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type")) {
            mType = bundle.getString("type");
            if (mType.equals("pay_now")) {
                viewNoData.setVisibility(View.VISIBLE);
                displayServerMessage(getResources().getString(R.string.no_active_order));
            } else if (mType.equals("show_only_profiles")) {
                viewNoData.setVisibility(View.VISIBLE);
                displayServerMessage(getResources().getString(R.string.no_account));
            } else if (mType.equals("coming_from_dashborad")) {
                viewNoData.setVisibility(View.VISIBLE);
                if (bundle.containsKey("investType") && !TextUtils.isEmpty(bundle.getString("investType"))) {
                    investOption = bundle.getString("investType");
                }
                displayServerMessage(getResources().getString(R.string.no_account));
            } else if (mType.equals("for_mendate")) {
                viewNoData.setVisibility(View.VISIBLE);
                displayServerMessage(getResources().getString(R.string.no_account));
            } else if (mType.equals("coming_from_goal")) {
                mPaymentType = bundle.getString("investment_type");
            }

        }

        RecyclerView recycleView = view.findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mAdapter = new RecyCleProfilesAdapter(mActivity, new ArrayList<JSONObject>(), mType, new RecyCleProfilesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view1) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                if (view1.getId() == R.id.ll_card_parent) {
                    if (mType.equals("pay_now")) {


                        if (jsonObject.optString("ActiveOrder").equalsIgnoreCase("0")) {

                            mApplication.showSnackBar(viewNoData, "No Active Order");
                        } else {
                               /* Intent intent = new Intent(mActivity, PaymentNewActivity.class);
                                intent.putExtra("ucc_code", jsonObject.optString("UCC"));
                                intent.putExtra("type", "call_from_cleint");
                                startActivityForResult(intent, 500);*/

                            Bundle bundle1 = new Bundle();
                            bundle1.putString("ucc_code", jsonObject.optString("UCC"));
                            bundle1.putString("type", "call_from_cleint");
                            mActivity.displayViewOther(83, bundle1);
                        }
                    } else if (mType.equals("coming_from_dashborad")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "coming_from_dashborad");
                        bundle.putString("ucc_code", jsonObject.optString("UCC"));
                        if (jsonObject.optInt("MandateCount") > 0) {
                            mSession.setHasMendate(true);
                        } else {
                            mSession.setHasMendate(false);
                        }
                        /* mActivity.displayViewOther(11, bundle);*/
                        if (!TextUtils.isEmpty(investOption)) {
                            if (investOption.equalsIgnoreCase(AppConstants.INVEST_VIA_SIP)) {
                                bundle.putString("investType", AppConstants.INVEST_VIA_SIP);
                                mActivity.displayViewOther(13, bundle);


                            } else {
                                bundle.putString("investType", AppConstants.INVEST_VIA_LUMPSUM);
                                mActivity.displayViewOther(14, bundle);


                            }
                        } else {
                            mActivity.displayViewOther(11, bundle);


                        }

                    } /*else if (mType.equals("coming_from_invest_confirm")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "coming_from_invest_confirm");
                            bundle.putString("ucc_code", jsonObject.optString("UCC"));
                            if (jsonObject.optInt("MandateCount") > 0) {
                                mSession.setHasMendate(true);
                            } else {
                                mSession.setHasMendate(false);
                            }
                            if (investOption.equalsIgnoreCase(AppConstants.INVEST_VIA_SIP)) {
                                bundle.putString("investType", AppConstants.INVEST_VIA_SIP);
                                mActivity.displayViewOther(13, bundle);
                            } else {
                                bundle.putString("investType", AppConstants.INVEST_VIA_LUMPSUM);
                                mActivity.displayViewOther(14, bundle);
                            }
                        } */ else if (mType.equals("transfer_holding")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("allData", jsonObject.toString());
                        bundle.putString("ucc_code", jsonObject.optString("UCC"));
                        mActivity.displayViewOther(51, bundle);
                    } else if (mType.equals("show_only_profiles")) {

                    } else if (mType.equals("coming_from_goal")) {
                        Bundle bundle = new Bundle();
                        mUCC_Code = jsonObject.optString("UCC");
                        if (mPaymentType.equals("Lumpsum")) {
                            if (mCommingFrom.equals("i")) {
                                bundle.putString("ucc_code", mUCC_Code);
                                mActivity.displayViewOther(14, bundle);
                            } else {
                                if (Config.GoalBaseScheme.length() > 0 || mSession.getAddToCartList().length() > 0) {
                                    insertIntoLumSum("lumsum");
                                } else {
                                    bundle.putString("type", "LS");
                                    bundle.putString("ucc_code", mUCC_Code);
                                    mActivity.displayViewOther(14, bundle);
                                }
                            }
                        } else {
                            if (mSession.getHasMendate()) {
                                if (Config.GoalBaseScheme.length() > 0 || mSession.getAddToCartList().length() > 0) {
                                    insertIntoLumSum("sip");
                                } else {
                                    bundle.putString("type", "SIP");
                                    bundle.putString("ucc_code", mUCC_Code);
                                    mActivity.displayViewOther(13, bundle);
                                }
                            } else {
                                bundle.putString("ucc_code", mUCC_Code);
                                mActivity.displayViewOther(12, bundle);
                            }

                        }
                    } else if (mType.equalsIgnoreCase("coming_from_nfo")) {
                        mUCC_Code = jsonObject.optString("UCC");
                        callInvestNowApi("lumsum", mUCC_Code);
                    } else if (mType.equalsIgnoreCase("brokerActivitySearch")) {
                        bundle.putString("InvestorName", jsonObject.optString("InvestorName"));
                        bundle.putString("allData", jsonObject.toString());
                        bundle.putString("ucc_code", jsonObject.optString("UCC"));
                        mActivity.displayViewOther(16, bundle);

                    }
                }

            }
        });
        recycleView.setAdapter(mAdapter);
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else {
            mCid = mSession.getCID();
        }
        getProfileList(mCid);

        return view;
    }

    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_connection_timeout);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }

    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }

    //Display Network Error Content
    private void displayNoInternetMessage() {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }

    private void callInvestNowApi(final String type, final String mUcc) {
        viewNoData.setVisibility(View.GONE);
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

            if (Config.BASKET_SCHEMES_LIST.size() > 0) {
                for (int i = 0; i < Config.BASKET_SCHEMES_LIST.size(); i++) {
                    JSONObject jsonObject = Config.BASKET_SCHEMES_LIST.get(i);

                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                    }

                }

            } else {
                JSONArray jsonArray = new JSONArray(mSession.getAddToNFOCartList());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                    }
                }
            }


            jsonParam.put("UCC", mUcc);
            jsonParam.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonParam.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonParam.put("Fcode", FCode);
            jsonParam.put("Scode", SCode);
            jsonParam.put("FolioNo", "0|0|0|0|0");
            jsonParam.put("Reinvest", "Z|Z|Z|Z|Z");
            jsonParam.put("Amount", "0|0|0|0|0");
            jsonParam.put("Installment", "0|0|0|0|0");
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            //jsonParam.put("BasketCode", mSession.getSelectedBasketCode());
            jsonParam.put("BasketCode", "00");
            jsonParam.put("OnlineOption", mSession.getAppType());

            System.out.println("LUMPSUM" + jsonParam);


        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        mSession.setAddToNFOCartList("");
                        // mSession.setSelectedBasketCode("");
                        Config.BASKET_SCHEMES_LIST.clear();
                        //mActivity.removeAllStack();

                        if (type.equals("lumsum")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUcc);
                            mActivity.displayViewOther(14, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mUcc);
                            mActivity.displayViewOther(13, bundle);
                        }


                    } else {
                        viewNoData.setVisibility(View.VISIBLE);
                        displayServerMessage(object.optString("ServiceMSG"));

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
                    viewNoData.setVisibility(View.VISIBLE);
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());

                        displayServerErrorMessage(error);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError) {
                    displayNoInternetMessage();
                } else if (volleyError instanceof TimeoutError)
                    displayConnectionTimeOut();

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

    private void setUpToolBar() {
        fragmentToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragmentToolBar != null) {
            if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
                fragmentToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_invest_profile), true, false, false, true, false, false, false, getResources().getString(R.string.invest_profile_btn_header_txt));
            } else {
                fragmentToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_invest_profile), true, false, false, false, false, false, false, getResources().getString(R.string.invest_profile_btn_header_txt));

            }
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragmentToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity, R.color.colorPrimary));

            }
            fragmentToolBar.setCallback(this);
        }

    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        if (v.getId() == R.id.ivLeft) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mActivity).getSupportFragmentManager().popBackStack();
            }

          /*  case R.id.create_acnt_btn:
                AlreadyUser = "true";
                bundle.putString("AlreadyUser", AlreadyUser);
                mActivity.displayViewOther(5, bundle);
                break;*/
        }
    }

    private void getProfileList(String Cid) {
        viewNoData.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();

        String url = Config.PROFILE_LIST;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cid", Cid);
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();

                        if (response.optBoolean("Status")) {
                            JSONArray araArray = response.getJSONArray("ProfileListDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                if (mType.equals("pay_now")) {

                                    // if (!object.optString("ActiveOrder").equals("0"))
                                    list.add(object);
                                } else {
                                    list.add(object);
                                }

                            }

                            if (list.size() > 0) {
                                mAdapter.updateList(list, mType);
                                viewNoData.setVisibility(View.GONE);
                            } else {
                                viewNoData.setVisibility(View.VISIBLE);
                                displayServerMessage("No Data Found");
                            }

                        } else {
                            if (list.size() > 0) {
                                mAdapter.updateList(list, mType);
                                viewNoData.setVisibility(View.GONE);
                            } else {
                                viewNoData.setVisibility(View.VISIBLE);
                                displayServerMessage("No Data Found");

                            }
                            //Toast.makeText(mActivity, mDataList.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    viewNoData.setVisibility(View.VISIBLE);
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            displayServerErrorMessage(volleyError);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError) {
                        displayNoInternetMessage();
                    } else if (volleyError instanceof TimeoutError)
                        displayConnectionTimeOut();

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


    private void insertIntoLumSum(final String type) {
        viewNoData.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = "";
        if (type.equals("lumsum")) {
            url = Config.INSERT_INTO_LUMPSUM;
        } else {
            url = Config.INSERT_INTO_SIP;
        }
        JSONObject object = Config.GoalBaseScheme;

        JSONObject jsonParam = new JSONObject();

        try {
            String FCode = "";
            String SCode = "";
            String Amount = "";


            JSONArray jsonArray = object.optJSONArray("GoalBasedSchemeDetail");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsondata = jsonArray.optJSONObject(i);

                if (i == 0) {
                    FCode = jsondata.optString("Fcode");
                    SCode = jsondata.optString("Scode");
                    Amount = jsondata.optString("Amount");
                } else {
                    FCode = FCode + "|" + jsondata.optString("Fcode");
                    SCode = SCode + "|" + jsondata.optString("Scode");
                    Amount = Amount + "|" + jsondata.optString("Amount");
                }

            }


            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Cid", mSession.getCID());
            jsonParam.put("Fcode", FCode);
            jsonParam.put("Scode", SCode);
            jsonParam.put("FolioNo", "0|0|0|0|0");
            jsonParam.put("Reinvest", "Z|Z|Z|Z|Z");
            jsonParam.put("Amount", Amount);
            jsonParam.put("Installment", "0|0|0|0|0");
            jsonParam.put("Passkey", mSession.getPassKey());
            //jsonParam.put("BasketCode", mSession.getSelectedBasketCode());
            jsonParam.put("BasketCode", "00");
            jsonParam.put("OnlineOption", mSession.getAppType());

            System.out.println("LUMPSUM" + jsonParam);


        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mShimmerViewContainer.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmerAnimation();
                try {
                    if (object.optString("Status").equals("True")) {
                        mSession.setAddToCartList("");
                        Config.GoalBaseScheme = new JSONObject();
                        // mSession.setSelectedBasketCode("");
                        Config.BASKET_SCHEMES_LIST.clear();
                        // mActivity.removeAllStack();

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
                        viewNoData.setVisibility(View.VISIBLE);
                        displayServerMessage(object.optString("ServiceMSG"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mShimmerViewContainer.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmerAnimation();
                viewNoData.setVisibility(View.VISIBLE);
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        displayServerMessage(jsonObject.optString("ServiceMSG"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    displayNoInternetMessage();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    private void createInvestmentProfile() {
        if (mSession.getHasLoging()) {
            Bundle bundle = new Bundle();
            AlreadyUser = "true";
            bundle.putString("AlreadyUser", AlreadyUser);
            mActivity.displayViewOther(5, bundle);


        } else {
            startActivity(new Intent(getActivity(), SignUpActivity.class));
        }
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.btn_add_new) {/*mApplication.showSnackBar(view, getResources().getString(R.string.work_under_development));*/
            createInvestmentProfile();
        }
    }
}
