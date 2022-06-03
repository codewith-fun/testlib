package investwell.client.fragment.others;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Objects;

import investwell.client.activity.MainActivity;
import investwell.client.adapter.FolioItemListAdapter;
import investwell.client.adapter.RecycleTransectionAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.model.AddTrans;


public class SchemeDetailFragment extends Fragment implements View.OnClickListener, RecycleTransectionAdapter.OnItemClickListener , ToolbarFragment.ToolbarCallback{
    Bundle bundle;
    TextView user_name, colorBlue, purchase_cost, market_value, folio_number,current_nav, gain, balance_unit, holding,
            abs_return, cagr, divident;


    Bundle new_bundle;
    private AppSession mSession;
    private String mCID = "";
    private MainActivity mActivity;
    private RecycleTransectionAdapter mAdapter;

    private List<AddTrans> financialToolsList;
    private RecyclerView rvAddTrans;
    private CardView mCardVidew;
    private ArrayList<JSONObject> list;
    private FolioItemListAdapter folioItemListAdapter;
    private JSONArray TransactionPermission;
    private LinearLayout liner1, liner2, liner3, liner4, liner5, liner6;
    private View view;
    private ToolbarFragment fragToolBar;
    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout mLinearContainer, mll_divident_container;
    private View v_divider_cagr, v_divider_divident;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_scheme__detail_, container, false);

        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(mActivity);
        setUpToolBar();
        setInitializers();
        getDataFromBundle();
        setListener();
        getFolioDetail();
        return view;
    }
    @Override
    public void onToolbarItemClick(View view) {
        if(view.getId()==R.id.btn_add_new){
            mActivity.displayViewOther(0,null);
        }
    }
    private void setInitializers() {
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        user_name = view.findViewById(R.id.et_name);
        financialToolsList = new ArrayList<>();
        colorBlue = view.findViewById(R.id.colorBlue);
        colorBlue.setSelected(true);
        purchase_cost = view.findViewById(R.id.purchase_cost);
        market_value = view.findViewById(R.id.tv_market_value);
        rvAddTrans = view.findViewById(R.id.rv_add_trans);
        folio_number = view.findViewById(R.id.folio_number);
        current_nav = view.findViewById(R.id.current_nav);
        gain = view.findViewById(R.id.gain);
        balance_unit = view.findViewById(R.id.balance_unit);
        holding = view.findViewById(R.id.holding);
        abs_return = view.findViewById(R.id.abs_return);
        v_divider_cagr = view.findViewById(R.id.divider_cagr);
        v_divider_divident = view.findViewById(R.id.divider_divident);
        mll_divident_container = view.findViewById(R.id.ll_divident_container);
        cagr = view.findViewById(R.id.cagr);
        divident = view.findViewById(R.id.divident);
        mCardVidew = view.findViewById(R.id.cv_additional_trans);
        mLinearContainer = view.findViewById(R.id.ll_main_container);
        new_bundle = new Bundle();

    }

    private void setAdditonalTransAdapter() {
        mAdapter = new RecycleTransectionAdapter(mActivity, financialToolsList, this);
        rvAddTrans.setLayoutManager(new GridLayoutManager(mActivity, 3));
       /* rvAddTrans.addItemDecoration(new DividerItemDecoration(mActivity,
                DividerItemDecoration.HORIZONTAL));
        rvAddTrans.addItemDecoration(new DividerItemDecoration(mActivity,
                DividerItemDecoration.VERTICAL));*/
        rvAddTrans.setItemAnimator(new DefaultItemAnimator());
        rvAddTrans.setNestedScrollingEnabled(false);
        rvAddTrans.setAdapter(mAdapter);
        checkTransactionPermission();
    }

    /*******************************************
     * Method contains data for transaction items
     *******************************************/
    private void prepareTransactionTools(JSONObject jsonObject) {

        int[] covers;
        //   {"Purchase":"Y","AdditionalPurchase":"Y","SIP":"Y","STP":"Y","SWITCH":"Y","Redemption":"Y","SWP":"Y"}

        covers = new int[]{
                R.mipmap.purchase2,
                R.mipmap.sip2,
                R.mipmap.switch2,
                R.mipmap.swp2,
                R.mipmap.stp2,
                R.mipmap.redeem2};
        AddTrans a;
        if (jsonObject.optString("AdditionalPurchase").equalsIgnoreCase("Y")) {
            a = new AddTrans(covers[0], getResources().getString(R.string.add_tranc_additional_purchase_txt), new_bundle);
            financialToolsList.add(a);
        }
        if (jsonObject.optString("SIP").equalsIgnoreCase("Y")) {
            a = new AddTrans(covers[1], getResources().getString(R.string.add_tranc_sip_txt), new_bundle);
            financialToolsList.add(a);
        }
        if (jsonObject.optString("SWITCH").equalsIgnoreCase("Y")) {
            a = new AddTrans(covers[2], getResources().getString(R.string.add_tranc_switch_txt), new_bundle);
            financialToolsList.add(a);
        }
        if (jsonObject.optString("SWP").equalsIgnoreCase("Y")) {
            a = new AddTrans(covers[3], getResources().getString(R.string.add_tranc_swp_txt), new_bundle);
            financialToolsList.add(a);
        }
        if (jsonObject.optString("STP").equalsIgnoreCase("Y")) {
            a = new AddTrans(covers[4], getResources().getString(R.string.add_tranc_stp_stxt), new_bundle);
            financialToolsList.add(a);
        }
        if (jsonObject.optString("Redemption").equalsIgnoreCase("Y")) {
            a = new AddTrans(covers[5], getResources().getString(R.string.add_tranc_redeem_txt), new_bundle);
            financialToolsList.add(a);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void setListener() {


    }

    /****************************************Frag
     * Converting dp to pixel
     ****************************************/
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void getDataFromBundle() {
        bundle = getArguments();
        if(bundle!=null) {
            user_name.setText(bundle.getString("applicant_name"));
            colorBlue.setText(bundle.getString("colorBlue"));
            if (!TextUtils.isEmpty(bundle.getString("purchase_cost"))) {
                purchase_cost.setText(getString(R.string.rs) + bundle.getString("purchase_cost"));
                view.findViewById(R.id.ll_investment_container).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.ll_investment_container).setVisibility(View.GONE);
            }
            market_value.setText(getString(R.string.rs) + bundle.getString("market_position"));
            folio_number.setText("Folio No: " + bundle.getString("folio"));

            //current_nav.setText("Current Nav:" + bundle.getString("current_nav"));


            if (!TextUtils.isEmpty(bundle.getString("current_nav"))){
                current_nav.setText("Current Nav:" + bundle.getString("current_nav"));
                current_nav.setVisibility(View.VISIBLE);
            }else {
                current_nav.setVisibility(View.GONE);
            }




            if (!TextUtils.isEmpty(bundle.getString("gain"))) {

                gain.setText(getString(R.string.rs) + bundle.getString("gain"));
                gain.setVisibility(View.VISIBLE);
            } else {
                gain.setVisibility(View.GONE);
            }

            balance_unit.setText(bundle.getString("unit"));
            holding.setText(bundle.getString("holding"));
            //holding.setText(Math.round(Float.parseFloat(bundle.getString("holding"))));
            holding.setText(bundle.getString("holding"));
            abs_return.setText(bundle.getString("absreturn") + "%");
            if (!TextUtils.isEmpty(bundle.getString("cagr"))) {
                cagr.setText(bundle.getString("cagr") + "%");

                view.findViewById(R.id.ll_cagr_container).setVisibility(View.VISIBLE);
                v_divider_cagr.setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.ll_cagr_container).setVisibility(View.GONE);
                v_divider_cagr.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(bundle.getString("dividend"))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.requireNonNull(bundle.getString("dividend")).isEmpty() || bundle.getString("dividend").equalsIgnoreCase("0")) {
                        v_divider_divident.setVisibility(View.GONE);
                        mll_divident_container.setVisibility(View.GONE);
                    } else {
                        divident.setText(bundle.getString("dividend"));
                        v_divider_divident.setVisibility(View.VISIBLE);
                        mll_divident_container.setVisibility(View.VISIBLE);
                    }
                }

            }
            if (!TextUtils.isEmpty(bundle.getString("UCC"))) {
                if (bundle.getString("UCC").equals("") || bundle.getString("UCC").equalsIgnoreCase("NA")) {
                    mCardVidew.setVisibility(View.GONE);
                } else {
                    mCardVidew.setVisibility(View.VISIBLE);
                }
            } else {
                mCardVidew.setVisibility(View.GONE);
            }
            if (bundle != null && bundle.containsKey("cid")) {
                mCID = bundle.getString("cid");
            } else
                mCID = mSession.getCID();


            new_bundle.putString("UCC", bundle.getString("UCC"));
            new_bundle.putString("Bid", AppConstants.APP_BID);
            new_bundle.putString("Fcode", bundle.getString("fund_code"));
            new_bundle.putString("Scode", bundle.getString("scheme_code"));
            new_bundle.putString("FolioNo", bundle.getString("folio"));
            new_bundle.putString("CurrentNAV",bundle.getString("current_nav"));
            new_bundle.putString("ExcelCode", bundle.getString("Exlcode"));
            new_bundle.putString("Passkey", mSession.getPassKey());

            new_bundle.putString("applicant_name", bundle.getString("applicant_name"));
            new_bundle.putString("colorBlue", bundle.getString("colorBlue"));
            new_bundle.putString("purchase_cost", bundle.getString("unit"));
            new_bundle.putString("market_position", bundle.getString("market_position"));

            if (gain.getText().toString().contains("-")) {
                gain.setTextColor(Color.parseColor("#d01f1f"));
            } else {
                gain.setTextColor(Color.parseColor("#43ce41"));
            }


            if (abs_return.getText().toString().contains("-")) {
                abs_return.setTextColor(Color.parseColor("#d01f1f"));
            } else {
                abs_return.setTextColor(Color.parseColor("#43ce41"));
            }

            if (cagr.getText().toString().contains("-")) {
                cagr.setTextColor(Color.parseColor("#d01f1f"));
            } else {
                cagr.setTextColor(Color.parseColor("#43ce41"));
            }
            view.findViewById(R.id.folio_detail_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.displayViewOther(80, bundle);
                }
            });

            view.findViewById(R.id.outstanding_units).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.displayViewOther(81, bundle);
                }
            });
            folioItemListAdapter = new FolioItemListAdapter(mActivity, new ArrayList<JSONObject>());
            setAdditonalTransAdapter();
        }
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_scheme_details), true, false, false, true, false, false, false, "home");
            fragToolBar.setCallback(this);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
        }
    }

    private void checkTransactionPermission() {
        try {
            TransactionPermission = new JSONArray(mSession.getTransactionPermission());
            JSONObject jsonObject = TransactionPermission.optJSONObject(0);
            prepareTransactionTools(jsonObject);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getFolioDetail() {
        mLinearContainer.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.My_Transaction_url;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", mCID);
            jsonObject.put("FromDate", "NA");
            jsonObject.put("ToDate", "NA");
            jsonObject.put("Foliono", bundle.getString("folio"));
            jsonObject.put("Fcode", bundle.getString("fund_code"));
            jsonObject.put("Scode", bundle.getString("scheme_code"));
            jsonObject.put("TranType", "");


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    list = new ArrayList<>();

                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("MyTransactionDetail");
                        //   nav.setText(jsonArray.optJSONObject(0).optString("nav"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                            list.add(jsonObject1);

                        }

                        folioItemListAdapter.updateList(list);

                    } else {

                        Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();

                    }
                    mLinearContainer.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mLinearContainer.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
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


    @Override
    public void onClick(View v) {


    }


    @Override
    public void onItemClick(int position) {
       /* switch (financialToolsList.get(position).getMenuId()) {
            case "A":
                mActivity.displayViewOther(28, new_bundle);
                break;

            case "B":
                mActivity.displayViewOther(29, new_bundle);
                //Toast.makeText(mActivity, "Work in progress..", Toast.LENGTH_SHORT).show();
                break;

            case "C":
                mActivity.displayViewOther(30, new_bundle);
                break;

            case "D":
                if (bundle.getString("Objective").contains("ELSS") || bundle.getString("Objective").contains("Tax Saving")) {

                } else {
                    mActivity.displayViewOther(31, new_bundle);
                }
                break;

            case "E":
                //Toast.makeText(mActivity, "Work in progress..", Toast.LENGTH_SHORT).show();
                mActivity.displayViewOther(32, new_bundle);
                break;

            case "F":
                mActivity.displayViewOther(33, new_bundle);
                break;

        }*/
    }
}
