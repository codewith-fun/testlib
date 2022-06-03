package investwell.client.fragment.taxsaver;

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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomDialog;

public class TaxSavingFragment extends Fragment implements ToolbarFragment.ToolbarCallback, CustomDialog.DialogBtnCallBack {

    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private RecyclerView tax_saving_recycle;
    private AdapterTaxSaving tax_saving_adapter;
    private ImageView Previous_Year_img, Next_Year_img;
    private TextView Previous_Year, Next_Year;
    private AppSession mSession;
    private String mCID = "";
    private Calendar c = Calendar.getInstance();
    private int year;
    private int next_year;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    private CustomDialog customDialog;
    private ShimmerFrameLayout mShimmerViewContainer;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_tax_saving, container, false);
        mActivity.setMainVisibility(this, null);
        setUpToolBar();
        errorContentInitializer(view);
        customDialog = new CustomDialog(this);
        tax_saving_recycle = view.findViewById(R.id.tax_saving_recycle);
        Previous_Year_img = view.findViewById(R.id.previous_year);
        Next_Year_img = view.findViewById(R.id.next_year);
        Next_Year = view.findViewById(R.id.next_year_txt);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mApplication = (AppApplication) mActivity.getApplication();

        tax_saving_recycle.setHasFixedSize(true);
        tax_saving_recycle.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        tax_saving_adapter = new AdapterTaxSaving(mActivity, new ArrayList<JSONObject>());
        tax_saving_recycle.setAdapter(tax_saving_adapter);


        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else
            mCID = mSession.getCID();

        Previous_Year = view.findViewById(R.id.previous_year_txt);
        Next_Year = view.findViewById(R.id.next_year_txt);
        year = c.get(Calendar.YEAR);
        next_year = year - 1;
        if (c.get(Calendar.MONTH) > 2) {
            next_year = next_year + 1;
            year = year + 1;

        }
        Previous_Year.setText("" + next_year);
        Next_Year.setText("" + year);
        Previous_Year_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = year - 1;
                next_year = next_year - 1;
                Previous_Year.setText("" + next_year);
                Next_Year.setText("" + year);
                callTaxInvestmentApi();
            }
        });

        Next_Year_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = year + 1;
                next_year = next_year + 1;
                Previous_Year.setText("" + next_year);
                Next_Year.setText("" + year);
                callTaxInvestmentApi();
            }
        });
        callTaxInvestmentApi();

        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_tax_saving), true, false, false, false, false, false, true, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
            fragToolBar.setCallback(this);
        }
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

    private void callTaxInvestmentApi() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        viewNoData.setVisibility(View.GONE);
        if (c.get(Calendar.YEAR) == Integer.parseInt(Previous_Year.getText().toString())) {

            Next_Year_img.setVisibility(View.INVISIBLE);
        } else {

            Next_Year_img.setVisibility(View.VISIBLE);
        }

        String from_date = Previous_Year.getText().toString() + "-" + "04" + "-" + "01";
        String to_date = Next_Year.getText().toString() + "-" + "03" + "-" + "31";
        String url = Config.Tax_Saving;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
            jsonObject.put("FromDate", from_date);
            jsonObject.put("ToDate", to_date);
          /*  mDataList.put("Foliono","");
            mDataList.put("Fcode","");
            mDataList.put("Scode","");
            mDataList.put("TranType","");
*/
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    try {
                        ArrayList<JSONObject> list = new ArrayList<>();

                        if (response.optString("Status").equalsIgnoreCase("True")) {

                            JSONArray jsonArray = response.optJSONArray("ELSSSummaryDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                list.add(jsonObject1);
                            }


                        } else {
                            viewNoData.setVisibility(View.VISIBLE);
                            displayServerMessage(response.optString("ServiceMSG"));
                        }
                        tax_saving_adapter.updateList(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    viewNoData.setVisibility(View.VISIBLE);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        displayServerErrorMessage(error);
                    } else {
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

    private void showTaxSavingInfo() {
        customDialog.showDialog(getActivity(), getResources().getString(R.string.fatca_instruction),
                getResources().getString(R.string.tax_saving_header_txt),
                getResources().getString(R.string.text_ok), "", true, false);
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_fatca_info) {
            showTaxSavingInfo();
        }
    }

    @Override
    public void onDialogBtnClick(View view) {
        if (view.getId() == R.id.btDone) {//Todo nothing
        }
    }
}
