package investwell.client.fragment.goalbased.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.piechart.callback.OnPieLegendBindListener;
import investwell.utils.piechart.callback.OnPieSelectListener;
import investwell.utils.piechart.data.IPieInfo;
import investwell.utils.piechart.data.SimplePieInfo;
import investwell.utils.piechart.others.AnimatedPieView;
import investwell.utils.piechart.others.AnimatedPieViewConfig;
import investwell.utils.piechart.others.BasePieLegendsView;
import investwell.utils.piechart.others.DefaultPieLegendsView;


public class FragAllocation extends Fragment {
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private String investment_amount,amount;
    private Bundle bundle;
    private AnimatedPieView mAnimatedPieView;
    private FlowLayout mViewGroup;
    private JSONArray jsonArray;
    private String[]Category,Amount;
    private String elss_value, mRiskID;
    private TextView mTvNothing;
private MainActivity mainActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            mainActivity=(MainActivity)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_allocation, container, false);
        mAnimatedPieView = view.findViewById(R.id.animatedPieView);
        mViewGroup = view.findViewById(R.id.ll_legends);
        mTvNothing = view.findViewById(R.id.tvNothing);
        mSession = AppSession.getInstance(getActivity());
        bundle = getArguments();
        if (bundle != null) {

            if (bundle.containsKey("ELSS")){

                elss_value = bundle.getString("ELSS");
            }else{
                elss_value = "N";
            }
            investment_amount=(bundle.getString("Amount"));
            amount = investment_amount.replace(",", "").replace(getString(R.string.rs), "");
            mRiskID = bundle.getString("RiskId");
            getData();
        }
        return view;
    }

    private void getData(){

        String url = Config.Goal_Based_Scheme_Allocation ;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey",mSession.getPassKey());
            jsonObject.put("Bid",AppConstants.APP_BID);
            jsonObject.put("Period",bundle.getInt("duration"));
            jsonObject.put("RiskCode",mRiskID);
            jsonObject.put("Amount",amount);
            jsonObject.put(AppConstants.CUSTOMER_ID,mSession.getCID());
            jsonObject.put("ELSS",elss_value);
            jsonObject.put("GoalCategoryID",bundle.getString("GoalCategoryID"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    if (jsonObject.optBoolean("Status")) {
                        jsonArray = jsonObject.optJSONArray("GoalBasedSchemeAllocationDetail");
                        Category = new String[jsonArray.length()];
                        Amount = new String[jsonArray.length()];
                        for(int i = 0; i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            Category[i]=jsonObject1.optString("Category");
                            Amount[i] = jsonObject1.optString("Amount");
                        }
                        if (jsonArray.length() > 0) {
                            mSession.setCLicked(true);
                        }else{
                            mSession.setCLicked(false);
                        }
                        setPieChart();
                        mTvNothing.setVisibility(View.GONE);
                    } else {
                        mTvNothing.setVisibility(View.VISIBLE);
                       // Toast.makeText(getActivity(), jsonObject.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            e.printStackTrace();
        }




    }


    private void setPieChart() {
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        Config.mGraphValue.clear();
        String[] colors = mainActivity.getResources().getStringArray(R.array.colors);
        for (int i = 0; i < jsonArray.length(); i++) {
            Config.mGraphValue.put(Category[i], Amount[i]);
            config.addData(new SimplePieInfo(Float.parseFloat(Amount[i]), Color.parseColor(colors[i]), ""), true);
        }
        config.startAngle(0.9224089f)
                .selectListener(new OnPieSelectListener() {
                    @Override
                    public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                        /*desc.setText(String.format(Locale.getDefault(),
                                "  value = %s\n  desc = %s", pieInfo.getValue(), pieInfo.getDesc()));*/
                    }
                })
                .drawText(true)
                .duration(500)
                .textSize(26)
                .focusAlphaType(AnimatedPieViewConfig.FOCUS_WITH_ALPHA_REV)
                .textGravity(AnimatedPieViewConfig.ABOVE)
                .interpolator(new DecelerateInterpolator())
                .legendsWith(mViewGroup, new OnPieLegendBindListener<BasePieLegendsView>() {
                    @Override
                    public BasePieLegendsView onCreateLegendView(int position, IPieInfo info) {
                        return position % 2 == 0 ?
                                DefaultPieLegendsView.newInstance(getActivity())
                                : DefaultPieLegendsView.newInstance(getActivity());
                    }

                    @Override
                    public boolean onAddView(ViewGroup parent, BasePieLegendsView view) {
                        return false;
                    }
                });
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();
    }



}
