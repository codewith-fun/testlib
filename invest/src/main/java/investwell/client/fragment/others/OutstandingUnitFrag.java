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


public class OutstandingUnitFrag extends Fragment {
    private Bundle bundle;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private AppSession mSession;
    private String mCid;
    private TextView mFolio, mScheme, mHolder, mJointOne, mJointTwo, mNominee;
    private RelativeLayout mMainLy;
    private RecyclerView mFolioDetail;
    private FolioItemListAdapter folioItemListAdapter;
    private MainActivity mActivity;
    private ToolbarFragment fragToolBar;
    private View view;
    private AppApplication mApplication;
    private ShimmerFrameLayout mShimmerViewContainer;
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
        view = inflater.inflate(R.layout.fragment_outstanding_unit, container, false);
        setUpToolBar();
        setInitializer();
        getDataFromBundle();
        setUserData();
        getOutstandingFolio();
        setRecyclerAdapter();

        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_outstanding_units),true, false, false, false,false,false,false,"");
        }
    }

    private void setInitializer() {
        mMainLy = view.findViewById(R.id.main_layout);
        mFolioDetail = view.findViewById(R.id.folio_detail);
        mFolio = view.findViewById(R.id.folio);
        mScheme = view.findViewById(R.id.colorBlue);
        mHolder = view.findViewById(R.id.investor_name);
        mJointOne = view.findViewById(R.id.joint_one);
        mJointTwo = view.findViewById(R.id.joint_two);
        mNominee = view.findViewById(R.id.nominee);
        mActivity.setMainVisibility(this,null);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
    }

    private void getDataFromBundle() {
        bundle = getArguments();
        mSession = AppSession.getInstance(getActivity());
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else
            mCid = mSession.getCID();
    }

    private void setRecyclerAdapter() {
        mFolioDetail.setHasFixedSize(true);
        mFolioDetail.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        folioItemListAdapter = new FolioItemListAdapter(getActivity(), new ArrayList<JSONObject>());
        mFolioDetail.addItemDecoration(new DividerItemDecoration(mFolioDetail.getContext(), DividerItemDecoration.VERTICAL));
        mFolioDetail.setAdapter(folioItemListAdapter);

    }

    private void getOutstandingFolio() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.Folio_Outstanding_Transaction;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCid);
            jsonObject.put("FromDate", "NA");
            jsonObject.put("ToDate", "NA");
            jsonObject.put("FolioNo", bundle.getString("folio"));
            jsonObject.put("Fcode", bundle.getString("fund_code"));
            jsonObject.put("Scode", bundle.getString("scheme_code"));
            jsonObject.put("TranType", "");
            jsonObject.put("whoseTransaction", mCid);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
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
            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {

        }
    }

    private void setUserData() {
        mMainLy.setVisibility(View.VISIBLE);


        mFolio.setText(bundle.getString("folio"));
        mScheme.setText(bundle.getString("colorBlue"));
        mHolder.setText(bundle.getString("applicant_name"));


    }

}
