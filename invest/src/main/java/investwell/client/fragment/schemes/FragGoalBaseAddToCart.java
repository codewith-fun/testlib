package investwell.client.fragment.schemes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.GoalBasketAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomButton;


public class FragGoalBaseAddToCart extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private TextView mTvNothing, mTvSchemesName, mTvSchemeDesc, mTvTitle;
    private ProgressDialog mBar;
    private GoalBasketAdapter mAdapter;
    private JSONObject mObjectData;
    private ImageView mIvSchemeGoal;
    private String toolBarTitle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private CustomButton create_acnt_btn;
    private ArrayList<JSONObject> mList;
    private BrokerActivity mBrokerActivity;
    private ToolbarFragment fragToolBar;
    private ShimmerFrameLayout mShimmerViewContainer;
    private CardView cvTopCard;
    private Bundle bundleForConfirm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mBrokerActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mBrokerActivity.getApplication();


        } else if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);

            mApplication = (AppApplication) mActivity.getApplication();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.busket_goal_base_activity, container, false);
        mSession = AppSession.getInstance(getActivity());
        mShimmerViewContainer = view.findViewById(R.id.shimmer_scheme_container);
        cvTopCard = view.findViewById(R.id.cardView);
        cvTopCard.setVisibility(View.GONE);
        bundleForConfirm = new Bundle();
        setUpToolBar();
        RecyclerView recyclerView = view.findViewById(R.id.basket_detail_recyclier);
        mTvNothing = view.findViewById(R.id.tvNothing);
        mIvSchemeGoal = view.findViewById(R.id.image);
        mTvSchemesName = view.findViewById(R.id.basketname);
        mTvSchemeDesc = view.findViewById(R.id.description);
        mTvTitle = view.findViewById(R.id.toolbar_title);
        create_acnt_btn = view.findViewById(R.id.create_acnt_btn);

        create_acnt_btn = view.findViewById(R.id.create_acnt_btn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new GoalBasketAdapter(getActivity(), new ArrayList<JSONObject>(), new GoalBasketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("passkey", mSession.getPassKey());
                bundle.putString("excl_code", jsonObject.optString("Exlcode"));
                bundle.putString(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
                bundle.putString("scheme", jsonObject.optString("SchName"));
                bundle.putString("object", jsonObject.toString());
                mActivity.displayViewOther(42, bundle);
            }
        });
        recyclerView.setAdapter(mAdapter);


        Bundle bundle = getArguments();
        if (bundle.containsKey("singleObject")) {
            try {
                String allData = bundle.getString("singleObject");
                mObjectData = new JSONObject(allData);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (mSession.getHasLoging()) {
            create_acnt_btn.setText("Get this Basket");
        } else {
            create_acnt_btn.setText("Sign-In");
        }
        /*     view.findViewById(R.id.ivLeft).setOnClickListener(this);*/




        getSchemes();
        create_acnt_btn.setOnClickListener(this);
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_goal_schemes),
                    true, false, false, false, false, false, false, "");
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create_acnt_btn) {
            if (mSession.getHasLoging()) {
                  /*  Config.BASKET_SCHEMES_LIST.clear();
                    Config.BASKET_SCHEMES_LIST.addAll(mList);
                    if (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "coming_from_dashborad");
                        mActivity.displayViewOther(11, bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "coming_from_dashborad");
                        mActivity.displayViewOther(36, bundle);
                    }*/
                bundleForConfirm.putString("type", "coming_from_dashborad");
                mActivity.displayViewOther(113, bundleForConfirm);
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));

            }
        }

    }

    private void getSchemes() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.RECOMENDED_SCHEMES;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("BasketCode", mObjectData.optString("BasketCode"));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    cvTopCard.setVisibility(View.VISIBLE);
                    try {
                        mList = new ArrayList<>();


                        if (jsonObject.optBoolean("Status")) {

                            mTvSchemesName.setText(jsonObject.optString("BasketName"));
                            mTvSchemeDesc.setText(jsonObject.optString("BasketDescription"));
                            String image_path = jsonObject.optString("BasketPicPath");
                            bundleForConfirm.putString("imagePath", image_path);
                            bundleForConfirm.putString("basketName", jsonObject.optString("BasketName"));
                            bundleForConfirm.putString("basketDescription", jsonObject.optString("BasketDescription"));
                            if (image_path.equals("") || image_path.equals("null")) {
                                mIvSchemeGoal.setImageResource(R.mipmap.logo_login);
                            } else {
                                Picasso.get().load(image_path).placeholder(R.mipmap.knowledge_area)
                                        .error(R.mipmap.knowledge_area).into(mIvSchemeGoal);
                            }

                            JSONArray araArray = jsonObject.getJSONArray("SchemeDetail");
                            List<String> list = new ArrayList<String>();
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                mList.add(object);
                                list.add(araArray.getJSONObject(i).toString());
                            }

                            mAdapter.updateList(mList);
                            bundleForConfirm.putString("listOfSchemes", jsonObject.toString());
                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                            }
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
                    cvTopCard.setVisibility(View.VISIBLE);
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

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
