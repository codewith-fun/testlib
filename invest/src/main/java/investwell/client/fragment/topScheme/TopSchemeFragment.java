package investwell.client.fragment.topScheme;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.Scheme_List_Adapter;
import investwell.client.adapter.YearRecyclieviewAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class TopSchemeFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static String TopInvest = "5 Years", mTopTime = "Year5";
    public List<JSONObject> mSelectedCartsList;
    public TextView mTvCart;
    private List<JSONObject> mAllComingList;
    private TopSchemeAdapter mSchemeAdapter;
    private RelativeLayout rl_filters, rl_search;
    private LinearLayout rl_filter, lysubcat;
    private List<String> amc_cati, mCategory_list, mSubcategory_list;
    private Spinner amc_spiner;
    private FloatingActionButton btnfilter;
    private JSONArray mArray, mCatergory_array, mSubcat_array;
    private Spinner mCategory_sp, mAmc_sp, mSubcat_sp, mTimeperiod_sp;
    private JSONArray mTopSchemeList;
    private String mCat, mTopS_AMC = "All", mTopS_Cat = "E", mTopS_Sort = "5Y", mTopS_TopNo = "50", mTopS_Rating = "0";
    private ArrayList<JSONObject> list;
    private Animation slideUpAnimation, slideDownAnimation;
    private AppSession mSession;
    private Scheme_List_Adapter scheme_list_adapter;
    private EditText colorBlue;
    private String mScheme_name_value;
    private RecyclerView mScheme_list_recycleview;
    private TextView titlename, sub_heading;
    private Bundle bundle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private List<JSONObject> mCartList;
    private RecyclerView mYears;
    private String[] yearsValue;
    private YearRecyclieviewAdapter adapter;
    public ToolbarFragment fragToolBar;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ImageButton close_filters;
    private RecyclerView recyclerView;
    private Button btnreset, btnsubmit;
    private ImageView close_search;
    private String mType = "";

    @Override
    public void onStop() {
        super.onStop();
        TopInvest = "5 Years";
        mTopTime = "Year5";
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();

            mActivity.setMainVisibility(this, null);

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_top_scheme, container, false);
        initializer(view);
        LinearLayout lldesc = view.findViewById(R.id.ll_description);
        TextView tvDesc = view.findViewById(R.id.tv_description);
        if (!TextUtils.isEmpty(mSession.getTopPerformerDesc())) {
            tvDesc.setText(mSession.getTopPerformerDesc());
            lldesc.setVisibility(View.VISIBLE);
        } else {
            lldesc.setVisibility(View.GONE);

        }
        setUpToolBar();
        setUpTopSchemeAdapter();
        bundle = getArguments();
        if (bundle != null && bundle.containsKey("type")) {
            mType = bundle.getString("type");
        }
        btnfilter.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                btnfilter.setVisibility(View.GONE);
                rl_filters.setVisibility(View.VISIBLE);
                rl_filter.startAnimation(slideUpAnimation);
            }
        });

        rl_filters.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                rl_filter.startAnimation(slideDownAnimation);
                rl_filters.setVisibility(View.GONE);
                btnfilter.setVisibility(View.VISIBLE);
            }
        });

        close_filters.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                rl_filter.startAnimation(slideDownAnimation);
