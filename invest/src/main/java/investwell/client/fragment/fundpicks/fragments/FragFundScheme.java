package investwell.client.fragment.fundpicks.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;
import investwell.client.fragment.fundpicks.adapter.FundSchemeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragFundScheme extends Fragment {
    private FundSchemeAdapter mFundSchemeAdapter;
    private Context context;
    public List<JSONObject> mCallLogsList;
    private JSONObject mObject;

    public static FragFundScheme newInstance() {
        return new FragFundScheme();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_fund_scheme, container, false);

        mCallLogsList = new ArrayList<>();
        Bundle bundle = getArguments();
        try {
            mObject = new JSONObject(bundle.getString("data"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = view.findViewById(R.id.fund_scheme_recyclier);
        mFundSchemeAdapter = new FundSchemeAdapter(new ArrayList<JSONObject>(), getActivity(), FragFundScheme.this);
        recyclerView.setAdapter(mFundSchemeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setLayoutManager(new LinearLayoutManager(FragFundScheme.this));

        dataset();
        setDisclaimerData(view);
        return view;
    }
    @SuppressLint("SetTextI18n")
    private void setDisclaimerData(View view){
        TextView mDiscDesc=view.findViewById(R.id.tv_disc);

        mDiscDesc.setText(getResources().getString(R.string.disc_content_top_scheme)+""+getResources().getString(R.string.top_scheme_desc_list));
    }
    public void dataset() {
        ArrayList<JSONObject> mAdapterItemData = new ArrayList<>();
        try {
            JSONArray mSchemeCatArray = mObject.getJSONArray("FundPickSubCategory");
            for (int i = 0; i < mSchemeCatArray.length(); i++) {
                JSONObject object = mSchemeCatArray.getJSONObject(i);
                mAdapterItemData.add(object);
            }

            mFundSchemeAdapter.updatelist(mAdapterItemData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
