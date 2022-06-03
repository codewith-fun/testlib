package investwell.client.fragment.transferholding;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;

public class FragTransferHoldingIntro extends Fragment {
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    private ToolbarFragment fragToolBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(getActivity());
            mApplication = (AppApplication) getActivity().getApplication();
            this.mActivity.setMainVisibility(this,null);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_transfer_holding_intro, container, false);

        setUpToolBar();

        view.findViewById(R.id.submit).setOnClickListener(view1 -> {
            if (AppSession.getInstance(getActivity()).getLoginType().equals("Prospects")) {
                Bundle bundle = new Bundle();
                bundle.putString("ucc_code", mSession.getUCC_CODE());
                mActivity.displayViewOther(51, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("type", "transfer_holding");
                mActivity.displayViewOther(36, bundle);
            }

        });


        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(mSession.getTHolding(), true, false, false, false, false, false, false,"");
        }
    }
}
