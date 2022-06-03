package investwell.client.fragment.schemes;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FragOrdersHistoryAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragOrderHistory extends Fragment implements View.OnClickListener {
    private ProgressDialog mBar;
    private FragOrdersHistoryAdapter mAdapter;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private String mType = "";
    private TextView mTvNothing;
    private String mUCC_Code = "";
    private ToolbarFragment fragToolBar;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_my_order_history, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();

        RecyclerView recycleView = view.findViewById(R.id.recycleView);
        mTvNothing = view.findViewById(R.id.tvNothing);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new FragOrdersHistoryAdapter(getActivity(), new ArrayList<JSONObject>(), new FragOrdersHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
            }
        });
        recycleView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
        } else {
            mUCC_Code = mSession.getUCC_CODE();
        }


        if (bundle != null) {
            mType = bundle.getString("type");
            if (mType.equals("sip_order")) {
                mTvNothing.setText(getResources().getString(R.string.error_no_order_found));
                setUpToolBar("SIP Orders");
                mTvNothing.setText("No Order Found");
                getMyOrders("SIP");

            } else {
                setUpToolBar("Lumpsum Orders");
                mTvNothing.setText(getResources().getString(R.string.error_no_order_found));
                getMyOrders("PUR");
            }
        }

        return view;
    }

    private void setUpToolBar(String title) {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(title, true, false, false, false, false, false, false,"");

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    private void getMyOrders(String type) {
        mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.ORDER_HISTORY ;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY,mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID,AppConstants.APP_BID);
            jsonObject.put("UCC",mUCC_Code);
            jsonObject.put("TranType",type);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();

                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("ProspectMyOrderDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                list.add(object);
                            }

                            if (list.size() > 0) {
                                mAdapter.updateList(list);
                                mTvNothing.setVisibility(View.GONE);
                            } else {
                                mTvNothing.setVisibility(View.VISIBLE);
                            }

                        } else {
                            if (list.size() > 0) {
                                mAdapter.updateList(list);
                                mTvNothing.setVisibility(View.GONE);
                            } else {
                                mTvNothing.setVisibility(View.VISIBLE);
                            }
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey",false,true);
                            }                        }

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

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
