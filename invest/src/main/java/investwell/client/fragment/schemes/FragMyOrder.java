package investwell.client.fragment.schemes;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;


public class FragMyOrder extends Fragment implements View.OnClickListener {
    private String ProspectMyOrderResult;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private String mUCC_Code = "";
private ToolbarFragment fragToolBar;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_my_order, container, false);
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        mApplication = (AppApplication) mActivity.getApplication();
        view.findViewById(R.id.lumpsum_btn).setOnClickListener(this);

        view.findViewById(R.id.sip_btn).setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
            /*  relToolBar.setVisibility(View.VISIBLE);*/
        } else {
            mUCC_Code = mSession.getUCC_CODE();
            /*relToolBar.setVisibility(View.GONE);*/
        }
setUpToolBar();

        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.main_nav_title_my_orders),true, false, false, false,false,false,false,"");
        }
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        int id = view.getId();
        if (id == R.id.sip_btn) {
            bundle.putString("type", "sip_order");
            bundle.putString("ucc_code", mUCC_Code);
            mActivity.displayViewOther(18, bundle);
        } else if (id == R.id.lumpsum_btn) {
            bundle.putString("type", "lumpsum_order");
            bundle.putString("ucc_code", mUCC_Code);
            mActivity.displayViewOther(18, bundle);
        }
    }
}
