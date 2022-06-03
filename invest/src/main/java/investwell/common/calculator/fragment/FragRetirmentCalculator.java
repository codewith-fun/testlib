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

public class FragRetirmentCalculator extends Fragment implements View.OnClickListener,
        View.OnTouchListener, RoundKnobButton.RoundKnobButtonListener, ToolbarFragment.ToolbarCallback {
    boolean dialogShown;
    private final long DELAY = 1000; // milliseconds
    Singleton m_Inst = Singleton.getInstance();
    double dblCurrentAge = 0, dblRetirementAge = 0, dblMonthlyExpenses = 0, inflationRate = 0,
            dblMonthlySavings = 0, dblPreRetirement = 0, postRetirmentRate = 0, dlbLifeExpetancy = 0, dlbExistingCorpus = 0;
    double dblTymLeft = 0, dblAmtReq = 0, dblExistingGrowth = 0, lumpsum = 0;
    double amt_acc = 0, amt_final = 0, short_fall = 0, LumpSumInvestment=0;
    double extra_amt = 0, ret_year =0, lumsum = 0, totalSavings = 0;
    String[] resultAmount;
    private EditText mEtPresentAge, mEtRetiermentAge, mEtMonthlyExpense, mEtInflations, mEtCurrentSaving, mEtExitingCurpus, mEtPreRetirement, mEtPostRetirement, mEtLifeExpectation;
    TextView mTvCorpusAge, mTvText1, mTvText2, mTvResult4, mTvImageTitle, mTvResult1, mTvResult2;
    private ImageView mIvImageRight, mIvRefresh, mIvBack;
    private int mCount1 = 0, mCount2 = 0, mCount3 = 0, mCount4 = 0, mCount5 = 0, mCount6 = 0, mCount7 = 0, mCount8 = 0, mCount9 = 0;
    private AudioPlayer audioPlayer;
    private ScrollView mScrollView;
    private RoundKnobButton mDailerInvest1, mDailerInvest2, mDailerInvest3, mDailerInvest4, mDailerInvest5, mDailerInvest6, mDailerInvest7, mDailerInvest8, mDailerInvest9;
    private LinearLayout mLinerFooter;
    private Timer timer = new Timer();
    private MainActivity mActivity;
    private AppApplication mApplication;
    private RelativeLayout mRelToolbar;
    private String mResultLumpsum, mResultSIP, mResultShortfall;
    private ProgressBar mProgressBar;
    private Bundle bundle;
    private ToolbarFragment fragToolBar;
    private CustomTextViewBold tvHeaderTitle;
    private LinearLayout ll_calculator_header, llSipLum;
    private TextView tvAmount, tvHeaderDesc;
    private BrokerActivity mBrokerActivity;
    private AppSession mSession;
    private String path = "";
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb, tv_amount2, tvLine5,
            tvSipLumSumMessage, tvLine6;
    Bitmap b;
    private TextView tvErrorAge, tvErrorRetAge, tvErrorMonthExp, tvErrorInfRate, tvErrorSaving, tvErrorCorpus, tvErrorPreRet, tvErrPostRet, tvErrLifeExpe;

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
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_retirement_calculator, container, false);
        initialChecks();
        initializer(view);
        setUpToolBar();
        initialDialerSetUp();
        setOnTouchListener(view);
        setDialerOneListener();
        setDialerTwoListener();
        setDialerThreeListener();
        setDialerFourListener();
        setDialerFiveListener();
        setDialerSixListener();
        setDialerSevenListener();
        setDialerOctaListener();
        setDialerNineListener();
        setListeners(view);
        calculateAmount();
        setUpFooterUiData();
        return view;
    }


    private void initialChecks() {
        bundle = getArguments();
        if (mBrokerActivity != null) {
            m_Inst.InitGUIFrame(mBrokerActivity);

        } else {
            m_Inst.InitGUIFrame(mActivity);

        }
    }

    private void initializer(View view) {
        audioPlayer = new AudioPlayer();
        mRelToolbar = view.findViewById(R.id.relToolBar);
        tv_amount2 = view.findViewById(R.id.tv_amount2);
        tvLine5 = view.findViewById(R.id.tvLine5);
        llSipLum = view.findViewById(R.id.llSipLum);
        tvSipLumSumMessage = view.findViewById(R.id.tvSipLumSumMessage);
        tvLine6 = view.findViewById(R.id.tvLine6);

        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
        mEtPresentAge = view.findViewById(R.id.edit1);
        mEtRetiermentAge = view.findViewById(R.id.edit2);
        mEtMonthlyExpense = view.findViewById(R.id.edit3);
        mEtInflations = view.findViewById(R.id.edit4);
        mEtCurrentSaving = view.findViewById(R.id.edit5);
        mEtExitingCurpus = view.findViewById(R.id.edit6);
        mEtPreRetirement = view.findViewById(R.id.edit7);
        mEtPostRetirement = view.findViewById(R.id.edit8);
        mEtLifeExpectation = view.findViewById(R.id.edit9);
        tvErrorAge = view.findViewById(R.id.tv_err_age);
        tvErrorRetAge = view.findViewById(R.id.tv_err_ret_age);
        tvErrorMonthExp = view.findViewById(R.id.tv_err_month_exp);
        tvErrorInfRate = view.findViewById(R.id.tv_err_expec);
        tvErrorSaving = view.findViewById(R.id.tv_err_saving);
        tvErrorCorpus = view.findViewById(R.id.tv_err_corpus);
        tvErrorPreRet = view.findViewById(R.id.tv_err_pre_retire);
        tvErrPostRet = view.findViewById(R.id.tv_err_post_retire);
        tvErrLifeExpe = view.findViewById(R.id.tv_err_life_expectancy);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        mLinerFooter.setVisibility(View.GONE);
        mProgressBar = view.findViewById(R.id.progress_bar1);
        tvAddress = view.findViewById(R.id.tv_address);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        mScrollView = view.findViewById(R.id.scrollView);
        mEtPresentAge.requestFocus();
        mTvText2 = view.findViewById(R.id.textView2);
        mTvText1 = view.findViewById(R.id.textView1);
        tvAmount = view.findViewById(R.id.tv_amount);
        tvHeaderDesc = view.findViewById(R.id.tv_header_desc);
        mTvResult4 = view.findViewById(R.id.shortfallAmount);
        mIvBack = view.findViewById(R.id.ivLeft);
        mTvResult1 = view.findViewById(R.id.tvResult);
        mTvResult2 = view.findViewById(R.id.tvResult2);
        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest4 = view.findViewById(R.id.dialer4);
        mDailerInvest5 = view.findViewById(R.id.dialer5);
        mDailerInvest6 = view.findViewById(R.id.dialer6);
        mDailerInvest7 = view.findViewById(R.id.dialer7);
        mDailerInvest8 = view.findViewById(R.id.dialer8);
        mDailerInvest9 = view.findViewById(R.id.dialer9);
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {

            if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot")) &&
                    investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot").equalsIgnoreCase("Y")) {

                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_retirement_cal),
                        true, true, false, false, true, false, false, "");
            } else {
                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_retirement_cal),
                        true, false, false, false, true, false, false, "");

            }
            fragToolBar.setCallback(this);
        }
        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_retirement_cal));
    }

    private void setOnTouchListener(View view) {
        mDailerInvest1.setOnTouchListener(this);
        mDailerInvest2.setOnTouchListener(this);
        mDailerInvest3.setOnTouchListener(this);
        mDailerInvest4.setOnTouchListener(this);
        mDailerInvest5.setOnTouchListener(this);
        mDailerInvest6.setOnTouchListener(this);
        mDailerInvest7.setOnTouchListener(this);
        mDailerInvest8.setOnTouchListener(this);
        mDailerInvest9.setOnTouchListener(this);
        view.findViewById(R.id.sip_btn).setOnClickListener(this);


        if (bundle != null && bundle.containsKey("viewLayout"))
            view.findViewById(R.id.button_layout).setVisibility(View.VISIBLE);

    }

    private void initialDialerSetUp() {
        if (mBrokerActivity != null) {
            mDailerInvest1.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest5.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest6.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest7.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest8.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest9.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        } else {
            mDailerInvest1.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest5.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest6.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest7.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest8.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest9.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        }
    }

    private void setDialerNineListener() {
        mDailerInvest9.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtLifeExpectation.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtLifeExpectation.getText().toString());
                        int updateValue = 0;
                        if (mCount9 != percentage) {
                            if (percentage > mCount9) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount9) {
                                updateValue = (currentValue - 1);
                            }

                            mCount9 = percentage;
                            if (updateValue < 25) {
                                mEtLifeExpectation.setText("" + 25);
                            } else if (updateValue > 100)
                                mEtLifeExpectation.setText("" + 100);
                            else {
                                mEtLifeExpectation.setText("" + updateValue);
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

    private void setDialerOctaListener() {
        mDailerInvest8.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtPreRetirement.post(new Runnable() {
                    public void run() {
                        Double currentValue = Double.parseDouble(mEtPostRetirement.getText().toString());
                        double updateValue = 0;
                        if (mCount8 != percentage) {
                            if (percentage > mCount8) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount8) {
                                updateValue = (currentValue - 1);
                            }

                            mCount8 = percentage;
                            if (updateValue < 1) {
                                mEtPostRetirement.setText("" + 1.00);
                            } else if (updateValue > 30)
                                mEtPostRetirement.setText("" + 30.00);
                            else {
                                mEtPostRetirement.setText("" + updateValue);
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

    private void setDialerSevenListener() {
        mDailerInvest7.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtInflations.post(new Runnable() {
                    public void run() {
                        double currentValue = Double.parseDouble(mEtPreRetirement.getText().toString());
                        double updateValue = 0;
                        if (mCount7 != percentage) {
                            if (percentage > mCount7) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount7) {
                                updateValue = (currentValue - 1);
                            }

                            mCount7 = percentage;
                            if (updateValue < 1) {
                                mEtPreRetirement.setText("" + 1.00);
                            } else if (updateValue > 30)
                                mEtPreRetirement.setText("" + 30.00);
                            else {
                                mEtPreRetirement.setText("" + updateValue);
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

    private void setDialerSixListener() {
        mDailerInvest6.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtExitingCurpus.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtExitingCurpus.getText().toString().replaceAll(",", ""));
                        long updateValue = 0;
                        if (mCount6 != percentage) {
                            if (percentage > mCount6) {
                                updateValue = (currentValue + 1000);
                            } else if (percentage < mCount6) {
                                updateValue = (currentValue - 1000);
                            }

                            mCount6 = percentage;
                            if (updateValue < 1) {
                                mEtExitingCurpus.setText("" + 0);
                            } else {
                                mEtExitingCurpus.setText("" + updateValue);
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

    private void setDialerFiveListener() {
        mDailerInvest5.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtCurrentSaving.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtCurrentSaving.getText().toString().replaceAll(",", ""));
                        long updateValue = 0;
                        if (mCount5 != percentage) {
                            if (percentage > mCount5) {
                                updateValue = (currentValue + 1000);
                            } else if (percentage < mCount5) {
                                updateValue = (currentValue - 1000);
                            }

                            mCount5 = percentage;
                            if (updateValue < 1000) {
                                mEtCurrentSaving.setText("" + 1000);
                            } else {
                                mEtCurrentSaving.setText("" + updateValue);
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

    private void setDialerFourListener() {
        mDailerInvest4.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtInflations.post(new Runnable() {
                    public void run() {
                        double currentValue = Double.parseDouble(mEtInflations.getText().toString());
                        double updateValue = 0;
                        if (mCount4 != percentage) {
                            if (percentage > mCount4) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount4) {
                                updateValue = (currentValue - 1);
                            }

                            mCount4 = percentage;

                            if (updateValue < 1) {
                                mEtInflations.setText("" + 1.00);
                            } else if (updateValue > 30)
                                mEtInflations.setText("" + 30.00);
                            else {
                                mEtInflations.setText("" + updateValue);
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

    private void setDialerThreeListener() {
        mDailerInvest3.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {
                mEtMonthlyExpense.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtMonthlyExpense.getText().toString().replaceAll(",", ""));
                        long updateValue = 0;
                        if (mCount3 != percentage) {
                            if (percentage > mCount3) {
                                updateValue = (currentValue + 1000);
                            } else if (percentage < mCount3) {
                                updateValue = (currentValue - 1000);
                            }

                            mCount3 = percentage;
                            if (updateValue < 1000)
                                mEtMonthlyExpense.setText("" + 1000);
                            else
                                mEtMonthlyExpense.setText("" + updateValue);

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
                mEtPresentAge.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtPresentAge.getText().toString());
                        int retirementAge = Integer.parseInt(mEtRetiermentAge.getText().toString());
                        int updateValue = 0;


                        if (mCount1 != percentage) {
                            if (percentage > mCount1) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount1) {
                                updateValue = (currentValue - 1);
                            }

                            mCount1 = percentage;
                            if (currentValue < retirementAge || updateValue < retirementAge) {
                                if (updateValue < 20) {
                                    mEtPresentAge.setText("" + 20);
                                } else if (updateValue > 80)
                                    mEtPresentAge.setText("" + 80);
                                else {
                                    mEtPresentAge.setText("" + updateValue);
                                }


                                if (mBrokerActivity != null) {
                                    audioPlayer.play(mBrokerActivity);
                                } else {
                                    audioPlayer.play(mActivity);
                                }
                                calculateAmount();

                            } else {
                               /* if (mBrokerActivity != null) {
                                    mBrokerActivity.showCommonDialog(mBrokerActivity, "Error", "Current Age always less than Retirement age");

                                } else {
                                    if(dialogShown){
                                        return;
                                    }else{
                                        dialogShown=true;
                                        mActivity.showCommonDialog(mActivity, "Error", "Current Age always less than Retirement age");
                                    }


                                }*/
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
                mEtRetiermentAge.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtRetiermentAge.getText().toString());
                        int presentAge = Integer.parseInt(mEtPresentAge.getText().toString());
                        int updateValue = 0;

                        if (mCount2 != percentage) {
                            if (percentage > mCount2) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount2) {
                                updateValue = (currentValue - 1);
                            }

                            mCount2 = percentage;
                            if (currentValue > presentAge || updateValue > presentAge) {

                                if (updateValue < 25)
                                    mEtRetiermentAge.setText("" + 25);
                                else if (updateValue > 100)
                                    mEtRetiermentAge.setText("" + 100);
                                else
                                    mEtRetiermentAge.setText("" + updateValue);


                                if (mBrokerActivity != null) {
                                    audioPlayer.play(mBrokerActivity);
                                } else {
                                    audioPlayer.play(mActivity);
                                }
                                calculateAmount();

                            } else {
                               /* if (mBrokerActivity != null)
                                    mBrokerActivity.showCommonDialog(mBrokerActivity, "Error", "Retirement age always greater than Current Age");
                                else
                                    mActivity.showCommonDialog(mActivity, "Error", "Retirement age always greater than Current Age");
*/
                            }
                        }

                    }
                });
            }
        });

    }

    private void setListeners(View view) {
        mEtRetiermentAge.addTextChangedListener(new GenericTextWatcher(mEtRetiermentAge));
        mEtMonthlyExpense.addTextChangedListener(new GenericTextWatcher(mEtMonthlyExpense));
        mEtPresentAge.addTextChangedListener(new GenericTextWatcher(mEtPresentAge));
        mEtInflations.addTextChangedListener(new GenericTextWatcher(mEtInflations));
        mEtCurrentSaving.addTextChangedListener(new GenericTextWatcher(mEtCurrentSaving));
        mEtExitingCurpus.addTextChangedListener(new GenericTextWatcher(mEtExitingCurpus));
        mEtPreRetirement.addTextChangedListener(new GenericTextWatcher(mEtPreRetirement));
        mEtPostRetirement.addTextChangedListener(new GenericTextWatcher(mEtPostRetirement));
        mEtLifeExpectation.addTextChangedListener(new GenericTextWatcher(mEtLifeExpectation));
    }

    private void setUpFooterUiData() {
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

    private void clearErrorMessage() {
        tvErrPostRet.setText("");
        tvErrorPreRet.setText("");
        tvErrLifeExpe.setText("");
        tvErrorSaving.setText("");
        tvErrorMonthExp.setText("");
        tvErrorInfRate.setText("");
        tvErrorCorpus.setText("");
        tvErrorAge.setText("");
        tvErrorRetAge.setText("");
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrAgeVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.VISIBLE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrRetAgeVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.VISIBLE);
    }

    private void setErrPreRetAgeVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.VISIBLE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrPostRetAgeVisibility() {
        tvErrPostRet.setVisibility(View.VISIBLE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrCorpusVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.VISIBLE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrInfRateVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.VISIBLE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrMonthExpVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.VISIBLE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrLifeExpVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.VISIBLE);
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    private void setErrSaveVisibility() {
        tvErrPostRet.setVisibility(View.GONE);
        tvErrorPreRet.setVisibility(View.GONE);
        tvErrLifeExpe.setVisibility(View.GONE);
        tvErrorSaving.setVisibility(View.VISIBLE);
        tvErrorMonthExp.setVisibility(View.GONE);
        tvErrorInfRate.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRetAge.setVisibility(View.GONE);
    }

    @Override
    public void onStateChange(boolean newstate) {

    }

    @Override
    public void onRotate(int percentage) {

    }

    @Override
    public void onClick(View view) {
        int duration = Integer.parseInt(mEtRetiermentAge.getText().toString()) - Integer.parseInt(mEtPresentAge.getText().toString());

        Bundle bundle = new Bundle();
        if (view.getId() == R.id.lumpsum_btn) {
            bundle.putString("type", "coming_from_goal");
            bundle.putString("investment_type", "Lumpsum");
            bundle.putString("ic_invest_route_goal", "Retirement Planning");
            bundle.putString("Amount", mResultLumpsum);
            bundle.putInt("duration", duration);
            mActivity.displayViewOther(64, bundle);
            System.out.println(bundle.toString());

        } else if (view.getId() == R.id.sip_btn) {
            bundle.putString("type", "coming_from_goal");
            bundle.putString("investment_type", "SIP");
            bundle.putString("ic_invest_route_goal", "Retirement Planning");
            bundle.putString("Amount", mResultSIP);
            bundle.putInt("duration", duration);
            mActivity.displayViewOther(64, bundle);
        }


    }


    private void refreshView() {

        mDailerInvest1.setRotorPercentage(0);
        mDailerInvest2.setRotorPercentage(0);
        mDailerInvest3.setRotorPercentage(0);
        mDailerInvest4.setRotorPercentage(0);
        mDailerInvest5.setRotorPercentage(0);
        mDailerInvest6.setRotorPercentage(0);
        mDailerInvest7.setRotorPercentage(0);
        mDailerInvest8.setRotorPercentage(0);
        mDailerInvest9.setRotorPercentage(0);
        mEtPresentAge.setText("30");
        mEtRetiermentAge.setText("60");
        mEtMonthlyExpense.setText("30000");
        mEtInflations.setText("6.00");
        mEtCurrentSaving.setText("5000");
        mEtExitingCurpus.setText("100000");
        mEtPreRetirement.setText("12.00");
        mEtPostRetirement.setText("7.00");
        mEtLifeExpectation.setText("80");

        calculateAmount();
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
            if (mEtPresentAge.getText().toString().equals("") || mEtPresentAge.getText().toString().equals("0")) {
                if (dialogShown) {
                    return;
                } else {
                    dialogShown = true;
                    // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_current_age));
                    tvErrorAge.setText(getResources().getString(R.string.error_current_age));
                    setErrAgeVisibility();
                }

            } else if (Integer.parseInt(mEtPresentAge.getText().toString()) < 18) {
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_minn_retire_age));
                tvErrorAge.setText(getResources().getString(R.string.error_minn_retire_age));
                setErrAgeVisibility();
            } else if (mEtRetiermentAge.getText().toString().equals("") || mEtRetiermentAge.getText().toString().equals("0")) {
                //   mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_empty_retiremnt_age));
                tvErrorRetAge.setText(getResources().getString(R.string.error_empty_retiremnt_age));
                setErrRetAgeVisibility();
            } else if (Integer.parseInt(mEtRetiermentAge.getText().toString()) > 80) {
                mEtRetiermentAge.setText("80");
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_maxx_retire_age));
                tvErrorRetAge.setText(getResources().getString(R.string.error_maxx_retire_age));
                setErrRetAgeVisibility();
            } else if (Integer.parseInt(mEtPresentAge.getText().toString()) > Integer.parseInt(mEtRetiermentAge.getText().toString())) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", "Current Age always less than Retirement age");
                tvErrorAge.setText("Current Age always less than Retirement age");
                setErrAgeVisibility();
            } else if (mEtMonthlyExpense.getText().toString().equals("") || mEtMonthlyExpense.getText().toString().equals("0")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_empty_monthly_expense));
                tvErrorMonthExp.setText(getResources().getString(R.string.error_empty_monthly_expense));
                setErrMonthExpVisibility();
            } else if (Long.parseLong(mEtMonthlyExpense.getText().toString().replaceAll(",", "")) < 1000) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_minn_me));
                tvErrorMonthExp.setText(getResources().getString(R.string.error_minn_me));
                setErrMonthExpVisibility();
            } else if (mEtPreRetirement.getText().toString().equals("") || mEtPreRetirement.getText().toString().equals("0")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_expected_ror));
                tvErrorPreRet.setText(getResources().getString(R.string.error_expected_ror));
                setErrPreRetAgeVisibility();
            } else if (Double.parseDouble(mEtPreRetirement.getText().toString()) > 50) {
                mEtPreRetirement.setText("30.00");
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_max_ror));
                tvErrorPreRet.setText(getResources().getString(R.string.error_max_ror));
                setErrPreRetAgeVisibility();
            } else if (mEtPostRetirement.getText().toString().equals("") || mEtPostRetirement.getText().toString().equals("0")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_post_retiremnt_return));
                tvErrPostRet.setText(getResources().getString(R.string.error_post_retiremnt_return));
                setErrPostRetAgeVisibility();
            } else if (Double.parseDouble(mEtPostRetirement.getText().toString()) > 30) {
                mEtPostRetirement.setText("30.00");
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_max_expected_post_retiremnt_ror));
                tvErrPostRet.setText(getResources().getString(R.string.error_max_expected_post_retiremnt_ror));
                setErrPostRetAgeVisibility();
            } else if (mEtLifeExpectation.getText().toString().equals("")) {
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_empty_something));
                tvErrLifeExpe.setText(getResources().getString(R.string.error_empty_something));
                setErrLifeExpVisibility();
            } else if (Integer.parseInt(mEtLifeExpectation.getText().toString()) > 100) {
                mEtLifeExpectation.setText("100");
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.error_max_life));
                tvErrLifeExpe.setText(getResources().getString(R.string.error_max_life));
                setErrLifeExpVisibility();
            } else if ((Integer.parseInt(mEtLifeExpectation.getText().toString())) < (Integer.parseInt(mEtRetiermentAge.getText().toString()))) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.invalid_life_retire));
                tvErrLifeExpe.setText(getResources().getString(R.string.error_max_life));
                setErrLifeExpVisibility();
            } else {
                String rawValue = mEtMonthlyExpense.getText().toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue, mEtMonthlyExpense);

                String rawValue2 = mEtCurrentSaving.getText().toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue2, mEtCurrentSaving);

                String rawValue3 = mEtExitingCurpus.getText().toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue3, mEtExitingCurpus);
                try {

                    ret_year = 0;
                    dblAmtReq = 0;
                    dblExistingGrowth = 0;
                    lumsum = 0;
                    amt_acc = 0;
                    amt_final = 0;
                    short_fall = 0;
                    extra_amt = 0;
                    LumpSumInvestment = 0;

                    dblCurrentAge = Double.parseDouble(mEtPresentAge.getText().toString());
                    dblRetirementAge = Double.parseDouble(mEtRetiermentAge.getText().toString());
                    dblMonthlyExpenses = Double.parseDouble(mEtMonthlyExpense.getText().toString().replaceAll(",", ""));
                    dblMonthlySavings = Double.parseDouble(mEtCurrentSaving.getText().toString().replaceAll(",", ""));
                    dlbExistingCorpus = Double.parseDouble(mEtExitingCurpus.getText().toString().replaceAll(",", ""));
                    dblPreRetirement = Double.parseDouble(mEtPreRetirement.getText().toString());
                    postRetirmentRate = Double.parseDouble(mEtPostRetirement.getText().toString());
                    inflationRate = Double.parseDouble(mEtInflations.getText().toString());
                    dlbLifeExpetancy = Double.parseDouble(mEtLifeExpectation.getText().toString());

                    ///calculation for yeras in retirement
                    ret_year = dblRetirementAge - dblCurrentAge;

                    //Age After retirement
                    double xAfterRetAge = dlbLifeExpetancy - dblRetirementAge;

                    double finalrate = postRetirmentRate - inflationRate;
                    if (finalrate == 0) {
                        finalrate = 0.0001;
                    }
                    double xRRR = (finalrate / (1 + 0.01 * inflationRate)) / 100;

                    //After Retirement Rate
                    double xRRRAR = postRetirmentRate / 100;

                    //Calculation for amount require after retirement --- This is for Expense after Retirement
                    dblAmtReq = dblMonthlyExpenses * (Math.pow((1 + inflationRate / 100), ret_year));

                    //Calculation for existing corpus gain after retirement
                    dblExistingGrowth = dlbExistingCorpus * (Math.pow((1 + dblPreRetirement / 100), ret_year));

                    //Calculation for Corpus suggested by Jain Sir applied in Fincart
                    double pow = Math.pow(1 + xRRR, (xAfterRetAge + 1)) - (1 + xRRR);
                    double pow1 = (xRRR * Math.pow(1 + xRRR, xAfterRetAge));

                    //calculation for lum sum amount ---- Corpus Required
                    lumsum = (dblAmtReq * 12 * (pow / pow1));

                 /*   //Based on PV formula by Moneycontrol
                    double pow = Math.pow(1 + xRRR, (xAfterRetAge + 1)) - (1 + xRRR);
                    double pow1 = (xRRR * Math.pow(1 + xRRR, xAfterRetAge));

                    lumsum = (dblAmtReq * 12 * (pow / pow1));
//mTvResult1.setText((int) lumsum);*/

                    // Calculation for amount accumulate with current saving

                    double tt = ret_year * 12;
                    double rt = dblPreRetirement / 12;

                    for (int i = 1; i <= tt; i++) {
                        amt_acc = dblMonthlySavings * (Math.pow((1 + rt / 100), i));
                        amt_final += amt_acc;
                    }

                    //Total Savings based on Present Saving Structures
                    totalSavings = amt_final + dblExistingGrowth;

                    //Shortfall Calculation
                    short_fall = lumsum - (amt_final + dblExistingGrowth);

                    //Calculation for lumpsum amount
                    LumpSumInvestment=short_fall/(Math.pow((1+dblPreRetirement/100),ret_year));



                    //Calculation for extra amount to save

                    double sipAmount = 0;
                    if (short_fall > 0) {
                        double pm = 12 * ret_year;
                        double intr = dblPreRetirement / 1200;
                        double powr = Math.pow((1 + intr), pm);
                        double nSip = short_fall;
                        double nYears = ret_year;
                        double nRate = dblPreRetirement;
                        double r = nRate / 100;
                        double nMonths = (nYears * 12);
                        for (int i = 0; i < nMonths; i++) {
                            sipAmount = sipAmount + Math.pow((1 + r), (nMonths - i) / 12);
                        }
                        sipAmount = nSip / sipAmount;
                    }

                    if (short_fall > 0) {
                        double pm = 12 * ret_year;
                        double intr = dblPreRetirement / 1200;

                        double powr = Math.pow((1 + intr), pm);
                        extra_amt = ((short_fall * ((intr * powr) / (powr - 1))) / powr);
                        if (dblPreRetirement == 0) {
                            extra_amt = (short_fall / (ret_year * 12));
                        }
                        if (ret_year == 0) {
                            extra_amt = short_fall;
                        }
                    }
                    mTvResult4.setText(String.valueOf(short_fall));

                    String strAmount = "";

                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                    mTvText1.setText("Expected Monthly Expenses\nWhen you Retire at " + (int)dblRetirementAge + " years would be");
                    long value1 = Math.round(dblAmtReq);
                    strAmount = format.format(value1);
                    resultAmount = strAmount.split("\\.", 0);
                    tvAmount.setText(resultAmount[0] + "/-");
                    tvHeaderDesc.setText("& Estimated amount required as Retirement");

                    long value2 = Math.round(lumsum);
                    strAmount = format.format(value2);
                    resultAmount = strAmount.split("\\.", 0);
                    tv_amount2.setText(resultAmount[0] + "/-");

                    double totalAmount = Math.round(amt_final + dblExistingGrowth);
                    long value3 = Math.round(totalAmount);
                    strAmount = format.format(value3);
                    resultAmount = strAmount.split("\\.", 0);
                    tvLine5.setText("Total savings at the time of retirement will be \n "+resultAmount[0]);

                    double shortFall = Math.round(short_fall);
                    long value4 =  Math.round(shortFall);
                    strAmount = format.format(value4);
                    resultAmount = strAmount.split("\\.", 0);
                    if (shortFall > 0) {
                        llSipLum.setVisibility(View.VISIBLE);
                        tvLine6.setVisibility(View.VISIBLE);

                        tvLine6.setText("Shortfall in savings  "+resultAmount[0]);
                        tvSipLumSumMessage.setText("To achieve this goal, you need to do");

                        long valueSip = Math.round(sipAmount);
                        strAmount = format.format(valueSip);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvResult1.setText(resultAmount[0]);

                        long valueshortfall = Math.round(LumpSumInvestment);
                        strAmount = format.format(valueshortfall);
                        resultAmount = strAmount.split("\\.", 0);
                        mResultShortfall = resultAmount[0];
                        mTvResult4.setText(mResultShortfall);

                    }else{
                        tvSipLumSumMessage.setText("Congratulations!\n" +
                                "Your current savings is enough to sustain your post-retirement expenses. Should there be a change in expense pattern or Rate of Return, please do recalculate.");

                        llSipLum.setVisibility(View.GONE);
                        tvLine6.setVisibility(View.GONE);
                    }


  /*                  if (extra_amt == 0) {
                        // mTvText2.setText("Not Required");
                        mTvText1.setText("Your current saving " + getString(R.string.Rs) + mEtCurrentSaving.getText().toString() + " per month along with existing Corpus " + getString(R.string.Rs) + mEtExitingCurpus.getText().toString() + " is enough to meet your expenses after retirement");

                    } else {
                        long value1 = Math.round(extra_amt);
                        strAmount = format.format(value1);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvText1.setText(getString(R.string.retirement_calc_You_need_to_invest_an_additional));
                        tvAmount.setText(resultAmount[0] + " per month ");
                        tvHeaderDesc.setText(getString(R.string.retirement_calc_to_secure_a_comfortable_retirement));
//                    mTvResult1.setText(resultAmount[0]);
                        mResultSIP = resultAmount[0];
                        // mTvResult2.setText(mResultSIP);
//                    btnsip.setText("Continue with SIP  " + mResultSIP + "/monthly");
//                    mTvResult1.setText(String.valueOf(lumsum));
                    }
                    double totalAmount = Math.round(amt_final + dblExistingGrowth);
                    long value2 = Math.round(totalAmount);
                    strAmount = format.format(value2);
                    resultAmount = strAmount.split("\\.", 0);
                    mTvResult1.setText(resultAmount[0]);

                    long valueshortfall = Math.round(short_fall);
                    strAmount = format.format(valueshortfall);
                    resultAmount = strAmount.split("\\.", 0);
                    mResultShortfall = resultAmount[0];
                    mTvResult4.setText(mResultShortfall);

                    double percentage = ((totalAmount * 100) / lumsum);
                    mProgressBar.setProgress((int) percentage);
*/
                /*long value5 = Math.round(lumsum);
                strAmount = format.format(value5);
                resultAmount = strAmount.split("\\.", 0);
                mResultLumpsum = resultAmount[0];
                mTvResult1.setText(mResultLumpsum);*/



              /*  double totalAmount = Math.round(amt_final + dblExistingGrowth);
                long value2 =  Math.round(totalAmount);
                strAmount = format.format(value2);
                resultAmount = strAmount.split("\\.", 0);
                mTvResult1.setText(resultAmount[0]);

                double shortFall = Math.round(short_fall);
                long value3 =  Math.round(shortFall);
                strAmount = format.format(value3);
                resultAmount = strAmount.split("\\.", 0);
                mTvResult2.setText(resultAmount[0]);

                double percentage = ((totalAmount * 100) / lumsum);
                if (shortFall < 0) {
                    mTvResult2.setText("0");
                } else
                    mProgressBar.setProgress((int) percentage);



                mTvText3.setText(String.valueOf(resultAmount[0] + " required per month post retirement and Existing Corpus will grow to "));

                strAmount = format.format(dblExistingGrowth);
                resultAmount = strAmount.split("\\.", 0);
                mTvText4.setText(resultAmount[0]);

                mTvCorpusAge.setText("Required corpus at age" + (int) dblRetirementAge);

                long value4 =  Math.round(amt_final);
                strAmount = format.format(value4);
                resultAmount = strAmount.split("\\.", 0);

                mTvResult3.setText(resultAmount[0]);

                long value5 =  Math.round(lumsum);
                strAmount = format.format(value5);
                resultAmount = strAmount.split("\\.", 0);
                mTvResult4.setText(resultAmount[0]);

                int percentage2 = (int) ((amt_final * 100) / lumsum);
                mProgressBar2.setProgress(percentage2);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        } else {
            if (mEtPresentAge.getText().toString().equals("") || mEtPresentAge.getText().toString().equals("0")) {
                //mActivity.showCommonDialog(mActivity, "Not Valid", "Please enter Current Age");
                if (dialogShown) {
                    return;
                } else {
                    dialogShown = true;
                    // mActivity.showCommonDialog(mActivity, "Not Valid", "Please enter Current Age");
                    tvErrorAge.setText(getResources().getString(R.string.error_current_age));
                    setErrAgeVisibility();
                }
            } else if (Integer.parseInt(mEtPresentAge.getText().toString()) < 18) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_current_age));
                tvErrorAge.setText(getResources().getString(R.string.error_current_age));
                setErrAgeVisibility();
            } else if (mEtRetiermentAge.getText().toString().equals("") || mEtRetiermentAge.getText().toString().equals("0")) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_minn_retire_age));
                tvErrorRetAge.setText(getResources().getString(R.string.error_empty_retiremnt_age));
                setErrRetAgeVisibility();
            } else if (Integer.parseInt(mEtRetiermentAge.getText().toString()) > 80) {
                mEtRetiermentAge.setText("80");
                //mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_empty_retiremnt_age));
                tvErrorRetAge.setText(getResources().getString(R.string.error_maxx_retire_age));
                setErrRetAgeVisibility();
            } else if (Integer.parseInt(mEtPresentAge.getText().toString()) > Integer.parseInt(mEtRetiermentAge.getText().toString())) {
                tvErrorAge.setText("Current Age always less than Retirement age");
                setErrAgeVisibility();
                mApplication.showSnackBar(mEtPresentAge, getResources().getString(R.string.error_maxx_retire_age));
                /*  }*/

            } else if (mEtMonthlyExpense.getText().toString().equals("") || mEtMonthlyExpense.getText().toString().equals("0")) {
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_empty_monthly_expense));
                tvErrorMonthExp.setText(getResources().getString(R.string.error_empty_monthly_expense));
                setErrMonthExpVisibility();
            } else if (Long.parseLong(mEtMonthlyExpense.getText().toString().replaceAll(",", "")) < 1000) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_minn_me));
                tvErrorMonthExp.setText(getResources().getString(R.string.error_minn_me));
                setErrMonthExpVisibility();
            } else if (mEtPreRetirement.getText().toString().equals("") || mEtPreRetirement.getText().toString().equals("0")) {
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_expected_ror));
                tvErrorPreRet.setText(getResources().getString(R.string.error_expected_ror));
                setErrPreRetAgeVisibility();
            } else if (Double.parseDouble(mEtPreRetirement.getText().toString()) > 50) {
                mEtPreRetirement.setText("30.00");
                tvErrorPreRet.setText(getResources().getString(R.string.error_max_ror));
                setErrPreRetAgeVisibility();
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_max_ror));
            } else if (mEtPostRetirement.getText().toString().equals("") || mEtPostRetirement.getText().toString().equals("0")) {
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_post_retiremnt_return));
                tvErrPostRet.setText(getResources().getString(R.string.error_post_retiremnt_return));
                setErrPostRetAgeVisibility();
            } else if (Double.parseDouble(mEtPostRetirement.getText().toString()) > 30) {
                mEtPostRetirement.setText("30.00");
                tvErrPostRet.setText(getResources().getString(R.string.error_max_expected_post_retiremnt_ror));
                setErrPostRetAgeVisibility();
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_max_expected_post_retiremnt_ror));
            } else if (mEtLifeExpectation.getText().toString().equals("")) {
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_empty_something));
                tvErrLifeExpe.setText(getResources().getString(R.string.error_empty_something));
                setErrLifeExpVisibility();
            } else if (Integer.parseInt(mEtLifeExpectation.getText().toString()) > 100) {
                mEtLifeExpectation.setText("100");
                tvErrLifeExpe.setText(getResources().getString(R.string.error_max_life));
                setErrLifeExpVisibility();
                //  mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.error_max_life));
            } else if ((Integer.parseInt(mEtLifeExpectation.getText().toString())) < (Integer.parseInt(mEtRetiermentAge.getText().toString()))) {
                // mActivity.showCommonDialog(mActivity, "Not Valid", getResources().getString(R.string.invalid_life_retire));
                tvErrLifeExpe.setText(getResources().getString(R.string.error_max_life));
                setErrLifeExpVisibility();
            } else {
                String rawValue = mEtMonthlyExpense.getText().toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue, mEtMonthlyExpense);

                String rawValue2 = mEtCurrentSaving.getText().toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue2, mEtCurrentSaving);

                String rawValue3 = mEtExitingCurpus.getText().toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue3, mEtExitingCurpus);
                try {

                    ret_year = 0;
                    dblAmtReq = 0;
                    dblExistingGrowth = 0;
                    lumsum = 0;
                    amt_acc = 0;
                    amt_final = 0;
                    short_fall = 0;
                    extra_amt = 0;
                    LumpSumInvestment = 0;

                    dblCurrentAge = Double.parseDouble(mEtPresentAge.getText().toString());
                    dblRetirementAge = Double.parseDouble(mEtRetiermentAge.getText().toString());
                    dblMonthlyExpenses = Double.parseDouble(mEtMonthlyExpense.getText().toString().replaceAll(",", ""));
                    dblMonthlySavings = Double.parseDouble(mEtCurrentSaving.getText().toString().replaceAll(",", ""));
                    dlbExistingCorpus = Double.parseDouble(mEtExitingCurpus.getText().toString().replaceAll(",", ""));
                    dblPreRetirement = Double.parseDouble(mEtPreRetirement.getText().toString());
                    postRetirmentRate = Double.parseDouble(mEtPostRetirement.getText().toString());
                    inflationRate = Double.parseDouble(mEtInflations.getText().toString());
                    dlbLifeExpetancy = Double.parseDouble(mEtLifeExpectation.getText().toString());

                    ///calculation for yeras in retirement
                    ret_year = dblRetirementAge - dblCurrentAge;

                    //Age After retirement
                    double xAfterRetAge = dlbLifeExpetancy - dblRetirementAge;

                    double finalrate = postRetirmentRate - inflationRate;
                    if (finalrate == 0) {
                        finalrate = 0.0001;
                    }
                    double xRRR = (finalrate / (1 + 0.01 * inflationRate)) / 100;

                    //After Retirement Rate
                    double xRRRAR = postRetirmentRate / 100;

                    //Calculation for amount require after retirement --- This is for Expense after Retirement
                    dblAmtReq = dblMonthlyExpenses * (Math.pow((1 + inflationRate / 100), ret_year));

                    //Calculation for existing corpus gain after retirement
                    dblExistingGrowth = dlbExistingCorpus * (Math.pow((1 + dblPreRetirement / 100), ret_year));

                    //Calculation for Corpus suggested by Jain Sir applied in Fincart
                    double pow = Math.pow(1 + xRRR, (xAfterRetAge + 1)) - (1 + xRRR);
                    double pow1 = (xRRR * Math.pow(1 + xRRR, xAfterRetAge));

                    //calculation for lum sum amount ---- Corpus Required
                    lumsum = (dblAmtReq * 12 * (pow / pow1));

                 /*   //Based on PV formula by Moneycontrol
                    double pow = Math.pow(1 + xRRR, (xAfterRetAge + 1)) - (1 + xRRR);
                    double pow1 = (xRRR * Math.pow(1 + xRRR, xAfterRetAge));

                    lumsum = (dblAmtReq * 12 * (pow / pow1));
//mTvResult1.setText((int) lumsum);*/

                    // Calculation for amount accumulate with current saving

                    double tt = ret_year * 12;
                    double rt = dblPreRetirement / 12;

                    for (int i = 1; i <= tt; i++) {
                        amt_acc = dblMonthlySavings * (Math.pow((1 + rt / 100), i));
                        amt_final += amt_acc;
                    }

                    //Total Savings based on Present Saving Structures
                    totalSavings = amt_final + dblExistingGrowth;

                    //Shortfall Calculation
                    short_fall = lumsum - (amt_final + dblExistingGrowth);

                    //Calculation for lumpsum amount
                    LumpSumInvestment=short_fall/(Math.pow((1+dblPreRetirement/100),ret_year));



                    //Calculation for extra amount to save

                    double sipAmount = 0;
                    if (short_fall > 0) {
                        double pm = 12 * ret_year;
                        double intr = dblPreRetirement / 1200;
                        double powr = Math.pow((1 + intr), pm);
                        double nSip = short_fall;
                        double nYears = ret_year;
                        double nRate = dblPreRetirement;
                        double r = nRate / 100;
                        double nMonths = (nYears * 12);
                        for (int i = 0; i < nMonths; i++) {
                            sipAmount = sipAmount + Math.pow((1 + r), (nMonths - i) / 12);
                        }
                        sipAmount = nSip / sipAmount;
                    }

                    if (short_fall > 0) {
                        double pm = 12 * ret_year;
                        double intr = dblPreRetirement / 1200;

                        double powr = Math.pow((1 + intr), pm);
                        extra_amt = ((short_fall * ((intr * powr) / (powr - 1))) / powr);
                        if (dblPreRetirement == 0) {
                            extra_amt = (short_fall / (ret_year * 12));
                        }
                        if (ret_year == 0) {
                            extra_amt = short_fall;
                        }
                    }
                    mTvResult4.setText(String.valueOf(short_fall));

                    String strAmount = "";

                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                    mTvText1.setText("Expected Monthly Expenses\nWhen you Retire at " + (int)dblRetirementAge + " years would be");
                    long value1 = Math.round(dblAmtReq);
                    strAmount = format.format(value1);
                    resultAmount = strAmount.split("\\.", 0);
                    tvAmount.setText(resultAmount[0] + "/-");
                    tvHeaderDesc.setText("& Estimated amount required as Retirement");

                    long value2 = Math.round(lumsum);
                    strAmount = format.format(value2);
                    resultAmount = strAmount.split("\\.", 0);
                    tv_amount2.setText(resultAmount[0] + "/-");

                    double totalAmount = Math.round(amt_final + dblExistingGrowth);
                    long value3 = Math.round(totalAmount);
                    strAmount = format.format(value3);
                    resultAmount = strAmount.split("\\.", 0);
                    tvLine5.setText("Total savings at the time of retirement will be \n "+resultAmount[0]);

                    double shortFall = Math.round(short_fall);
                    long value4 =  Math.round(shortFall);
                    strAmount = format.format(value4);
                    resultAmount = strAmount.split("\\.", 0);
                    if (shortFall > 0) {
                        llSipLum.setVisibility(View.VISIBLE);
                        tvLine6.setVisibility(View.VISIBLE);

                        tvLine6.setText("Shortfall in savings  "+resultAmount[0]);
                        tvSipLumSumMessage.setText("To achieve this goal, you need to do");

                        long valueSip = Math.round(sipAmount);
                        strAmount = format.format(valueSip);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvResult1.setText(resultAmount[0]);

                        long valueshortfall = Math.round(LumpSumInvestment);
                        strAmount = format.format(valueshortfall);
                        resultAmount = strAmount.split("\\.", 0);
                        mResultShortfall = resultAmount[0];
                        mTvResult4.setText(mResultShortfall);

                    }else{
                        tvSipLumSumMessage.setText("Congratulations!\n" +
                                "Your current savings is enough to sustain your post-retirement expenses. Should there be a change in expense pattern or Rate of Return, please do recalculate.");

                        llSipLum.setVisibility(View.GONE);
                        tvLine6.setVisibility(View.GONE);
                    }


  /*                  if (extra_amt == 0) {
                        // mTvText2.setText("Not Required");
                        mTvText1.setText("Your current saving " + getString(R.string.Rs) + mEtCurrentSaving.getText().toString() + " per month along with existing Corpus " + getString(R.string.Rs) + mEtExitingCurpus.getText().toString() + " is enough to meet your expenses after retirement");

                    } else {
                        long value1 = Math.round(extra_amt);
                        strAmount = format.format(value1);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvText1.setText(getString(R.string.retirement_calc_You_need_to_invest_an_additional));
                        tvAmount.setText(resultAmount[0] + " per month ");
                        tvHeaderDesc.setText(getString(R.string.retirement_calc_to_secure_a_comfortable_retirement));
//                    mTvResult1.setText(resultAmount[0]);
                        mResultSIP = resultAmount[0];
                        // mTvResult2.setText(mResultSIP);
//                    btnsip.setText("Continue with SIP  " + mResultSIP + "/monthly");
//                    mTvResult1.setText(String.valueOf(lumsum));
                    }
                    double totalAmount = Math.round(amt_final + dblExistingGrowth);
                    long value2 = Math.round(totalAmount);
                    strAmount = format.format(value2);
                    resultAmount = strAmount.split("\\.", 0);
                    mTvResult1.setText(resultAmount[0]);

                    long valueshortfall = Math.round(short_fall);
                    strAmount = format.format(valueshortfall);
                    resultAmount = strAmount.split("\\.", 0);
                    mResultShortfall = resultAmount[0];
                    mTvResult4.setText(mResultShortfall);

                    double percentage = ((totalAmount * 100) / lumsum);
                    mProgressBar.setProgress((int) percentage);
*/
                /*long value5 = Math.round(lumsum);
                strAmount = format.format(value5);
                resultAmount = strAmount.split("\\.", 0);
                mResultLumpsum = resultAmount[0];
                mTvResult1.setText(mResultLumpsum);*/



              /*  double totalAmount = Math.round(amt_final + dblExistingGrowth);
                long value2 =  Math.round(totalAmount);
                strAmount = format.format(value2);
                resultAmount = strAmount.split("\\.", 0);
                mTvResult1.setText(resultAmount[0]);

                double shortFall = Math.round(short_fall);
                long value3 =  Math.round(shortFall);
                strAmount = format.format(value3);
                resultAmount = strAmount.split("\\.", 0);
                mTvResult2.setText(resultAmount[0]);

                double percentage = ((totalAmount * 100) / lumsum);
                if (shortFall < 0) {
                    mTvResult2.setText("0");
                } else
                    mProgressBar.setProgress((int) percentage);



                mTvText3.setText(String.valueOf(resultAmount[0] + " required per month post retirement and Existing Corpus will grow to "));

                strAmount = format.format(dblExistingGrowth);
                resultAmount = strAmount.split("\\.", 0);
                mTvText4.setText(resultAmount[0]);

                mTvCorpusAge.setText("Required corpus at age" + (int) dblRetirementAge);

                long value4 =  Math.round(amt_final);
                strAmount = format.format(value4);
                resultAmount = strAmount.split("\\.", 0);

                mTvResult3.setText(resultAmount[0]);

                long value5 =  Math.round(lumsum);
                strAmount = format.format(value5);
                resultAmount = strAmount.split("\\.", 0);
                mTvResult4.setText(resultAmount[0]);

                int percentage2 = (int) ((amt_final * 100) / lumsum);
                mProgressBar2.setProgress(percentage2);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyPad();
        String currentValue = "";
        int i = view.getId();
        if (i == R.id.dialer1) {
            currentValue = mEtPresentAge.getText().toString();

            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 20)
                    mEtPresentAge.setText("" + 20);
                else if (value > 80)
                    mEtPresentAge.setText("" + 80);
            } else {
                mEtPresentAge.setText("" + 25);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer2) {
            currentValue = mEtRetiermentAge.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 25)
                    mEtRetiermentAge.setText("" + 25);
                else if (value > 100)
                    mEtRetiermentAge.setText("" + 100);

            } else {
                mEtRetiermentAge.setText("" + 60);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer3) {
            currentValue = mEtMonthlyExpense.getText().toString().replaceAll(",", "");
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 1000)
                    mEtMonthlyExpense.setText("" + 1000);

            } else {
                mEtMonthlyExpense.setText("" + 1000);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer4) {
            currentValue = mEtInflations.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtInflations.setText("" + 1.00);
                else if (value > 30)
                    mEtInflations.setText("" + 30.00);
            } else {
                mEtInflations.setText("" + 1.00);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer5) {
            currentValue = mEtCurrentSaving.getText().toString().replaceAll(",", "");
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 1000)
                    mEtCurrentSaving.setText("" + 1000);

            } else {
                mEtCurrentSaving.setText("" + 1000);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer6) {
            currentValue = mEtExitingCurpus.getText().toString().replaceAll(",", "");
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 1)
                    mEtExitingCurpus.setText("" + 0);

            } else {
                mEtExitingCurpus.setText("" + 0);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer7) {
            currentValue = mEtPreRetirement.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtPreRetirement.setText("" + 1.00);
                else if (value > 30)
                    mEtPreRetirement.setText("" + 30.00);

            } else {
                mEtPreRetirement.setText("" + 1.00);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer8) {
            currentValue = mEtPostRetirement.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtPostRetirement.setText("" + 1.00);
                else if (value > 30)
                    mEtPostRetirement.setText("" + 30.00);

            } else {
                mEtPostRetirement.setText("" + 1.00);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer9) {
            currentValue = mEtLifeExpectation.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 30)
                    mEtLifeExpectation.setText("" + 30);

            } else {
                mEtLifeExpectation.setText("" + 30);
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

        path = path + "/" + "Calculator-" + getResources().getString(R.string.toolBar_title_retirement_cal) + System.currentTimeMillis() + ".pdf";// path where pdf will be stored


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
                if (mBrokerActivity.getCurrentFocus() == mEtPresentAge) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtRetiermentAge) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtMonthlyExpense) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtInflations) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtCurrentSaving) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtExitingCurpus) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtPreRetirement) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtPostRetirement) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtLifeExpectation) {
                    delayCall();
                }

            } else {
                if (mActivity.getCurrentFocus() == mEtPresentAge) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtRetiermentAge) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtMonthlyExpense) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtInflations) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtCurrentSaving) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtExitingCurpus) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtPreRetirement) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtPostRetirement) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtLifeExpectation) {
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