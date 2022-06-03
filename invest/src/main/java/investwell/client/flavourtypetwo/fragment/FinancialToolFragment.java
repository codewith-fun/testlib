package investwell.client.flavourtypetwo.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.FinancialToolAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.GridSpacingItemDecoration;
import investwell.utils.Utils;
import investwell.utils.model.FinancialTools;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinancialToolFragment extends Fragment implements View.OnClickListener, FinancialToolAdapter.FinancialToolListener, ToolbarFragment.ToolbarCallback {

    public FinancialToolFragment() {
        // Required empty public constructor
    }

    private View view;
    private FinancialToolAdapter financialToolAdapter;
    private List<FinancialTools> financialToolsList;
    private RecyclerView rvFinancialTools;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession=AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this,null);
        }
        mActivity.updateCart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_financial_tool, container, false);
        initializer();
        setUpToolBar();
        setFinancialToolAdapter();
        return view;
    }

    private void initializer() {
        financialToolsList = new ArrayList<>();
        rvFinancialTools = view.findViewById(R.id.rv_financial_tools);
    }
    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.dasbboard_financial_tools_title_txt), true, false, false, false, false, false, false, "");
            fragToolBar.setCallback(this);
        }
    }
    /*******************************************
     * Method used to set dynamic financial items
     *******************************************/

    private void setFinancialToolAdapter() {
        financialToolAdapter = new FinancialToolAdapter(mActivity, financialToolsList, this);
        rvFinancialTools.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rvFinancialTools.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        rvFinancialTools.setItemAnimator(new DefaultItemAnimator());
        rvFinancialTools.setNestedScrollingEnabled(false);
        rvFinancialTools.setAdapter(financialToolAdapter);
        prepareFinancialTools();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onToolsClick(int position) {
        switch (position) {
            case 0:
                mActivity.displayFinancialCalculators(10, null);
                break;
            case 1:
                mActivity.displayFinancialCalculators(3, null);

                break;
            case 2:
                mActivity.displayFinancialCalculators(5, null);
                break;
            case 3:
                mActivity.displayFinancialCalculators(7, null);

                break;
            case 4:
                mActivity.displayFinancialCalculators(8, null);
                break;
            case 5:
                mActivity.displayFinancialCalculators(6, null);

                break;


        }
    }

    /****************************************Frag
     * Converting dp to pixel
     ****************************************/
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /*******************************************
     * Method contains data for investment route items
     *******************************************/
    private void prepareFinancialTools() {
        int[] covers;
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
            covers = new int[]{
                    R.mipmap.ic_calculator_lumpsum_1a,
                    R.mipmap.ic_calculator_sip_1a,
                    R.mipmap.ic_calculator_cost_delay_sip_1a,
                    R.mipmap.ic_calculator_education_1a,
                    R.mipmap.ic_calculator_marriage_1a,
                    R.mipmap.ic_calculator_retirement_1a};
        } else {
            covers = new int[]{
                    R.mipmap.ic_calculator_lumpsum,
                    R.mipmap.ic_calculator_sip,
                    R.mipmap.ic_calculator_cost_delay_sip,
                    R.mipmap.ic_calculator_education,
                    R.mipmap.ic_calculator_marriage,
                    R.mipmap.ic_calculator_retirement};
        }

        FinancialTools a = new FinancialTools(covers[0], getResources().getString(R.string.dashboard_finance_tool_lumpsum));
        financialToolsList.add(a);

        a = new FinancialTools(covers[1], getResources().getString(R.string.dashboard_finance_tool_sip_cal));
        financialToolsList.add(a);

        a = new FinancialTools(covers[2], getResources().getString(R.string.dashboard_finance_tool_cost_sip));
        financialToolsList.add(a);

        a = new FinancialTools(covers[3], getResources().getString(R.string.dashboard_finance_tool_education_cal));
        financialToolsList.add(a);

        a = new FinancialTools(covers[4], getResources().getString(R.string.dashboard_finance_tool_marriage_cal));
        financialToolsList.add(a);

        a = new FinancialTools(covers[5], getResources().getString(R.string.dashboard_finance_tool_retirement_cal));
        financialToolsList.add(a);


        financialToolAdapter.notifyDataSetChanged();
    }

    @Override
    public void onToolbarItemClick(View view) {

    }
}
