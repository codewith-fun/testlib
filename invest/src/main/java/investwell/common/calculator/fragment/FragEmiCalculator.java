package investwell.common.calculator.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.iw.acceleratordemo.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.common.calculator.utils.AudioPlayer;
import investwell.common.calculator.utils.RoundKnobButton;
import investwell.common.calculator.utils.Singleton;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomTextViewBold;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FragEmiCalculator extends Fragment implements View.OnClickListener, View.OnTouchListener, RoundKnobButton.RoundKnobButtonListener , ToolbarFragment.ToolbarCallback{
    private final long DELAY = 1000; // milliseconds
    double nFV = 0;
    double nYears = 0, nMonths = 0;
    double r, nRate, nRate1 = 0, p, z, i;
    float intpaid, principal;
    Singleton m_Inst = Singleton.getInstance();
    private EditText mEtYear, mEtAmount, mEtRate;
    private TextView mTvSipTitle, mTvFutureAmount, mTvTotalInvest, mTvInvestedTimes, mTvWealth, mTvImageTitle;
    private LinearLayout mLinerResult;
    private ImageView mIvImageRight, mIvRefresh;
    private int mAmountCount = 0, mAnnualAmountCount = 0, mYearCount = 0, mReturn = 01;
    private ProgressBar mProgressBar;
    private AudioPlayer audioPlayer;
    private ScrollView mScrollView;
    private RoundKnobButton mDailerInvest1, mDailerInvest2, mDailerInvest3;
    private LinearLayout mLinerFooter;
    private Timer timer = new Timer();
    private BrokerActivity mBrokerActivity;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private RelativeLayout mRelToolbar;
    private ToolbarFragment fragToolBar;
    private AppSession mSession;
    private CustomTextViewBold tvHeaderTitle;
    private LinearLayout ll_calculator_header;
    Bitmap b;
    private String path = "";
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mBrokerActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(mBrokerActivity);

        } else if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
            mSession = AppSession.getInstance(mActivity);
        }
    }
    private void setFooterViewsData() {
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("BrokerName"))) {
            tvBrokerName.setText(investwell.utils.Utils.getConfigData(mSession).optString("BrokerName"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Address"))) {
            tvAddress.setText(investwell.utils.Utils.getConfigData(mSession).optString("Address"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Email"))) {
            tvEmail.setText(investwell.utils.Utils.getConfigData(mSession).optString("Email"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Mobile"))) {
            tvPhone.setText(investwell.utils.Utils.getConfigData(mSession).optString("Mobile"));
        }
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("Website"))) {
            tvWeb.setText(investwell.utils.Utils.getConfigData(mSession).optString("Website"));
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_emi_calculator, container, false);
        m_Inst.InitGUIFrame(mActivity);
        mActivity.setMainVisibility(this, null);
        audioPlayer = new AudioPlayer();
        mActivity = (MainActivity) mActivity;
        mApplication = (AppApplication) mActivity.getApplication();
        mRelToolbar = view.findViewById(R.id.relToolBar);

        mEtAmount = view.findViewById(R.id.edit1);
        mEtYear = view.findViewById(R.id.edit2);
        mEtRate = view.findViewById(R.id.edit3);
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        mTvWealth = view.findViewById(R.id.textView8);
        mScrollView = view.findViewById(R.id.scrollView);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
        mTvSipTitle = view.findViewById(R.id.textView1);
        mTvFutureAmount = view.findViewById(R.id.textView2);
        mTvTotalInvest = view.findViewById(R.id.textView4);
        mTvInvestedTimes = view.findViewById(R.id.textView6);
        mLinerResult = view.findViewById(R.id.linerResult);

        mProgressBar = view.findViewById(R.id.progress_bar);
setUpToolBar();
    /*    TextView toolbar_title_left = view.findViewById(R.id.toolbar_title_left);
        toolbar_title_left.setText("EMI Calculator");*/




        tvAddress = view.findViewById(R.id.tv_address);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest1.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        mDailerInvest2.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        mDailerInvest3.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));


        mDailerInvest1.setOnTouchListener(this);
        mDailerInvest2.setOnTouchListener(this);
        mDailerInvest3.setOnTouchListener(this);
        setFooterViewsData();
        mDailerInvest1.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
            }

            public void onRotate(final int percentage) {
                System.out.println("" + percentage);
                mEtAmount.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtAmount.getText().toString().replaceAll(",", ""));
                        long updateValue = 0;
                        if (mAmountCount != percentage) {
                            if (percentage > mAmountCount) {
                                if (currentValue < 10000) {
                                    updateValue = (currentValue + 500);
                                } else {
                                    updateValue = (currentValue + 1000);
                                }
                            } else if (percentage < mAmountCount) {
                                if (currentValue < 10000) {
                                    updateValue = (currentValue - 500);
                                } else {
                                    updateValue = (currentValue - 1000);
                                }
                            }

                            mAmountCount = percentage;
                            if (updateValue < 50000) {
                                mEtAmount.setText("" + 50000);
                            } else {
                                mEtAmount.setText("" + updateValue);
                            }

                            audioPlayer.play(mActivity);
                            calculateAmount();
                        }


                    }
                });
            }
        });

        mDailerInvest2.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {
                mEtYear.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtYear.getText().toString());
                        int updateValue = 0;
                        if (mYearCount != percentage) {
                            if (percentage > mYearCount) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mYearCount) {
                                updateValue = (currentValue - 1);
                            }

                            mYearCount = percentage;
                            if (updateValue < 1)
                                mEtYear.setText("" + 1);
                            else if (updateValue > 50)
                                mEtYear.setText("" + 50);
                            else
                                mEtYear.setText("" + updateValue);

                            audioPlayer.play(mActivity);
                            calculateAmount();
                        }

                    }
                });
            }
        });


        mDailerInvest3.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {
                mEtRate.post(new Runnable() {
                    public void run() {
                        double currentValue = Double.parseDouble(mEtRate.getText().toString());
                        double updateValue = 0;
                        if (mReturn != percentage) {
                            if (percentage > mReturn) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mReturn) {
                                updateValue = (currentValue - 1);
                            }

                            mReturn = percentage;
                            if (updateValue < 1)
                                mEtRate.setText("" + 1.00);
                            else if (updateValue > 30)
                                mEtRate.setText("" + 30.00);
                            else
                                mEtRate.setText("" + updateValue);

                            audioPlayer.play(mActivity);
                            calculateAmount();
                        }

                    }
                });
            }
        });


        mEtAmount.addTextChangedListener(new GenericTextWatcher(mEtAmount));
        mEtRate.addTextChangedListener(new GenericTextWatcher(mEtRate));
        mEtYear.addTextChangedListener(new GenericTextWatcher(mEtYear));

        calculateAmount();
        return view;
    }


    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_emi_Cal),
                    true, !TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot")) &&
                            investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot").equalsIgnoreCase("Y"), false, false, true, false, false, "");
            fragToolBar.setCallback(this);
        }
        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_education_cal));
    }

    @Override
    public void onStateChange(boolean newstate) {

    }

    @Override
    public void onRotate(int percentage) {

    }

    @Override
    public void onClick(View view) {
        /*if (view.getId() == R.id.ivRight) {
            saveFrameLayout();
        }*//* else if (view.getId() == R.id.ivRight2) {
            refreshView();
        } *//*else*/ if (view.getId() == R.id.ivLeft) {
            mActivity.getSupportFragmentManager().popBackStack();
        }

    }

    private void refreshView() {
        mDailerInvest1.setRotorPercentage(0);
        mDailerInvest2.setRotorPercentage(0);
        mDailerInvest3.setRotorPercentage(0);
        mEtAmount.setText("500000");
        mEtYear.setText("10");
        mEtRate.setText("10.00");
        calculateAmount();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyPad();
        String currentValue = "";
        int i1 = view.getId();
        if (i1 == R.id.dialer1) {
            currentValue = mEtAmount.getText().toString().replaceAll(",", "");
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 50000)
                    mEtAmount.setText("" + 50000);
            } else {
                mEtAmount.setText("" + 50000);
            }
            desableScrollView(motionEvent);

        } else if (i1 == R.id.dialer2) {
            currentValue = mEtYear.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 1)
                    mEtYear.setText("" + 1);
                else if (value > 50)
                    mEtYear.setText("" + 50);

            } else {
                mEtYear.setText("" + 1);
            }

            desableScrollView(motionEvent);

        } else if (i1 == R.id.dialer3) {
            currentValue = mEtRate.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtRate.setText("" + 1.00);
                else if (value > 30)
                    mEtRate.setText("" + 30.00);
            } else {
                mEtRate.setText("" + 1.00);
            }
            desableScrollView(motionEvent);

        }
        return false;
    }

    private void desableScrollView(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            mScrollView.requestDisallowInterceptTouchEvent(true);

        else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            mScrollView.requestDisallowInterceptTouchEvent(false);

    }

    public void hideKeyPad() {
        InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = mActivity.getCurrentFocus();

        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    private void delayCall() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                calculateAmount();
                            }
                        });


                    }
                },
                DELAY
        );
    }

    private void calculateAmount() {
        if (mEtAmount.getText().toString().replaceAll(",", "").equals("") || mEtAmount.getText().toString().replaceAll(",", "").equals("0")) {
            mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.emi_cal_error_empty_loan));
        } else if ((Long.parseLong(mEtAmount.getText().toString().replaceAll(",", ""))) < 50000) {
            mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.emi_cal_error_minnimum_loan_amount));
        } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
            mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.emi_cal_error_empty_loan_duration));
        } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
            mEtYear.setText("50");
            mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.emi_cal_error_invalid_loan_duration));
        } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
            mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.emi_cal_error_empty_roi));
        } else if (Double.parseDouble(mEtRate.getText().toString()) > 30) {
            mEtRate.setText("30.00");
            mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.emi_cal_error_max_roi));
        } else {
            String rawValue = mEtAmount.getText().toString().replaceAll(",", "").replaceAll(",", "");
            mActivity.convertIntoCurrencyFormat(rawValue, mEtAmount);

            try {


                nFV = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));
                nYears = Double.parseDouble(mEtYear.getText().toString());
                nMonths = nYears * 12;
                nRate1 = Double.parseDouble(mEtRate.getText().toString());
                nRate = nRate1 / 1200;
                intpaid = Math.round((((nFV - p * nMonths)) / (p * nMonths)) * 100);
                principal = Math.round((nFV / p * nMonths) * 100);

                z = 1;
                for (i = 1; i <= nMonths; i++) {
                    z = z * (1 + nRate);
                }
                p = nFV * nRate * (z / (z - 1));

                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                String strAmount = format.format(nFV);
                String[] resultAmount = strAmount.split("\\.", 0);

                mTvSipTitle.setText("Loan amount " + resultAmount[0] + " for " + ((int) nYears) + " years and rate " + ((int) nRate1) + "% yearly, Your estimated EMI would be");
                mTvTotalInvest.setText(resultAmount[0]);

                long value1 = Math.round(p);
                String str = format.format(value1);
                String[] result = str.split("\\.", 0);
                mTvFutureAmount.setText(result[0]);

                Double totalAmountPay = (nFV + ((p * nMonths) - nFV));
                long value2 = Math.round(totalAmountPay);
                String totalAmountPayable = format.format(value2);
                String[] invesRresult = totalAmountPayable.split("\\.", 0);

                mTvInvestedTimes.setText(invesRresult[0]);

                Double totalIntrest = ((p * nMonths) - nFV);
                long value3 = Math.round(totalIntrest);
                String strProfit = format.format(value3);
                String[] arrayProfit = strProfit.split("\\.", 0);
                mTvWealth.setText(arrayProfit[0]);


                int percentage = (int) ((nFV * 100) / totalAmountPay);
                mProgressBar.setProgress(percentage);
                double times = (totalIntrest / nFV);
              /*  if (Double.isNaN(times)) {
                    mTvInvestedTimes.setText("0 times");
                } else {
                    String strTimes = String.format("%.2f", times);
                    mTvInvestedTimes.setText("" + strTimes + " times");
                }*/
            } catch (Exception e) {

            }

        }

    }

    public void saveFrameLayout() {
        mLinerFooter.setVisibility(View.VISIBLE);
        ll_calculator_header.setVisibility(View.VISIBLE);

        onShareOptionClick();
    }

    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            mApplication.showSnackBar(tvAddress, "All permissions are granted!");
                            createImage();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /***********************************************Share option Click operation********************************************/
    private void onShareOptionClick() {
        mLinerFooter.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          requestStoragePermission();
                                      }
                                  },

                100);
    }

    private void createImage() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Calculators/");

        if (!folder.exists()) {
            folder.mkdir();
        }

        path = folder.getAbsolutePath();

        path = path + "/" + "Calculator-" + getResources().getString(R.string.toolBar_title_education_cal) + System.currentTimeMillis() + ".pdf";// path where pdf will be stored


        int totalHeight = mScrollView.getChildAt(0).getHeight();
        int totalWidth = mScrollView.getChildAt(0).getWidth();

        //Save bitmap to  below path
        String extr = Environment.getExternalStorageDirectory() + "/Calculators/";
        File file = new File(extr);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileName = "calculator_img_" + ".jpg";
        File myPath = new File(extr, fileName);
        String imagesUri = myPath.getPath();
        FileOutputStream fos = null;
        b = getBitmapFromView(mScrollView, totalHeight, totalWidth);

        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        createPdf();


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


    private void createPdf() {

        PdfDocument document = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            document = new PdfDocument();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(b.getWidth(), b.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();


            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);


            paint.setColor(Color.BLUE);
            canvas.drawBitmap(b, 0, 0, null);
            document.finishPage(page);
            File filePath = new File(path);
            try {
                document.writeTo(new FileOutputStream(filePath));
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("application/pdf");
                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", filePath);
                email.putExtra(Intent.EXTRA_STREAM, uri);
                if (mActivity != null) {
                    mActivity.startActivity(email);
                } else {
                    mBrokerActivity.startActivity(email);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
            } finally {
                mLinerFooter.setVisibility(View.GONE);
                mScrollView.destroyDrawingCache();
            }

            // close the document
            document.close();

        }
    }

    @Override
    public void onToolbarItemClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_share) {
            saveFrameLayout();
        } else if (id == R.id.iv_refresh) {
            refreshView();
        }
    }

    private class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void afterTextChanged(Editable editable) {
            if (mActivity.getCurrentFocus() == mEtAmount) {
                delayCall();
            } else if (mActivity.getCurrentFocus() == mEtYear) {
                delayCall();
            } else if (mActivity.getCurrentFocus() == mEtRate) {
                delayCall();
            }


        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

    }


}
