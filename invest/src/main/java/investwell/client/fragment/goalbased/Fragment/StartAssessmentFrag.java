package investwell.client.fragment.goalbased.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;

public class StartAssessmentFrag extends Fragment {


    private MainActivity mActivity;
    private AppApplication mApplication;
    private ToolbarFragment fragToolBar;
private AppSession mSession;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_risk_assessment, container, false);
        mActivity = (MainActivity) getActivity();
        setUpToolBar();


        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getArguments();
                mActivity.displayViewOther(62, bundle);
            }
        });

        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_risk_profile_start), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(mActivity, ContextCompat.getColor(mActivity,R.color.colorPrimary));

            }
        }
    }

}
