package investwell.broker.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import investwell.broker.activity.BrokerActivity;
import investwell.broker.adapter.Broker_Client_Adapter;
import investwell.client.activity.AppApplication;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.SearchAnimationToolbar;


public class FragClientSearch extends Fragment implements View.OnClickListener/*, SearchAnimationToolbar.OnSearchQueryChangedListener*/ {
    public static String values;
    private final long DELAY = 1000; // milliseconds
    private AppSession mSession;
    private BrokerActivity mActivity;
    private AppApplication mApplication;
    private Broker_Client_Adapter mAdapter;
    private TextView mTvLoading;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView recyclerView;
    private LinearLayout mLlMain;
    private LinearLayoutManager mLayoutManager;
    private ImageView ivClearSearch;
    private Timer timer = new Timer();
    private SearchAnimationToolbar toolbar;
    private String mCid = "NA", mSearchType = "Group"; // Group
    private EditText etSearchView;
    private View view;
    private String text = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
        }

    }


    @Override
    public void onStop() {
        super.onStop();

        try {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            IBinder binder = mActivity.getCurrentFocus().getWindowToken();
            if (imm.isAcceptingText() && binder != null) {
                imm.hideSoftInputFromWindow(binder, 0);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_client_search, container, false);
        etSearchView = view.findViewById(R.id.et_search_view);
        ivClearSearch = view.findViewById(R.id.iv_clear_search);
        ivClearSearch.setOnClickListener(this);
        if (mSession.getLoginType().equalsIgnoreCase("Broker")) {
            mCid = "NA";
        } else {
            if (mSession.getSecondryCID().length() > 0)
                mCid = mSession.getSecondryCID();

            else
                mCid = mSession.getCID();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*toolbar = view.findViewById(R.id.toolbar);*/
        /*toolbar.setSupportActionBar(mActivity);
        toolbar.setOnSearchQueryChangedListener(this);*/
        mActivity.setMainVisibility(this, null);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);


        mTvLoading = view.findViewById(R.id.tvLoading);
        mTvLoading.setVisibility(View.GONE);

        mLlMain = view.findViewById(R.id.llMain);
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setNestedScrollingEnabled(true);
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new Broker_Client_Adapter(mActivity, new ArrayList<JSONObject>());
        recyclerView.setAdapter(mAdapter);

        RadioGroup search_group = view.findViewById(R.id.search_group);
        search_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.group) {
                    mSearchType = "Group";
                    if (text.length() > 0) {
                        searchClients(text);
                    }
                } else if (checkedId == R.id.investor) {
                    mSearchType = "Client";
                    if (text.length() > 0) {
                        searchClients(text);
                    }
                }
            }
        });
        etSearchView.addTextChangedListener(etSearchTextWatcher);
        /*searchClients("a");*/

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_clear_search) {
            etSearchView.setText("");
            ivClearSearch.setVisibility(View.GONE);
            mAdapter.updateList(new ArrayList<JSONObject>(), mSearchType);
        }
    }

    private TextWatcher etSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ivClearSearch.setVisibility(etSearchView.getText().length() > 0 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            text = editable.toString();
            if (!text.isEmpty()) {
                startSearch(text);
            } else {
                mAdapter.updateList(new ArrayList<JSONObject>(), mSearchType);
            }


        }
    };

    private void startSearch(final String searchText) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchClients(searchText);
                                }
                            });

                        }
                    }
                },
                DELAY
        );
    }


    public void searchClients(final String searchText) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        mLlMain.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.GONE);
        String url = Config.Broker_Dashbord_Search;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCid);
            jsonObject.put("LoginCategory", mSession.getLoginType());
            jsonObject.put("SearchValue", searchText);
            jsonObject.put("UserType", mSearchType);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    List<JSONObject> list = new ArrayList<>();

                    try {
                        if (jsonObject.optBoolean("Status")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("BrokerDBClientSearchDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                list.add(jsonObject1);
                            }

                        }
                    } catch (Exception e) {

                    } finally {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        if (list.size() > 0) {

                            mAdapter.updateList(list, mSearchType);
                            mTvLoading.setVisibility(View.GONE);
                            mLlMain.setVisibility(View.VISIBLE);
                        } else {
                            mAdapter.updateList(new ArrayList<JSONObject>(), mSearchType);
                            mTvLoading.setVisibility(View.VISIBLE);
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mLlMain.setVisibility(View.GONE);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    mLlMain.setVisibility(View.GONE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mTvLoading.setVisibility(View.VISIBLE);

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                    }
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
