package investwell.client.fragment.others;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.DashboardMenuAdapter;
import investwell.utils.AppSession;
import investwell.utils.CircleTransform;
import investwell.utils.Utils;
import investwell.utils.model.FinancialTools;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener, DashboardMenuAdapter.DashboardMenuListener {
    private DashboardMenuAdapter dashboardMenuAdapter;
    private List<FinancialTools> financialToolsList;
    private RecyclerView rvFinancialTools;
    private View view;
    private MainActivity mActivity;
    private AppSession mSession;
    private Bundle mBundle;
    private AppApplication mApplication;
    private ImageView ivLogout, ivSetting, mIvProfileImage;
    private TextView tvName;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        setInitializer();
        setDashboardMenuAdapter();
        return view;
    }

    private void setInitializer() {
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(mActivity);
        financialToolsList = new ArrayList<>();
        rvFinancialTools = view.findViewById(R.id.rv_menus);
        tvName = view.findViewById(R.id.tv_user_name);
        mIvProfileImage = view.findViewById(R.id.iv_profile_image);
        mBundle = getArguments();
        mApplication = (AppApplication) mActivity.getApplication();

        ivLogout = view.findViewById(R.id.iv_shut_down);
        ivSetting = view.findViewById(R.id.iv_setting);
        if ((mSession.getLoginType().equals(getString(R.string.login_type_broker))
                || mSession.getLoginType().equals(getResources().getString(R.string.login_type_sub_broker))
                || mSession.getLoginType().equals(getResources().getString(R.string.login_type_rm)))) {
            ivSetting.setVisibility(View.GONE);
            ivLogout.setVisibility(View.GONE);
        } else {
            ivSetting.setVisibility(View.VISIBLE);
            ivLogout.setVisibility(View.VISIBLE);
        }
        tvName.setText(!TextUtils.isEmpty(mSession.getFullName()) ? mSession.getFullName() : "");
        view.findViewById(R.id.iv_shut_down).setOnClickListener(this);
        view.findViewById(R.id.iv_setting).setOnClickListener(this);
        if ((mSession.getLoginType().equals("Broker") || mSession.getLoginType().equals("SubBroker") || mSession.getLoginType().equals("RM") ||mSession.getLoginType().equalsIgnoreCase("Zone")
                ||mSession.getLoginType().equalsIgnoreCase("Region")
                ||mSession.getLoginType().equalsIgnoreCase("Branch"))) {
            String path = mSession.getClientImage();
            Picasso.get().load(path).error(R.mipmap.profileplaceholder).transform(new CircleTransform()).into(mIvProfileImage);
        } else if (mSession.getHasLoging() && mSession.getImageRawData().length() > 0) {
            String path = mSession.getImageRawData();
            Picasso.get().load(path).error(R.mipmap.profileplaceholder).transform(new CircleTransform()).into(mIvProfileImage);

        } else {
            mIvProfileImage.setImageResource(R.mipmap.profileplaceholder);
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_shut_down) {
            mActivity.showLogOutAlert();
            dismiss();
        } else if (id == R.id.iv_setting) {
            mActivity.displayViewOther(89, null);
        }
    }

    /*******************************************
     * Method used to set dynamic financial items
     *******************************************/

    private void setDashboardMenuAdapter() {

        dashboardMenuAdapter = new DashboardMenuAdapter(mActivity, financialToolsList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
        rvFinancialTools.setLayoutManager(gridLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.seperator));
        DividerItemDecoration divideHorizontal = new DividerItemDecoration(getContext(),
                LinearLayoutManager.HORIZONTAL);
        divideHorizontal.setDrawable(getContext().getResources().getDrawable(R.drawable.seperator));
        rvFinancialTools.addItemDecoration(divideHorizontal);
        rvFinancialTools.addItemDecoration(dividerItemDecoration);
        rvFinancialTools.setItemAnimator(new DefaultItemAnimator());
        rvFinancialTools.setNestedScrollingEnabled(false);
        rvFinancialTools.setAdapter(dashboardMenuAdapter);
        prepareDashboardMenu();
    }

    /****************************************
     * Converting dp to pixel
     ****************************************/
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /*******************************************
     * Method contains data for investment route items
     *******************************************/

    private void prepareDashboardMenu() {
        try {
            JSONArray jsonArray = new JSONArray(Utils.getConfigData(mSession).optString("MenuList"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String menuId = jsonObject.optString("MenuID");

                if (menuId.equalsIgnoreCase("MNU001")) { /* "MenuName":"My Journey"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_journey, getResources().getString(R.string.main_nav_title_journey), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU002")) {/* "MenuName":"Watchlist"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_watch_list, getResources().getString(R.string.main_nav_title_watch_list), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU003")) { /* "MenuName":"Tax"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_tax_saving, getResources().getString(R.string.main_nav_title_tax_saving), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU004")) { /* "MenuName":"Dividend"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_dividend, getResources().getString(R.string.main_nav_title_dividend), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU005")) { /* "MenuName":"Insurance"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_my_insurance, getResources().getString(R.string.main_nav_title_my_insurance), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU006")) { /*"MenuName":"My Goal"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_mygoal, getResources().getString(R.string.main_nav_title_goal), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU007")) { /*"MenuName":"My SIP"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_my_sip, getResources().getString(R.string.main_nav_title_my_sip), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU008")) { /*"MenuName":"Invest Now" */
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_invest_now, getResources().getString(R.string.main_nav_title_invest), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU009")) { /*"MenuName":"Pay Now" */


                } else if (menuId.equalsIgnoreCase("MNU010")) { /*"MenuName":"Investment Profile"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_investment_profile, getResources().getString(R.string.main_nav_title_invest_profile), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU011")) { /*"MenuName":"My Folio"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_my_folio, getResources().getString(R.string.main_nav_title_my_folio), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU012")) { /* "MenuName":"Transaction"*/

                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_transaction, getResources().getString(R.string.main_bottom_tab_menu_transactions), menuId);
                    financialToolsList.add(a);
                } else if (menuId.equalsIgnoreCase("MNU013")) { /*"MenuName":"Transfer Holding"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_transfer_holding, getResources().getString(R.string.main_nav_title_holding), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU014")) { /*"MenuName":"Risk Profile"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_risk_assessment, getResources().getString(R.string.main_nav_title_my_risk_profile), menuId);
                    financialToolsList.add(a);

                } else if (menuId.equalsIgnoreCase("MNU015")) { /*"MenuName":"Send Mail"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_mail, getResources().getString(R.string.main_nav_title_send_mail), menuId);
                    financialToolsList.add(a);
                }else if (menuId.equalsIgnoreCase("MNU016")) { /*"MenuName":"My Goal v2"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_mygoal, getResources().getString(R.string.main_nav_title_goal), menuId);
                    financialToolsList.add(a);
                }else if (menuId.equalsIgnoreCase("MNU017")) { /*"MenuName":"Send Mail"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_kyc, getResources().getString(R.string.main_nav_title_kyc), menuId);
                    financialToolsList.add(a);
                }else if (menuId.equalsIgnoreCase("MNU018")) { /*"MenuName":"Send Mail"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_my_sip, getResources().getString(R.string.systemetic_investment_txt), menuId);
                    financialToolsList.add(a);
                } else if (menuId.equalsIgnoreCase("MNU019")) { /*"MenuName":"Document Viewer"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_document_viewer, "My Documents", menuId);
                    financialToolsList.add(a);
                } else if (menuId.equalsIgnoreCase("MNU020")) { /*"MenuName":"Document Viewer"*/
                    FinancialTools a = new FinancialTools(R.mipmap.ic_bottomsheet_myorder, "My Orders", menuId);
                    financialToolsList.add(a);
                }
            } 
        } catch (Exception e) {

        }
        dashboardMenuAdapter.notifyDataSetChanged();
    }

    private void showDialog() {
        if (mActivity != null) {
            final Dialog dialog = new Dialog(mActivity);
            dialog.setContentView(R.layout.dailog_investnow);
            investwell.utils.customView.CustomButton rdybtn = dialog.findViewById(R.id.ready_btn);
            investwell.utils.customView.CustomButton notrdybtn = dialog.findViewById(R.id.notready_btn);
            TextView notes = dialog.findViewById(R.id.notes);
            notes.setText(R.string.notes);

            rdybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.displayViewOther(16, null);
                    dialog.dismiss();
                }
            });

            notrdybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    public void onToolsClick(int position) {
        FinancialTools toolObject = financialToolsList.get(position);
        String menuId = toolObject.getMenuId();

        if (menuId.equalsIgnoreCase("MNU001")) { /* "MenuName":"My Journey"*/
            mActivity.displayViewOther(24, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU002")) {/* "MenuName":"Watchlist"*/
            mActivity.displayViewOther(25, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU003")) { /* "MenuName":"Tax"*/
            mActivity.displayViewOther(48, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU004")) { /* "MenuName":"Dividend"*/
            mActivity.displayViewOther(43, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU005")) { /* "MenuName":"Insurance"*/
            mActivity.displayViewOther(44, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU006")) { /*"MenuName":"My Goal"*/
            mActivity.displayViewOther(107, null);

        } else if (menuId.equalsIgnoreCase("MNU007")) { /*"MenuName":"My SIP"*/
            mActivity.displayViewOther(34, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU008")) { /*"MenuName":"Invest Now" */
            Bundle bundle1 = new Bundle();
            bundle1.putString("heading_name", "Select Scheme to Invest");
            mActivity.displayViewOther(75, null);

        } else if (menuId.equalsIgnoreCase("MNU009")) { /*"MenuName":"Pay Now" */


        } else if (menuId.equalsIgnoreCase("MNU010")) { /*"MenuName":"Investment Profile"*/
            Bundle bundle = new Bundle();
            bundle.putString("type", "show_only_profiles");
            mActivity.displayViewOther(36, bundle);

        } else if (menuId.equalsIgnoreCase("MNU011")) { /*"MenuName":"My Folio"*/
            mActivity.displayViewOther(78, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU012")) { /* "MenuName":"Transaction"*/
            mActivity.displayViewOther(112, mBundle);

        } else if (menuId.equalsIgnoreCase("MNU013")) { /*"MenuName":"Transfer Holding"*/
            mActivity.displayViewOther(52, null);
            /*if (mSession.getUCC_CODE().equalsIgnoreCase("NA") || mSession.getUCC_CODE().equalsIgnoreCase("")) {
                showDialog();
            } else {
                mActivity.displayViewOther(52, null);
            }*/

        } else if (menuId.equalsIgnoreCase("MNU014")) { /*"MenuName":"Risk Profile"*/
            if (mSession.getRiskName().isEmpty()) {
                mActivity.displayViewOther(61, null);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("type", "showRiskProfile");
                mActivity.displayViewOther(60, bundle);
            }

        } else if (menuId.equalsIgnoreCase("MNU015")) { /*"MenuName":"Send Mail"*/
            mActivity.displayViewOther(23, mBundle);
        }else if (menuId.equalsIgnoreCase("MNU016")) { /*"MenuName":"Send Mail"*/
            mActivity.displayViewOther(70, null);
        }else if(menuId.equalsIgnoreCase("MNU017")){
            mActivity.displayViewOther(104, mBundle);

        }else if(menuId.equalsIgnoreCase("MNU018")){
            mActivity.displayViewOther(109, mBundle);

        } else if(menuId.equalsIgnoreCase("MNU019")){  /*"MenuName":"My Documents"*/
            mActivity.displayViewOther(114, mBundle);

        }
        else if(menuId.equalsIgnoreCase("MNU020")){  /*"MenuName":"My Documents"*/
            mActivity.displayViewOther(121, mBundle);

        }

    }



}
