package investwell.client.fragment.taxsaver;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.client.fragment.others.ToolbarFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragTaxSavingDetail extends Fragment {

    private TextView mApplicantName, mSchemeName;
    private Bundle bundle;
    private RecyclerView mDetailRecycle;
    private AdapterTaxSavingDetail taxSavingDetailAdapter;

    private ToolbarFragment fragToolBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_tax_saving_detail, container, false);
        bundle = getArguments();
        setUpToolBar();
        mApplicantName = view.findViewById(R.id.investor_name);
        mSchemeName = view.findViewById(R.id.colorBlue);
        mDetailRecycle = view.findViewById(R.id.detail_recycle);
        mDetailRecycle.setHasFixedSize(true);
        mDetailRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mDetailRecycle.addItemDecoration(new DividerItemDecoration(mDetailRecycle.getContext(), DividerItemDecoration.VERTICAL));

        taxSavingDetailAdapter = new AdapterTaxSavingDetail(getActivity(), new ArrayList<JSONObject>());
        mDetailRecycle.setAdapter(taxSavingDetailAdapter);


        if (bundle != null) {

            mApplicantName.setText(bundle.getString("applicant_name"));
            mSchemeName.setText(bundle.getString("colorBlue"));
            try {
                ArrayList<JSONObject> list = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(bundle.getString("data"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(jsonObject);
                }
                taxSavingDetailAdapter.updateList(list);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_tax_saving_details), true, false, false, false, false, false, false, "");
        }
    }
}
