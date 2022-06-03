package investwell.client.fragment.foliolookup;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.MainActivity;
import investwell.client.adapter.MyFolioAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FolioLookupFragment extends Fragment {
    private RecyclerView folio_detail_recycle;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private MainActivity mActivity;
    private AppSession mSession;
    private String mCid = "";
    private MyFolioAdapter folio_adapter;
    private ArrayList<JSONObject> folioLists;
    private SearchView searchView;
    private View view;
    private ToolbarFragment fragToolBar;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.frag_folio_lookup, container, false);
        setInitializer();
        errorContentInitializer(view);
        setUpToolBar();
        setAdapter();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else
            mCid = mSession.getCID();

        callMyFolioApi();
        return view;
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

    private void setInitializer() {

        setHasOptionsMenu(true);
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(mActivity);
        folioLists = new ArrayList<>();
        folio_detail_recycle = view.findViewById(R.id.rv_my_folio_list);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_my_folio), true, false, false, false, false, false, false, "");
        }
    }

    private void setAdapter() {
        folio_adapter = new MyFolioAdapter(mActivity, folioLists);
        folio_detail_recycle.setHasFixedSize(true);
        folio_detail_recycle.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        folio_detail_recycle.setAdapter(folio_adapter);
    }

    private void callMyFolioApi() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.Folio_Lookup;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCid);
            jsonObject.put("FormatReq", "Y");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    ArrayList<JSONObject> list = new ArrayList<>();

                    try {
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {

                            JSONArray jsonArray = jsonObject.optJSONArray("FolioLookUpDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                list.add(jsonObject1);
                            }

                            folio_adapter.updateList(list);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    } finally {
                        if (list.size() > 0) {
                            folio_adapter.updateList(list);
                            viewNoData.setVisibility(View.GONE);
                        } else {
                            folio_adapter.updateList(new ArrayList<JSONObject>());
                            viewNoData.setVisibility(View.VISIBLE);
                            displayServerMessage(jsonObject.optString("ServiceMSG"));
                        }

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
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

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