//                int i = ((Integer.valueOf(slideDownAnimation.getDuration()).intValue()));
                rl_filters.setVisibility(View.GONE);
                btnfilter.setVisibility(View.VISIBLE);

            }
        });
        rl_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        timeperiod_spinner();


        btnreset.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                rl_filters.setVisibility(View.GONE);
                btnfilter.setVisibility(View.VISIBLE);
                mTopS_AMC = "All";
                mTopS_Cat = "E";
                mTopS_Sort = "3Y";
                mTopS_TopNo = "80";
                mTopS_Rating = "0";
                TopInvest = "5 Years";
                mTopTime = "Year5";
                mTimeperiod_sp.setSelection(7);


                refreshlist();
                // category();
                ;

            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                rl_filters.setVisibility(View.GONE);
                btnfilter.setVisibility(View.VISIBLE);
                refreshlist();
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnfilter.getVisibility() == View.VISIBLE) {
                    btnfilter.hide();
                } else if (dy < 0 && btnfilter.getVisibility() == View.VISIBLE) {
                    btnfilter.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == recyclerView.SCROLL_STATE_IDLE && btnfilter.getVisibility() != View.VISIBLE) {
                    btnfilter.show();
                }
            }
        });


        mScheme_list_recycleview.setHasFixedSize(true);
        mScheme_list_recycleview.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        scheme_list_adapter = new Scheme_List_Adapter(mActivity, new ArrayList<JSONObject>());
        mScheme_list_recycleview.setAdapter(scheme_list_adapter);

        colorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                colorBlue.setFocusable(true);
                mScheme_list_recycleview.setVisibility(View.VISIBLE);
                btnfilter.hide();

            }
        });

        colorBlue.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mScheme_list_recycleview.setVisibility(View.VISIBLE);
                btnfilter.hide();
                if (s.toString().length() >= 3) {
                    colorBlue.getText().toString().replace(" ", "%20");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mScheme_name_value = colorBlue.getText().toString();
                            if (mScheme_name_value.isEmpty()) {
                            } else {
                                GetSchemeName();
                            }
                        }
                    }, 1500);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (colorBlue.getText().toString().isEmpty()) {
                    mScheme_list_recycleview.setVisibility(View.GONE);
                    btnfilter.show();
                }
            }
        });


        close_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorBlue.setText("");
                rl_search.setVisibility(View.GONE);
                btnfilter.show();
                rl_search.startLayoutAnimation();
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(colorBlue.getWindowToken(), 0);
            }
        });


        if (mApplication.getTopSchemeList().size() > 0) {
            addToCartList();
            mSchemeAdapter.updatelist(mApplication.getTopSchemeList());
        } else {
            topSchemeList(mTopS_AMC, mTopS_Cat, mTopS_Sort, mTopS_TopNo, mTopS_Rating);
        }


        if (mApplication.getTopSchemeCaregoryList().size() > 0) {
            mCategory_list.clear();
            mCategory_list.addAll(mApplication.getTopSchemeCaregoryList());
            category_spinner();
        } else {
            category();
        }
        mYears.setHasFixedSize(true);
        mYears.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        adapter = new YearRecyclieviewAdapter(mActivity, yearsValue, new YearRecyclieviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                switch (position) {
                    case 0:
                        mTopS_Sort = "15D";
                        mTopTime = "Day15";
                        break;
                    case 1:
                        mTopS_Sort = "30D";
                        mTopTime = "Day30";
                        break;
                    case 2:
                        mTopS_Sort = "3M";
                        mTopTime = "Month3";
                        break;
                    case 3:
                        mTopS_Sort = "6M";
                        mTopTime = "Month6";
                        break;
                    case 4:
                        mTopS_Sort = "1Y";
                        mTopTime = "Year1";
                        break;
                    case 5:
                        mTopS_Sort = "2Y";
                        mTopTime = "Year2";
                        break;
                    case 6:
                        mTopS_Sort = "3Y";
                        mTopTime = "Year3";
                        break;
                    case 7:
                        mTopS_Sort = "5Y";
                        mTopTime = "Year5";
                        break;
                    case 8:
                        mTopS_Sort = "10Y";
                        mTopTime = "Year10";
                        break;

                    case 9:
                        mTopS_Sort = "SI";
                        mTopTime = "SinceInception";
                        break;

                }

                topSchemeList(mTopS_AMC, mTopS_Cat, mTopS_Sort, mTopS_TopNo, mTopS_Rating);
            }
        });
        mYears.setAdapter(adapter);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mYears.scrollToPosition(8);
                    }
                }, 100);


        fragToolBar.updateCart(true);
        setDisclaimerData(view);
        return view;
    }
