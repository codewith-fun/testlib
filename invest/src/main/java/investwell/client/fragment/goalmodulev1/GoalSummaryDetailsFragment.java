package investwell.client.fragment.goalmodulev1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.goalbased.Adapter.FragGoalSummeryAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.CustomProgressBar;
import investwell.utils.ProgressItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalSummaryDetailsFragment extends Fragment {
    private ProgressDialog mBar;
    private FragGoalSummeryAdapter mAdapter;
    private AppSession mSession;
    private MainActivity mActivity;
    private String mType = "";
    private TextView mTvNothing;
    private String mUCC_Code = "";
    private CustomProgressBar mProgressBar;
    TextView tvComplete, tvProjected, tvShortFall, tvImageTitle;
    LinearLayout mLinerShortFall;
    private RelativeLayout mRelToolbar;
    private ImageView ImageTitle;
    private ScrollView scrollView;
    private AppApplication mApplication;
    public GoalSummaryDetailsFragment() {
        // Required empty public constructor
    }
    private ToolbarFragment toolbarFragment;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
            mActivity.setMainVisibility(this, null);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_summary_details, container, false);

setUpToolBar();
        scrollView = view.findViewById(R.id.scrollView);
        tvImageTitle = view.findViewById(R.id.tvImageTitle);

        TextView tvGoalName = view.findViewById(R.id.tvGoalName);
        TextView tvGoalUserName = view.findViewById(R.id.tvGoalUserName);
        TextView tv4 = view.findViewById(R.id.tv4);
        TextView tv6 = view.findViewById(R.id.tv6);
        TextView tv8 = view.findViewById(R.id.tv8);
        TextView tv10 = view.findViewById(R.id.tv10);
        TextView tv12 = view.findViewById(R.id.tv12);
        TextView tv14 = view.findViewById(R.id.tv14);
        TextView tv16 = view.findViewById(R.id.tv16);
        TextView tv18 = view.findViewById(R.id.tv18);
        TextView tv17 = view.findViewById(R.id.tv17);
        TextView tvLumsumm = view.findViewById(R.id.tvLumsumm);
        TextView tvSIP = view.findViewById(R.id.tvSIP);
        TextView tvDate = view.findViewById(R.id.tvDate);
        ImageView ivImage = view.findViewById(R.id.ivImage);
        CardView cardViewInvest = view.findViewById(R.id.cardViewInvest);
        mProgressBar = view.findViewById(R.id.seekBar0);
        tvComplete = view.findViewById(R.id.tvComplete);
        tvProjected = view.findViewById(R.id.tvProjected);
        tvShortFall = view.findViewById(R.id.tvShortFall);
        mLinerShortFall = view.findViewById(R.id.linerShortFall);



        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("data")) {
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("data"));
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                format.setMinimumFractionDigits(0);
                format.setMaximumFractionDigits(0);

                tvGoalName.setText(jsonObject.optString("GoalName"));
                tvDate.setText(" (Required in " + jsonObject.optString("YearToGet") + ")");
                tvGoalUserName.setText(jsonObject.optString("Purpose"));
                tv6.setText(jsonObject.optString("InflationRate") + "%");

                double CurrentAmount = jsonObject.optDouble("CurrentAmount");
                String strCurrentAmount = format.format(CurrentAmount);
                tv4.setText(strCurrentAmount);

                double ProjectedValue = jsonObject.optDouble("ExpectedCorpus");
                String strProjectedValue = format.format(ProjectedValue);
                tv8.setText(strProjectedValue);

                double CurrentValue = jsonObject.optDouble("CurrentValue");
                String strPCurrentValue = format.format(CurrentValue);
                tv10.setText(strPCurrentValue);

                double SIPAmt = jsonObject.optDouble("SIPAmt");
                String strSIPAmt = format.format(SIPAmt);
                tv12.setText(strSIPAmt);

                tv14.setText(jsonObject.optString("ExpectedReturn") + "%");

                double ExpectedCorpus = jsonObject.optDouble("ProjectedValue");
                String strExpectedCorpus = format.format(ExpectedCorpus);
                tv16.setText(strExpectedCorpus);

                double ShortFall = jsonObject.optDouble("ShortFall");
                String strShortFall = format.format(ShortFall);
                if (ShortFall < 0) {
                    tv17.setText("Excess");
                    double ShortF = Math.abs(ShortFall);
                    String strEShortF = format.format(ShortF);
                    tv18.setText("" + strEShortF);
                    cardViewInvest.setVisibility(View.GONE);
                } else {
                    tv18.setText(strShortFall);
                    cardViewInvest.setVisibility(View.VISIBLE);
                }


                double SIPInvestment = jsonObject.optDouble("SIPInvestment");
                String strSIPInvestment = format.format(SIPInvestment);
                tvSIP.setText(strSIPInvestment);

                double OneTimeInvestment = jsonObject.optDouble("OneTimeInvestment");
                String strOneTimeInvestment = format.format(OneTimeInvestment);
                tvLumsumm.setText(strOneTimeInvestment);
                setProgressBar(jsonObject);

                String goalName = jsonObject.optString("GoalName");
                if (goalName.equalsIgnoreCase("Education")) {
                    ivImage.setImageResource(R.mipmap.education);
                } else if (goalName.equalsIgnoreCase("Retirement")) {
                    ivImage.setImageResource(R.mipmap.retirement);
                } else if (goalName.equalsIgnoreCase("Marriage")) {
                    ivImage.setImageResource(R.mipmap.mariage);
                } else if (goalName.equalsIgnoreCase("House") || goalName.equalsIgnoreCase("Home")) {
                    ivImage.setImageResource(R.mipmap.house);
                } else if (goalName.equalsIgnoreCase("Car")) {
                    ivImage.setImageResource(R.mipmap.car);
                } else if (goalName.equalsIgnoreCase("Loan")) {
                    ivImage.setImageResource(R.mipmap.other);
                } else if (goalName.equalsIgnoreCase("Tour")) {
                    ivImage.setImageResource(R.mipmap.vacation);
                } else {
                    ivImage.setImageResource(R.mipmap.other);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }


    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getString(R.string.goal_summary_details_header_txt), true, false, false, false, false, false, false, "");
        }
    }

    public void saveFrameLayout() {
        mRelToolbar.setVisibility(View.GONE);
        tvImageTitle.setVisibility(View.VISIBLE);
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
        int totalHeight = scrollView.getChildAt(0).getHeight();
        int totalWidth = scrollView.getChildAt(0).getWidth();
        Bitmap bitmap = getBitmapFromView(scrollView, totalHeight, totalWidth);

        try {
            Uri imageUri = saveImage(bitmap);
        /*    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);*/


            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/*");
            waIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            tvImageTitle.setVisibility(View.GONE);
            mRelToolbar.setVisibility(View.VISIBLE);
            scrollView.destroyDrawingCache();
        }
    }


    private Uri saveImage(Bitmap image) {
        File imagesFolder = new File(getActivity().getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), file);
            System.out.println(uri);
        } catch (IOException e) {
            System.out.println("IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
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

    private void setProgressBar(JSONObject jsonObject) {
        mProgressBar.getThumb().mutate().setAlpha(0);
        ArrayList<ProgressItem> progressItemList;
        ProgressItem mProgressItem;

        Double currentAmount = jsonObject.optDouble("CurrentValue");
        Double ExpectedCorpus = jsonObject.optDouble("ExpectedCorpus");
        Double ProjectedValue = jsonObject.optDouble("ProjectedValue");
        Double ShortFall = jsonObject.optDouble("ShortFall");

        Double percentagecomplete = currentAmount * 100 / ExpectedCorpus;
        Double percentageProjected = (ProjectedValue * 100) / ExpectedCorpus;
        Double percentageShortFall = (ShortFall * 100) / ExpectedCorpus;
        DecimalFormat twoDForm = new DecimalFormat("#.00");
        percentagecomplete = Double.valueOf(twoDForm.format(percentagecomplete));
        percentageProjected = Double.valueOf(twoDForm.format(percentageProjected));
        percentageShortFall = Double.valueOf(twoDForm.format(percentageShortFall));
        tvComplete.setText("Completed\n(" + percentagecomplete + "%)");

        if (percentageShortFall < 0) {
            mLinerShortFall.setVisibility(View.GONE);
            tvProjected.setText("Projected\n(Excess)");
        } else {
            mLinerShortFall.setVisibility(View.VISIBLE);
            tvShortFall.setText("Shortfall\n(" + percentageShortFall + "%)");
            double finalValue = percentageProjected - percentageShortFall;
            finalValue = Double.valueOf(twoDForm.format(finalValue));
            tvProjected.setText("Projected\n(" + finalValue + "%)");

        }

        progressItemList = new ArrayList<ProgressItem>();
        // red span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = percentagecomplete;
        mProgressItem.color = R.color.colorGreen;
        progressItemList.add(mProgressItem);

        // blue span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (percentageProjected - percentagecomplete);
        mProgressItem.color = R.color.colorOrange;
        progressItemList.add(mProgressItem);

        // green span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = percentageShortFall;
        mProgressItem.color = R.color.colorRed;
        progressItemList.add(mProgressItem);


        mProgressBar.initData(progressItemList);
        mProgressBar.invalidate();
    }

}
