package investwell.client.fragment.schemes;

import android.app.ProgressDialog;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.RecycleBucketAddToCartAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class FragBucket extends Fragment implements View.OnClickListener {
    public List<JSONObject> mCallLogsList;
    //private FragCallLogAdapter mAdapter;
    private AppSession mSession;
    TextView mTvNothing;
    private String mType = "all";
    private ProgressDialog mBar;
    private RecycleBucketAddToCartAdapter mAdapter;
    private JSONObject mObjectData;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bucket_detail, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_scheme_container);
        mCallLogsList = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.basket_detail_recyclier);
        mTvNothing = view.findViewById(R.id.tvNothing);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new RecycleBucketAddToCartAdapter(getActivity(), FragBucket.this, new ArrayList<JSONObject>(), new RecycleBucketAddToCartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    JSONObject jsonObject = mAdapter.mDataList.get(position);
                    MainActivity mainActivity = (MainActivity) getActivity();

                    Bundle bundle = new Bundle();
                    bundle.putString("passkey", AppSession.getInstance(getActivity()).getPassKey());
                    bundle.putString("excl_code", jsonObject.optString("Exlcode"));
                    bundle.putString("bid", AppConstants.APP_BID);
                    bundle.putString("scheme", jsonObject.optString("SchName"));
                    bundle.putString("object", jsonObject.toString());
                    mainActivity.displayViewOther(42, bundle);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
        Bundle bundle = getArguments();
        try {
            mType = bundle.getString("type");
            mObjectData = new JSONObject(bundle.getString("dataObject"));
          /*  JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
            mAtivity.mTvCart.setText("" + jsonArray.length());
            for (int i = 0; i <jsonArray.length(); i++) {
                mAtivity.mSelectedCartsList.add(jsonArray.getJSONObject(i));
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSchemes();
        return view;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.ivcancel:
                mEtSearch.setText("");
                break;*/
        }

    }

    private void getSchemes() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();   String url = Config.RECOMENDED_SCHEMES;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY,mSession.getPassKey());
            jsonObject.put("Bid",AppConstants.APP_BID);
            jsonObject.put("BasketCode",mObjectData.optString("BasketCode"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();


                        if (jsonObject.optBoolean("Status")) {
                            JSONArray araArray = jsonObject.getJSONArray("SchemeDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                list.add(object);
                            }

                            mAdapter.updateList(list);
                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey",false,true);
                            }else{
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message",false,true);
                            }                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
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
