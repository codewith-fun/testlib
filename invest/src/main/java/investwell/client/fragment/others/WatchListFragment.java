package investwell.client.fragment.others;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.WatchListAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class WatchListFragment extends Fragment implements View.OnClickListener, ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack {

    public ArrayList<JSONObject> list = new ArrayList<>();
    RecyclerView recycle_watch_list;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    WatchListAdapter watch_list_adapter;
    private MainActivity mActivty;
    private AppSession mSession;
    private String mCID = "";
    private LinearLayout mSchemeLayout, mReturnLayout;
    private int SchemeCount = 0, ReturnCount = 0;
    private View view;
    private ToolbarFragment fragToolBar;
    private CustomDialog customDialog;
    private AppApplication mApplication;
    private TextView mTvNothing;
    private ShimmerFrameLayout mShimmerViewContainer;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivty = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_watch_list, container, false);
        setInitializer();
        errorContentInitializer(view);
        setUpToolBar();
        setAdapter();
        getDataFromBundle();
        callWatchListApi();
        setListeners();
        return view;
    }

    private void setInitializer() {
        recycle_watch_list = view.findViewById(R.id.rv_watch_list);
        mTvNothing = view.findViewById(R.id.tvNothing);
        mSchemeLayout = view.findViewById(R.id.ll_scheme);
        mReturnLayout = view.findViewById(R.id.ll_return);
        mActivty.setMainVisibility(this, null);
        mSession = AppSession.getInstance(mActivty);
        customDialog = new CustomDialog(this);
        mApplication = (AppApplication) mActivty.getApplication();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
    }

    private void getDataFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else
            mCID = mSession.getCID();

    }

    private void setAdapter() {
        recycle_watch_list.setHasFixedSize(true);
        recycle_watch_list.setLayoutManager(new LinearLayoutManager(mActivty, LinearLayoutManager.VERTICAL, false));
        watch_list_adapter = new WatchListAdapter(mActivty, new ArrayList<JSONObject>());
        recycle_watch_list.setAdapter(watch_list_adapter);

    }

    private void setListeners() {
        mSchemeLayout.setOnClickListener(this);
        mReturnLayout.setOnClickListener(this);
    }
    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_connection_timeout);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }

    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }

    //Display Network Error Content
    private void displayNoInternetMessage() {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }

    private void callWatchListApi() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.Watch_List;
        viewNoData.setVisibility(View.GONE);
        list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //   mSession.set_watch_list(response.toString());

                    AppApplication.watch_list = response.toString();
                    setUpWatchListData();
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
viewNoData.setVisibility(View.VISIBLE);
displayServerErrorMessage(error);
                    } else {
                        if (error instanceof TimeoutError)
                            displayConnectionTimeOut();
                        else if (error instanceof NoConnectionError)
                            displayNoInternetMessage();
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
            requestQueue = Volley.newRequestQueue(mActivty);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_watch_list), true, false, false, false, false, false, true, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivty, ContextCompat.getColor(mActivty,R.color.colorPrimary));

            }
            fragToolBar.setCallback(this);
        }
    }

    private void setUpWatchListData() {
        try {

            JSONObject jsonObject = new JSONObject(AppApplication.watch_list);
            String Status = jsonObject.optString("Status");
            if (Status.equalsIgnoreCase("True")) {

                JSONArray jsonArray = jsonObject.optJSONArray("WatchListDetail");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    list.add(jsonObject1);

                }

                Collections.sort(list, new Comparator<JSONObject>() {

                    public int compare(JSONObject a, JSONObject b) {
                        Double valA = 0.0;
                        Double valB = 0.0;

                        try {
                            valA = Double.parseDouble((String) a.get("Change"));
                            valB = Double.parseDouble((String) b.get("Change"));
                        } catch (JSONException e) {
                        }

                        return valB.compareTo(valA);
                    }
                });


            } else {
displayServerMessage("Something went wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (list.size() > 0) {
                watch_list_adapter.updateList(list);
                viewNoData.setVisibility(View.GONE);
            } else {
                watch_list_adapter.updateList(new ArrayList<JSONObject>());
                viewNoData.setVisibility(View.VISIBLE);
                displayServerMessage("Something went wrong");
            }
        }

    }

    private void setSchemeData(final int count) {

        Collections.sort(list, new Comparator<JSONObject>() {

            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get("SchName");
                    valB = (String) b.get("SchName");
                } catch (JSONException e) {
//do something
                }

                if (count == 0) {

                    SchemeCount = 1;

                    return valA.compareTo(valB);

                } else
                    SchemeCount = 0;
                return valB.compareTo(valA);
            }
        });

        watch_list_adapter.updateList(list);
    }

    private void setUpReturnData(final int count) {
        Collections.sort(list, new Comparator<JSONObject>() {

            public int compare(JSONObject a, JSONObject b) {
                Double valA = 0.0;
                Double valB = 0.0;

                try {
                    valA = Double.parseDouble((String) a.get("Change"));
                    valB = Double.parseDouble((String) b.get("Change"));
                } catch (JSONException e) {
//do something
                }

                if (count == 0) {

                    ReturnCount = 1;
                    return valA.compareTo(valB);
                } else
                    ReturnCount = 0;
                return valB.compareTo(valA);


            }
        });

        watch_list_adapter.updateList(list);

    }

    private void showWatchListInfoDialog() {
        customDialog.showDialog(getActivity(), getResources().getString(R.string.fatca_instruction),
                getResources().getString(R.string.watch_list_header_txt),
                getResources().getString(R.string.text_ok), "", true, false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_scheme) {
            setSchemeData(SchemeCount);
        } else if (id == R.id.ll_return) {
            setUpReturnData(ReturnCount);
        }
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_fatca_info) {
            showWatchListInfoDialog();
        }
    }

    @Override
    public void onDialogBtnClick(View view) {
        if (view.getId() == R.id.btDone) {//Todo nothing
        }
    }
}

