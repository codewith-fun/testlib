package investwell.client.fragment.InvestInExistingSchemes.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.iw.acceleratordemo.R;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;

public class FragExistingHome extends Fragment implements View.OnClickListener {


    private MainActivity mActivity;
    private View view;
    private ToolbarFragment fragToolBar;
    private Bundle bundle;
    private RelativeLayout mRlInvestment;

    


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
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_frag_existing_home, container, false);
        mActivity = (MainActivity) getActivity();
        bundle = getArguments();
        setInitializer();
        setUpToolBar();
//****///
        return view;
    }

    private void setInitializer() {
        mRlInvestment = view.findViewById(R.id.RlInvestment);
        mRlInvestment.setOnClickListener(this);
        mActivity.setMainVisibility(this, null);

    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_invest_existing_scheme), true, false, false, false, false, false, false, "");

        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.RlInvestment) {
            mActivity.displayViewOther(106, bundle);
        }
    }



}
