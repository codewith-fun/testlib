package investwell.client.fragment.documentViewer;

import android.content.Context;
import android.os.Bundle;
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
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.MyDocsAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragClientDocView extends Fragment {
    private RecyclerView doc_detail_recycle;
    private RequestQueue requestQueue;
    private MainActivity mActivity;
    private AppSession mSession;
    private String mCid = "";
    private MyDocsAdapter mydocs_adapter;
    private ArrayList<JSONObject> docLists;
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
        view = inflater.inflate(R.layout.frag_client_doc_list, container, false);
        setInitializer();
        errorContentInitializer(view);
        setUpToolBar();
        setAdapter();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCid = bundle.getString("cid");
        } else
            mCid = mSession.getCID();

        getDocList();
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
        docLists = new ArrayList<>();
        doc_detail_recycle = view.findViewById(R.id.rv_my_doc_list);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar("My Documents", true, false, false, false, false, false, false, "");
        }
    }

    private void setAdapter() {
        mydocs_adapter = new MyDocsAdapter(mActivity, docLists);
        doc_detail_recycle.setHasFixedSize(true);
        doc_detail_recycle.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        doc_detail_recycle.setAdapter(mydocs_adapter);
    }

    private void getDocList() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.CLIENT_DOC_LIST;

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

                            JSONArray jsonArray = jsonObject.optJSONArray("ResponseData");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                jsonObject1.put("cid", mCid);
                                list.add(jsonObject1);
                            }
                            mydocs_adapter.updateList(list);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    } finally {
                        if (list.size() > 0) {
                            mydocs_adapter.updateList(list);
                            viewNoData.setVisibility(View.GONE);
                        } else {
                            mydocs_adapter.updateList(new ArrayList<JSONObject>());
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
