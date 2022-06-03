package investwell.client.fragment.InvestInExistingSchemes.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.InvestInExistingSchemes.Adpter.ExistingInvestmentAdapter;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class ExistingInvestmentSchemeFragment extends Fragment {

    private View view;
    public ToolbarFragment fragToolBar;
    private Bundle bundle;
    private RecyclerView mExistingInvestmentRecycle;
    public List<JSONObject> mSelectedCartsList;
    private ExistingInvestmentAdapter existingInvestmentAdapter;
    private AppSession mSession;
    private String mCid;
    private AppApplication mApplication;
    private MainActivity mActivity;
    private ShimmerFrameLayout mShimmerViewContainer;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_existing_investment_scheme, container, false);

        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();
        bundle = getArguments();
        setInitializer();
        setUpToolBar();
        //******//


        fragToolBar.updateCart(true);
        return view;
    }

    private void setInitializer() {
        mActivity.setMainVisibility(this,null);
        mSelectedCartsList = new ArrayList<>();
        mExistingInvestmentRecycle = view.findViewById(R.id.ExistingInvestmentRecycle);
        mExistingInvestmentRecycle.setHasFixedSize(true);
        mExistingInvestmentRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        existingInvestmentAdapter = new ExistingInvestmentAdapter(getActivity(), new ArrayList<JSONObject>(), ExistingInvestmentSchemeFragment.this);
        mExistingInvestmentRecycle.setAdapter(existingInvestmentAdapter);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else {
            mCid = mSession.getCID();
        }
        getData(mCid);
        addToCartList();
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(mSession.getExistingScheme(), true, false, false, false, false, false, false, "");

        }
    }



    private void addToCartList() {
        try {
            JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
            for (int i = 0; i < jsonArray.length(); i++) {
                mSelectedCartsList.add(jsonArray.getJSONObject(i));
            }

        } catch (Exception e) {
e.printStackTrace();
        }



    }

    private void getData(String Cid) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.GOAL_MAPPING_V4_2;
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, Cid);
            jsonObject.put("UCC", mSession.getUCC_CODE());
            jsonObject.put("Fcode", "All");
            jsonObject.put("OnlyMF", "Y");

            jsonObject.put("OnlineOption", mSession.getAppType());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                    ArrayList<JSONObject> list = new ArrayList<>();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        try {
                            JSONArray jsonArray = response.optJSONArray("ResponseData");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                list.add(jsonObject1);
                            }
                            existingInvestmentAdapter.updateList(list);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{

                        mApplication.showSnackBar(view,response.optString("ServiceMSG"));
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                    }
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
