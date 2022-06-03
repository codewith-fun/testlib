package investwell.client.fragment.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class FragProfileDetails extends Fragment {
    private View view;
    private RequestQueue requestQueue;
    private ProgressDialog mBar;
    private MainActivity mActivity;
    private AppSession mSession;
    private String comingFrom;
    TextView tvSchemePlaceholder, tvFolioPlaceholder, tvBalaceUnitPlaceholder,
            mTvEmail, tvInvestorValue, tvPanValue, mTvMobile, tvHoldingsValue, tvUccValue, mTvNiminee, tvBankNameValue, mTvAccountType, tvIfscValue, tvAccNoValue;
    private CardView  cvDetailSecond, cvDetailThird;
    private ToolbarFragment fragmentToolBar;
    private RelativeLayout mRelCard;
    private String mUcc = "",mCid = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity = (MainActivity) getActivity();
            mSession = AppSession.getInstance(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment

        view = inflater.inflate(R.layout.fragment_folio_detail, container, false);
        setInitializer();
        setUpToolBar();
        hideUi();
        setUpUi();
        return view;
    }

    private void setInitializer() {

        tvAccNoValue = view.findViewById(R.id.tv_acc_no_value);
        tvBalaceUnitPlaceholder = view.findViewById(R.id.tv_balace_unit_placeholder);
        mTvAccountType = view.findViewById(R.id.tv_balace_unit_value);
        tvBankNameValue = view.findViewById(R.id.tv_bank_name_value);
        tvFolioPlaceholder = view.findViewById(R.id.tv_folio_placeholder);
        tvSchemePlaceholder = view.findViewById(R.id.tv_scheme_placeholder);
        mTvEmail = view.findViewById(R.id.tv_scheme_value);
        tvInvestorValue = view.findViewById(R.id.tv_investor_value);
        tvHoldingsValue = view.findViewById(R.id.tv_holdings_value);
        tvPanValue = view.findViewById(R.id.tv_pan_value);
        mTvMobile = view.findViewById(R.id.tv_folio_value);
        tvUccValue = view.findViewById(R.id.tv_ucc_value);
        mTvNiminee = view.findViewById(R.id.tv_mapped_value);
        tvIfscValue = view.findViewById(R.id.tv_ifsc_value);
        mRelCard = view.findViewById(R.id.relCard);
        cvDetailSecond = view.findViewById(R.id.cv_detail_second);
        cvDetailThird = view.findViewById(R.id.cv_detail_third);
      final   Bundle bundle = getArguments();
        if (bundle!=null && bundle.containsKey("ucc_code")){
            mUcc = bundle.getString("ucc_code");
            mCid = bundle.getString("cid");

        }else{
            mUcc = mSession.getUCC_CODE();
            mCid = mSession.getCID();
        }

        view.findViewById(R.id.ivMandateList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.displayViewOther(37, bundle);
            }
        });
        getBseProfileDetail();
    }

    private void setUpToolBar() {
        fragmentToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragmentToolBar != null) {
            fragmentToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_profile_detail),true, false, false, false,false,false,false,"");
        }
    }

    private void setUpUi() {
        tvSchemePlaceholder.setText(getResources().getString(R.string.txt__email));
        tvFolioPlaceholder.setText(getResources().getString(R.string.txt_mobile));
        tvBalaceUnitPlaceholder.setText(getResources().getString(R.string.txt_acc_type));
        //toolbarTitle.setText("Profile Details");

    }

    private void hideUi() {
        mRelCard.setVisibility(View.GONE);
        cvDetailSecond.setVisibility(View.GONE);
        cvDetailThird.setVisibility(View.GONE);
    }

    private void showUi() {
        mRelCard.setVisibility(View.VISIBLE);
        cvDetailSecond.setVisibility(View.VISIBLE);
        cvDetailThird.setVisibility(View.VISIBLE);
        view.findViewById(R.id.ivMandateList).setVisibility(View.VISIBLE);
    }


    private void getBseProfileDetail() {
        mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        String url = Config.BSE_PROFILE_DETAILS;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cid", mCid);
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("UCC", mUcc);
            jsonObject.put("OnlineOption", mSession.getAppType());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    try {
                        if (response != null) {
                            String status = response.optString("Status");
                            if (status.equalsIgnoreCase("True")) {
                                showUi();
                                JSONObject jsonObject1 = response.getJSONObject("ProfileDetail");
                                mTvEmail.setText(!TextUtils.isEmpty(jsonObject1.optString("EMAIL")) ? jsonObject1.optString("EMAIL") : "N/A");
                                tvInvestorValue.setText(!TextUtils.isEmpty(jsonObject1.optString("APPNAME1")) ? jsonObject1.optString("APPNAME1") : "N/A");
                                tvPanValue.setText(!TextUtils.isEmpty(jsonObject1.optString("PAN")) ? jsonObject1.optString("PAN") : "N/A");
                                mTvMobile.setText(!TextUtils.isEmpty(jsonObject1.optString("MOBILE")) ? jsonObject1.optString("MOBILE") : "N/A");
                                tvHoldingsValue.setText(!TextUtils.isEmpty(jsonObject1.optString("HOLDING")) ? jsonObject1.optString("HOLDING") : "N/A");

                                mTvNiminee.setText(!TextUtils.isEmpty(jsonObject1.optString("NOMINEE")) ? jsonObject1.optString("NOMINEE") : "N/A");
                                mTvAccountType.setText(!TextUtils.isEmpty(jsonObject1.optString("ACCTYPE1")) ? jsonObject1.optString("ACCTYPE1") : "N/A");
                                tvUccValue.setText(!TextUtils.isEmpty(jsonObject1.optString("UCC")) ? jsonObject1.optString("UCC") : "N/A");


                                tvBankNameValue.setText(!TextUtils.isEmpty(jsonObject1.optString("BANK1")) ? jsonObject1.optString("BANK1") : "N/A");
                                tvIfscValue.setText(!TextUtils.isEmpty(jsonObject1.optString("IFSCCODE1")) ? jsonObject1.optString("IFSCCODE1") : "N/A");
                                tvAccNoValue.setText(!TextUtils.isEmpty(jsonObject1.optString("ACCNO1")) ? jsonObject1.optString("ACCNO1") : "N/A");



                            } else {

                                //Toast.makeText(getActivity(), mDataList.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                            }
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
                            Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            if (getActivity() != null) {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(jsonObjectRequest);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

