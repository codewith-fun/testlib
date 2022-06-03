package investwell.client.fragment.others;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.adapter.SchemeCategoryAdapter;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooserSchemeTypeFragment extends Fragment implements View.OnClickListener, SchemeCategoryAdapter.SchemeCategoryListener {

    private TextView tvCategoryTitle, tvSchemeTypeTitle;
    private Button btnEquity, btnDebt, btnFmp, btnGrowth, btnDividend;
    private View view;
    private ChooserTypeCallback chooserTypeCallback;
    private Button[] btnSchemeType = new Button[2];
    private Button[] btnCategory = new Button[3];
    private Button btn_unfocus_scheme, btn_unfocus_category;
    private int[] btn_category_id = {R.id.btn_equity, R.id.btn_debt, R.id.btn_fmp};
    private int[] btn_scheme_id = {R.id.btn_growth, R.id.btn_dividend};
    private AppSession appSession;
    private AppApplication mApplication;
    private JSONArray mTopSchemeList = new JSONArray();
    private ArrayList<JSONObject> list = new ArrayList<>();
    private SchemeCategoryAdapter mSchemeCategoryAdapter;
    private RecyclerView rvSchemeCategory;

    public ChooserSchemeTypeFragment() {
        // Required empty public constructor
    }

    public void setChooserTypeCallBack(ChooserTypeCallback chooserTypeCallBack) {
        this.chooserTypeCallback = chooserTypeCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_chooser_scheme_type, container, false);
        setInitializer(view);
        callSchemeCategoryApi();
        setAdapter();
        setListener();
        return view;
    }

    private void setInitializer(View view) {
        appSession = AppSession.getInstance(getActivity());
        mApplication = (AppApplication) getActivity().getApplication();
        tvCategoryTitle = view.findViewById(R.id.tv_cat_chooser_header);
        tvSchemeTypeTitle = view.findViewById(R.id.tv_scheme_chooser_header);
        btnEquity = view.findViewById(R.id.btn_equity);
        btnDebt = view.findViewById(R.id.btn_debt);
        btnFmp = view.findViewById(R.id.btn_fmp);
        btnGrowth = view.findViewById(R.id.btn_growth);
        btnDividend = view.findViewById(R.id.btn_dividend);
        rvSchemeCategory = view.findViewById(R.id.rv_scheme_category);
        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btnGrowth.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btnGrowth.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btnGrowth.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btnGrowth.setTextColor(getResources().getColor(R.color.lightPrimaryTextColor));

        }

    }

    private void setAdapter() {
        mSchemeCategoryAdapter = new SchemeCategoryAdapter(getContext(), new ArrayList<JSONObject>(), this);
        rvSchemeCategory.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvSchemeCategory.setAdapter(mSchemeCategoryAdapter);
    }

    private void setListener() {
        for (int i = 0; i < btnSchemeType.length; i++) {
            btnSchemeType[i] = view.findViewById(btn_scheme_id[i]);
            if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                    Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                btnSchemeType[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
            } else {
                btnSchemeType[i].setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));

            }
            btnSchemeType[i].setOnClickListener(this);
        }
        btn_unfocus_scheme = btnSchemeType[0];

        for (int i = 0; i < btnCategory.length; i++) {
            btnCategory[i] = view.findViewById(btn_category_id[i]);
            if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                    Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                btnCategory[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
            } else {
                btnCategory[i].setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));

            }
            btnCategory[i].setOnClickListener(this);
        }
        btn_unfocus_category = btnCategory[0];
        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btnSchemeType[0].setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
            btnSchemeType[0].setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btnSchemeType[0].setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
            btnSchemeType[0].setTextColor(getResources().getColor(R.color.colorWhite));
        }


    }

    public void setUpChooserCategoryElement(String title, String btnCategoryPrimary, String btnCategorySecondary,
                                            String btnCategoryTertiary, boolean isShowingPrimaryCategoryBtn, boolean isShowingSecondaryCategoryBtn,
                                            boolean isShowingTertiaryCategoryBtn) {

        if (isShowingPrimaryCategoryBtn) {
            btnEquity.setVisibility(View.VISIBLE);
        } else {
            btnEquity.setVisibility(View.GONE);
        }


        if (isShowingSecondaryCategoryBtn) {
            btnDebt.setVisibility(View.VISIBLE);
        } else {
            btnDebt.setVisibility(View.GONE);
        }

        if (isShowingTertiaryCategoryBtn) {
            btnFmp.setVisibility(View.VISIBLE);
        } else {
            btnFmp.setVisibility(View.GONE);
        }
        tvCategoryTitle.setText(!TextUtils.isEmpty(title) ? title : "");
        btnEquity.setText(!TextUtils.isEmpty(btnCategoryPrimary) ? btnCategoryPrimary : "");
        btnDebt.setText(!TextUtils.isEmpty(btnCategorySecondary) ? btnCategorySecondary : "");
        btnFmp.setText(!TextUtils.isEmpty(btnCategoryTertiary) ? btnCategoryTertiary : "");
    }

    public void setUpChooserSchemeTypeElement(String title, String btnPrimarySchemeType, String btnSecondarySchemeType,
                                              boolean isShowingPrimarySchemeBtn, boolean isShowingSecondarySchemeBtn) {


        if (isShowingPrimarySchemeBtn) {
            btnGrowth.setVisibility(View.VISIBLE);
        } else {
            btnGrowth.setVisibility(View.GONE);
        }


        if (isShowingSecondarySchemeBtn) {
            btnDividend.setVisibility(View.VISIBLE);
        } else {
            btnDividend.setVisibility(View.GONE);
        }

        tvSchemeTypeTitle.setText(!TextUtils.isEmpty(title) ? title : "");
        btnGrowth.setText(!TextUtils.isEmpty(btnPrimarySchemeType) ? btnPrimarySchemeType : "");
        btnDividend.setText(!TextUtils.isEmpty(btnSecondarySchemeType) ? btnSecondarySchemeType : "");

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_growth) {
            chooserTypeCallback.onSchemeChosen(btnGrowth);
            setFocusScheme(btn_unfocus_scheme, btnSchemeType[0]);
        } else if (id == R.id.btn_dividend) {
            chooserTypeCallback.onSchemeChosen(btnDividend);
            setFocusScheme(btn_unfocus_scheme, btnSchemeType[1]);
        } else if (id == R.id.btn_equity) {
            chooserTypeCallback.onSchemeChosen(btnEquity);
            setFocusCategory(btn_unfocus_category, btnCategory[0]);
        } else if (id == R.id.btn_debt) {
            chooserTypeCallback.onSchemeChosen(btnDebt);
            setFocusCategory(btn_unfocus_category, btnCategory[1]);
        } else if (id == R.id.btn_fmp) {
            chooserTypeCallback.onSchemeChosen(btnFmp);
            setFocusCategory(btn_unfocus_category, btnCategory[2]);
        }
    }

    @Override
    public void onSchemeCategoryClick(int position) {
        JSONObject object = null;
        if (list.size() > 0) {
            object = list.get(position);
            appSession.setSchemeCategory(object.optString("ItemValue"));
            chooserTypeCallback.onSchemeCategoryChosen(position);
        }

       /* switch (position) {
            case 0:
                appSession.setSchemeCategory("D");
                chooserTypeCallback.onSchemeCategoryChosen(position);
                break;
            case 1:
                appSession.setSchemeCategory("E");
                chooserTypeCallback.onSchemeCategoryChosen(position);
                break;

            case 2:
                appSession.setSchemeCategory("H");
                chooserTypeCallback.onSchemeCategoryChosen(position);
                break;
            case 3:
                appSession.setSchemeCategory("O");
                chooserTypeCallback.onSchemeCategoryChosen(position);
                break;

        }*/
    }

    public interface ChooserTypeCallback {
        void onSchemeChosen(View view);

        void onSchemeCategoryChosen(int i);
    }

    private void setFocusScheme(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setTextColor(getResources().getColor(R.color.lightPrimaryTextColor));
        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));
        } else {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
        }
        this.btn_unfocus_scheme = btn_focus;
    }

    private void setFocusCategory(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setTextColor(getResources().getColor(R.color.lightPrimaryTextColor));
        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));
        } else {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
        }
        this.btn_unfocus_category = btn_focus;
    }

    /***********************Category API *************************************/
    private void callSchemeCategoryApi() {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        String URL = Config.Category_List;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, appSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    try {
                        if (response.optString("Status").equals("True")) {
                            mTopSchemeList = response.getJSONArray("BroadCategoryList");
                            for (int i = 0; i < mTopSchemeList.length(); i++) {
                                JSONObject object2 = mTopSchemeList.getJSONObject(i);
                                list.add(object2);
                            }
                            mSchemeCategoryAdapter.updateList(list);
                        } else {

                            mApplication.showSnackBar(btnGrowth, response.optString("ServiceMSG"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mBar.dismiss();
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)

                        mApplication.showSnackBar(btnGrowth, getResources().getString(R.string.no_internet));

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

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
