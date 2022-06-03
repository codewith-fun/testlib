package investwell.client.fragment.CASUpload;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.Utils;


public class FragPreCASUpload extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private ToolbarFragment fragToolBar;
    private TextView mTvUploadCas;
    private AppSession mSession;
    private TextView tvKarvy, tvCams;
    private AppApplication appApplication;
    private RelativeLayout rlCas;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            appApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_pre_casupload, container, false);
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(getActivity());
        tvKarvy = view.findViewById(R.id.tv_carvy_url);
        rlCas = view.findViewById(R.id.rl_cas);
        tvCams = view.findViewById(R.id.tv_cams_url);
        view.findViewById(R.id.btnContinue).setOnClickListener(this);
        mTvUploadCas = view.findViewById(R.id.tvupload_cas);
        mTvUploadCas.setText(getString(R.string.txt_upload_cas1) + " " + Utils.getConfigData(mSession).optString("Email") +","+ " "+getString(R.string.txt_upload_cas2));
        setUpToolBar();
        setUpUiVisibility();
        return view;
    }

    private void setUpUiVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CAMSURL"))) {

            tvCams.setText(Utils.getConfigData(mSession).optString("CAMSURL"));
            tvCams.setVisibility(View.VISIBLE);
        } else {
            tvCams.setVisibility(View.GONE);

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("KARVYURL"))) {

            tvKarvy.setText(Utils.getConfigData(mSession).optString("KARVYURL"));
            tvKarvy.setVisibility(View.VISIBLE);
        } else {
            tvKarvy.setVisibility(View.GONE);

        }
      /*  if (mSession.getHasLoging() && (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("UploadExistingCAS"))) &&
                Utils.getConfigData(mSession).optString("UploadExistingCAS").equalsIgnoreCase("Y")) {
            rlCas.setVisibility(View.VISIBLE);
        } else {
            rlCas.setVisibility(View.GONE);
            appApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.Error), getString(R.string.user_not_login_and_try_create_goal), "message", false, true);

        }*/
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(mSession.getCas(), true, false, false, false, false, false, false, "");

        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnContinue) {
            if (mSession.getHasLoging()) {
                mActivity.displayViewOther(103, null);
            } else {
                appApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.Error), getString(R.string.user_not_login_and_try_create_goal), "message", false, true);
            }
        }
    }
}
