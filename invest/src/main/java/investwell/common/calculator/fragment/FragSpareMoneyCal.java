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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class FragSpareMoneyCal extends Fragment implements View.OnClickListener, View.OnTouchListener,
        RoundKnobButton.RoundKnobButtonListener, ToolbarFragment.ToolbarCallback {

    private final long DELAY = 1000; // milliseconds
    double w_delay = 0;
    double w_tot = 0;
    double amt_inv = 0;
    double nYears = 0, amtD = 0, rorD = 0, inflate = 0;
    Singleton m_Inst = Singleton.getInstance();
    private EditText mEtYear, mEtAmount, mEtRate, mEtExpectedInflation;
    private TextView mTvSipTitle, mTvFutureAmount, mTvTotalInvest, mTvInvestedTimes, mTvWealth, mTvImageTitle, mTvMainAmount, mTvResultAmount, invest_btn;
    private ImageView mIvImageRight, mIvRefresh, mIvBack;
    private int mAmountCount = 0, mAnnualAmountCount = 0, mYearCount = 0, mReturn = 0, mCount6 = 0;
    private ProgressBar mProgressBar;
    private AudioPlayer audioPlayer;
    private ScrollView mScrollView;
    private RoundKnobButton mDailerInvest1, mDailerInvest2, mDailerInvest3, mDailerInvest4;
    private CheckBox mInflationbox;
    private Timer timer = new Timer();
    private MainActivity mActivity;
    private AppApplication mApplication;
    private RelativeLayout mRelToolbar;
    private Bundle bundle;
    private LinearLayout mLinerFooter;
    private ToolbarFragment fragToolBar;
    private CustomTextViewBold tvHeaderTitle;
    private LinearLayout ll_calculator_header;
    private BrokerActivity mBrokerActivity;
    private AppSession mSession;
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb;
    private TextView tvErrorOneTime, tvErrorInvestPeriod, tvErrorAnnualReturn, tvErrorInflationRate;
    Bitmap b;
    private String path = "";

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
        View view = inflater.inflate(R.layout.frag_sparemoney, container, false);
        initialChecks();
        initializer(view);
        setUpToolBar();
        setTouchEventListeners();
        initialDialerSetup();
        setDialerOneListener();
        setDialerTwoListener();
        setDialerThreeListener();
        setDialerFourListener();
        setListeners(view);
        initialEditTextViewsSetup();
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
        bundle = getArguments();
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
        mRelToolbar = view.findViewById(R.id.relToolBar);
        mTvMainAmount = view.findViewById(R.id.result1);
        mTvResultAmount = view.findViewById(R.id.result2);
        mEtAmount = view.findViewById(R.id.edit1);
        mEtYear = view.findViewById(R.id.edit2);
        mEtRate = view.findViewById(R.id.edit3);
        mEtExpectedInflation = view.findViewById(R.id.edit4);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        mTvWealth = view.findViewById(R.id.textView8);
        mScrollView = view.findViewById(R.id.sv_lumpsum);
        mInflationbox = view.findViewById(R.id.ch_inflation);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        tvAddress = view.findViewById(R.id.tv_address);
        mTvSipTitle = view.findViewById(R.id.result_text);
        mTvFutureAmount = view.findViewById(R.id.textView2);
        mTvTotalInvest = view.findViewById(R.id.textView4);
        mTvInvestedTimes = view.findViewById(R.id.textView6);
        mIvBack = view.findViewById(R.id.ivLeft);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        invest_btn = view.findViewById(R.id.invest_btn);
        mLinerFooter.setVisibility(View.GONE);
        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest4 = view.findViewById(R.id.dialer4);
        tvErrorOneTime = view.findViewById(R.id.tv_error_one_time);
        tvErrorInvestPeriod = view.findViewById(R.id.tv_error_invest_period);
        tvErrorAnnualReturn = view.findViewById(R.id.tv_error_annual_return);
        tvErrorInflationRate = view.findViewById(R.id.tv_error_inflate_rate);
    }

    private void clearErrorMessage() {
        tvErrorInflationRate.setText("");
        tvErrorAnnualReturn.setText("");
        tvErrorInvestPeriod.setText("");
        tvErrorOneTime.setText("");
        tvErrorOneTime.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorInflationRate.setVisibility(View.GONE);
    }

    private void setOneTimeErrorVisibility() {
        tvErrorOneTime.setVisibility(View.VISIBLE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorInflationRate.setVisibility(View.GONE);
    }

    private void setInvestPeriodErrorVisibility() {
        tvErrorOneTime.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.VISIBLE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorInflationRate.setVisibility(View.GONE);
    }

    private void setAnnualReturnErrorVisibility() {
        tvErrorOneTime.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.VISIBLE);
        tvErrorInflationRate.setVisibility(View.GONE);
    }

    private void setRateErrorVisibility() {
        tvErrorOneTime.setVisibility(View.GONE);
        tvErrorInvestPeriod.setVisibility(View.GONE);
        tvErrorAnnualReturn.setVisibility(View.GONE);
        tvErrorInflationRate.setVisibility(View.VISIBLE);
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot")) &&
                    investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot").equalsIgnoreCase("Y")) {

                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_lumpsum_cal), true, true, false, false, true, false, false, "");
            } else {
                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_lumpsum_cal), true, false, false, false, true, false, false, "");

            }


            fragToolBar.setCallback(this);
        }
        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_lumpsum_cal));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchEventListeners() {
        mDailerInvest1.setOnTouchListener(this);
        mDailerInvest2.setOnTouchListener(this);
        mDailerInvest3.setOnTouchListener(this);
        mDailerInvest4.setOnTouchListener(this);
    }

    private void initialDialerSetup() {
        if (mBrokerActivity != null) {
            mDailerInvest1.RoundKnobButton3(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton3(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton3(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton3(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        } else {
            mDailerInvest1.RoundKnobButton3(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton3(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton3(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton3(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        }
    }

    private void setDialerOneListener() {
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
                            if (updateValue < 500) {
                                mEtAmount.setText("" + 500);
                            } else {
                                mEtAmount.setText("" + updateValue);
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

    private void setDialerFourListener() {
        mDailerInvest4.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {
                mEtExpectedInflation.post(new Runnable() {
                    public void run() {
                        double currentValue = Double.parseDouble(mEtExpectedInflation.getText().toString());
                        double updateValue = 0;
                        if (mCount6 != percentage) {
                            if (percentage > mCount6) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount6) {
                                updateValue = (currentValue - 1);
                            }

                            mCount6 = percentage;
                            if (updateValue < 1) {
                                mEtExpectedInflation.setText("" + 1.00);
                            } else if (updateValue > 30) {
                                mEtExpectedInflation.setText("" + 20.00);
                            } else {
                                mEtExpectedInflation.setText("" + updateValue);
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

    private void initialEditTextViewsSetup() {
        mEtAmount.setText("10000");
        mEtYear.setText("10");
        mEtRate.setText("10.00");
        mEtExpectedInflation.setText("6.00");
    }

    private void setListeners(View view) {
        view.findViewById(R.id.invest_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int duration = Integer.parseInt(mEtYear.getText().toString());

                Bundle bundle = new Bundle();
                bundle.putString("type", "coming_from_goal");
                bundle.putString("investment_type", "Lumpsum");
                bundle.putString("ic_invest_route_goal", "Spare Money");
                bundle.putString("Amount", mTvMainAmount.getText().toString());
                bundle.putInt("duration", duration);
                if (mBrokerActivity != null)
                    mBrokerActivity.displayViewOther(64, bundle);
                else
                    mActivity.displayViewOther(64, bundle);

            }
        });
        mInflationbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculateAmount();
            }
        });
        //refreshView();
        mEtAmount.addTextChangedListener(new GenericTextWatcher(mEtAmount));
        mEtRate.addTextChangedListener(new GenericTextWatcher(mEtRate));
        mEtYear.addTextChangedListener(new GenericTextWatcher(mEtYear));
        mEtExpectedInflation.addTextChangedListener(new GenericTextWatcher(mEtExpectedInflation));
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
        } *//*else if (view.getId() == R.id.ivRight2) {
            refreshView();
        }*//* else if (view.getId() == R.id.ivLeft) {
            mActivity.getSupportFragmentManager().popBackStack();
        }*/

    }

    private void refreshView() {
        mDailerInvest1.setRotorPercentage(50);
        mDailerInvest2.setRotorPercentage(50);
        mDailerInvest3.setRotorPercentage(50);
        mDailerInvest4.setRotorPercentage(50);
        mEtAmount.setText("10000");
        mEtYear.setText("10");
        mEtRate.setText("10.00");
        mEtExpectedInflation.setText("6.00");
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
                mEtRate.setText("" + 1.00);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer4) {
            currentValue = mEtExpectedInflation.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtExpectedInflation.setText("" + 0.00);
                else if (value > 30)
                    mEtRate.setText("" + 30.00);

            } else {
                mEtExpectedInflation.setText("" + 6.00);
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
                        if (mBrokerActivity != null)
                            mBrokerActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    calculateAmount();
                                }
                            });

                        else
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

        if (mBrokerActivity != null) {
            if (mEtAmount.getText().toString().replaceAll(",", "").replaceAll(",", "").equals("") || mEtAmount.getText().toString().replaceAll(",", "").equals("0")) {
                //   mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumsum_cal_error_empty_sip_amnt));
                tvErrorOneTime.setText(getResources().getString(R.string.lumsum_cal_error_empty_sip_amnt));
                setOneTimeErrorVisibility();
            } else if ((Long.parseLong(mEtAmount.getText().toString().replaceAll(",", ""))) < 500) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_less_amnt));
                tvErrorOneTime.setText(getResources().getString(R.string.lumpsum_cal_error_less_amnt));
                setOneTimeErrorVisibility();
            } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_more_amnt));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.lumpsum_cal_error_more_amnt));
                setInvestPeriodErrorVisibility();
            } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
                mEtYear.setText("50");
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumspum_cal_error_duration));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.lumspum_cal_error_duration));
                setInvestPeriodErrorVisibility();
            } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_ror));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.lumpsum_cal_error_ror));
                setAnnualReturnErrorVisibility();
            } else if (Double.parseDouble(mEtRate.getText().toString()) > 30.00) {
                mEtRate.setText("30.00");
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_max_amnt));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.lumpsum_cal_error_max_amnt));
                setAnnualReturnErrorVisibility();
            } else if (mEtExpectedInflation.getText().toString().equals("") || mEtExpectedInflation.getText().toString().equals("0")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_roi));
                tvErrorInflationRate.setText(getResources().getString(R.string.lumpsum_cal_error_roi));
                setRateErrorVisibility();
            } else if (Double.parseDouble(mEtExpectedInflation.getText().toString()) > 30) {
                mEtExpectedInflation.setText("30.00");
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_max_roi));
                tvErrorInflationRate.setText(getResources().getString(R.string.lumpsum_cal_error_max_roi));
                setRateErrorVisibility();
            } else {

                String rawValue = mEtAmount.getText().toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue, mEtAmount);

                try {
                    w_delay = 0;
                    w_tot = 0;
                    amt_inv = 0;
                    amtD = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    nYears = Integer.parseInt(mEtYear.getText().toString());
                    rorD = Double.parseDouble(mEtRate.getText().toString());
                    inflate = Double.parseDouble(mEtExpectedInflation.getText().toString());
                    double inrateD = Double.parseDouble("0");


                    double r = rorD / 100;
                    double nMonths = (nYears * 12);
                    double v = 0;

                    if (mInflationbox.isChecked()) {

                        double ror = rorD - inflate;
//
                        w_tot = amtD * (Math.pow((1 + ror / 100), nYears));
                    } else {
                        w_tot = amtD * (Math.pow((1 + r), nYears));
                    }

/*                for (int i = 0; i < nMonths; i++) {
                    amt_inv = amt_inv + amtD;
                    v = v + Math.pow((1 + r), (nMonths - i) / 12);
                }*/

//                w_tot = amtD * v;

                    /*  mLinerResult.setVisibility(View.VISIBLE);*/
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));


                    String strAmount = format.format(amtD);
                    String[] resultAmount = strAmount.split("\\.", 0);
                    String strAmount1 = format.format(w_tot);
                    String[] resultAmount1 = strAmount1.split("\\.", 0);

                    mTvResultAmount.setText(resultAmount1[0]);
                    mTvSipTitle.setText(" for " + ((int) nYears) + " years at " + (rorD) + "% annual return will grow\nyour money to ");
                    mTvMainAmount.setText(resultAmount[0]);

                    long value = Math.round(w_tot);
                    String str = format.format(value);
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
                    mProgressBar.setProgress(percentage);
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


            }
        } else {
            if (mEtAmount.getText().toString().replaceAll(",", "").replaceAll(",", "").equals("") || mEtAmount.getText().toString().replaceAll(",", "").equals("0")) {
                // mEtAmount.setError(getResources().getString(R.string.lumsum_cal_error_empty_sip_amnt));
                tvErrorOneTime.setText(getResources().getString(R.string.lumsum_cal_error_empty_sip_amnt));
                setOneTimeErrorVisibility();

            } else if ((Long.parseLong(mEtAmount.getText().toString().replaceAll(",", ""))) < 500) {
                //mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_less_amnt));
                tvErrorOneTime.setText(getResources().getString(R.string.lumpsum_cal_error_less_amnt));
                setOneTimeErrorVisibility();
                //mEtAmount.setError("Minimum amount will be at least 500");
            } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_more_amnt));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.lumpsum_cal_error_more_amnt));
                setInvestPeriodErrorVisibility();
            } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
                mEtYear.setText("50");
                setInvestPeriodErrorVisibility();
                //mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.lumspum_cal_error_duration));
                tvErrorInvestPeriod.setText(getResources().getString(R.string.lumspum_cal_error_duration));
            } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
                //mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_ror));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.lumpsum_cal_error_ror));
                setAnnualReturnErrorVisibility();
            } else if (Double.parseDouble(mEtRate.getText().toString()) > 30) {
                mEtRate.setText("30.00");
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_max_amnt));
                tvErrorAnnualReturn.setText(getResources().getString(R.string.lumpsum_cal_error_max_amnt));
                setAnnualReturnErrorVisibility();
            } else if (mEtExpectedInflation.getText().toString().equals("") || mEtExpectedInflation.getText().toString().equals("0")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_roi));
                tvErrorInflationRate.setText(getResources().getString(R.string.lumpsum_cal_error_roi));
                setRateErrorVisibility();
            } else if (Double.parseDouble(mEtExpectedInflation.getText().toString()) > 30) {
                mEtExpectedInflation.setText("30.00");
                //mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.lumpsum_cal_error_max_roi));
                tvErrorInflationRate.setText(getResources().getString(R.string.lumpsum_cal_error_max_roi));
                setRateErrorVisibility();
            } else {

                String rawValue = mEtAmount.getText().toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue, mEtAmount);

                try {
                    w_delay = 0;
                    w_tot = 0;
                    amt_inv = 0;
                    amtD = Double.parseDouble(mEtAmount.getText().toString().replaceAll(",", ""));  // spi calculator
                    nYears = Integer.parseInt(mEtYear.getText().toString());
                    rorD = Double.parseDouble(mEtRate.getText().toString());
                    inflate = Double.parseDouble(mEtExpectedInflation.getText().toString());
                    double inrateD = Double.parseDouble("0");


                    double r = rorD / 100;
                    double nMonths = (nYears * 12);
                    double v = 0;

                    if (mInflationbox.isChecked()) {

                        double ror = rorD - inflate;
//
                        w_tot = amtD * (Math.pow((1 + ror / 100), nYears));
                    } else {
                        w_tot = amtD * (Math.pow((1 + r), nYears));
                    }

/*                for (int i = 0; i < nMonths; i++) {
                    amt_inv = amt_inv + amtD;
                    v = v + Math.pow((1 + r), (nMonths - i) / 12);
                }*/

//                w_tot = amtD * v;

                    /*          mLinerResult.setVisibility(View.VISIBLE);*/
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));


                    String strAmount = format.format(amtD);
                    String[] resultAmount = strAmount.split("\\.", 0);
                    String strAmount1 = format.format(w_tot);
                    String[] resultAmount1 = strAmount1.split("\\.", 0);

                    mTvResultAmount.setText(resultAmount1[0]);
                    mTvSipTitle.setText(" for " + ((int) nYears) + " years at " + (rorD) + "% annual return will grow\nyour money to ");
                    mTvMainAmount.setText(resultAmount[0]);

                    long value = Math.round(w_tot);
                    String str = format.format(value);
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
                    mProgressBar.setProgress(percentage);
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

        path = path + "/" + "Calculator-" + getResources().getString(R.string.toolBar_title_lumpsum_cal) + System.currentTimeMillis() + ".pdf";// path where pdf will be stored


        int totalHeight = mScrollView.getChildAt(0).getHeight();
        int totalWidth = mScrollView.getChildAt(0).getWidth();

        //Save bitmap to  below path
        String extr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Calculators/";
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
               /*   if(mActivity!=null){
                    mActivity.startActivity(email);
                }else {
                    mBrokerActivity.startActivity(email);
                }*/
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
                } else if (mBrokerActivity.getCurrentFocus() == mEtExpectedInflation) {
                    delayCall();
                }

            } else {
                if (mActivity.getCurrentFocus() == mEtAmount) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtYear) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtRate) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtExpectedInflation) {
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
