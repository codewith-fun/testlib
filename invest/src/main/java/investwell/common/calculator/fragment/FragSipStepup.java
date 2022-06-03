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
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import investwell.client.fragment.others.ToolbarFragment;
import investwell.common.calculator.utils.AudioPlayer;
import investwell.common.calculator.utils.RoundKnobButton;
import investwell.common.calculator.utils.Singleton;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomTextViewBold;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FragSipStepup extends Fragment implements View.OnClickListener, View.OnTouchListener,
        RoundKnobButton.RoundKnobButtonListener, ToolbarFragment.ToolbarCallback {
    private final long DELAY = 1000; // milliseconds
    double w_delay = 0;
    double w_tot = 0;
    double amt_inv = 0, amtD = 0, amount = 0, tD = 0, rorD = 0, yearlyiD = 0;
    Singleton m_Inst = Singleton.getInstance();
    private EditText mEtYear, mEtAmount, mEtRate, mEtAnnualIncrement;
    private TextView mTvSipTitle, mTvFutureAmount, mTvTotalInvest, mTvInvestedTimes, mTvWealth, mTvImageTitle, mResulttxt;
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb;

    private int mAmountCount = 0, mAnnualAmountCount = 0, mYearCount = 0, mReturn = 01;
    private AudioPlayer audioPlayer;
    private ScrollView mScrollView;
    private RoundKnobButton mDailerInvest1, mDailerInvest2, mDailerInvest3, mDailerInvest4;
    private LinearLayout mLinerFooter;
    private Timer timer = new Timer();
    private MainActivity mActivity;
    private AppApplication mApplication;
    private RelativeLayout mRelToolbar;
    private ToolbarFragment toolbarFragment;
    private CustomTextViewBold tvHeaderTitle;
    private LinearLayout ll_calculator_header;
    private BrokerActivity mBrokerActivity;
    private AppSession mSession;
    Bitmap b;
    private String path = "";
    private TextView tvErrorMonth, tvErrorInvestPeriod, tvErrorAnnualReturn, tvErrorIAnnualInc;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_sip_step_up, container, false);

        initialChecks();
        initializer(view);
        setUpToolBar();
        setUpFooterViewData();
        initialDialerSetup();
        setOnTouchListeners();
        setDialerOneListener();
        setDialerTwoListener();
        setDialerThreeListener();
        setDialerFourListener();
        setListeners(view);
        calculateAmount();
        return view;
    }

    private void setListeners(View view) {
        mEtAmount.addTextChangedListener(new GenericTextWatcher(mEtAmount));
        mEtRate.addTextChangedListener(new GenericTextWatcher(mEtRate));
        mEtYear.addTextChangedListener(new GenericTextWatcher(mEtYear));
        mEtAnnualIncrement.addTextChangedListener(new GenericTextWatcher(mEtAnnualIncrement));
    }


    private void initialChecks() {
        if (mBrokerActivity != null) {
            m_Inst.InitGUIFrame(mBrokerActivity);

        } else {
            m_Inst.InitGUIFrame(mActivity);

        }
    }

    private void initializer(View view) {
        audioPlayer = new AudioPlayer();
        mRelToolbar = view.findViewById(R.id.relToolBar);
        mEtAmount = view.findViewById(R.id.edit1);
        mEtYear = view.findViewById(R.id.edit2);
        mEtRate = view.findViewById(R.id.edit3);
        mEtAnnualIncrement = view.findViewById(R.id.edit4);
        tvAddress = view.findViewById(R.id.tv_address);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        mTvWealth = view.findViewById(R.id.tvResult2);
        mScrollView = view.findViewById(R.id.scrollView);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
        mTvSipTitle = view.findViewById(R.id.textView1);
        mTvFutureAmount = view.findViewById(R.id.textView2);
        mTvTotalInvest = view.findViewById(R.id.tvResult);
        mTvInvestedTimes = view.findViewById(R.id.textView6);
        tvErrorMonth = view.findViewById(R.id.tv_error_monthly_invest);
        tvErrorInvestPeriod = view.findViewById(R.id.tv_error_invest_period);
        tvErrorAnnualReturn = view.findViewById(R.id.tv_error_ann_ret);
        tvErrorIAnnualInc = view.findViewById(R.id.tv_error_ann_inc);
        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest4 = view.findViewById(R.id.dialer4);
        mResulttxt = view.findViewById(R.id.textView3);
        mResulttxt.setText("To achieve this Goal you must start Investment of \u20B9 " + mEtAmount.getText().toString() + " Monthly SIP Today.");
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot")) &&
                    investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot").equalsIgnoreCase("Y")) {

                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_sip_cal), true, true, false, false, true, false, false, "");
            } else {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_sip_cal), true, false, false, false, true, false, false, "");

            }
        }
        if (toolbarFragment != null) {
            toolbarFragment.setCallback(this);
        }
        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_sip_cal));
    }

    private void setUpFooterViewData() {
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

    private void initialDialerSetup() {

        if (mBrokerActivity != null) {
            mDailerInvest1.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        } else {
            mDailerInvest1.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));

        }

    }

    private void setOnTouchListeners() {


        mDailerInvest1.setOnTouchListener(this);
        mDailerInvest2.setOnTouchListener(this);
        mDailerInvest3.setOnTouchListener(this);
        mDailerInvest4.setOnTouchListener(this);
    }

    private void setDialerFourListener() {

        mDailerInvest4.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {
                mEtAnnualIncrement.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtAnnualIncrement.getText().toString());
                        int updateValue = 0;
                        if (mAnnualAmountCount != percentage) {
                            if (percentage > mAnnualAmountCount) {
                                updateValue = (currentValue + 100);
                            } else if (percentage < mAnnualAmountCount) {
                                updateValue = (currentValue - 100);
                            }

                            mAnnualAmountCount = percentage;
                            if (updateValue < 100) {
                                mEtAnnualIncrement.setText("" + 0);
                            } else {
                                mEtAnnualIncrement.setText("" + updateValue);
                            }
                            if (mBrokerActivity != null)
                                audioPlayer.play(mBrokerActivity);
                            else
                                audioPlayer.play(mActivity);
                            calculateAmount();
                        }

                    }
                });
            }
        });
    }

    private void setDialerThreeListener() {
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
                            else if (updateValue > 30.00)
                                mEtRate.setText("" + 30.00);
                            else
                                mEtRate.setText("" + updateValue);
                            if (mBrokerActivity != null) {
                                audioPlayer.play(mBrokerActivity);
                            } else {
                                audioPlayer.play(mActivity);
                            }
                            calculateAmount();
                        }
                    }
                });
            }
        });
    }

    private void setDialerTwoListener() {

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
                            if (mBrokerActivity != null) {
                                audioPlayer.play(mBrokerActivity);
                            } else {
                                audioPlayer.play(mActivity);
                            }
                            calculateAmount();
                        }

                    }
                });
            }
        });
    }

    private void setDialerOneListener() {
        mDailerInvest1.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
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
                            if (updateValue < 500) {
                                mEtAmount.setText("" + 500);
                            } else {
                                mEtAmount.setText("" + updateValue);
                            }
                            if (mBrokerActivity != null) {
                                audioPlayer.play(mBrokerActivity);
                            } else {
                                audioPlayer.play(mActivity);
                            }
                            calculateAmount();
                        }


                    }
                });
            }
        });
    }

    @Override
    public void onStateChange(boolean newstate) {

    }

    @Override
    public void onRotate(int percentage) {

    }

    @Override
    public void onClick(View view) {
      /*  if (view.getId() == R.id.ivRight) {
            saveFrameLayout();
        }*//* else if (view.getId() == R.id.ivRight2) {
            refreshView();
        }*/ /*else*/


    }


    private void refreshView() {
        mDailerInvest1.setRotorPercentage(0);
        mDailerInvest2.setRotorPercentage(0);
        mDailerInvest3.setRotorPercentage(0);
        mDailerInvest4.setRotorPercentage(0);
        mEtAmount.setText("1000");
        mEtYear.setText("10");
        mEtRate.setText("10.00");
        mEtAnnualIncrement.setText("0");
        calculateAmount();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyPad();
        String currentValue = "";
        int i = view.getId();
        if (i == R.id.dialer1) {
            currentValue = mEtAmount.getText().toString().replaceAll(",", "");
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 500)
                    mEtAmount.setText("" + 500);
            } else {
                mEtAmount.setText("" + 500);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer2) {
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

        } else if (i == R.id.dialer3) {
            currentValue = mEtRate.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 2)
                    mEtRate.setText("" + 1.00);
                else if (value > 30)
                    mEtRate.setText("" + 30.00);
            } else {
                mEtRate.setText("" + 1);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer4) {
            currentValue = mEtAnnualIncrement.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 100)
                    mEtAnnualIncrement.setText("" + 0);

            } else {
                mEtAnnualIncrement.setText("" + 0);
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
        if (mBrokerActivity != null) {
            InputMethodManager inputManager = (InputMethodManager) mBrokerActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = mBrokerActivity.getCurrentFocus();

            if (focusedView != null) {
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } else {
            InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = mActivity.getCurrentFocus();

            if (focusedView != null) {
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void delayCall() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (mBrokerActivity != null) {
                            mBrokerActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    calculateAmount();
                                }
                            });

                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    calculateAmount();
                                }
                            });
                        }
                    }
                },
                DELAY
        );
    }

    private void calculateAmount() {
        if (mBrokerActivity != null) {
            if (mEtAmount.getText().toString().replaceAll(",", "").equals("") || mEtAmount.getText().toString().replaceAll(",", "").equals("0")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.new_purchase_error_empty_amnt));
                tvErrorMonth.setText(getResources().getString(R.string.new_purchase_error_empty_amnt));
                setMonthlyErrorVisibility();
            } else if ((Integer.parseInt(mEtAmount.getText().toString().replaceAll(",", ""))) < 500) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_setup_error_amnt));
                tvErrorMonth.setText(getResources().getString(R.string.sip_setup_error_amnt));
                setMonthlyErrorVisibility();
            } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_setup_invest_duration));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_setup_invest_duration));
                setInvestPeriodErrorVisibility();
            } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
                mEtYear.setText("50");
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_setup_max_duration));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_setup_max_duration));
                setInvestPeriodErrorVisibility();
            } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_setup_min_duration));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_setup_min_duration));
                setAnnualReturnErrorVisibility();
            } else if (Double.parseDouble(mEtRate.getText().toString()) > 30) {
                mEtRate.setText("30.00");
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_setup_error_min_ror));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_setup_error_min_ror));
                setAnnualReturnErrorVisibility();
            } else {
                String rawValue = mEtAmount.getText().toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue, mEtAmount);

                try {
                    w_delay = 0;
                    ;
                    w_tot = 0;
                    amt_inv = 0;
                    amtD = 0;
                    amtD = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    amount = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    tD = Integer.parseInt(mEtYear.getText().toString());
                    rorD = Double.parseDouble(mEtRate.getText().toString());
                    if (mEtAnnualIncrement.getText().toString().equals(""))
                        yearlyiD = 0;
                    else
                        yearlyiD = Double.parseDouble(mEtAnnualIncrement.getText().toString());

                    //***************************** Formula as per Jain Sir
                  /* double tt = tD * 12;
                    double rt = rorD / 100;
                    //amt_inv = tt * amtD;
                    // Formula as per Jain Sir
                    for (int i = 1; i <= tt; i++) {
                        if (i != 1) {
                            if (i % 12 == 1) {
                                amtD = amtD + yearlyiD;

                            }
                        }
                        amt_inv = amt_inv + amtD;
                        w_delay = amtD * (Math.pow((1 + rt), (tt - i) / 12));
                        w_tot += w_delay;

                    }*/
                    //*****************************

                    //***************************** formula2 as per Anuj 17 June 2021
                    double month = tD * 12;
                    double rateOfReturn = rorD / 12;
                    for (int i = 1; i <= month; i++) {
                        if (i != 1) {
                            if (i % 12 == 1) {
                                amtD = amtD + yearlyiD;

                            }
                        }
                        amt_inv = amt_inv + amtD;

                        w_delay = amtD * (Math.pow((1 + rateOfReturn / 100), i));
                        w_tot += w_delay;
                    }
                    //*****************************

                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    String strAmount = format.format(amount);
                    String[] resultAmount = strAmount.split("\\.", 0);

                    String strAnnualAmount = format.format(yearlyiD);
                    String[] resultAmount2 = strAnnualAmount.split("\\.", 0);

                    // mTvSipTitle.setText("Monthly Step up SIP of " + resultAmount[0] + " for a period of 10 years with Returns of " + rorD + "% yearly, will grow to ");
                    mTvSipTitle.setText("Monthly SIP of " + resultAmount[0] + " incremented annually by " + resultAmount2[0] + " for " + (int) tD + " years will grow to");

                    long value1 = Math.round(w_tot);
                    String str = format.format(value1);
                    String[] result = str.split("\\.", 0);
                    mTvFutureAmount.setText(result[0]);

                    long value2 = Math.round(amt_inv);
                    String investAmount = format.format(value2);
                    String[] invesRresult = investAmount.split("\\.", 0);
                    mTvTotalInvest.setText(invesRresult[0]);

                    Double onlyProfit = (w_tot - amt_inv);
                    long value3 = Math.round(onlyProfit);
                    String strProfit = format.format(value3);
                    String[] arrayProfit = strProfit.split("\\.", 0);
                    mTvWealth.setText(arrayProfit[0]);


                    int percentage = (int) ((amt_inv * 100) / w_tot);
                    double times = (w_tot / amt_inv);
                    if (Double.isNaN(times)) {
                        mTvInvestedTimes.setText("0 times");
                    } else {
                        String strTimes = String.format("%.2f", times);
                        mTvInvestedTimes.setText("" + strTimes + " times");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


               /* try {
                    w_delay = 0;
                    ;
                    w_tot = 0;
                    amt_inv = 0;
                    amtD = 0;
                    amtD = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    amount = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    tD = Integer.parseInt(mEtYear.getText().toString());
                    rorD = Double.parseDouble(mEtRate.getText().toString());
                    if (mEtAnnualIncrement.getText().toString().equals(""))
                        yearlyiD = 0;
                    else
                        yearlyiD = Double.parseDouble(mEtAnnualIncrement.getText().toString());

                    double tt = tD * 12;
                    double rt = rorD / 100;
                    //amt_inv = tt * amtD;
                    // Formula as per Jain Sir
                    for (int i = 1; i <= tt; i++) {
                        if (i != 1) {
                            if (i % 12 == 1) {
                                amtD = amtD + yearlyiD;

                            }
                        }
                        amt_inv = amt_inv + amtD;
                        w_delay = amtD * (Math.pow((1 + rt), (tt - i) / 12));
                        w_tot += w_delay;

                    }


                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    String strAmount = format.format(amount);
                    String[] resultAmount = strAmount.split("\\.", 0);

                    String strAnnualAmount = format.format(yearlyiD);
                    String[] resultAmount2 = strAnnualAmount.split("\\.", 0);

                    // mTvSipTitle.setText("Monthly Step up SIP of " + resultAmount[0] + " for a period of 10 years with Returns of " + rorD + "% yearly, will grow to ");
                    mTvSipTitle.setText("Monthly SIP of " + resultAmount[0] + " incremented annually by " + resultAmount2[0] + " for " + (int) tD + " years will grow to");

                    long value1 = Math.round(w_tot);
                    String str = format.format(value1);
                    String[] result = str.split("\\.", 0);
                    mTvFutureAmount.setText(result[0]);

                    long value2 = Math.round(amt_inv);
                    String investAmount = format.format(value2);
                    String[] invesRresult = investAmount.split("\\.", 0);
                    mTvTotalInvest.setText(invesRresult[0]);

                    Double onlyProfit = (w_tot - amt_inv);
                    long value3 = Math.round(onlyProfit);
                    String strProfit = format.format(value3);
                    String[] arrayProfit = strProfit.split("\\.", 0);
                    mTvWealth.setText(arrayProfit[0]);


                    int percentage = (int) ((amt_inv * 100) / w_tot);
                    double times = (w_tot / amt_inv);
                    if (Double.isNaN(times)) {
                        mTvInvestedTimes.setText("0 times");
                    } else {
                        String strTimes = String.format("%.2f", times);
                        mTvInvestedTimes.setText("" + strTimes + " times");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        } else {
            if (mEtAmount.getText().toString().replaceAll(",", "").equals("") || mEtAmount.getText().toString().replaceAll(",", "").equals("0")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.new_purchase_error_empty_amnt));
                tvErrorMonth.setText(getResources().getString(R.string.new_purchase_error_empty_amnt));
                setMonthlyErrorVisibility();
            } else if ((Integer.parseInt(mEtAmount.getText().toString().replaceAll(",", ""))) < 500) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_setup_error_amnt));
                tvErrorMonth.setText(getResources().getString(R.string.sip_setup_error_amnt));
                setMonthlyErrorVisibility();
            } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_setup_invest_duration));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_setup_invest_duration));
                setInvestPeriodErrorVisibility();
            } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
                mEtYear.setText("50");
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_setup_max_duration));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_setup_max_duration));
                setInvestPeriodErrorVisibility();
            } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_setup_min_duration));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_setup_min_duration));
                setAnnualReturnErrorVisibility();
            } else if (Double.parseDouble(mEtRate.getText().toString()) > 30) {
                mEtRate.setText("30.00");
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_setup_error_min_ror));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_setup_error_min_ror));
                setAnnualReturnErrorVisibility();
            } else {
                String rawValue = mEtAmount.getText().toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue, mEtAmount);
                try {
                    w_delay = 0;
                    ;
                    w_tot = 0;
                    amt_inv = 0;
                    amtD = 0;
                    amtD = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    amount = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    tD = Integer.parseInt(mEtYear.getText().toString());
                    rorD = Double.parseDouble(mEtRate.getText().toString());
                    if (mEtAnnualIncrement.getText().toString().equals(""))
                        yearlyiD = 0;
                    else
                        yearlyiD = Double.parseDouble(mEtAnnualIncrement.getText().toString());

                    //***************************** Formula as per Jain Sir
                  /* double tt = tD * 12;
                    double rt = rorD / 100;
                    //amt_inv = tt * amtD;
                    // Formula as per Jain Sir
                    for (int i = 1; i <= tt; i++) {
                        if (i != 1) {
                            if (i % 12 == 1) {
                                amtD = amtD + yearlyiD;

                            }
                        }
                        amt_inv = amt_inv + amtD;
                        w_delay = amtD * (Math.pow((1 + rt), (tt - i) / 12));
                        w_tot += w_delay;

                    }*/
                    //*****************************

                    //***************************** formula2 as per Anuj 17 June 2021
                    double month = tD * 12;
                    double rateOfReturn = rorD / 12;
                    for (int i = 1; i <= month; i++) {
                        if (i != 1) {
                            if (i % 12 == 1) {
                                amtD = amtD + yearlyiD;

                            }
                        }
                        amt_inv = amt_inv + amtD;

                        w_delay = amtD * (Math.pow((1 + rateOfReturn / 100), i));
                        w_tot += w_delay;
                    }
                    //*****************************


                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    String strAmount = format.format(amount);
                    String[] resultAmount = strAmount.split("\\.", 0);

                    String strAnnualAmount = format.format(yearlyiD);
                    String[] resultAmount2 = strAnnualAmount.split("\\.", 0);

                    // mTvSipTitle.setText("Monthly Step up SIP of " + resultAmount[0] + " for a period of 10 years with Returns of " + rorD + "% yearly, will grow to ");
                    mTvSipTitle.setText("Monthly SIP of " + resultAmount[0] + " incremented annually by " + resultAmount2[0] + " for " + (int) tD + " years will grow to");

                    long value1 = Math.round(w_tot);
                    String str = format.format(value1);
                    String[] result = str.split("\\.", 0);
                    mTvFutureAmount.setText(result[0]);

                    long value2 = Math.round(amt_inv);
                    String investAmount = format.format(value2);
                    String[] invesRresult = investAmount.split("\\.", 0);
                    mTvTotalInvest.setText(invesRresult[0]);

                    Double onlyProfit = (w_tot - amt_inv);
                    long value3 = Math.round(onlyProfit);
                    String strProfit = format.format(value3);
                    String[] arrayProfit = strProfit.split("\\.", 0);
                    mTvWealth.setText(arrayProfit[0]);


                    int percentage = (int) ((amt_inv * 100) / w_tot);
                    double times = (w_tot / amt_inv);
                    if (Double.isNaN(times)) {
                        mTvInvestedTimes.setText("0 times");
                    } else {
                        String strTimes = String.format("%.2f", times);
                        mTvInvestedTimes.setText("" + strTimes + " times");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*try {
                    w_delay = 0;
                    ;
                    w_tot = 0;
                    amt_inv = 0;
                    amtD = 0;
                    amtD = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    amount = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    tD = Integer.parseInt(mEtYear.getText().toString());
                    rorD = Double.parseDouble(mEtRate.getText().toString());
                    if (mEtAnnualIncrement.getText().toString().equals(""))
                        yearlyiD = 0;
                    else
                        yearlyiD = Double.parseDouble(mEtAnnualIncrement.getText().toString());

                    double tt = tD * 12;
                    double rt = rorD / 100;
                    //amt_inv = tt * amtD;
                    // Formula as per Jain Sir
                    for (int i = 1; i <= tt; i++) {
                        if (i != 1) {
                            if (i % 12 == 1) {
                                amtD = amtD + yearlyiD;

                            }
                        }
                        amt_inv = amt_inv + amtD;
                        w_delay = amtD * (Math.pow((1 + rt), (tt - i) / 12));
                        w_tot += w_delay;

                    }


                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    String strAmount = format.format(amount);
                    String[] resultAmount = strAmount.split("\\.", 0);

                    String strAnnualAmount = format.format(yearlyiD);
                    String[] resultAmount2 = strAnnualAmount.split("\\.", 0);

                    // mTvSipTitle.setText("Monthly Step up SIP of " + resultAmount[0] + " for a period of 10 years with Returns of " + rorD + "% yearly, will grow to ");
                    mTvSipTitle.setText("Monthly SIP of " + resultAmount[0] + " incremented annually by " + resultAmount2[0] + " for " + (int) tD + " years will grow to");

                    long value1 = Math.round(w_tot);
                    String str = format.format(value1);
                    String[] result = str.split("\\.", 0);
                    mTvFutureAmount.setText(result[0]);

                    long value2 = Math.round(amt_inv);
                    String investAmount = format.format(value2);
                    String[] invesRresult = investAmount.split("\\.", 0);
                    mTvTotalInvest.setText(invesRresult[0]);

                    Double onlyProfit = (w_tot - amt_inv);
                    long value3 = Math.round(onlyProfit);
                    String strProfit = format.format(value3);
                    String[] arrayProfit = strProfit.split("\\.", 0);
                    mTvWealth.setText(arrayProfit[0]);


                    int percentage = (int) ((amt_inv * 100) / w_tot);

                    double times = (w_tot / amt_inv);
                    if (Double.isNaN(times)) {
                        mTvInvestedTimes.setText("0 times");
                    } else {
                        String strTimes = String.format("%.2f", times);
                        mTvInvestedTimes.setText("" + strTimes + " times");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
            }
        }
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
    private void clearErrorMessage() {
        tvErrorIAnnualInc.setText("");
        tvErrorAnnualReturn.setText("");
        tvErrorInvestPeriod.setText("");
        tvErrorMonth.setText("");
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorIAnnualInc.setVisibility(View.GONE);
    }
    private void setMonthlyErrorVisibility() {
        tvErrorMonth.setVisibility(View.VISIBLE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorIAnnualInc.setVisibility(View.GONE);
    }

    private void setInvestPeriodErrorVisibility() {
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.VISIBLE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorIAnnualInc.setVisibility(View.GONE);
    }

    private void setAnnualReturnErrorVisibility() {
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.VISIBLE);
        tvErrorIAnnualInc.setVisibility(View.GONE);
    }

    private void setAnnIncErrorVisibility() {
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorIAnnualInc.setVisibility(View.VISIBLE);
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

    public void saveFrameLayout() {
        mLinerFooter.setVisibility(View.VISIBLE);
        ll_calculator_header.setVisibility(View.VISIBLE);
        onShareOptionClick();
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

        path = path + "/" + "Calculator-" + getResources().getString(R.string.toolBar_title_sip_cal) + System.currentTimeMillis() + ".pdf";// path where pdf will be stored


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
            if (mBrokerActivity != null) {
                if (mBrokerActivity.getCurrentFocus() == mEtAmount) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtYear) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtRate) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtAnnualIncrement) {
                    delayCall();
                }
            } else {
                if (mActivity.getCurrentFocus() == mEtAmount) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtYear) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtRate) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtAnnualIncrement) {
                    delayCall();
                }
            }
            mResulttxt.setText("To achieve this Goal you must start Investment of \u20B9 " + mEtAmount.getText().toString() + " Monthly SIP Today.");

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            clearErrorMessage();
        }

    }
}