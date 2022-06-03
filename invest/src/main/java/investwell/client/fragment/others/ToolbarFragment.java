package investwell.client.fragment.others;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.LanguageSupportActivity;
import investwell.client.activity.MainActivity;
import investwell.client.activity.UserTypesActivity;
import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.common.applock.AppLockOptionActivity;
import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.customView.CustomTextViewBold;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToolbarFragment extends Fragment implements View.OnClickListener {
    public ToolbarCallback toolbarCallbacks;
    private CustomTextViewBold tvToolBarTitle;
    TextView tvCartBadge, tvNfoCart;
    private ImageButton btnAddnew;
    private CardView cvToolbar;
    private ImageView ivRefresh, ivBack, ivShare, ivSearch, ivInfo, ivCart;
    private FrameLayout flCart;
    private LinearLayout llMiddleView, llEndViewPrimary, llEndViewSecondary, llEndViewTertiary;
    private View view;
    private BrokerActivity mBrokerActivity;
    private MainActivity mMainActivity;
    private AppLockOptionActivity mAppLockActivity;
    private LanguageSupportActivity languageSupportActivity;
    private UserTypesActivity userTypesActivity;
    private MainActivityTypeTwo mMainActivityTwo;
    private AppSession mSession;
    private AppApplication mApplication;


    public ToolbarFragment() {
        // Required empty public constructor
    }


    public void setCallback(ToolbarCallback toolbarCallback) {
        this.toolbarCallbacks = toolbarCallback;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());
        } else if (context instanceof MainActivity) {
            this.mMainActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mMainActivity);
            mApplication = (AppApplication) mMainActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());
        } else if (context instanceof MainActivityTypeTwo) {
            this.mMainActivityTwo = (MainActivityTypeTwo) context;
            mSession = AppSession.getInstance(mMainActivityTwo);
            mApplication = (AppApplication) mMainActivityTwo.getApplication();
            mSession = AppSession.getInstance(getActivity());
        } else if (context instanceof AppLockOptionActivity) {
            this.mAppLockActivity = (AppLockOptionActivity) context;
            mSession = AppSession.getInstance(mAppLockActivity);
            mApplication = (AppApplication) mAppLockActivity.getApplication();
            mSession = AppSession.getInstance(mAppLockActivity);
        }else if (context instanceof UserTypesActivity) {
            this.userTypesActivity = (UserTypesActivity) context;
            mSession = AppSession.getInstance(userTypesActivity);
            mApplication = (AppApplication) userTypesActivity.getApplication();
            mSession = AppSession.getInstance(userTypesActivity);
        }else if (context instanceof LanguageSupportActivity) {
            this.languageSupportActivity = (LanguageSupportActivity) context;
            mSession = AppSession.getInstance(languageSupportActivity);
            mApplication = (AppApplication) languageSupportActivity.getApplication();
            mSession = AppSession.getInstance(languageSupportActivity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_toolbar, container, false);
        setInitializer(view);

        setListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCart(false);


    }

    /*************************************************
     * Method called when user clicks on cart icon
     ***********************************************************/
    private void onCartIconClick() {
        try {
            if (mMainActivity != null) {
                if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                    mMainActivity.displayViewOther(39, null);
                } else {
                    mMainActivity.displayViewOther(4, null);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInitializer(View view) {
        cvToolbar = view.findViewById(R.id.cv_toolBar_parent);
        tvToolBarTitle = view.findViewById(R.id.tv_toolbar_title);
        btnAddnew = view.findViewById(R.id.btn_add_new);
        tvCartBadge = view.findViewById(R.id.tv_cart_badge);
        llEndViewPrimary = view.findViewById(R.id.ll_end_content_primary);
        llMiddleView = view.findViewById(R.id.rl_middle_search_view);
        ivRefresh = view.findViewById(R.id.iv_refresh);
        ivBack = view.findViewById(R.id.ivBack);
        ivShare = view.findViewById(R.id.iv_share);
        ivSearch = view.findViewById(R.id.iv_search);
        ivCart = view.findViewById(R.id.cart_icon);
        flCart = view.findViewById(R.id.fl_cart);
        ivInfo = view.findViewById(R.id.iv_fatca_info);
        tvNfoCart = view.findViewById(R.id.tv_cart_badge_nfo);
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
            if(getActivity()!=null) {
                cvToolbar.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                ivBack.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivShare.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivSearch.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivInfo.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
                ivCart.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRefresh.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                btnAddnew.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvToolBarTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
            }
        }else if(Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")){
            if(getActivity()!=null) {
                cvToolbar.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.darkColorToolBar));
                ivBack.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivShare.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivSearch.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivCart.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivInfo.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRefresh.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                btnAddnew.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvToolBarTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
            }
        }else {
            if(getActivity()!=null) {
                cvToolbar.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.lightColorToolBar));
                ivBack.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivShare.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivSearch.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivCart.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivInfo.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
                ivRefresh.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                btnAddnew.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvToolBarTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            }
        }
    }

    private void setListeners() {
        ivRefresh.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        view.findViewById(R.id.fl_cart).setOnClickListener(this);
        view.findViewById(R.id.iv_search).setOnClickListener(this);
        view.findViewById(R.id.btn_add_new).setOnClickListener(this);
        ivInfo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivBack) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMainActivity != null) {
                        mMainActivity.onBackPressed();
                    } else if (mBrokerActivity != null) {
                        mBrokerActivity.onBackPressed();
                    } else if (mMainActivityTwo != null) {
                        mMainActivityTwo.onBackPressed();
                    } else if (mAppLockActivity != null) {
                        mAppLockActivity.onBackPressed();
                    }

                }
            }, 100);
        } else if (id == R.id.iv_share) {
            final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
            ivShare.startAnimation(animShake);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animShake.cancel();
                }
            }, 1000);
            toolbarCallbacks.onToolbarItemClick(ivShare);
        } else if (id == R.id.iv_refresh) {
            final Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anim);
            ivRefresh.startAnimation(rotate);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rotate.cancel();
                }
            }, 1000);
            toolbarCallbacks.onToolbarItemClick(ivRefresh);
        } else if (id == R.id.fl_cart) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onCartIconClick();

                }
            }, 200);
        } else if (id == R.id.iv_search) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMainActivity != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("openDirectSearch", "DirectSearch");
                        mMainActivity.displayViewOther(75, bundle);
                    }

                }
            }, 200);
        } else if (id == R.id.btn_add_new) {
            toolbarCallbacks.onToolbarItemClick(btnAddnew);
            //mApplication.showSnackBar(view, getResources().getString(R.string.work_under_development));
        } else if (id == R.id.iv_fatca_info) {
            toolbarCallbacks.onToolbarItemClick(ivInfo);
        }
    }

    public void setUpToolBar(String toolBarTitle, boolean isShowingBckBtn, boolean isShowingShareBtn, boolean isShowingSearchBtn, boolean isShowingAddNewBtn, boolean isShowingRefreshBtn, boolean isShowingCartBtn, boolean isShowingInfo, String btnText) {

        if (isShowingShareBtn) {
            ivShare.setVisibility(View.VISIBLE);

        } else {
            ivShare.setVisibility(View.GONE);
        }
        if (isShowingSearchBtn) {
            ivSearch.setVisibility(View.VISIBLE);
        } else {
            ivSearch.setVisibility(View.GONE);
        }

        if (isShowingAddNewBtn) {
            btnAddnew.setVisibility(View.VISIBLE);
            if (btnText.equalsIgnoreCase(getResources().getString(R.string.clear))) {
                btnAddnew.setImageResource(R.drawable.ic_clear_notification);

            }else if(btnText.equalsIgnoreCase("home")){
                btnAddnew.setImageResource(R.drawable.ic_home);
            } else {
                btnAddnew.setImageResource(R.drawable.ic_create_new);
            }
        } else {
            btnAddnew.setVisibility(View.GONE);
        }

        if (isShowingInfo) {
            ivInfo.setVisibility(View.VISIBLE);
        } else {
            ivInfo.setVisibility(View.GONE);
        }

        if (isShowingBckBtn) {
            final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.left_from_right);
            ivBack.startAnimation(animShake);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animShake.cancel();
                }
            }, 1000);

            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.INVISIBLE);
        }

        if (isShowingRefreshBtn) {
            ivRefresh.setVisibility(View.VISIBLE);
        } else {
            ivRefresh.setVisibility(View.GONE);
        }
        if (isShowingCartBtn) {
            flCart.setVisibility(View.VISIBLE);
        } else {


            flCart.setVisibility(View.GONE);

        }
        tvToolBarTitle.setText(!TextUtils.isEmpty(toolBarTitle) ? toolBarTitle : "");

    }

    public void updateCart(boolean isVisible) {
        if (isVisible) {
            try {
                if (mSession.getAddToCartList().isEmpty()) {
                    tvCartBadge.setVisibility(View.GONE);
                    tvNfoCart.setVisibility(View.GONE);
                } else {
                    tvCartBadge.setVisibility(View.VISIBLE);
                    tvNfoCart.setVisibility(View.GONE);
                    final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
                    flCart.startAnimation(animShake);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            animShake.cancel();
                        }
                    }, 1000);

                    JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                    tvCartBadge.setText("" + jsonArray.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setToolBarColor(Context context, int color) {
        if (color != 0) {
            cvToolbar.setCardBackgroundColor(color);
            ivBack.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivShare.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivSearch.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivCart.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivInfo.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            ivRefresh.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
            btnAddnew.setColorFilter(ContextCompat.getColor(context, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);
            tvToolBarTitle.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));
        }

    }

    public void updateNFOCart(boolean isVisible) {
        if (isVisible) {
            try {
                if (mSession.getAddToNFOCartList().isEmpty() || mSession.getAddToNFOCartList().length() == 2) {
                    tvNfoCart.setVisibility(View.GONE);
                    tvCartBadge.setVisibility(View.GONE);
                } else {
                    tvNfoCart.setVisibility(View.VISIBLE);
                    tvCartBadge.setVisibility(View.GONE);
                    JSONArray jsonArray = new JSONArray(mSession.getAddToNFOCartList());
                    tvNfoCart.setText("" + jsonArray.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            tvNfoCart.setVisibility(View.GONE);
        }
    }


    public void hasSecondryToolBarVisibility(boolean value) {
        cvToolbar.setVisibility(value ? View.VISIBLE : View.GONE);

    }


    public interface ToolbarCallback {
        void onToolbarItemClick(View view);
    }
}
