package investwell.client.fragment.others;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FolioItemListAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class BseProfileDetailFrag extends Fragment {

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private String bid, passkey, ucc, cid;
    private AppSession mSession;
    private MainActivity mActivity;
    private Bundle bundle;
    private RecyclerView mFolioDetail;
    private FolioItemListAdapter folioItemListAdapter;
    private TextView mFolio, mScheme, mHolder, mJointOne, mJointTwo, mNominee;
    private LinearLayout mJoint1Ly, mJoint2Ly, mNomineeLy;
    private RelativeLayout mMainLy;
    private View view;
    private ToolbarFragment fragToolBar;
    private ShimmerFrameLayout mShimmerViewContainer;
    private AppApplication mApplication;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_bse_profile_detail, container, false);
        setUpToolBar();
        setInitializer();
        getDataFromBundle();
        setRecyclerAdapter();
        getFolioQuery(bid, passkey, cid);
        return view;
    }

    private void setInitializer() {
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        bundle = getArguments();
        mFolioDetail = view.findViewById(R.id.folio_detail);
        bid = AppConstants.APP_BID;
        passkey = mSession.getPassKey();
        mMainLy = view.findViewById(R.id.main_layout);
        mFolio = view.findViewById(R.id.folio);
        mScheme = view.findViewById(R.id.colorBlue);
        mHolder = view.findViewById(R.id.investor_name);
        mJointOne = view.findViewById(R.id.joint_one);
        mJointTwo = view.findViewById(R.id.joint_two);
        mNominee = view.findViewById(R.id.nominee);
        mJoint1Ly = view.findViewById(R.id.joint1_layout);
        mJoint2Ly = view.findViewById(R.id.joint2_layout);
        mNomineeLy = view.findViewById(R.id.nominee_layout);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("cid")) {
            cid = bundle.getString("cid");
        } else
            cid = mSession.getCID();

    }

    private void setRecyclerAdapter() {

        mFolioDetail.setHasFixedSize(true);
        mFolioDetail.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        folioItemListAdapter = new FolioItemListAdapter(mActivity, new ArrayList<JSONObject>());
        mFolioDetail.addItemDecoration(new DividerItemDecoration(mFolioDetail.getContext(), DividerItemDecoration.VERTICAL));
        mFolioDetail.setAdapter(folioItemListAdapter);

    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_folio_transactions), true, false, false, false, false, false, false, "");
        }
    }

    private void getFolioQuery(final String mBid, final String mPasskey, final String mCid) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.Folio_Query;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mPasskey);
            jsonObject.put("Bid", mBid);
            jsonObject.put("Cid", mCid);
            jsonObject.put("FolioNo", bundle.getString("folio"));
            jsonObject.put("SchemeCode", bundle.getString("scheme_code"));

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("FolioQueryDetail");
                        JSONObject jsonObject1 = jsonArray.optJSONObject(0);
                        setUserData(jsonObject1);
                        getFolioDetail(mBid, mPasskey, mCid);
                    } else {
                        mApplication.showSnackBar(mFolioDetail, response.optString("ServiceMSG"));

                    }
                }
            }, error -> {
                mShimmerViewContainer.setVisibility(View.GONE);
                mShimmerViewContainer.stopShimmerAnimation();
                mApplication.showSnackBar(mFolioDetail, error.getLocalizedMessage());
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
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setUserData(JSONObject jsonObject) {
        mMainLy.setVisibility(View.VISIBLE);
        if (jsonObject.optString("Joint1Name").isEmpty()) {
            mJoint1Ly.setVisibility(View.GONE);
        }

        if (jsonObject.optString("Joint2Name").isEmpty()) {
            mJoint2Ly.setVisibility(View.GONE);
        }

        if (jsonObject.optString("Nominee").isEmpty()) {
            mNomineeLy.setVisibility(View.GONE);
        }

        mFolio.setText(bundle.getString("folio"));
        mScheme.setText(bundle.getString("colorBlue"));
        mHolder.setText(bundle.getString("applicant_name"));
        mJointOne.setText(jsonObject.optString("Joint1Name") + " , " + jsonObject.optString("Joint2Name"));
        mJointTwo.setText(jsonObject.optString("Joint2Name"));
        mNominee.setText(jsonObject.optString("Nominee"));

    }


    private void getFolioDetail(String mBid, String mPasskey, String mCid) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.My_Transaction_url;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mPasskey);
            jsonObject.put("Bid", mBid);
            jsonObject.put("Cid", mCid);
            jsonObject.put("FromDate", "NA");
            jsonObject.put("ToDate", "NA");
            jsonObject.put("Foliono", bundle.getString("folio"));
            jsonObject.put("Fcode", bundle.getString("fund_code"));
            jsonObject.put("Scode", bundle.getString("scheme_code"));
            jsonObject.put("TranType", "");


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    ArrayList<JSONObject> list = new ArrayList<>();

                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("MyTransactionDetail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);

                        }
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmerAnimation();
                        folioItemListAdapter.updateList(list);

                    } else {
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmerAnimation();
                        mApplication.showSnackBar(mFolioDetail, response.optString("ServiceMSG"));

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    mApplication.showSnackBar(mFolioDetail, error.getLocalizedMessage());
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

}
