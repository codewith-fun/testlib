package investwell.client.fragment.user_info;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;
import investwell.utils.Utils;


public class ActiveStatus extends Fragment {
    private MainActivity mActivity;
    TextView TvDetail;
    private String appAddress = "";
    private String email="";
    private String callBack="";
    private AppSession mSession;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_active_status, container, false);
        mActivity = (MainActivity) getActivity();
        mSession = AppSession.getInstance(mActivity);
        setUpVisibility();
        view.findViewById(R.id.home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mActivity.displayViewOther(0, null);
            }
        });
        TvDetail = view.findViewById(R.id.detail);

        TvDetail.setText(getString(R.string.iin_text) + "\n\n" + "\nYou can also contact \n" + getString(R.string.app_name) + " Group" + "\n\n" + "Email: " + email + "\n" + "Phone: " + callBack);
        return view;
    }
    private void setUpVisibility() {
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Email"))) {
            email = Utils.getConfigData(mSession).optString("Email");

        } else {
            email = "";


        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CallBack"))) {
            callBack = Utils.getConfigData(mSession).optString("CallBack");

        } else {
            callBack = "";


        }
    }

}
