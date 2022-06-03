package investwell.client.fragment.factsheet.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONArray;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.common.calculator.utils.Utils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomTextViewBold;


public class FragFactsheetHome extends Fragment implements ToolbarFragment.ToolbarCallback {

    private ViewPager pager;
    private TabLayout tabs;
    private Bundle bundle_new = new Bundle();
    private ImageView back_arrow;
    private TextView heading, mTvCart;
    private AppSession mSession;
    private FrameLayout cart;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private Bundle bundle;
    public ToolbarFragment toolbarFragment;
    private ScrollView nsvFactsheet;
    private CustomTextViewBold tvHeaderTitle;
    private LinearLayout mLinerFooter;
    private LinearLayout ll_calculator_header;
    private View view;

    @Override
    public void onResume() {
        super.onResume();

        updateCart();
    }

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
        view = inflater.inflate(R.layout.fragment_factsheet, container, false);
        setInitializer();
        bundle = getArguments();
        setUpToolBar();
        getDataFromBundle();

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(mActivity, getChildFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);


        return view;
    }

    private void setInitializer() {
        mSession = AppSession.getInstance(mActivity);
        mActivity.setMainVisibility(this, null);
        pager = view.findViewById(R.id.viewpager_allocation_charts);
        tabs = view.findViewById(R.id.tabs);
        back_arrow = view.findViewById(R.id.back_arrow);
        heading = view.findViewById(R.id.heading);
        cart = view.findViewById(R.id.fl_cart);
        mTvCart = view.findViewById(R.id.tv_cart_badge);
        nsvFactsheet = view.findViewById(R.id.nsv_factsheet);
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        mLinerFooter.setVisibility(View.GONE);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
    }

    private void getDataFromBundle() {
        if (bundle != null) {

            if (bundle.containsKey("comming_from")) {
                bundle_new.putString("passkey", bundle.getString("passkey"));
                bundle_new.putString("excl_code", bundle.getString("excl_code"));
                bundle_new.putString("bid", bundle.getString("bid"));
                bundle_new.putString("colorBlue", bundle.getString("colorBlue"));
                bundle_new.putString("hide_icon", "Y");
                bundle_new.putString("object", bundle.getString("object"));
                cart.setVisibility(View.INVISIBLE);

            } else {
                bundle_new.putString("passkey", bundle.getString("passkey"));
                bundle_new.putString("excl_code", bundle.getString("excl_code"));
                bundle_new.putString("bid", bundle.getString("bid"));
                bundle_new.putString("object", bundle.getString("object"));
                bundle_new.putString("colorBlue", bundle.getString("scheme"));
                bundle_new.putString("object", bundle.getString("object"));
                //  heading.setText(bundle.getString("scheme"));
            }
        }
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if ((mSession.getLoginType().equals("Broker")
                || mSession.getLoginType().equals("SubBroker")
                || mSession.getLoginType().equals("RM") ||mSession.getLoginType().equalsIgnoreCase("Zone")
                ||mSession.getLoginType().equalsIgnoreCase("Region")
                ||mSession.getLoginType().equalsIgnoreCase("Branch"))) {
            if (toolbarFragment != null) {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_factsheet), true, true, false, false, false, true, false,"");
                toolbarFragment.setCallback(this);
            }
        }else{
            if (toolbarFragment != null) {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_factsheet), true, false, false, false, false, true, false,"");
                toolbarFragment.setCallback(this);
            }
        }

        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_factsheet));
    }

    public void updateCart() {
        try {
            if (mSession.getAddToCartList().isEmpty() || mSession.getAddToCartList().length() == 2) {
                mTvCart.setVisibility(View.GONE);
            } else {
                mTvCart.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                mTvCart.setText("" + jsonArray.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveFrameLayout() {
        mLinerFooter.setVisibility(View.VISIBLE);
        ll_calculator_header.setVisibility(View.VISIBLE);
        loadUpdatedView();
    }

    private void loadUpdatedView() {
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          createImage();
                                      }
                                  },

                100);
    }

    private void createImage() {
        int totalHeight = nsvFactsheet.getChildAt(0).getHeight();
        int totalWidth = nsvFactsheet.getChildAt(0).getWidth();
        Bitmap bitmap = getBitmapFromView(nsvFactsheet, totalHeight, totalWidth);

        try {
            Uri imageUri = Utils.saveImage(bitmap, mActivity);

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/*");
            waIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            mLinerFooter.setVisibility(View.GONE);
            ll_calculator_header.setVisibility(View.VISIBLE);
            nsvFactsheet.destroyDrawingCache();
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_share) {
            saveFrameLayout();
        }
    }

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                OverViewFragment overViewFragment = new OverViewFragment();
                overViewFragment.setArguments(bundle_new);
                return overViewFragment;

            } else if (position == 1) {

              /*  Holdings holdings = new Holdings();
                holdings.setArguments(bundle_new);
                return holdings;*/
            }
            return null;
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return 2;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:

                    return mContext.getString(R.string.overview);
                case 1:
                    return mContext.getString(R.string.holdings);

                default:
                    return null;
            }
        }

    }

}
