package investwell.client.fragment.goalbased.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class FragConfirmRiskProfile extends Fragment implements View.OnClickListener {
    private ImageView mIvRiskoMeter;
    private String riskcode;
    private TextView mTvRiskName, mTvDescription, mTvUserName;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private ToolbarFragment toolbarFragment;
    private String mPro, mType = "", mDescription = "",RiskImage;
    private Button mBtAgree, mBtRetake;
    private View mView;

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
        View view = inflater.inflate(R.layout.frag_confirm_risk_profile, container, false);
        mSession = AppSession.getInstance(mActivity);
        setUpToolBar(getString(R.string.toolBar_title_assesment_result));
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        mTvRiskName = view.findViewById(R.id.tvRiskName);
        mTvDescription = view.findViewById(R.id.tv_desc);
        mIvRiskoMeter = view.findViewById(R.id.iv_riskometer_graph);
        mTvUserName = view.findViewById(R.id.tvName);
        mView = view.findViewById(R.id.riskColor);

        mBtAgree = view.findViewById(R.id.agreed_btn);
        mBtRetake = view.findViewById(R.id.btRetake);
        mBtAgree.setOnClickListener(this);
        mBtRetake.setOnClickListener(this);
        getDataFromBundle();


        return view;
    }

    private void getDataFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type"))
            mType = bundle.getString("type");

        if (mType.equalsIgnoreCase("showRiskProfile")) {
            setUpToolBar(getString(R.string.toolBar_title_risk_profile_start));
            riskcode = mSession.getRiskCode();
            mPro = mSession.getRiskName();
            mBtRetake.setVisibility(View.GONE);
            mBtAgree.setText(getString(R.string.risk_profile_confirmation_Access_Again));
        } else if (mType.equalsIgnoreCase("create_goal")) {
            setUpToolBar(getString(R.string.toolBar_title_assesment_result));
            mPro = bundle.getString("riskname");
            riskcode = bundle.getString("riskcode");
            mBtRetake.setVisibility(View.VISIBLE);
            mBtAgree.setText(getString(R.string.risk_profile_confirmation_i_agree));

        } else if (mType.equalsIgnoreCase("create_goal_for_sip_lumpsum")) {
            setUpToolBar(getString(R.string.toolBar_title_assesment_result));
            mPro = bundle.getString("riskname");
            riskcode = bundle.getString("riskcode");
            mBtRetake.setVisibility(View.VISIBLE);
            mBtAgree.setText(getString(R.string.risk_profile_confirmation_i_agree));
        } else {
            setUpToolBar(getString(R.string.toolBar_title_assesment_result));
            mPro = bundle.getString("riskname");
            riskcode = bundle.getString("riskcode");
            mBtRetake.setVisibility(View.VISIBLE);
            mBtAgree.setText(getString(R.string.risk_profile_confirmation_i_agree));
        }

        if (bundle != null && bundle.containsKey("riskdescription"))
            mDescription = bundle.getString("riskdescription");
        else
            mDescription = mSession.getRiskDescription();

        if (bundle!=null && bundle.containsKey("riskimage"))
            RiskImage = bundle.getString("riskimage");
            else
               RiskImage = mSession.getRiskImage();

        setRiskoMeter();

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.agreed_btn) {
            if (mType.equalsIgnoreCase("showRiskProfile")) {
                mActivity.displayViewOther(61, null);
            } else if (mBtAgree.getText().toString().equalsIgnoreCase(getString(R.string.risk_profile_confirmation_i_agree))) {
                callRiskProfileUpdateApi();
            } else if (mBtAgree.getText().toString().equalsIgnoreCase(getString(R.string.risk_profile_create_goal_txt))) {
                mActivity.removesFragmentsFromBackStack(3);
                mActivity.displayViewOther(77, null);
            } else if (mBtAgree.getText().toString().equalsIgnoreCase(getString(R.string.risk_profile_continue_for_sip_lumpsum_txt))) {
                mActivity.removesFragmentsFromBackStack(4);
                Bundle bundle = null;
                bundle = mApplication.sStoreGoalBundle;
                mApplication.sStoreGoalBundle = new Bundle();
                mActivity.displayViewOther(71, bundle);
            } else {

            }
        } else if (id == R.id.btRetake) {
            mActivity.getSupportFragmentManager().popBackStack();
        }

    }


    private void setRiskoMeter() {
        mTvUserName.setText(mSession.getFullName());
        mTvRiskName.setText(mPro);
        mTvDescription.setText(mDescription);
        mView.setBackgroundResource(R.drawable.dynamic_for_risko_meter);
        GradientDrawable drawable = (GradientDrawable) mView.getBackground();

        if (RiskImage.isEmpty()){
            mIvRiskoMeter.setVisibility(View.GONE);
        }else {
            mIvRiskoMeter.setVisibility(View.VISIBLE);
            Picasso.get().load(RiskImage).into(mIvRiskoMeter);
        }


       /* if (mPro.equalsIgnoreCase("Low")) {
            mIvRiskoMeter.setImageResource(R.mipmap.ic_factsheet_riskometer_low);
            drawable.setColor(ContextCompat.getColor(mActivity, R.color.colorLowRisk));
        } else if (mPro.equalsIgnoreCase("Below Average")) {
            drawable.setColor(ContextCompat.getColor(mActivity, R.color.colorBelowAvgRisk));
            mIvRiskoMeter.setImageResource(R.mipmap.ic_factsheet_riskometer_below_avg);
        } else if (mPro.equalsIgnoreCase("Average")) {
            drawable.setColor(ContextCompat.getColor(mActivity, R.color.colorAvgRisk));
            mIvRiskoMeter.setImageResource(R.mipmap.ic_factsheet_riskometer_avg);
        } else if (mPro.equalsIgnoreCase("Above Average")) {
            drawable.setColor(ContextCompat.getColor(mActivity, R.color.colorAboveAvgRisk));
            mIvRiskoMeter.setImageResource(R.mipmap.ic_factsheet_riskometer_above_avg);
        } else if (mPro.equalsIgnoreCase("High")) {
            drawable.setColor(ContextCompat.getColor(mActivity, R.color.colorHighRisk));
            mIvRiskoMeter.setImageResource(R.mipmap.ic_factsheet_riskometer_high);
        }*/
    }


    private void setUpToolBar(String title) {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(title, true, false, false, false, false, false, false, "");
        }
    }


    private void callRiskProfileUpdateApi() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        String url = Config.UPDATE_RISK_PROFILE;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonObject.put("RiskCode", riskcode);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    if (jsonObject.optBoolean("Status")) {
                        mSession.setRiskCode(riskcode);
                        mSession.setRiskName(mPro);
                        mSession.setRiskDescription(mDescription);
                        mSession.setRiskImage(RiskImage);
                        if (mType.equalsIgnoreCase("newRiskProfile")) {
                            setUpToolBar(getString(R.string.toolBar_title_risk_profile_start));
                            mBtRetake.setVisibility(View.GONE);
                            mBtAgree.setText(getString(R.string.risk_profile_confirmation_Access_Again));
                        } else if (mType.equalsIgnoreCase("create_goal")) {
                            setUpToolBar(getString(R.string.toolBar_title_risk_profile_start));
                            mBtRetake.setVisibility(View.GONE);
                            mBtAgree.setText(getString(R.string.risk_profile_create_goal_txt));
                        } else if (mType.equalsIgnoreCase("create_goal_for_sip_lumpsum")) {
                            setUpToolBar(getString(R.string.toolBar_title_risk_profile_start));
                            mBtRetake.setVisibility(View.GONE);
                            mBtAgree.setText(getString(R.string.risk_profile_continue_for_sip_lumpsum_txt));
                        } else {

                        }


                    } else {
                        if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                            mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                        } else {
                            mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

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
