package investwell.client.fragment.foliolookup;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.adapter.FolioSchemeAdapter;
import investwell.client.fragment.others.ToolbarFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragFolioLookupDetails extends Fragment {
    private Bundle bundle;
    private TextView mTvHeader, mTvSchemeName, mTvInvestorValue, mTvPanValue, mTvFolioValue,
            mTvHoldingsValue, mTvBankName, mTvBankAccount, mTvBankAccountType, mTVBankIFSC;
    private RecyclerView mSchemeRecycle;
    private FolioSchemeAdapter folioSchemeAdapter;
    private ToolbarFragment toolbarFragment;
    private JSONObject mFolioJsonObject;
    private LinearLayout mLinerBankDetails;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.frag_folio_lookup_details, container, false);
        bundle = getArguments();
        setUpToolBar();
        /*mTvHeader = view.findViewById(R.id.tvheader);*/
        mTvInvestorValue = view.findViewById(R.id.tv_investor_value);
        mTvPanValue = view.findViewById(R.id.tv_pan_value);
        mTvFolioValue = view.findViewById(R.id.tv_folio_value);
        mTvHoldingsValue = view.findViewById(R.id.tv_holdings_value);
        mSchemeRecycle = view.findViewById(R.id.schemeRecycle);

        mLinerBankDetails = view.findViewById(R.id.linerBankLevel);
        mTvBankName = view.findViewById(R.id.tvBankName);
        mTvBankAccount = view.findViewById(R.id.tvAccountNo);
        mTvBankAccountType = view.findViewById(R.id.tvAccountType);
        mTVBankIFSC = view.findViewById(R.id.tvIFSC);
        mTvSchemeName = view.findViewById(R.id.tv_scheme_value);

        try {
            Bundle bundle = getArguments();
            if (bundle != null && bundle.containsKey("data")) {
                mFolioJsonObject = new JSONObject(bundle.getString("data"));
            }
        } catch (Exception e) {

        }

        /*mTvHeader.setText(bundle.getString("applicant_name") + "'s Folio Details");*/
        mTvInvestorValue.setText(bundle.getString("applicant_name"));
        mTvPanValue.setText(!TextUtils.isEmpty(bundle.getString("pan")) ? bundle.getString("pan") : "N/A");
        mTvFolioValue.setText(!TextUtils.isEmpty(bundle.getString("folio_no")) ? bundle.getString("folio_no") : "N/A");
        mTvHoldingsValue.setText(!TextUtils.isEmpty(bundle.getString("holding")) ? bundle.getString("holding") : "N/A");

        mTvSchemeName.setText(mFolioJsonObject.optString("AMCName"));
        if (mFolioJsonObject.optString("BankName").equalsIgnoreCase("") || mFolioJsonObject.optString("BankName").equalsIgnoreCase("null")) {
            mLinerBankDetails.setVisibility(View.GONE);
        } else {
            mTvBankName.setText(mFolioJsonObject.optString("BankName"));
            mTvBankAccount.setText(mFolioJsonObject.optString("AccountNo"));
            mTVBankIFSC.setText(mFolioJsonObject.optString("IFSC"));
            String accountType = mFolioJsonObject.optString("accountType");
            if (accountType.equalsIgnoreCase("") || accountType.equalsIgnoreCase("null")) {
                mTvBankAccountType.setText(getString(R.string.not_available));
            } else {
                mTvBankAccountType.setText(accountType);
            }
        }


        mSchemeRecycle.setHasFixedSize(true);
        mSchemeRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        folioSchemeAdapter = new FolioSchemeAdapter(getActivity(), new ArrayList<JSONObject>(), bundle.getString("folio_no"));
        mSchemeRecycle.setAdapter(folioSchemeAdapter);
        try {
            JSONArray jsonArray = new JSONArray(bundle.getString("all_data"));
            ArrayList<JSONObject> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.optJSONObject(i);
                list.add(jsonObject);
            }
            folioSchemeAdapter.updateList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setUpToolBar() {
        toolbarFragment
                = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment
                != null) {
            toolbarFragment
                    .setUpToolBar(getResources().getString(R.string.main_nav_title_my_folio_schemes), true, false, false, false, false, false, false, "");
        }
    }
}