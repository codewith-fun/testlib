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
import androidx.core.content.ContextCompat;
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

public class FragMarriegeCalculator extends Fragment implements View.OnClickListener,
        View.OnTouchListener, RoundKnobButton.RoundKnobButtonListener, ToolbarFragment.ToolbarCallback {

    private final long DELAY = 1000; // milliseconds
    Singleton m_Inst = Singleton.getInstance();
    double dblChildAge = 0, dblMrgCost = 0, dblYrlySaving = 0, dblExpReturnRate = 0, dblExtInflammation = 0, dblMrgAge = 0;
    double dblTymLeft = 0, dblExistingCorpus = 0, dblExistingGrowth = 0, futureSaving = 0;
    double add_fund = 0, add_sav = 0, inflammationCost = 0, onetimeinv = 0, monthlyinv = 0, yearlyinv = 0;
    private EditText mEtPresentAge, mEtMarrigeAge, mEtWeddingCost, mEtSaving, mEtExitingCurpus, mEtExpectedRate, mEtExpectedInflation;
    private TextView mTvTitle, mTvText2, mTvText3, mTvText4, mTvResult4, mTvResult1, mTvResult2, mTvImageTitle;
    private RelativeLayout mLinerResult;
    private ImageView mIvImageRight, mIvRefresh, mIvBack;
    private int mCount1 = 0, mCount2 = 0, mCount3 = 0, mCount4 = 0, mCount5 = 0, mCount6 = 0;
    private AudioPlayer audioPlayer;
    private ScrollView mScrollView;
    private RoundKnobButton mDailerInvest1, mDailerInvest2, mDailerInvest3, mDailerInvest4, mDailerInvest5, mDailerInvest6, mDailerInvest7;
    private LinearLayout mLinerFooter, mLinerResultPart;
    private Timer timer = new Timer();
    private MainActivity mActivity;
    private AppApplication mApplication;
    private RelativeLayout mRelToolbar;
    private Bundle bundle;
    private ToolbarFragment fragToolBar;
    private CustomTextViewBold tvHeaderTitle;
    private LinearLayout ll_calculator_header;
    private BrokerActivity mBrokerActivity;
    private AppSession mSession;
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb;
    Bitmap b;
    private String path = "";
    private TextView tvErrorAge, tvErrorMarAge, tvErrAmountReq, tvErrorRoi, tvErrorSaving, tvErrorCorpus, tvErrorRor;

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
        View view = inflater.inflate(R.layout.frag_marriege, container, false);
        initialChecks();
        initializer(view);
        setUpToolBar();
        setButtonListener(view);
        setFooterViewsData();
        initialDialerSetup();
        setOnTouchListener(view);
        setDialerOneListener();
        setDialerTwoListener();
        setDialerThreeListener();
        setDialerFourListener();
        setDialerFiveListener();
        setDialerSixListener();
        setDialerSevenListener();
        setListeners();
        calculateAmount();
        return view;
    }

    private void setListeners() {
        mEtPresentAge.addTextChangedListener(new GenericTextWatcher(mEtPresentAge));
        mEtMarrigeAge.addTextChangedListener(new GenericTextWatcher(mEtMarrigeAge));
        mEtWeddingCost.addTextChangedListener(new GenericTextWatcher(mEtWeddingCost));
        mEtSaving.addTextChangedListener(new GenericTextWatcher(mEtSaving));
        mEtExitingCurpus.addTextChangedListener(new GenericTextWatcher(mEtExitingCurpus));
        mEtExpectedRate.addTextChangedListener(new GenericTextWatcher(mEtExpectedRate));
        mEtExpectedInflation.addTextChangedListener(new GenericTextWatcher(mEtExpectedInflation));
    }

    private void setDialerSevenListener() {
        mDailerInvest7.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
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
                mEtExpectedRate.post(new Runnable() {
                    public void run() {
                        double currentValue = Double.parseDouble(mEtExpectedRate.getText().toString());
                        double updateValue = 0;
                        if (mCount6 != percentage) {
                            if (percentage > mCount6) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount6) {
                                updateValue = (currentValue - 1);
                            }

                            mCount6 = percentage;
                            if (updateValue < 1) {
                                mEtExpectedRate.setText("" + 1.00);
                            } else if (updateValue > 30) {
                                mEtExpectedRate.setText("" + 30.00);
                            } else {
                                mEtExpectedRate.setText("" + updateValue);
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

                mEtExitingCurpus.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtExitingCurpus.getText().toString().replaceAll(",", "").toString());
                        int updateValue = 0;
                        if (mCount5 != percentage) {
                            if (percentage > mCount5) {
                                updateValue = (currentValue + 10000);
                            } else if (percentage < mCount5) {
                                updateValue = (currentValue - 10000);
                            }

                            mCount5 = percentage;
                            if (updateValue < 10000) {
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

    private void setDialerFourListener() {
        mDailerInvest4.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtSaving.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtSaving.getText().toString().replaceAll(",", "").toString());
                        long updateValue = 0;
                        if (mCount4 != percentage) {
                            if (percentage > mCount4) {
                                updateValue = (currentValue + 1000);
                            } else if (percentage < mCount4) {
                                updateValue = (currentValue - 1000);
                            }

                            mCount4 = percentage;
                            if (updateValue < 1000)
                                mEtSaving.setText("" + 0);
                            else
                                mEtSaving.setText("" + updateValue);

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
            }

            public void onRotate(final int percentage) {
                mEtWeddingCost.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtWeddingCost.getText().toString().replaceAll(",", "").toString());
                        long updateValue = 0;
                        if (mCount3 != percentage) {
                            if (percentage > mCount3) {
                                updateValue = (currentValue + 10000);
                            } else if (percentage < mCount3) {
                                updateValue = (currentValue - 10000);
                            }

                            mCount3 = percentage;
                            if (updateValue < 100000)
                                mEtWeddingCost.setText("" + 100000);
                            else
                                mEtWeddingCost.setText("" + updateValue);

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
                mEtMarrigeAge.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtMarrigeAge.getText().toString());
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

                                if (updateValue < 18)
                                    mEtMarrigeAge.setText("" + 18);
                                else if (updateValue > 35)
                                    mEtMarrigeAge.setText("" + 35);
                                else
                                    mEtMarrigeAge.setText("" + updateValue);

                                if (mBrokerActivity != null) {
                                    audioPlayer.play(mBrokerActivity);
                                } else {
                                    audioPlayer.play(mActivity);
                                }
                                calculateAmount();

                            } else {
                                mApplication.showSnackBar(mEtPresentAge, getResources().getString(R.string.marriage_cal_error_wedding_age));
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
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {
                mEtPresentAge.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtPresentAge.getText().toString());
                        int collegeStart = Integer.parseInt(mEtMarrigeAge.getText().toString());
                        int updateValue = 0;


                        if (mCount1 != percentage) {
                            if (percentage > mCount1) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount1) {
                                updateValue = (currentValue - 1);
                            }

                            mCount1 = percentage;
                            if (currentValue < collegeStart || updateValue < collegeStart) {
                                if (updateValue < 2) {
                                    mEtPresentAge.setText("" + 1);
                                } else {
                                    mEtPresentAge.setText("" + updateValue);
                                }

                                if (mBrokerActivity != null) {
                                    audioPlayer.play(mBrokerActivity);
                                } else {
                                    audioPlayer.play(mActivity);
                                }
                                calculateAmount();

                            } else {
                                mApplication.showSnackBar(mEtPresentAge, getResources().getString(R.string.marriage_cal_error_child_age));

                            }

                        }


                    }
                });
            }
        });
    }

    private void setOnTouchListener(View view) {
        mDailerInvest1.setOnTouchListener(this);
        mDailerInvest2.setOnTouchListener(this);
        mDailerInvest3.setOnTouchListener(this);
        mDailerInvest4.setOnTouchListener(this);
        mDailerInvest5.setOnTouchListener(this);
        mDailerInvest6.setOnTouchListener(this);
        mDailerInvest7.setOnTouchListener(this);
        if (bundle != null && bundle.containsKey("viewLayout"))
            view.findViewById(R.id.button_layout).setVisibility(View.VISIBLE);
    }

    private void initialDialerSetup() {
        if (mBrokerActivity != null) {
            mDailerInvest1.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest5.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest6.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest7.RoundKnobButton2(mBrokerActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
        } else {
            mDailerInvest1.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest2.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest3.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest4.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest5.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest6.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
            mDailerInvest7.RoundKnobButton2(mActivity, 0, m_Inst.Scale(110), m_Inst.Scale(110));
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

    private void setButtonListener(View view) {
        view.findViewById(R.id.lumpsum_btn).setOnClickListener(this);
        view.findViewById(R.id.sip_btn).setOnClickListener(this);
    }

    private void initializer(View view) {
        audioPlayer = new AudioPlayer();
        mRelToolbar = view.findViewById(R.id.relToolBar);
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
        mEtPresentAge = view.findViewById(R.id.edit1);
        mEtMarrigeAge = view.findViewById(R.id.edit2);
        mEtWeddingCost = view.findViewById(R.id.edit3);
        mEtSaving = view.findViewById(R.id.edit4);
        mEtExitingCurpus = view.findViewById(R.id.edit5);
        mEtExpectedRate = view.findViewById(R.id.edit6);
        mEtExpectedInflation = view.findViewById(R.id.edit7);
        mScrollView = view.findViewById(R.id.scrollView);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        mLinerResultPart = view.findViewById(R.id.linerResultPart);
        tvAddress = view.findViewById(R.id.tv_address);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        mScrollView = view.findViewById(R.id.scrollView);
        mTvTitle = view.findViewById(R.id.textView1);
        mTvText2 = view.findViewById(R.id.textView2);
        mTvText3 = view.findViewById(R.id.textView3);
        mTvText4 = view.findViewById(R.id.textView4);
        mTvResult1 = view.findViewById(R.id.tvResult);
        mTvResult2 = view.findViewById(R.id.tvResult2);
        mLinerResult = view.findViewById(R.id.linerResult);
        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest4 = view.findViewById(R.id.dialer4);
        mDailerInvest5 = view.findViewById(R.id.dialer5);
        mDailerInvest6 = view.findViewById(R.id.dialer6);
        mDailerInvest7 = view.findViewById(R.id.dialer7);
        tvErrorAge = view.findViewById(R.id.tv_err_child_age);
        tvErrorMarAge = view.findViewById(R.id.tv_err_child_mar_age);
        tvErrAmountReq = view.findViewById(R.id.tv_err_wed_amount);
        tvErrorCorpus = view.findViewById(R.id.tv_err_corpus);
        tvErrorRoi = view.findViewById(R.id.tv_err_roi);
        tvErrorRor = view.findViewById(R.id.tv_err_ror);
        tvErrorSaving = view.findViewById(R.id.tv_err_annual_save);
    }

    private void initialChecks() {
        bundle = getArguments();
        if (mBrokerActivity != null) {
            m_Inst.InitGUIFrame(mBrokerActivity);

        } else {
            m_Inst.InitGUIFrame(mActivity);

        }
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot")) &&
                    investwell.utils.Utils.getConfigData(mSession).optString("SharingSnapshot").equalsIgnoreCase("Y")) {

                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_marriage_cal),
                        true, true, false, false, true, false, false, "");
            } else {
                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_marriage_cal),
                        true, false, false, false, true, false, false, "");

            }


            fragToolBar.setCallback(this);
        }
        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_marriage_cal));
    }

    private void clearErrorMessage() {

        tvErrorSaving.setText("");
        tvErrorMarAge.setText("");
        tvErrorRor.setText("");
        tvErrorCorpus.setText("");
        tvErrorAge.setText("");
        tvErrorRoi.setText("");
        tvErrAmountReq.setText("");
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMarAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);

    }

    private void setErrCorpusVisibility() {
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMarAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.VISIBLE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrInfRateVisibility() {
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMarAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.VISIBLE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrSaveVisibility() {
        tvErrorSaving.setVisibility(View.VISIBLE);
        tvErrorMarAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrAgeVisibility() {
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMarAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.VISIBLE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrRetAgeVisibility() {
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMarAge.setVisibility(View.VISIBLE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrRor() {
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMarAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.VISIBLE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setTvErrAmountReq() {
        tvErrorSaving.setVisibility(View.GONE);
        tvErrorMarAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateChange(boolean newstate) {

    }

    @Override
    public void onRotate(int percentage) {

    }

    @Override
    public void onClick(View view) {
        int duration = Integer.parseInt(mEtMarrigeAge.getText().toString()) - Integer.parseInt(mEtPresentAge.getText().toString());
        Bundle bundle = new Bundle();
       /* if (view.getId() == R.id.ivRight) {
            saveFrameLayout();
        } *//*else if (view.getId() == R.id.ivRight2) {
            refreshView();
        } *//*else if (view.getId() == R.id.ivLeft) {
            mActivity.getSupportFragmentManager().popBackStack();
        }*/
        if (view.getId() == R.id.lumpsum_btn) {
            bundle.putString("type", "coming_from_goal");
            bundle.putString("investment_type", "Lumpsum");
            bundle.putString("ic_invest_route_goal", "Marriage Planning");
            bundle.putString("Amount", mTvResult1.getText().toString());
            bundle.putInt("duration", duration);
            if (mBrokerActivity != null) {
                mBrokerActivity.displayViewOther(64, bundle);
            } else {
                mActivity.displayViewOther(64, bundle);
            }
            System.out.println(bundle.toString());

        } else if (view.getId() == R.id.sip_btn) {
            bundle.putString("type", "coming_from_goal");
            bundle.putString("investment_type", "SIP");
            bundle.putString("ic_invest_route_goal", "Marriage Planning");
            bundle.putString("Amount", mTvResult2.getText().toString());
            bundle.putInt("duration", duration);
            if (mBrokerActivity != null) {
                mBrokerActivity.displayViewOther(64, bundle);
            } else {
                mActivity.displayViewOther(64, bundle);
            }
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

        mEtPresentAge.setText("2");
        mEtMarrigeAge.setText("25");
        mEtWeddingCost.setText("5");
        mEtSaving.setText("0");
        mEtExitingCurpus.setText("0");
        mEtWeddingCost.setText("500000");
        mEtExpectedRate.setText("12.00");
        mEtExpectedInflation.setText("6.00");
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
            if (mEtPresentAge.getText().toString().equals("")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_calculator_error_empty_child_age));
                tvErrorAge.setText(getResources().getString(R.string.education_calculator_error_empty_child_age));
                setErrAgeVisibility();
            } else if (mEtMarrigeAge.getText().toString().equals("")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_error_empty_start_wed_age));
                tvErrorMarAge.setText(getResources().getString(R.string.marriage_cal_error_empty_start_wed_age));
                setErrRetAgeVisibility();
            } else if (Integer.parseInt(mEtPresentAge.getText().toString()) > Integer.parseInt(mEtMarrigeAge.getText().toString())) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_invalid_child_age));
                tvErrorAge.setText(getResources().getString(R.string.marriage_cal_invalid_child_age));
                setErrAgeVisibility();
            } else if (mEtWeddingCost.getText().toString().replaceAll(",", "").equals("") || mEtWeddingCost.getText().toString().replaceAll(",", "").equals("0")) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_error_wed_amnt));
                tvErrAmountReq.setText(getResources().getString(R.string.marriage_cal_error_wed_amnt));
                setTvErrAmountReq();
            } else if ((Long.parseLong(mEtWeddingCost.getText().toString().replaceAll(",", ""))) < 100000) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_error_minnimum_age));
                tvErrAmountReq.setText(getResources().getString(R.string.marriage_cal_error_minnimum_age));
                setTvErrAmountReq();
            } else if ((Long.parseLong(mEtWeddingCost.getText().toString().replaceAll(",", ""))) > 100000000) {
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_maxx_amnt));
                tvErrAmountReq.setText(getResources().getString(R.string.marriage_cal_maxx_amnt));
                setTvErrAmountReq();
            } else if (mEtExpectedRate.getText().toString().equals("") || mEtExpectedRate.getText().toString().equals("0.00")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_error_expected_return));
                tvErrorRor.setText(getResources().getString(R.string.marriage_cal_error_expected_return));
                setErrRor();
            } else if (mEtExpectedRate.getText().toString().equals("") || mEtExpectedRate.getText().toString().equals("0.00")) {
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_error_expected_rate_return));
                tvErrorRor.setText(getResources().getString(R.string.marriage_cal_error_expected_rate_return));
                setErrRor();
            } else if (Double.parseDouble(mEtExpectedRate.getText().toString()) > 30) {
                mEtExpectedRate.setText("30.00");
                // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_error_less_expected_ror));
                tvErrorRor.setText(getResources().getString(R.string.marriage_cal_error_less_expected_ror));
                setErrRor();
            } else if (mEtExpectedInflation.getText().toString().equals("")) {
                //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_some_errror));
                tvErrorRoi.setText(getResources().getString(R.string.marriage_cal_some_errror));
                setErrInfRateVisibility();
            } else if (Double.parseDouble(mEtExpectedInflation.getText().toString()) > 30) {
                mEtExpectedInflation.setText("30.00");
                //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.marriage_cal_expected_inflation_rate));
                tvErrorRoi.setText(getResources().getString(R.string.marriage_cal_expected_inflation_rate));
                setErrInfRateVisibility();
            } else {
                String rawValue = mEtWeddingCost.getText().toString().replaceAll(",", "").toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue, mEtWeddingCost);

                String rawValue2 = mEtSaving.getText().toString().replaceAll(",", "").toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue2, mEtSaving);

                String rawValue3 = mEtExitingCurpus.getText().toString().replaceAll(",", "").toString().replaceAll(",", "").toString().replaceAll(",", "");
                mBrokerActivity.convertIntoCurrencyFormat(rawValue3, mEtExitingCurpus);

                try {
                    dblTymLeft = 0;
                    dblExistingCorpus = 0;
                    dblExistingGrowth = 0;
                    futureSaving = 0;
                    add_sav = 0;
                    inflammationCost = 0;
                    onetimeinv = 0;
                    monthlyinv = 0;
                    yearlyinv = 0;


                    dblChildAge = Double.parseDouble(mEtPresentAge.getText().toString());
                    dblMrgAge = Double.parseDouble(mEtMarrigeAge.getText().toString());
                    dblMrgCost = Double.parseDouble(mEtWeddingCost.getText().toString().replaceAll(",", "").toString());
                    dblYrlySaving = Double.parseDouble(mEtSaving.getText().toString().replaceAll(",", "").toString());
                    dblExpReturnRate = Double.parseDouble(mEtExpectedRate.getText().toString());
                    dblExtInflammation = Double.parseDouble(mEtExpectedInflation.getText().toString());
                    dblExistingCorpus = Double.parseDouble(mEtExitingCurpus.getText().toString().replaceAll(",", "").toString());
//hcuipwenc
                    //calculation for inflation adjusted cost
                    dblTymLeft = dblMrgAge - dblChildAge;

                    inflammationCost = dblMrgCost * (Math.pow((1 + dblExtInflammation / 100), dblTymLeft));

                    //Calculation for existing corpus gain after retirement
                    dblExistingGrowth = dblExistingCorpus * (Math.pow((1 + dblExpReturnRate / 100), dblTymLeft));

                    //double leftAmount=inflammationCost-dblExistingGrowth;

                    //calculation for Future value of my savings

                    double p = Math.pow((1 + dblExpReturnRate / 100), dblTymLeft);

                    futureSaving = (dblYrlySaving * (p - 1)) / (dblExpReturnRate / 100);


                    //calculation for Additional funds required to meet expenses

                    add_fund = inflammationCost - (futureSaving + dblExistingGrowth);

                    //calculation for Additional savings required for year

                    add_sav = (add_fund * (dblExpReturnRate / 100)) / (p - 1);


                    onetimeinv = add_fund / (Math.pow((1 + dblExpReturnRate / 100), dblTymLeft));

                    yearlyinv = (add_fund * dblExpReturnRate / 100) / ((Math.pow((1 + dblExpReturnRate / 100), dblTymLeft)) - 1);


                    // Calculation for Monthly Investment
                    double pm = 12 * dblTymLeft;
                    double intr = dblExpReturnRate / 1200;


                    double powr = Math.pow((1 + intr), pm);
                    monthlyinv = ((add_fund * ((intr * powr) / (powr - 1))) / powr);
                    if (dblExpReturnRate == 0) {
                        monthlyinv = (add_fund / (dblTymLeft * 12));
                    }
                    if (dblTymLeft == 0) {
                        monthlyinv = add_fund;
                    }

                    mLinerResult.setVisibility(View.VISIBLE);

                    String strAmount = "";
                    String[] resultAmount;
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                    long value1 = Math.round(dblExistingGrowth);
                    strAmount = format.format(value1);
                    resultAmount = strAmount.split("\\.", 0);
                    String existingGroth = resultAmount[0];

                    long value2 = Math.round(add_fund);
                    strAmount = format.format(value2);
                    resultAmount = strAmount.split("\\.", 0);
                    String leftAmount = resultAmount[0];

                    long value3 = Math.round(dblExistingCorpus);
                    strAmount = format.format(value3);
                    resultAmount = strAmount.split("\\.", 0);
                    String curpus_vlaue = resultAmount[0];

                    long value4 = Math.round(futureSaving);
                    strAmount = format.format(value4);
                    resultAmount = strAmount.split("\\.", 0);
                    String future_Saving = resultAmount[0];

                    long value5 = Math.round(inflammationCost);
                    strAmount = format.format(value5);
                    resultAmount = strAmount.split("\\.", 0);
                    String totalAmountAtMarrige = resultAmount[0];
                    mTvText2.setText(totalAmountAtMarrige);

                    if (add_fund > 0) {
                        mLinerResultPart.setVisibility(View.VISIBLE);
                        mTvText4.setVisibility(View.GONE);
                        mTvText3.setTextColor(ContextCompat.getColor(mBrokerActivity, R.color.colorGrey_600));
                        mTvText3.setText(getResources().getString(R.string.marriage_cal_goal_txt));
                    } else {
                        mTvText3.setText(getResources().getString(R.string.marriage_cal_congrats));
                        mTvText4.setText(getResources().getString(R.string.marriage_cal_desc));
                        mTvText3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
                        mLinerResultPart.setVisibility(View.GONE);
                        mTvText4.setVisibility(View.VISIBLE);
                    }


                    long value6 = Math.round(onetimeinv);
                    double totalAmount = Math.round(value6);
                    strAmount = format.format(totalAmount);
                    resultAmount = strAmount.split("\\.", 0);
                    mTvResult1.setText(resultAmount[0]);

                    long value7 = Math.round(monthlyinv);
                    double shortFall = Math.round(value7);
                    strAmount = format.format(shortFall);
                    resultAmount = strAmount.split("\\.", 0);
                    mTvResult2.setText(resultAmount[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        } else {
            if (mEtPresentAge.getText().toString().equals("")) {
                // mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.education_calculator_error_empty_child_age));
                tvErrorAge.setText(getResources().getString(R.string.education_calculator_error_empty_child_age));
                setErrAgeVisibility();
            } else if (mEtMarrigeAge.getText().toString().equals("")) {
                //mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_error_empty_start_wed_age));
                tvErrorMarAge.setText(getResources().getString(R.string.marriage_cal_error_empty_start_wed_age));
                setErrRetAgeVisibility();
            } else if (Integer.parseInt(mEtPresentAge.getText().toString()) > Integer.parseInt(mEtMarrigeAge.getText().toString())) {
                // mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_invalid_child_age));
                tvErrorAge.setText(getResources().getString(R.string.marriage_cal_invalid_child_age));
                setErrAgeVisibility();
            } else if (mEtWeddingCost.getText().toString().replaceAll(",", "").equals("") || mEtWeddingCost.getText().toString().replaceAll(",", "").equals("0")) {
                //  mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_error_wed_amnt));
                tvErrAmountReq.setText(getResources().getString(R.string.marriage_cal_error_wed_amnt));
                setTvErrAmountReq();
            } else if ((Long.parseLong(mEtWeddingCost.getText().toString().replaceAll(",", ""))) < 100000) {
                //mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_error_minnimum_age));
                tvErrAmountReq.setText(getResources().getString(R.string.marriage_cal_error_minnimum_age));
                setTvErrAmountReq();
            } else if ((Long.parseLong(mEtWeddingCost.getText().toString().replaceAll(",", ""))) > 100000000) {
                // mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_maxx_amnt));
                tvErrAmountReq.setText(getResources().getString(R.string.marriage_cal_maxx_amnt));
                setTvErrAmountReq();
            } else if (mEtExpectedRate.getText().toString().equals("") || mEtExpectedRate.getText().toString().equals("0.00")) {
                // mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_error_expected_return));
                tvErrorRor.setText(getResources().getString(R.string.marriage_cal_error_expected_return));
                setErrRor();
            } else if (mEtExpectedRate.getText().toString().equals("") || mEtExpectedRate.getText().toString().equals("0.00")) {
                //  mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_error_expected_rate_return));
                tvErrorRor.setText(getResources().getString(R.string.marriage_cal_error_expected_rate_return));
                setErrRor();
            } else if (Double.parseDouble(mEtExpectedRate.getText().toString()) > 30) {
                mEtExpectedRate.setText("30.00");
                tvErrorRor.setText(getResources().getString(R.string.marriage_cal_error_less_expected_ror));
                setErrRor();
                //mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_error_less_expected_ror));
            } else if (mEtExpectedInflation.getText().toString().equals("")) {
                //mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_some_errror));
                tvErrorRoi.setText(getResources().getString(R.string.marriage_cal_some_errror));
                setErrInfRateVisibility();
            } else if (Double.parseDouble(mEtExpectedInflation.getText().toString()) > 30) {
                mEtExpectedInflation.setText("30.00");
                //mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.marriage_cal_expected_inflation_rate));
                tvErrorRoi.setText(getResources().getString(R.string.marriage_cal_expected_inflation_rate));
                setErrInfRateVisibility();
            } else {
                String rawValue = mEtWeddingCost.getText().toString().replaceAll(",", "").toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue, mEtWeddingCost);

                String rawValue2 = mEtSaving.getText().toString().replaceAll(",", "").toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue2, mEtSaving);

                String rawValue3 = mEtExitingCurpus.getText().toString().replaceAll(",", "").toString().replaceAll(",", "").toString().replaceAll(",", "");
                mActivity.convertIntoCurrencyFormat(rawValue3, mEtExitingCurpus);

                try {
                    dblTymLeft = 0;
                    dblExistingCorpus = 0;
                    dblExistingGrowth = 0;
                    futureSaving = 0;
                    add_sav = 0;
                    inflammationCost = 0;
                    onetimeinv = 0;
                    monthlyinv = 0;
                    yearlyinv = 0;


                    dblChildAge = Double.parseDouble(mEtPresentAge.getText().toString());
                    dblMrgAge = Double.parseDouble(mEtMarrigeAge.getText().toString());
                    dblMrgCost = Double.parseDouble(mEtWeddingCost.getText().toString().replaceAll(",", "").toString());
                    dblYrlySaving = Double.parseDouble(mEtSaving.getText().toString().replaceAll(",", "").toString());
                    dblExpReturnRate = Double.parseDouble(mEtExpectedRate.getText().toString());
                    dblExtInflammation = Double.parseDouble(mEtExpectedInflation.getText().toString());
                    dblExistingCorpus = Double.parseDouble(mEtExitingCurpus.getText().toString().replaceAll(",", "").toString());
//hcuipwenc
                    //calculation for inflation adjusted cost
                    dblTymLeft = dblMrgAge - dblChildAge;

                    inflammationCost = dblMrgCost * (Math.pow((1 + dblExtInflammation / 100), dblTymLeft));

                    //Calculation for existing corpus gain after retirement
                    dblExistingGrowth = dblExistingCorpus * (Math.pow((1 + dblExpReturnRate / 100), dblTymLeft));

                    //double leftAmount=inflammationCost-dblExistingGrowth;

                    //calculation for Future value of my savings

                    double p = Math.pow((1 + dblExpReturnRate / 100), dblTymLeft);

                    futureSaving = (dblYrlySaving * (p - 1)) / (dblExpReturnRate / 100);


                    //calculation for Additional funds required to meet expenses

                    add_fund = inflammationCost - (futureSaving + dblExistingGrowth);

                    //calculation for Additional savings required for year

                    add_sav = (add_fund * (dblExpReturnRate / 100)) / (p - 1);


                    onetimeinv = add_fund / (Math.pow((1 + dblExpReturnRate / 100), dblTymLeft));

                    yearlyinv = (add_fund * dblExpReturnRate / 100) / ((Math.pow((1 + dblExpReturnRate / 100), dblTymLeft)) - 1);


                    // Calculation for Monthly Investment
                    double pm = 12 * dblTymLeft;
                    double intr = dblExpReturnRate / 1200;


                    double powr = Math.pow((1 + intr), pm);
                    monthlyinv = ((add_fund * ((intr * powr) / (powr - 1))) / powr);
                    if (dblExpReturnRate == 0) {
                        monthlyinv = (add_fund / (dblTymLeft * 12));
                    }
                    if (dblTymLeft == 0) {
                        monthlyinv = add_fund;
                    }

                    mLinerResult.setVisibility(View.VISIBLE);

                    String strAmount = "";
                    String[] resultAmount;
                    Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                    long value1 = Math.round(dblExistingGrowth);
                    strAmount = format.format(value1);
                    resultAmount = strAmount.split("\\.", 0);
                    String existingGroth = resultAmount[0];

                    long value2 = Math.round(add_fund);
                    strAmount = format.format(value2);
                    resultAmount = strAmount.split("\\.", 0);
                    String leftAmount = resultAmount[0];

                    long value3 = Math.round(dblExistingCorpus);
                    strAmount = format.format(value3);
                    resultAmount = strAmount.split("\\.", 0);
                    String curpus_vlaue = resultAmount[0];

                    long value4 = Math.round(futureSaving);
                    strAmount = format.format(value4);
                    resultAmount = strAmount.split("\\.", 0);
                    String future_Saving = resultAmount[0];

                    long value5 = Math.round(inflammationCost);
                    strAmount = format.format(value5);
                    resultAmount = strAmount.split("\\.", 0);
                    String totalAmountAtMarrige = resultAmount[0];
                    mTvText2.setText(totalAmountAtMarrige);

                    if (add_fund > 0) {
                        mLinerResultPart.setVisibility(View.VISIBLE);
                        mTvText4.setVisibility(View.GONE);
                        mTvText3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGrey_600));
                        mTvText3.setText(getResources().getString(R.string.marriage_cal_goal_txt));
                    } else {
                        mTvText3.setText(getResources().getString(R.string.marriage_cal_congrats));
                        mTvText4.setText(getResources().getString(R.string.marriage_cal_desc));
                        mTvText3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
                        mLinerResultPart.setVisibility(View.GONE);
                        mTvText4.setVisibility(View.VISIBLE);
                    }


                    long value6 = Math.round(onetimeinv);
                    double totalAmount = Math.round(value6);
                    strAmount = format.format(totalAmount);
                    resultAmount = strAmount.split("\\.", 0);
                    mTvResult1.setText(resultAmount[0]);

                    long value7 = Math.round(monthlyinv);
                    double shortFall = Math.round(value7);
                    strAmount = format.format(shortFall);
                    resultAmount = strAmount.split("\\.", 0);
                    mTvResult2.setText(resultAmount[0]);

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
                if (value < 2)
                    mEtPresentAge.setText("" + 1);
                else if (value > 25)
                    mEtPresentAge.setText("" + 25);
            } else {
                mEtPresentAge.setText("" + 1);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer2) {
            currentValue = mEtMarrigeAge.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 18)
                    mEtMarrigeAge.setText("" + 18);
                else if (value > 35)
                    mEtMarrigeAge.setText("" + 35);

            } else {
                mEtMarrigeAge.setText("" + 18);
            }

            desableScrollView(motionEvent);


        } else if (i == R.id.dialer3) {
            currentValue = mEtWeddingCost.getText().toString().replaceAll(",", "").toString();
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 100000)
                    mEtWeddingCost.setText("" + 100000);

            } else {
                mEtWeddingCost.setText("" + 100000);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer4) {
            currentValue = mEtSaving.getText().toString().replaceAll(",", "").toString();
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 1000)
                    mEtSaving.setText("" + 0);
            } else {
                mEtSaving.setText("" + 5000);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer5) {
            currentValue = mEtExitingCurpus.getText().toString().replaceAll(",", "").toString();
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 1)
                    mEtExitingCurpus.setText("" + 0);

            } else {
                mEtExitingCurpus.setText("" + 10000);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer6) {
            currentValue = mEtExpectedRate.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtExpectedRate.setText("" + 1.00);
                else if (value > 30)
                    mEtExpectedRate.setText("" + 30.00);

            } else {
                mEtExpectedRate.setText("" + 12.00);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer7) {
            currentValue = mEtExpectedInflation.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtExpectedInflation.setText("" + 0.00);
                else if (value > 30)
                    mEtExpectedRate.setText("" + 30.00);

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

        path = path + "/" + "Calculator-" + getResources().getString(R.string.toolBar_title_marriage_cal) + System.currentTimeMillis() + ".pdf";// path where pdf will be stored


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
                } else if (mBrokerActivity.getCurrentFocus() == mEtMarrigeAge) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtWeddingCost) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtSaving) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtExitingCurpus) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtExpectedRate) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtExpectedInflation) {
                    delayCall();
                }
            } else {
                if (mActivity.getCurrentFocus() == mEtPresentAge) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtMarrigeAge) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtWeddingCost) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtSaving) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtExitingCurpus) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtExpectedRate) {
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

