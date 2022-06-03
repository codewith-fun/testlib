package investwell.client.fragment.portfolio;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PortFolioSchemeListFragment extends Fragment {

    public PortFolioSchemeListFragment() {
        // Required empty public constructor
    }

    private AppApplication mApplication;
    private MainActivity mActivity;

    private View view;
    private int positionOfView = 0;
    private String nameOfTheView = "", mCID = "";
    private AdapterPortfolioDetail portfolioDetailAdapter;

    private RecyclerView mRvPortfolioSchemeList;

    private ArrayList<JSONObject> portfolioSchemeList1;
    private ArrayList<JSONObject> portfolioSchemeList2;
    private ArrayList<JSONObject> portfolioSchemeList3;
    private ArrayList<JSONObject> portfolioSchemeList4;
    private ArrayList<JSONObject> portfolioSchemeList5;

    public static PortFolioSchemeListFragment newInstance(int position, String sectionName, String portfolioList) {
        PortFolioSchemeListFragment fragment = new PortFolioSchemeListFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("sectionName", sectionName);
        args.putString("list", portfolioList);
        fragment.setArguments(args);
        return fragment;
    }

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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_port_folio_scheme_list, container, false);

        if (getArguments() != null) {
            positionOfView = getArguments().getInt("position");
            nameOfTheView = getArguments().getString("sectionName");
            mCID = getArguments().getString("list");
        }
        initializer();
        setUpSectionsAdapter(view);
        return view;
    }

    private void initializer() {
        mApplication = (AppApplication) mActivity.getApplication();
        mRvPortfolioSchemeList = view.findViewById(R.id.rv_portfolio_scheme_list);
        portfolioSchemeList1 = new ArrayList<>();
        portfolioSchemeList2 = new ArrayList<>();
        portfolioSchemeList3 = new ArrayList<>();
        portfolioSchemeList4 = new ArrayList<>();
        portfolioSchemeList5 = new ArrayList<>();
    }


    private void setUpListPartition() {
        String schemeCategory = "";
        if (mApplication.getPortfolioSchemeList().size() > 0) {
            Log.e("Application List ", mApplication.getPortfolioSchemeList().toString());
            for (int i = 0; i < mApplication.portFolioList.size(); i++) {
                JSONObject object = null;
                object = mApplication.portFolioList.get(i);
                schemeCategory = object.optString("Objective");
                if (schemeCategory.contains("Equity Shares")) {
                    portfolioSchemeList1.add(object);
                } else if (schemeCategory.contains("Equity MF")) {
                    portfolioSchemeList2.add(object);
                } else if (schemeCategory.contains("Hybrid MF")) {
                    portfolioSchemeList3.add(object);
                } else if (schemeCategory.contains("Debt FD")) {
                    portfolioSchemeList4.add(object);
                } else if (schemeCategory.contains("Debt MF")) {
                    portfolioSchemeList5.add(object);

                }
            }
ArrayList<JSONObject> newList=new ArrayList<>();
            if (Utils.tabText.contains("Equity Shares")) {
                portfolioDetailAdapter.updateList(portfolioSchemeList1, mApplication.applicantName);
            } else if (Utils.tabText.contains("Equity")) {
                portfolioDetailAdapter.updateList(portfolioSchemeList2, mApplication.applicantName);
            } else if (Utils.tabText.contains("Hybrid")) {
                portfolioDetailAdapter.updateList(portfolioSchemeList3, mApplication.applicantName);
            } else if (Utils.tabText.contains("Debt FD")) {
                portfolioDetailAdapter.updateList(portfolioSchemeList4, mApplication.applicantName);
            } else if (Utils.tabText.contains("Debt")) {
                portfolioDetailAdapter.updateList(portfolioSchemeList5, mApplication.applicantName);

            }

        } else {

            portfolioDetailAdapter.updateList(new ArrayList<JSONObject>(), mApplication.applicantName);

        }
    }

    private void setUpSectionsAdapter(View view) {
        portfolioDetailAdapter = new AdapterPortfolioDetail(mActivity, new ArrayList<JSONObject>(), mCID);
        mRvPortfolioSchemeList.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRvPortfolioSchemeList.setItemAnimator(new DefaultItemAnimator());
        mRvPortfolioSchemeList.setNestedScrollingEnabled(false);
        mRvPortfolioSchemeList.setAdapter(portfolioDetailAdapter);
        setUpListPartition();
    }
}