@SuppressLint("SetTextI18n")
private void setDisclaimerData(View view){
        TextView mDiscDesc=view.findViewById(R.id.tv_disc);

        mDiscDesc.setText(getResources().getString(R.string.disc_content_top_scheme)+""+getResources().getString(R.string.top_scheme_desc_list));
}
    private void setUpTopSchemeAdapter() {
        mSchemeAdapter = new TopSchemeAdapter(new ArrayList<JSONObject>(), mActivity, TopSchemeFragment.this);
        recyclerView.setAdapter(mSchemeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new DividerItemDecoration(mActivity,DividerItemDecoration.VERTICAL));

    }

    private void initializer(View view) {
        bundle = getArguments();
        mCartList = new ArrayList<>();
        mAllComingList = new ArrayList<>();
        yearsValue = getResources().getStringArray(R.array.top_scheme_years_array);
        recyclerView = view.findViewById(R.id.rv_performances);
        mYears = view.findViewById(R.id.years);
        mArray = new JSONArray();
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        amc_cati = new ArrayList<String>();
        mCategory_list = new ArrayList<String>();
        mSubcategory_list = new ArrayList<>();
        slideUpAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up);
        slideDownAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.slide_down);
        mSelectedCartsList = new ArrayList<>();
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        rl_filters = view.findViewById(R.id.rl_filters);
        titlename = view.findViewById(R.id.titlename);
        rl_filter = view.findViewById(R.id.rl_filter);
        lysubcat = view.findViewById(R.id.ly_subcat);
        btnfilter = view.findViewById(R.id.filters);
        close_filters = view.findViewById(R.id.close_filter);
        mCategory_sp = view.findViewById(R.id.categ_spinner);
        mTimeperiod_sp = view.findViewById(R.id.top_investment_time);
        mAmc_sp = view.findViewById(R.id.amc_spinner);
        mSubcat_sp = view.findViewById(R.id.sub_categ_spinner);
        btnreset = view.findViewById(R.id.filter_reset);
        btnsubmit = view.findViewById(R.id.filter_submit);
        sub_heading = view.findViewById(R.id.subheading);
        rl_search = view.findViewById(R.id.lysearch);
        colorBlue = view.findViewById(R.id.colorBlue);
        close_search = view.findViewById(R.id.text_clear);
        mScheme_list_recycleview = view.findViewById(R.id.scheme_list_recycleview);

    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("TopSchemeAddToCart")) &&
                Utils.getConfigData(mSession).optString("TopSchemeAddToCart").equalsIgnoreCase("Y")) {
            if (fragToolBar != null) {
                fragToolBar.setUpToolBar(mSession.getTopPerformer(),
                        true, false, true, false, false, true, false, "");
            }
        } else {
            if (fragToolBar != null) {
                fragToolBar.setUpToolBar(mSession.getTopPerformer(),
                        true, false, true, false, false, false, false, "");
            }


        }


    }

    private void GetSchemeName() {

        String url = Config.Search_list;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("SearchValue", mScheme_name_value);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        ArrayList<JSONObject> list = new ArrayList<>();

                        String Status = response.optString("Status");
                        if (Status.equalsIgnoreCase("True")) {
                            JSONArray jsonArray = response.optJSONArray("SchemeSearchDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                list.add(object);
                            }

                            scheme_list_adapter.updateList(list);
                        } else {
                            String ServiceMSG = response.optString("ServiceMSG");
                            list.clear();
                            scheme_list_adapter.updateList(list);
                            Toast.makeText(mActivity, ServiceMSG, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                    } else {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                    }
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void timeperiod_spinner() {

        mTimeperiod_sp.setOnItemSelectedListener(this);
        ArrayAdapter<String> invt_adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, getResources().getStringArray(R.array.top_scheme_spinner_array_elements));
        invt_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeperiod_sp.setAdapter(invt_adapter);
        mTimeperiod_sp.setSelection(7);

    }

    private void category_spinner() {

        mCategory_sp.setOnItemSelectedListener(this);
        ArrayAdapter<String> cat_adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, mCategory_list);
        cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory_sp.setAdapter(cat_adapter);
        mCategory_sp.setSelection(2);
    }

    private void sub_category_spinner() {
        lysubcat.setVisibility(View.VISIBLE);
        mSubcategory_list.add(0, "All");
        mSubcat_sp.setOnItemSelectedListener(this);
        ArrayAdapter<String> cat_adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, mSubcategory_list);
        cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSubcat_sp.setAdapter(cat_adapter);
    }

    private void amc_spinner() {
        mAmc_sp.setOnItemSelectedListener(this);
        ArrayAdapter<String> amc_adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, amc_cati);
        amc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAmc_sp.setAdapter(amc_adapter);
    }


    private void refreshlist() {

        topSchemeList(mTopS_AMC, mTopS_Cat, mTopS_Sort, mTopS_TopNo, mTopS_Rating);


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

    private void topSchemeList(String AMC, String Cat, String Sort, String TopNo, String Rating) {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        try {
            JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
            mTvCart.setText("" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                mSelectedCartsList.add(jsonArray.getJSONObject(i));
            }

        } catch (Exception e) {

        }


        String URL = Config.Top_scheme;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("AMC", AMC);
            if (mType.equalsIgnoreCase("elss")) {
                jsonObject.put("Category", "Tax");
            } else if (mType.equalsIgnoreCase("liquid")) {
                jsonObject.put("Category", "Liq");
            }else if (mType.equalsIgnoreCase("Ret")) {
                jsonObject.put("Category", "Ret");
            }else if (mType.equalsIgnoreCase("Child")) {
                jsonObject.put("Category", "Child");
            }else if (mType.equalsIgnoreCase("G")) {
                jsonObject.put("Category", "G");
            } else {
                jsonObject.put("Category", Cat);
            }
            jsonObject.put("Sort", Sort);
            jsonObject.put("TopNo", TopNo);
            jsonObject.put("Rating", Rating);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (bundle != null && bundle.containsKey("heading_name")) {
                            sub_heading.setVisibility(View.GONE);
                            sub_heading.setText(bundle.getString("heading_name"));
                        } else {
                            sub_heading.setVisibility(View.GONE);

                        }
                        list = new ArrayList<>();
                        if (response.optString("Status").equals("True")) {
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            mTopSchemeList = response.getJSONArray("TopSchemeList");
                            for (int i = 0; i < mTopSchemeList.length(); i++) {
                                JSONObject object2 = mTopSchemeList.getJSONObject(i);
                                list.add(object2);
                            }
                            mSchemeAdapter.updatelist(list);
                        } else {
                            Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(mActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void category() {


        String URL = Config.Category_List;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String firstvalue = "All";
                        mCategory_list.add(0, firstvalue);
                        if (response.optString("Status").equals("True")) {
                            mCatergory_array = response.getJSONArray("BroadCategoryList");
                            for (int i = 0; i < mCatergory_array.length(); i++) {

                                JSONObject jsonObject = mCatergory_array.getJSONObject(i);
                                mCategory_list.add(jsonObject.optString("ItemName"));
                            }

                            category_spinner();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mActivity, jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Spinner spinner = (Spinner) adapterView;

        if (spinner.getId() == R.id.categ_spinner) {
            String item = adapterView.getItemAtPosition(i).toString();
            mSubcategory_list.clear();
            if (!item.equals("All")) {
                try {
                    JSONObject category = mCatergory_array.getJSONObject(i - 1);
                    mSubcat_array = category.getJSONArray("SubCategoryList");
                    mCat = category.optString("ItemValue");
                    for (int j = 0; j < mSubcat_array.length(); j++) {
                        JSONObject sub_object = mSubcat_array.getJSONObject(j);
                        mSubcategory_list.add(sub_object.optString("ObjName"));

                    }
                    sub_category_spinner();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mTopS_Cat = "All";
                lysubcat.setVisibility(View.GONE);
            }

            /*String item = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(this, "Selected" + item, Toast.LENGTH_SHORT).show();*/
        }

        if (spinner.getId() == R.id.amc_spinner) {
            String item = adapterView.getItemAtPosition(i).toString();
            if (!item.equals("All")) {

                try {
                    JSONObject amc_code_array = mArray.getJSONObject(i - 1);

                    mTopS_AMC = amc_code_array.optString("AMCCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (spinner.getId() == R.id.sub_categ_spinner) {
            String item = adapterView.getItemAtPosition(i).toString();
            if (!item.equals("All")) {

                try {
                    JSONObject amc_code_array = mSubcat_array.getJSONObject(i - 1);

                    mTopS_Cat = amc_code_array.optString("ObjCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mTopS_Cat = mCat;
            }
        }

        if (spinner.getId() == R.id.top_investment_time) {
            String item = adapterView.getItemAtPosition(i).toString();
            TopInvest = item;
            switch (i) {
                case 0:
                    mTopS_Sort = "15D";
                    mTopTime = "Day15";
                    break;
                case 1:
                    mTopS_Sort = "30D";
                    mTopTime = "Day30";
                    break;
                case 2:
                    mTopS_Sort = "3M";
                    mTopTime = "Month3";
                    break;
                case 3:
                    mTopS_Sort = "6M";
                    mTopTime = "Month6";
                    break;
                case 4:
                    mTopS_Sort = "1Y";
                    mTopTime = "Year1";
                    break;
                case 5:
                    mTopS_Sort = "2Y";
                    mTopTime = "Year2";
                    break;
                case 6:
                    mTopS_Sort = "3Y";
                    mTopTime = "Year3";
                    break;
                case 7:
                    mTopS_Sort = "5Y";
                    mTopTime = "Year5";
                    break;
                case 8:
                    mTopS_Sort = "10Y";
                    mTopTime = "Year10";
                    break;
                case 9:
                    mTopS_Sort = "SI";
                    mTopTime = "SinceInception";
                    break;

            }

//            top_invest_time_set(i);
//            Toast.makeText(this, "Selected" + item, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


   /* public void updateCart() {
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

    }*/

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivLeft) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.fl_cart) {
            try {
                if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                    mActivity.displayViewOther(39, null);
                } else {
                    // mSession.setGetSchemeData(mAllComingList.toString());
                    mActivity.displayViewOther(4, null);
                }

            } catch (Exception e) {

            }
        }
    }
}