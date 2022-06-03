package investwell.client.fragment.others;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.iw.acceleratordemo.R;

import investwell.utils.AppSession;
import investwell.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooserFragment extends Fragment implements View.OnClickListener {

    private TextView tvChooserTitle;
    private Button btnPrimary, btnSecondary, btnTertiary, btnUPi;
    private View view;
    private ChooserCallBack chooserCallBack;
    private int rowIndex;
    private Button[] btn = new Button[4];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.btn_primary, R.id.btn_secondary, R.id.btn_tertiary, R.id.btnUpi};
    private AppSession mSession;
    private LinearLayout llUpi;

    public ChooserFragment() {
        // Required empty public constructor
    }

    public void setChooserCallback(ChooserCallBack choserCallback) {
        this.chooserCallBack = choserCallback;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_chooser, container, false);
        setInitializer(view);
        setListeners();
        return view;
    }

    private void setInitializer(View view) {
        tvChooserTitle = view.findViewById(R.id.tv_chooser_header);
        btnPrimary = view.findViewById(R.id.btn_primary);
        mSession = AppSession.getInstance(getActivity());
        btnSecondary = view.findViewById(R.id.btn_secondary);
        btnTertiary = view.findViewById(R.id.btn_tertiary);
        btnUPi = view.findViewById(R.id.btnUpi);
        llUpi = view.findViewById(R.id.llUpi);

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btnPrimary.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btnPrimary.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btnPrimary.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btnPrimary.setTextColor(getResources().getColor(R.color.lightPrimaryTextColor));

        }
    }

    private void setListeners() {
        for (int i = 0; i < btn.length; i++) {
            btn[i] = view.findViewById(btn_id[i]);
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                btn[i].setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
            } else {
                btn[i].setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));

            }
            btn[i].setOnClickListener(this);
        }
        btn_unfocus = btn[0];
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn[0].setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btn[0].setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));

        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn[0].setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn[0].setTextColor(getResources().getColor(R.color.colorWhite));

        }

    }

    public void setUpChooserElements(String title, String btnNamePrimary, String btnNameSecondary,
                                     String btnNameTertiary, boolean isShowingPrimaryBtn, boolean isShowingSecondaryBtn,
                                     boolean isShowingTertiaryBtn, boolean isShowingUPIBtn) {
        if (isShowingPrimaryBtn) {
            btnPrimary.setVisibility(View.VISIBLE);
        } else {
            btnPrimary.setVisibility(View.GONE);
        }


        if (isShowingSecondaryBtn) {
            btnSecondary.setVisibility(View.VISIBLE);
        } else {
            btnSecondary.setVisibility(View.GONE);
        }

        if (isShowingTertiaryBtn) {
            btnTertiary.setVisibility(View.VISIBLE);
        } else {
            btnTertiary.setVisibility(View.GONE);
        }

        if (isShowingUPIBtn) {
            if (mSession.getAppType().equals("N") || mSession.getAppType().equals("DN")) {
                llUpi.setVisibility(View.VISIBLE);
                btnUPi.setText("UPI");
            } else {
                llUpi.setVisibility(View.GONE);
            }
        } else {
            llUpi.setVisibility(View.GONE);
        }





        tvChooserTitle.setText(!TextUtils.isEmpty(title) ? title : "");
        btnTertiary.setText(!TextUtils.isEmpty(btnNameTertiary) ? btnNameTertiary : "");
        btnPrimary.setText(!TextUtils.isEmpty(btnNamePrimary) ? btnNamePrimary : "");
        btnSecondary.setText(!TextUtils.isEmpty(btnNameSecondary) ? btnNameSecondary : "");

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_primary) {
            chooserCallBack.onItemChosen(btnPrimary);
            setFocus(btn_unfocus, btn[0]);
        } else if (id == R.id.btn_secondary) {
            chooserCallBack.onItemChosen(btnSecondary);
            setFocus(btn_unfocus, btn[1]);
        } else if (id == R.id.btn_tertiary) {
            chooserCallBack.onItemChosen(btnTertiary);
            setFocus(btn_unfocus, btn[2]);
        } else if (id == R.id.btnUpi) {
            chooserCallBack.onItemChosen(btnUPi);
            setFocus(btn_unfocus, btn[3]);
        }

    }


    public interface ChooserCallBack {
        void onItemChosen(View view);
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setTextColor(getResources().getColor(R.color.lightPrimaryTextColor));
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_tertiary_dark));
        } else {
            btn_unfocus.setBackground(getResources().getDrawable(R.drawable.btn_bg_tertiary));
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btn_focus.setTextColor(getResources().getColor(R.color.colorWhite));

        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_primary_dark));
        } else {
            btn_focus.setBackground(getResources().getDrawable(R.drawable.btn_bg_primary));
        }

        this.btn_unfocus = btn_focus;
    }
}
