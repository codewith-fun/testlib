package investwell.client.fragment.nav.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.flavourTypeThree.NavSchemeSearchAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

public class FragNavScheme extends Fragment implements View.OnClickListener {

    public List<JSONObject> mSelectedCartsList, mAllComingList;
    public TextView mTvCart;
    private Bundle mBundle;
    private ImageView mIvScheme;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ViewPager pager;
    private TabLayout tabs;
    private String mSchemeName, mSchemeCode, mUrl;
    private TextView mTvSchemeName;
    public ToolbarFragment toolbarFragment;
    private String search_value = "";
    private RecyclerView rvSchemes;
    private ArrayList<JSONObject> list;
    public NavSchemeSearchAdapter mAdapter;
    private TextInputEditText etSearchSchemes;
    private TextInputLayout tilSearchSchemes;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateCart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_navscheme_item, container, false);
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        setUpToolBar();
        mApplication = (AppApplication) mActivity.getApplication();
        pager = view.findViewById(R.id.pager_nav);
        tabs = view.findViewById(R.id.tl_nav);
        mIvScheme = view.findViewById(R.id.iv_scheme);
        pager.setOffscreenPageLimit(3);
        tilSearchSchemes = view.findViewById(R.id.til_search);
        etSearchSchemes = view.findViewById(R.id.et_search_scheme);
        rvSchemes = view.findViewById(R.id.rvSchemeSearch);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        mTvSchemeName = view.findViewById(R.id.tv_SchemeName);
        mSelectedCartsList = new ArrayList<>();
        mAllComingList = new ArrayList<>();
//        back_arrow = view.findViewById(R.id.back_arrow);
        mBundle = getArguments();
        if (mBundle != null) {
            mSchemeName = mBundle.getString("schemename");
            mSchemeCode = mBundle.getString("schemecode");
            mUrl = mBundle.getString("schemeicon");
        }

        if (mUrl != null) {
            Picasso.get().load(mUrl).placeholder(R.mipmap.tranparent)
                    .error(R.mipmap.tranparent).into(mIvScheme);
        }
        mTvSchemeName.setText(mSchemeName);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(mActivity, getChildFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);


        etSearchSchemes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    view.findViewById(R.id.textView15).setVisibility(View.GONE);
                    view.findViewById(R.id.textView16).setVisibility(View.GONE);
                    view.findViewById(R.id.v_div_2).setVisibility(View.GONE);
                    view.findViewById(R.id.v_div1).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.textView15).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.textView16).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.v_div_2).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.v_div1).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                search_value = editable.toString();
                //  mSession.set_serachValue(search_value);

                if (!search_value.isEmpty()) {
                    ArrayList<JSONObject> new_list = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).optString("SchemeName").toUpperCase().contains(search_value.toUpperCase())) {
                            new_list.add(list.get(i));
                            rvSchemes.setVisibility(View.VISIBLE);
                            pager.setVisibility(View.GONE);
                            tabs.setVisibility(View.GONE);
                        }
                    }
                    mAdapter.updatelist(new_list);

                } else {
                    rvSchemes.setVisibility(View.GONE);
                    pager.setVisibility(View.VISIBLE);
                    tabs.setVisibility(View.VISIBLE);

                }


            }
        });

        rvSchemes.setHasFixedSize(true);
        rvSchemes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new NavSchemeSearchAdapter(getActivity(), new ArrayList<JSONObject>(), FragNavScheme.this);
        rvSchemes.setAdapter(mAdapter);
        getSchemesData();
        view.findViewById(R.id.fl_cart).setOnClickListener(this);

        toolbarFragment.updateCart(true);

        return view;
    }

    public void getSchemesData() {


        Map<String, String> params = new HashMap<String, String>();

        params.put("Bid", AppConstants.APP_BID);
        params.put("Passkey", mSession.getPassKey());
        params.put("ObjCode", "All");
        params.put("Fcode", mSchemeCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.GET_NAV, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                list = new ArrayList<>();

                if (response.optBoolean("Status")) {
                    JSONArray araArray = null;
                    try {
                        araArray = response.getJSONArray("LatestNAVDetail");
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
                    //  Toast.makeText(getActivity(), response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {

                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("SearchbyNAVAddToCart")) &&
                Utils.getConfigData(mSession).optString("SearchbyNAVAddToCart").equalsIgnoreCase("Y")) {
            if (toolbarFragment != null) {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_select_scheme), true, false, false, false, false, true, false, "");

            }
        } else {
            if (toolbarFragment != null) {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_select_scheme), true, false, false, false, false, false, false, "");

            }
        }
    }

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
                    //mSession.setGetSchemeData(mAllComingList.toString());
                    mActivity.displayViewOther(4, null);
                }

            } catch (Exception e) {

            }
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
                for (int i = 0; i < jsonArray.length(); i++) {
                    mSelectedCartsList.add(jsonArray.getJSONObject(i));
                }
                mTvCart.setText("" + jsonArray.length());


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("schemecode", mSchemeCode);
            if (position == 0) {
                FragNavItemSchemes schemes = new FragNavItemSchemes();
                bundle.putString("objcode", "E");
                schemes.setArguments(bundle);
                return schemes;
            } else if (position == 1) {
                bundle.putString("objcode", "D");
                FragNavItemSchemes schemes = new FragNavItemSchemes();
                schemes.setArguments(bundle);
                return schemes;
            } else if (position == 2) {
                bundle.putString("objcode", "H");
                FragNavItemSchemes schemes = new FragNavItemSchemes();
                schemes.setArguments(bundle);
                return schemes;
            }
            return null;
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return 3;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:

                    return mContext.getString(R.string.new_purcahse_form_txt_Equity);
                case 1:
                    return mContext.getString(R.string.new_purcahse_form_txt_Debt);
                case 2:
                    return mContext.getString(R.string.txt_Hybrid);

                default:
                    return null;
            }
        }

    }

}
