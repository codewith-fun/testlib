package investwell.client.fragment.mysip;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomTextViewBold;

public class FragSipStpSwpDetails extends Fragment {
    private AppSession mSession;
    private MainActivity mActivity;
    private ToolbarFragment fragToolBar;

    private AdapterSipDetail sipDetailAdapter;
    private ArrayList<JSONObject> sipSchemeList;
    private RecyclerView rvSchemeDetails;
    private CustomTextViewBold tvNoRecordFound;
    private Bundle bundle;
    private String investorName = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity = (MainActivity) getActivity();
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_sip_stp_swp_details, container, false);

        bundle = getArguments();
        initializer(view);
        setUpToolBar();
        return view;
    }


    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(investorName + " " + mSession.getSipType(), true, false, false, false, false, false, false, "");
        }
    }


    private void initializer(View view) {

        if (bundle != null && bundle.containsKey("data")) {
            try {
                sipSchemeList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(bundle.getString("data"));
                JSONArray jsonElements = jsonObject.optJSONArray("SchemeDetails");
                investorName = !TextUtils.isEmpty(jsonObject.optString("InvestorName")) ? jsonObject.optString("InvestorName") : "";
                for (int i = 0; i < jsonElements.length(); i++) {
                    JSONObject jsonObject1 = jsonElements.optJSONObject(i);
                    sipSchemeList.add(jsonObject1);
                }

            } catch (Exception e) {

            }
        }

        rvSchemeDetails = view.findViewById(R.id.rv_scheme_details);
        tvNoRecordFound = view.findViewById(R.id.tv_no_record_found);
        sipDetailAdapter = new AdapterSipDetail(mActivity, sipSchemeList, bundle.getString("comingFrom"));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        rvSchemeDetails.setLayoutManager(mLayoutManager);
        rvSchemeDetails.setItemAnimator(new DefaultItemAnimator());
        if (sipSchemeList.size() <= 0) {
            rvSchemeDetails.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        } else {
            rvSchemeDetails.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
        }
        rvSchemeDetails.setAdapter(sipDetailAdapter);


    }


}
