package investwell.common.calculator.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
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

public class FragSipDelayCost extends Fragment implements View.OnClickListener, View.OnTouchListener,
        RoundKnobButton.RoundKnobButtonListener, ToolbarFragment.ToolbarCallback {
    Bitmap b;
    private String path = "";
    private final long DELAY = 1000; // milliseconds
    Singleton m_Inst = Singleton.getInstance();
    double nSIP = 0, nYears = 0;
    double w_delay = 0;
    double w_tot = 0, w_tot_with_delay = 0;

    double r, nRate = 0, delaycharge = 0;
    double i, nMonths, z;
    double mMaturityDelay = 0, mInvestAmountFromToday = 0, mInvestAmountWithDelay = 0;
    private EditText mEtYear, mEtAmount, mEtRate, mEtDelay;
    private TextView mTvSipTitle, mTvSipTitle2, mTvSipValue, mTvFutureAmount, mTvResult1, mTvResult2, mTvResult3, mTvResult4, mTvImageTitle;
    private LinearLayout mLinerResult;
    private ImageView mIvImageRight, mIvRefresh, mIvBack;
    private int mAmountCount = 0, mAnnualAmountCount = 0, mYearCount = 0, mReturn = 01;
    private ProgressBar mProgressBar, mProgressBar2;
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
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb;
    private TextView tvErrorMonth, tvErrorInvestPeriod, tvErrorAnnualReturn, tvErrorDelay;

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
        View view = inflater.inflate(R.layout.frag_delay_cost, container, false);
        initialChecks();
        initializer(view);
        setUpToolBar();
        initialDialerSetUp();
        setOmTouchListeners();
        setDialerOneListener();
        setDialerTwoListener();
        setDialerThreeListener();
        setDialerFourListener();
        setListeners(view);
        calculateAmount();
        setUpFooterViewsData();
        return view;
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
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
        tvErrorMonth = view.findViewById(R.id.tv_error_monthly_invest);
        tvErrorInvestPeriod = view.findViewById(R.id.tv_error_invest_period);
        tvErrorAnnualReturn = view.findViewById(R.id.tv_error_ann_ret);
        tvErrorDelay = view.findViewById(R.id.tv_error_delay);
        mRelToolbar = view.findViewById(R.id.relToolBar);
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        mEtAmount = view.findViewById(R.id.edit1);
        mEtYear = view.findViewById(R.id.edit2);
        mEtRate = view.findViewById(R.id.edit3);
        mEtDelay = view.findViewById(R.id.edit4);
        mScrollView = view.findViewById(R.id.scrollView);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        mTvSipTitle = view.findViewById(R.id.textView1);
        mTvSipTitle2 = view.findViewById(R.id.textView11);
        mTvFutureAmount = view.findViewById(R.id.textView2);
        mTvSipValue = view.findViewById(R.id.textView22);
        mTvResult1 = view.findViewById(R.id.tvResult1);
        mTvResult2 = view.findViewById(R.id.tvResult2);
        mTvResult3 = view.findViewById(R.id.tvResult3);
        mTvResult4 = view.findViewById(R.id.tvResult4);
        tvAddress = view.findViewById(R.id.tv_address);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        mLinerResult = view.findViewById(R.id.linerResult);
        mIvBack = view.findViewById(R.id.ivLeft);
        mProgressBar = view.findViewById(R.id.progress_bar1);
        mProgressBar2 = view.findViewById(R.id.progress_bar2);
        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest4 = view.findViewById(R.id.dialer4);
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot")) &&
                    investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot").equalsIgnoreCase("Y")) {

                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_delay_sip_cal),
                        true, true, false, false, true, false, false, "");
            } else {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_delay_sip_cal),
                        true, false, false, false, true, false, false, "");

            }
        }
        if (toolbarFragment != null) {
            toolbarFragment.setCallback(this);
        }
        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_delay_sip_cal));
    }
    private void setDialerFourListener() {
        mDailerInvest4.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtDelay.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtDelay.getText().toString());
                        int updateValue = 0;
                        if (mAnnualAmountCount != percentage) {
                            if (percentage > mAnnualAmountCount) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mAnnualAmountCount) {
                                updateValue = (currentValue - 1);
                            }

                            mAnnualAmountCount = percentage;
                            if (updateValue < 1) {
                                mEtDelay.setText("" + 1);
                            } else if (updateValue > 120) {
                                mEtDelay.setText("" + 120);
                            } else {
                                mEtDelay.setText("" + updateValue);
                            }

                            if (mBrokerActivity != null) {
                                audioPlayer.play(mBrokerActivity);
                                calculateAmount();
                            } else {
                                audioPlayer.play(mActivity);
                                calculateAmount();
                            }
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
                                calculateAmount();
                            } else {
                                audioPlayer.play(mActivity);
                                calculateAmount();
                            }
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
                                calculateAmount();
                            } else {
                                audioPlayer.play(mActivity);
                                calculateAmount();
                            }
                        }

                    }
                });
            }
        });


    }

    private void setDialerOneListener() {
        mDailerInvest1.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
            }

            public void onRotate(final int percentage) {
                System.out.println("" + percentage);
                mEtAmount.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtAmount.getText().toString().replaceAll(",", "").toString());
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
                                calculateAmount();
                            } else {
                                audioPlayer.play(mActivity);
                                calculateAmount();
                            }

                        }


                    }
                });
            }
        });
    }


    private void setUpFooterViewsData() {
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

    private void setListeners(View view) {
        mEtAmount.addTextChangedListener(new GenericTextWatcher(mEtAmount));
        mEtRate.addTextChangedListener(new GenericTextWatcher(mEtRate));
        mEtYear.addTextChangedListener(new GenericTextWatcher(mEtYear));
        mEtDelay.addTextChangedListener(new GenericTextWatcher(mEtDelay));
    }

    private void setOmTouchListeners() {
        mDailerInvest1.setOnTouchListener(this);
        mDailerInvest2.setOnTouchListener(this);
        mDailerInvest3.setOnTouchListener(this);
        mDailerInvest4.setOnTouchListener(this);
    }

    private void initialDialerSetUp() {
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
    private void clearErrorMessage() {
        tvErrorDelay.setText("");
        tvErrorAnnualReturn.setText("");
        tvErrorInvestPeriod.setText("");
        tvErrorMonth.setText("");
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorDelay.setVisibility(View.GONE);
    }
    private void setMonthlyErrorVisibility() {
        tvErrorMonth.setVisibility(View.VISIBLE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorDelay.setVisibility(View.GONE);
    }

    private void setInvestPeriodErrorVisibility() {
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.VISIBLE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorDelay.setVisibility(View.GONE);
    }

    private void setAnnualReturnErrorVisibility() {
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.VISIBLE);
        tvErrorDelay.setVisibility(View.GONE);
    }

    private void setDelayErrorVisibility() {
        tvErrorMonth.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorDelay.setVisibility(View.VISIBLE);
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
        } *//*else */

    }


    private void refreshView() {
        mDailerInvest1.setRotorPercentage(0);
        mDailerInvest2.setRotorPercentage(0);
        mDailerInvest3.setRotorPercentage(0);
        mDailerInvest4.setRotorPercentage(0);
        mEtAmount.setText("1000");
        mEtYear.setText("10");
        mEtRate.setText("10.00");
        mEtDelay.setText("5");
        calculateAmount();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyPad();
        String currentValue = "";
        int i1 = view.getId();
        if (i1 == R.id.dialer1) {
            currentValue = mEtAmount.getText().toString().replaceAll(",", "").toString();
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 500)
                    mEtAmount.setText("" + 500);
            } else {
                mEtAmount.setText("" + 500);
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

        } else if (i1 == R.id.dialer4) {
            currentValue = mEtDelay.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 1)
                    mEtDelay.setText("" + 1);
                else if (value > 120)
                    mEtDelay.setText("" + 120);

            } else {
                mEtDelay.setText("" + 1);
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

    @SuppressLint("SetTextI18n")
    private void calculateAmount() {
        if (mBrokerActivity != null) {
            if (mEtAmount.getText().toString().replaceAll(",", "").toString().equals("") || mEtAmount.getText().toString().replaceAll(",", "").toString().equals("0")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_empty_amount));
                tvErrorMonth.setText(getResources().getString(R.string.error_empty_amount));
                setMonthlyErrorVisibility();
            } else if ((Integer.parseInt(mEtAmount.getText().toString().replaceAll(",", "").toString())) < 500) {
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_delay_cal_minn_amnt));
                tvErrorMonth.setText(getResources().getString(R.string.sip_delay_cal_minn_amnt));
                setMonthlyErrorVisibility();
            } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_empty_yr));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_delay_error_empty_yr));
                setInvestPeriodErrorVisibility();
            } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
                mEtYear.setText("50");
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_delay_error_minn_amnt));
                setInvestPeriodErrorVisibility();
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_minn_amnt));
            } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_ror));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_delay_error_ror));
                setAnnualReturnErrorVisibility();
            } else if (Double.parseDouble(mEtRate.getText().toString()) > 30) {
                mEtRate.setText("30.00");
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_minn_ror));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_delay_error_minn_ror));
                setAnnualReturnErrorVisibility();
            } else if (mEtDelay.getText().toString().equals("")) {
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_delay_empty_duration));
                tvErrorDelay.setText(getResources().getString(R.string.sip_delay_empty_duration));
                setDelayErrorVisibility();
            } else if ((Integer.parseInt(mEtDelay.getText().toString())) > 120) {
                //   mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.sip_delay_minn_delay));
                tvErrorDelay.setText(getResources().getString(R.string.sip_delay_minn_delay));
                setDelayErrorVisibility();
            } else {

                String rawValue = mEtAmount.getText().toString().replaceAll(",", "").toString();
                mBrokerActivity.convertIntoCurrencyFormat(rawValue, mEtAmount);

                try {
                    nSIP = 0;
                    nYears = 0;
                    mMaturityDelay = 0;
                    mInvestAmountFromToday = 0;
                    mInvestAmountWithDelay = 0;
                    w_delay = 0;
                    w_tot = 0;
                    w_tot_with_delay = 0;

                    nSIP = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", "").toString());
                    nYears = Double.parseDouble(mEtYear.getText().toString());
                    nRate = Double.parseDouble(mEtRate.getText().toString());
                    delaycharge = Double.parseDouble(mEtDelay.getText().toString());

                    r = nRate / 100;
                    double rateOfReturn = nRate / 12;

                    nMonths = 12 * nYears;
                    mInvestAmountFromToday = nMonths * nSIP;
                    mInvestAmountWithDelay = (nMonths - delaycharge) * nSIP;
//  SIP Starting after a Delay case ----------------------------------------------------------------------------
                    for (i = 1; i <= (nMonths - delaycharge); i++) {
                        mMaturityDelay = nSIP * (Math.pow((1 + rateOfReturn / 100), i));
                        w_tot_with_delay += mMaturityDelay;
                    }

                    // v = nFV * (1 + r) * ((Math.pow(1 + r, (z - (delaycharge / 12)))) - 1) / r;

//  SIP Starting Today case  ---------------------------------------------------------------------------------------
                    for (i = 1; i <= nMonths; i++) {
                       // mMuturityFromToday = mMuturityFromToday + Math.pow((1 + r), (i / 12));
                        w_delay = nSIP * (Math.pow((1 + rateOfReturn / 100), i));
                        w_tot += w_delay;
                    }


                    mLinerResult.setVisibility(View.VISIBLE);


                    mTvSipTitle.setText(getResources().getString(R.string.sip_delay_title_desc));

                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    long value1 = Math.round(w_tot);
                    String str = format.format(value1);
                    String[] result = str.split("\\.", 0);
                    mTvFutureAmount.setText(result[0]);

                    mTvSipTitle2.setText("If you start SIP with delay of " + Math.round(delaycharge) + " months then your Maturity value would be");

                    long value2 = Math.round(w_tot_with_delay);
                    String str2 = format.format(value2);
                    String[] result2 = str2.split("\\.", 0);
                    mTvSipValue.setText(result2[0]);


                    long value3 = Math.round(mInvestAmountFromToday);
                    String investAmount = format.format(value3);
                    String[] invesRresult = investAmount.split("\\.", 0);
                    mTvResult1.setText(invesRresult[0]);

                    Double onlyProfit = (w_tot - mInvestAmountFromToday);
                    long value4 = Math.round(onlyProfit);
                    String strProfit = format.format(value4);
                    String[] arrayProfit = strProfit.split("\\.", 0);
                    mTvResult2.setText(arrayProfit[0]);

                    String investAmount2 = format.format(mInvestAmountWithDelay);
                    String[] invesRresult2 = investAmount2.split("\\.", 0);
                    mTvResult3.setText(invesRresult2[0]);

                    Double onlyProfit2 = (w_tot_with_delay - mInvestAmountWithDelay);
                    long value5 = Math.round(onlyProfit2);
                    String strProfit2 = format.format(value5);
                    String[] arrayProfit2 = strProfit2.split("\\.", 0);
                    mTvResult4.setText(arrayProfit2[0]);


                    int percentage = (int) ((mInvestAmountFromToday * 100) / w_tot);
                    mProgressBar.setProgress(percentage);

                    int percentage2 = (int) ((mInvestAmountWithDelay * 100) / w_tot_with_delay);
                    mProgressBar2.setProgress(percentage2);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        } else {
            if (mEtAmount.getText().toString().replaceAll(",", "").toString().equals("") || mEtAmount.getText().toString().replaceAll(",", "").toString().equals("0")) {
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getString(R.string.error_empty_amount));
                tvErrorMonth.setText(getResources().getString(R.string.error_empty_amount));
                setMonthlyErrorVisibility();
            } else if ((Integer.parseInt(mEtAmount.getText().toString().replaceAll(",", "").toString())) < 500) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_delay_cal_minn_amnt));
                tvErrorMonth.setText(getResources().getString(R.string.sip_delay_cal_minn_amnt));
                setMonthlyErrorVisibility();
            } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_empty_yr));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_delay_error_empty_yr));
                setInvestPeriodErrorVisibility();
            } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
                mEtYear.setText("50");
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_minn_amnt));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.sip_delay_error_minn_amnt));
                setInvestPeriodErrorVisibility();
            } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_ror));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_delay_error_ror));
                setAnnualReturnErrorVisibility();
            } else if (Double.parseDouble(mEtRate.getText().toString()) > 30) {
                mEtRate.setText("30.00");
                tvErrorAnnualReturn.setText(getResources().getString(R.string.sip_delay_error_minn_ror));
                setAnnualReturnErrorVisibility();
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_delay_error_minn_ror));
            } else if (mEtDelay.getText().toString().equals("")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_delay_empty_duration));
                tvErrorDelay.setText(getResources().getString(R.string.sip_delay_empty_duration));
                setDelayErrorVisibility();
            } else if ((Integer.parseInt(mEtDelay.getText().toString())) > 120) {
                //mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.sip_delay_minn_delay));
                tvErrorDelay.setText(getResources().getString(R.string.sip_delay_minn_delay));
                setDelayErrorVisibility();
            } else {

                String rawValue = mEtAmount.getText().toString().replaceAll(",", "").toString();
                mActivity.convertIntoCurrencyFormat(rawValue, mEtAmount);

                try {
                    nSIP = 0;
                    nYears = 0;
                    mMaturityDelay = 0;
                    mInvestAmountFromToday = 0;
                    mInvestAmountWithDelay = 0;
                    w_delay = 0;
                    w_tot = 0;
                    w_tot_with_delay = 0;

                    nSIP = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", "").toString());
                    nYears = Double.parseDouble(mEtYear.getText().toString());
                    nRate = Double.parseDouble(mEtRate.getText().toString());
                    delaycharge = Double.parseDouble(mEtDelay.getText().toString());

                    r = nRate / 100;
                    double rateOfReturn = nRate / 12;
                    nMonths = 12 * nYears;
                    mInvestAmountFromToday = nMonths * nSIP;
                    mInvestAmountWithDelay = (nMonths - delaycharge) * nSIP;
//  SIP Starting after a Delay case ----------------------------------------------------------------------------
                    for (i = 1; i <= (nMonths - delaycharge); i++) {
                      //  mMaturityDelay = mMaturityDelay + Math.pow((1 + r), (i / 12));
                        mMaturityDelay = nSIP * (Math.pow((1 + rateOfReturn / 100), i));
                        w_tot_with_delay += mMaturityDelay;
                    }
                   // mMaturityDelay = nSIP * mMaturityDelay;

//  SIP Starting Today case  ---------------------------------------------------------------------------------------
                    for (i = 1; i <= nMonths; i++) {
                        //mMuturityFromToday = mMuturityFromToday + Math.pow((1 + r), (i / 12));
                        w_delay = nSIP * (Math.pow((1 + rateOfReturn / 100), i));
                        w_tot += w_delay;
                    }

                    mLinerResult.setVisibility(View.VISIBLE);
                    mTvSipTitle.setText("If you start SIP from today then your Maturity value would be");

                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                    long value1 = Math.round(w_tot);
                    String str = format.format(value1);
                    String[] result = str.split("\\.", 0);
                    mTvFutureAmount.setText(result[0]);

                    mTvSipTitle2.setText("If you start SIP with delay of " + Math.round(delaycharge) + " months then your Maturity value would be");

                    long value2 = Math.round(w_tot_with_delay);
                    String str2 = format.format(value2);
                    String[] result2 = str2.split("\\.", 0);
                    mTvSipValue.setText(result2[0]);


                    long value3 = Math.round(mInvestAmountFromToday);
                    String investAmount = format.format(value3);
                    String[] invesRresult = investAmount.split("\\.", 0);
                    mTvResult1.setText(invesRresult[0]);

                    Double onlyProfit = (w_tot - mInvestAmountFromToday);
                    long value4 = Math.round(onlyProfit);
                    String strProfit = format.format(value4);
                    String[] arrayProfit = strProfit.split("\\.", 0);
                    mTvResult2.setText(arrayProfit[0]);

                    String investAmount2 = format.format(mInvestAmountWithDelay);
                    String[] invesRresult2 = investAmount2.split("\\.", 0);
                    mTvResult3.setText(invesRresult2[0]);

                    Double onlyProfit2 = (w_tot_with_delay - mInvestAmountWithDelay);
                    long value5 = Math.round(onlyProfit2);
                    String strProfit2 = format.format(value5);
                    String[] arrayProfit2 = strProfit2.split("\\.", 0);
                    mTvResult4.setText(arrayProfit2[0]);

                    int percentage = (int) ((mInvestAmountFromToday * 100) / w_tot);
                    mProgressBar.setProgress(percentage);

                    int percentage2 = (int) ((mInvestAmountWithDelay * 100) / w_tot_with_delay);
                    mProgressBar2.setProgress(percentage2);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


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

        path = path + "/" + "Calculator-" + getResources().getString(R.string.toolBar_title_delay_sip_cal) + System.currentTimeMillis() + ".pdf";// path where pdf will be stored


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
                } else if (mBrokerActivity.getCurrentFocus() == mEtDelay) {
                    delayCall();
                }
            } else {
                if (mActivity.getCurrentFocus() == mEtAmount) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtYear) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtRate) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtDelay) {
                    delayCall();
                }
            }

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            clearErrorMessage();
        }

    }


}
