package investwell.client.fragment.nav.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.nav.adapter.NavAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class SearchAmcFragment extends Fragment {

    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private List<JSONObject> mCartList, mSelectedCartsList, list;
    private AppApplication mAppApplication;
    private TextView mTvCart;
    private List<String> mAMC_list;
    private Animation slideUpAnimation, slideDownAnimation;
    private NavAdapter mAdapter;
    private ToolbarFragment fragToolBar;
    private ShimmerFrameLayout mShimmerViewContainer;
    @Override
    public void onResume() {
        super.onResume();
        updateCart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_nav, container, false);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mActivity.setMainVisibility(this, null);
        LinearLayout lldesc = view.findViewById(R.id.ll_description);
        TextView tvDesc = view.findViewById(R.id.tv_description);
        if (!TextUtils.isEmpty(mSession.getAmcDesc())) {
            tvDesc.setText(mSession.getAmcDesc());
            lldesc.setVisibility(View.VISIBLE);
        } else {
            lldesc.setVisibility(View.GONE);

        }
        setUpToolBar();
        mApplication = (AppApplication) mActivity.getApplication();
        mCartList = new ArrayList<>();
        mAppApplication = (AppApplication) mActivity.getApplication();
        TextView title = view.findViewById(R.id.titlename);
        RecyclerView recyclerView = view.findViewById(R.id.rv_performances);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        mAMC_list = new ArrayList<String>();
        slideUpAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.slide_down);
        mSelectedCartsList = new ArrayList<>();


        mAdapter = new NavAdapter(mActivity, new ArrayList<JSONObject>());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        view.findViewById(R.id.ivLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.getSupportFragmentManager().popBackStack();
            }
        });
        getData();
        updateCart();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);

        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(mSession.getAmc(), true, false, true, false, false, false, false, "");

        }


    }

    private void getData() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
        params.put(AppConstants.PASSKEY, mSession.getPassKey());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.GET_ALL_AMC, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                 mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                list = new ArrayList<>();
                if (response.optBoolean("Status")) {
                    JSONArray araArray = null;
                    try {
                        araArray = response.getJSONArray("AMCList");
                        for (int i = 0; i < araArray.length(); i++) {
                            JSONObject object = araArray.getJSONObject(i);
                            list.add(object);
                        }
                        mAdapter.updatelist(list);
//                            setSpinnerAMC();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    mApplication.showSnackBar(mTvCart, response.optString("ServiceMSG"));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                 mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                if (e.networkResponse != null && e.networkResponse.data != null) {
                    mApplication.showSnackBar(mTvCart, e.getLocalizedMessage());
                } else {
                    mApplication.showSnackBar(mTvCart, getResources().getString(R.string.no_internet));

                }

            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }


    private void addToCartList() {
        try {
            JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
            mTvCart.setText("" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                mSelectedCartsList.add(jsonArray.getJSONObject(i));
            }

        } catch (Exception e) {

        }

    }


    public void updateCart() {
        try {
            mTvCart.setText("0");
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                mTvCart.setVisibility(View.GONE);
            } else {
                mTvCart.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                mTvCart.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
