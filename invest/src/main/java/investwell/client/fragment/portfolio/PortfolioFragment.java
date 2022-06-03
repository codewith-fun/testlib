package investwell.client.fragment.portfolio;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import investwell.broker.fragment.FragHomeBroker;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.home.FragHomeClient;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class PortfolioFragment extends Fragment implements View.OnClickListener {
    RequestQueue requestQueue;

    private View viewTopPortfolio, viewFilterPortfolio;
    RecyclerView sub_client;
    TextView applicant_name, purchase_cost, market_value, gain, cagr, tvLabelInvestment, tvLabelGain, tvLabelCagr, tvFilterLabel;
    ImageView gain_arrow, cagr_arrow;
    AdapterPortfolio portfolio_client_adapter;

    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private String mCID = "";
    private JSONObject jsonObject;
    private TextView mTvNothing, tvSchemeCategory, btnSchemeCatAsc, brnSchemeCatDsc;
    private FloatingActionButton fab, fabApplyFilter;
    private ShimmerFrameLayout mShimmerViewContainer;
    private boolean isOpen = false;
    private Button btnNameAsc;
    private Button[] btn = new Button[10];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btn_asc_name, R.id.btn_desc_name, R.id.btn_cv_filter_growth, R.id.btn_cv_filter_declined,
            R.id.btn_invest_raise, R.id.btn_invest_fall, R.id.btn_gain_raise, R.id.btn_gain_fall, R.id.btn_cagr_raise, R.id.btn_cagr_fall};

    private ArrayList<JSONObject> list;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);

        }
    }

    @Override
    public void onStart() {
        mActivity.setMainVisibility(this, null);
        super.onStart();
    }

    @SuppressLint("CommitTransaction")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        initializer(view);
        errorContentInitializer(view);
        getDataFromBundle();
        setPortfolioAdapter();
        if (AppApplication.portfolio_data.isEmpty()) {
            callPortFolioReturnApi();
        } else {

            setData();

        }
       // initialFilterView();
        setListeners(view);


        return view;
    }

    //Inside this method we receive data from bundle object
    private void getDataFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("cid")) {
            mCID = bundle.getString("cid");
        } else {
            mCID = mSession.getCID();
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

    //Inside this method we set our portfolio data adapter
    private void setPortfolioAdapter() {
        sub_client.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        portfolio_client_adapter = new AdapterPortfolio(mActivity, new ArrayList<JSONObject>());
        sub_client.setAdapter(portfolio_client_adapter);
        sub_client.setNestedScrollingEnabled(false);
    }

    private void initialFilterView() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btnNameAsc.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btnNameAsc.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btnNameAsc.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btnNameAsc.setTextColor(getResources().getColor(R.color.colorWhite));

        }
    }

    //Inside this method all ui views were initialised were set
    private void initializer(View view) {
        list = new ArrayList<>();
        viewTopPortfolio = view.findViewById(R.id.content_top_layout_portfolio);
        viewTopPortfolio.setBackground(ContextCompat.getDrawable(mActivity,R.mipmap.blank_card));
        viewFilterPortfolio = view.findViewById(R.id.content_filter_portfolio);
        fabApplyFilter = viewFilterPortfolio.findViewById(R.id.fab_apply_filter);
        btnNameAsc = viewFilterPortfolio.findViewById(R.id.btn_asc_name);
        applicant_name = viewTopPortfolio.findViewById(R.id.applicant_name);
        tvFilterLabel = viewFilterPortfolio.findViewById(R.id.tv_filter_name);
        tvSchemeCategory = viewFilterPortfolio.findViewById(R.id.tv_filter_category);
        btnSchemeCatAsc = viewFilterPortfolio.findViewById(R.id.btn_cat_raise);
        brnSchemeCatDsc = viewFilterPortfolio.findViewById(R.id.btn_cat_fall);
        tvSchemeCategory.setVisibility(View.GONE);
        btnSchemeCatAsc.setVisibility(View.GONE);

        brnSchemeCatDsc.setVisibility(View.GONE);
        tvLabelInvestment = viewTopPortfolio.findViewById(R.id.tv_label_investment);
        tvFilterLabel.setText("Name");
        tvLabelCagr = viewTopPortfolio.findViewById(R.id.tv_label_cagr);
        tvLabelGain = viewTopPortfolio.findViewById(R.id.tv_label_gain);
        purchase_cost = viewTopPortfolio.findViewById(R.id.purchase_cost);
        market_value = viewTopPortfolio.findViewById(R.id.tv_market_value);
        fab = view.findViewById(R.id.fab_filter_portfolio);
        gain = viewTopPortfolio.findViewById(R.id.gain);
        cagr = viewTopPortfolio.findViewById(R.id.cagr);
        gain_arrow = viewTopPortfolio.findViewById(R.id.gain_arrow);
        cagr_arrow = viewTopPortfolio.findViewById(R.id.cagr_arrow);
        sub_client = view.findViewById(R.id.sub_client);
        mTvNothing = view.findViewById(R.id.tvNothing);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
    }

    //Inside this method all event listeners were set
    private void setListeners(View view) {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.size() > 0) {
                    viewNoData.setVisibility(View.GONE);
                    viewFilterPortfolio();
                }else{
                    mApplication.showSnackBar(fab,"No data found to sort");
                }


            }
        });
        fabApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewFilterPortfolio();
                runLayoutAnimation(sub_client);
                if (list.size() > 0) {
                    viewNoData.setVisibility(View.GONE);
                } else {
                    viewNoData.setVisibility(View.VISIBLE);
                }
            }
        });
        NestedScrollView nsv = view.findViewById(R.id.nsv_port_frag);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab.hide();


                } else {
                    fab.show();
                }
            }
        });
        for (int i = 0; i < btn.length; i++) {
            btn[i] = viewFilterPortfolio.findViewById(btn_id[i]);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                btn[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
            } else {
                btn[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_secondary));

            }
            btn[i].setOnClickListener(this);
        }
        btn_unfocus = btn[0];
       /* if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn[0].setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btn[0].setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));

        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn[0].setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn[0].setTextColor(getResources().getColor(R.color.colorWhite));

        }*/
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        }
        recyclerView.scheduleLayoutAnimation();
    }

    //This method will view our Filter UI with Circular Reveal Animation
    private void viewFilterPortfolio() {

        if (!isOpen) {

            int x = viewFilterPortfolio.getRight();
            int y = viewFilterPortfolio.getBottom();

            int startRadius = 0;
            int endRadius = (int) Math.hypot(viewFilterPortfolio.getWidth(), viewFilterPortfolio.getHeight());

            Animator anim = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(viewFilterPortfolio, x, y, startRadius, endRadius);
            }

            viewFilterPortfolio.setVisibility(View.VISIBLE);
            if (anim != null) {
                anim.start();
            }

            isOpen = true;

        } else {

            int x = viewFilterPortfolio.getRight();
            int y = viewFilterPortfolio.getBottom();

            int startRadius = Math.max(viewFilterPortfolio.getWidth(), viewFilterPortfolio.getHeight());
            int endRadius = 0;


            Animator anim = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(viewFilterPortfolio, x, y, startRadius, endRadius);
            }
            if (anim != null) {
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        viewFilterPortfolio.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
            if (anim != null) {
                anim.start();
            }

            isOpen = false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setApplicantGroupTitle() {
        if (mSession.getLoginType().equalsIgnoreCase("ClientG") || FragHomeBroker.comming_from.equalsIgnoreCase("Broker_group")) {
            applicant_name.setText(mSession.getFullName() + "  " + getResources().getString(R.string.portfolio_family_group_txt));
        } else {
            applicant_name.setText(mSession.getFullName());
        }
    }

    //Inside Default Sorted List will be fetched
    private void setUpPortfolioListDefaultData(JSONArray jsonArray) {

        for (int i = 0; i < jsonArray.length(); i++) {
            /*JSONObject jsonObject1 = jsonArray.optJSONObject(i);*/
            try {
                list.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
     /*   Collections.sort(list, new Comparator<JSONObject>() {

            public int compare(JSONObject lhs, JSONObject rhs) {


                try {
                    return lhs.getString("ApplicantName").toLowerCase().compareTo(rhs.getString("ApplicantName").toLowerCase());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });*/
        Log.e("List PortFolio", list.toString());
        portfolio_client_adapter.updateList(list);
    }

    //Inside this method name sorting is done
    private void setPortFolioNameSortedData(String sortBy) {
        if (sortBy.equalsIgnoreCase("nameAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject lhs, JSONObject rhs) {


                    try {
                        return lhs.getString("ApplicantName").toLowerCase().compareTo(rhs.getString("ApplicantName").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        } else if (sortBy.equalsIgnoreCase("nameDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject lhs, JSONObject rhs) {


                    try {
                        return rhs.getString("ApplicantName").toLowerCase().compareTo(lhs.getString("ApplicantName").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        } else {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject lhs, JSONObject rhs) {


                    try {
                        return lhs.getString("ApplicantName").toLowerCase().compareTo(rhs.getString("ApplicantName").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
        portfolio_client_adapter.updateList(list);
    }

    //Inside this method Current Value sorting is done
    private void setPortFolioCurrentSortedData(String sortBy) {
        if (sortBy.equalsIgnoreCase("cvAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("CurrentVal").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("CurrentVal").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valB.compareTo(valA);

                }

            });
        } else if (sortBy.equalsIgnoreCase("cvDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("CurrentVal").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("CurrentVal").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valA.compareTo(valB);

                }

            });
        } else {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject lhs, JSONObject rhs) {


                    try {
                        return lhs.getString("ApplicantName").toLowerCase().compareTo(rhs.getString("ApplicantName").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
        portfolio_client_adapter.updateList(list);
    }

    //Inside this method Investment Value sorting is done
    private void setPortFolioInvestmentSortedData(String sortBy) {
        if (sortBy.equalsIgnoreCase("investAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("InitialVal").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("InitialVal").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valB.compareTo(valA);

                }

            });
        } else if (sortBy.equalsIgnoreCase("investDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("InitialVal").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("InitialVal").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valA.compareTo(valB);

                }

            });
        } else {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject lhs, JSONObject rhs) {


                    try {
                        return lhs.getString("ApplicantName").toLowerCase().compareTo(rhs.getString("ApplicantName").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
        portfolio_client_adapter.updateList(list);
    }

    //Inside this method GAIN Value sorting is done
    private void setPortFolioGainSortedData(String sortBy) {
        if (sortBy.equalsIgnoreCase("gainAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("Gain").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("Gain").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valB.compareTo(valA);

                }

            });
        } else if (sortBy.equalsIgnoreCase("gainDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Integer valA = 0;
                    Integer valB = 0;

                    try {
                        valA = Integer.parseInt(a.get("Gain").toString().replace(",", ""));
                        valB = Integer.parseInt(b.get("Gain").toString().replace(",", ""));


                    } catch (JSONException e) {
//do something
                    }
                    return valA.compareTo(valB);


                }

            });
        } else {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject lhs, JSONObject rhs) {


                    try {
                        return lhs.getString("ApplicantName").toLowerCase().compareTo(rhs.getString("ApplicantName").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
        portfolio_client_adapter.updateList(list);
    }

    //Inside this method GainSinceInception Value sorting is done
    private void setPortFolioGainSinceInceptionSortedData(String sortBy) {
        if (sortBy.equalsIgnoreCase("cagrAsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Double valA = 0.0;
                    Double valB = 0.0;

                    try {
                        valA = Double.parseDouble(a.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));
                        valB = Double.parseDouble(b.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return Double.compare(valB, valA);

                }

            });
        } else if (sortBy.equalsIgnoreCase("cagrDsc")) {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject a, JSONObject b) {
                    Double valA = 0.0;
                    Double valB = 0.0;

                    try {
                        valA = Double.parseDouble(a.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));
                        valB = Double.parseDouble(b.get("CAGR").toString().replace(",", "").
                                replaceAll("%", ""));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return Double.compare(valA, valB);

                }

            });
        } else {
            Collections.sort(list, new Comparator<JSONObject>() {

                public int compare(JSONObject lhs, JSONObject rhs) {


                    try {
                        return lhs.getString("ApplicantName").toLowerCase().compareTo(rhs.getString("ApplicantName").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
        portfolio_client_adapter.updateList(list);
    }

    //Inside this method we set data return for Portfolio api
    private void setData() {

        try {
            jsonObject = new JSONObject(AppApplication.portfolio_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String Status = jsonObject.optString("Status");
        if (Status.equalsIgnoreCase("True")) {
            JSONArray jsonArray = jsonObject.optJSONArray("PortfolioReturnDetail");
            setUpPortfolioListDefaultData(jsonArray);
            setApplicantGroupTitle();
            handlingTopCardViewData();
        } else {
            viewNoData.setVisibility(View.VISIBLE);
            viewTopPortfolio.setVisibility(View.GONE);
            displayServerMessage(jsonObject.optString("ServiceMSG"));
        }


    }

    //Inside this method we handle UI/Data for Top Card view
    @SuppressLint("SetTextI18n")
    private void handlingTopCardViewData() {
        try {
            if (!TextUtils.isEmpty(FragHomeClient.values)) {
                String[] values = FragHomeClient.values.split("\\|");
                market_value.setText(getString(R.string.Rs) + values[0]);
                purchase_cost.setText(getString(R.string.Rs) + values[1]);
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("PurchaseCostInnerCard")) &&
                        Utils.getConfigData(mSession).optString("PurchaseCostInnerCard").equalsIgnoreCase("Y")) {

                    tvLabelInvestment.setVisibility(View.VISIBLE);
                    purchase_cost.setVisibility(View.VISIBLE);
                } else {
                    tvLabelInvestment.setVisibility(View.GONE);
                    purchase_cost.setVisibility(View.GONE);
                }

                String input = values[2];
                boolean isFound = input.indexOf("-") != -1 ? true : false; //true
                if (isFound) {
                    input = input.substring(0, 1) + getResources().getString(R.string.Rs) + " " + input.substring(1, input.length());

                    gain.setText(input);
                } else {
                    gain.setText(getResources().getString(R.string.Rs) + " " + values[2]);

                }
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("GainInnerCard")) &&
                        Utils.getConfigData(mSession).optString("GainInnerCard").equalsIgnoreCase("Y")) {

                    tvLabelGain.setVisibility(View.VISIBLE);
                    gain.setVisibility(View.VISIBLE);
                    gain_arrow.setVisibility(View.VISIBLE);
                } else {
                    tvLabelGain.setVisibility(View.GONE);
                    gain.setVisibility(View.GONE);
                    gain_arrow.setVisibility(View.GONE);
                }
                cagr.setText(values[3] + "%");
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CAGRInnerMainCard")) &&
                        Utils.getConfigData(mSession).optString("CAGRInnerMainCard").equalsIgnoreCase("Y")) {

                    tvLabelCagr.setVisibility(View.VISIBLE);
                    cagr.setVisibility(View.VISIBLE);
                    cagr_arrow.setVisibility(View.VISIBLE);
                } else {
                    tvLabelCagr.setVisibility(View.GONE);
                    cagr.setVisibility(View.GONE);
                    cagr_arrow.setVisibility(View.GONE);
                }
                viewTopPortfolio.setVisibility(View.VISIBLE);
                viewNoData.setVisibility(View.GONE);
                if (gain.getText().toString().contains("-")) {
                    gain_arrow.setBackgroundResource(R.drawable.menu_down);
                } else {
                    gain_arrow.setBackgroundResource(R.drawable.menu_up);
                }
                if (cagr.getText().toString().contains("-")) {
                    cagr_arrow.setBackgroundResource(R.drawable.menu_down);
                } else {
                    cagr_arrow.setBackgroundResource(R.drawable.menu_up);
                }
            } else {
                displayServerMessage(getString(R.string.no_data_found));
                viewNoData.setVisibility(View.VISIBLE);
                viewTopPortfolio.setVisibility(View.GONE);

            }
        }catch (Exception e){

        }
    }

    //API PortFolio Return
    public void callPortFolioReturnApi() {
        viewTopPortfolio.setVisibility(View.GONE);
        viewNoData.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmerAnimation();
        String url = Config.Portfolio_Return;
        try {

            JSONObject jsonObject = new JSONObject();
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B")) {
                jsonObject.put("Passkey", mSession.getPassKey());
                jsonObject.put("Bid", AppConstants.APP_BID);
                jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
                jsonObject.put("OnlyMF", "Y");
                jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");
            }else{
                jsonObject.put("Passkey", mSession.getPassKey());
                jsonObject.put("Bid", AppConstants.APP_BID);
                jsonObject.put(AppConstants.CUSTOMER_ID, mCID);
                jsonObject.put(AppConstants.REQUEST_FORMAT, "Y");
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    viewTopPortfolio.setVisibility(View.VISIBLE);
                    viewNoData.setVisibility(View.GONE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    AppApplication.portfolio_data = response.toString();
                    setData();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    viewTopPortfolio.setVisibility(View.GONE);
                    viewNoData.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmerAnimation();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mApplication.showCommonDailog(mActivity, mActivity, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_asc_name) {
            String sortBy = "nameAsc";
            setPortFolioNameSortedData(sortBy);
            setFocus(btn_unfocus, btn[0]);
        } else if (id == R.id.btn_desc_name) {
            String sortBy;
            sortBy = "nameDsc";
            setPortFolioNameSortedData(sortBy);
            setFocus(btn_unfocus, btn[1]);
        } else if (id == R.id.btn_cv_filter_growth) {
            String sortBy;
            sortBy = "cvAsc";
            setPortFolioCurrentSortedData(sortBy);
            setFocus(btn_unfocus, btn[2]);
        } else if (id == R.id.btn_cv_filter_declined) {
            String sortBy;
            sortBy = "cvDsc";
            setPortFolioCurrentSortedData(sortBy);
            setFocus(btn_unfocus, btn[3]);
        } else if (id == R.id.btn_invest_raise) {
            String sortBy;
            sortBy = "investAsc";
            setPortFolioInvestmentSortedData(sortBy);
            setFocus(btn_unfocus, btn[4]);
        } else if (id == R.id.btn_invest_fall) {
            String sortBy;
            sortBy = "investDsc";
            setPortFolioInvestmentSortedData(sortBy);
            setFocus(btn_unfocus, btn[5]);
        } else if (id == R.id.btn_gain_raise) {
            String sortBy;
            sortBy = "gainAsc";
            setPortFolioGainSortedData(sortBy);
            setFocus(btn_unfocus, btn[6]);
        } else if (id == R.id.btn_gain_fall) {
            String sortBy;
            sortBy = "gainDsc";
            setPortFolioGainSortedData(sortBy);
            setFocus(btn_unfocus, btn[7]);
        } else if (id == R.id.btn_cagr_raise) {
            String sortBy;
            sortBy = "cagrAsc";
            setPortFolioGainSinceInceptionSortedData(sortBy);
            setFocus(btn_unfocus, btn[8]);
        } else if (id == R.id.btn_cagr_fall) {
            String sortBy;
            sortBy = "cagrDsc";
            setPortFolioGainSinceInceptionSortedData(sortBy);
            setFocus(btn_unfocus, btn[9]);
        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setTextColor(getResources().getColor(R.color.darkSecondaryBtnTextColor));
        } else {
            btn_unfocus.setTextColor(getResources().getColor(R.color.lightSecondaryBtnTextColor));
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));
        } else {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_bg_secondary));
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
        }

        this.btn_unfocus = btn_focus;
    }


}
