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

public class FragEducationCalculator extends Fragment implements View.OnClickListener,
        View.OnTouchListener, RoundKnobButton.RoundKnobButtonListener, ToolbarFragment.ToolbarCallback {

    private final long DELAY = 1000; // milliseconds
    Singleton m_Inst = Singleton.getInstance();
    ;
    double dblTymLeft = 0, inflammationCost = 0, totalCost = 0, dblExistingGrowth = 0, sav = 0, leftAmount = 0, onetimeinv = 0, monthlyinv = 0, yearlyinv = 0;
    double dblChildAge = 0, dblColgCost = 0, dblExistingCorpus = 0, dblExpReturnRate = 0, dblExtInflammation = 0, dblClgAge = 0, dlbDuration = 0;
    private EditText mEtPresentAge, mEtCollegeStart, mEtCollegeDurations, mEtCostPerYear, mEtExitingCurpus, mEtExpectedRate, mEtExpectedInflation;
    private TextView mTvTitle, mTvText2, mTvResult3, mTvResult4, mTvResult1, mTvResult2, mTvImageTitle;
    private RelativeLayout mLinerResult;
    private ImageView mIvImageRight, mIvRefresh, mIvBack;
    private int mCount1 = 0, mCount2 = 0, mCount3 = 0, mCount4 = 0, mCount5 = 0, mCount6 = 0, mCount7 = 0, mCount8 = 0, mCount9 = 0;
    private AudioPlayer audioPlayer;
    private ScrollView mScrollView;
    private RoundKnobButton mDailerInvest1, mDailerInvest2, mDailerInvest3, mDailerInvest4, mDailerInvest5, mDailerInvest6, mDailerInvest7;
    private LinearLayout mLinerFooter, mLinerResultPart;
    private Timer timer = new Timer();
    private AppApplication mApplication;
    private RelativeLayout mRelToolbar;

    private Bundle bundle;
    private ToolbarFragment fragToolBar;
    private CustomTextViewBold tvHeaderTitle;
    private LinearLayout ll_calculator_header;
    private BrokerActivity mBrokerActivity;
    private MainActivity mActivity;
    private AppSession mSession;
    boolean dialogShown;
    Bitmap b;
    private String path = "";
    private TextView tvBrokerName, tvAddress, tvEmail, tvPhone, tvWeb;
    private TextView tvErrorAge, tvErrorClgAge, tvErrAmountReq, tvErrorRoi, tvErrorDurEdu, tvErrorCorpus, tvErrorRor;

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
        View view = inflater.inflate(R.layout.frag_education, container, false);
        initialChecks();
        initializer(view);
        setUpToolBar();
        setButtonListener(view);
        setFooterViewsData();
        initialDialerSetUp();
        setOnTouchListener(view);
        setDialerOneListener();
        setDialerTwoListener();
        setDialerThreeListener();
        setDialerFourListener();
        setDialerFiveListener();
        setDialerSixListener();
        setDialerSevenListener();
        setListener(view);
        calculateAmount();
        return view;
    }

    private void setListener(View view) {
        mEtPresentAge.addTextChangedListener(new GenericTextWatcher(mEtPresentAge));
        mEtCollegeStart.addTextChangedListener(new GenericTextWatcher(mEtCollegeStart));
        mEtCollegeDurations.addTextChangedListener(new GenericTextWatcher(mEtCollegeDurations));
        mEtCostPerYear.addTextChangedListener(new GenericTextWatcher(mEtCostPerYear));
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
                                mEtExpectedInflation.setText("" + 30.00);
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

    private void setDialerFiveListener() {
        mDailerInvest5.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
                //Toast.makeText(SipActivity.this, "New state:" + newstate, Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {

                mEtExitingCurpus.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtExitingCurpus.getText().toString().replaceAll(",", ""));
                        long updateValue = 0;
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

                mEtCostPerYear.post(new Runnable() {
                    public void run() {
                        long currentValue = Long.parseLong(mEtCostPerYear.getText().toString().replaceAll(",", ""));
                        long updateValue = 0;
                        if (mCount4 != percentage) {
                            if (percentage > mCount4) {
                                updateValue = (currentValue + 10000);
                            } else if (percentage < mCount4) {
                                updateValue = (currentValue - 10000);
                            }

                            mCount4 = percentage;
                            if (updateValue < 10000)
                                mEtCostPerYear.setText("" + 10000);
                            else
                                mEtCostPerYear.setText("" + updateValue);

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
                mEtCollegeDurations.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtCollegeDurations.getText().toString());
                        int updateValue = 0;
                        if (mCount3 != percentage) {
                            if (percentage > mCount3) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount3) {
                                updateValue = (currentValue - 1);
                            }

                            mCount3 = percentage;
                            if (updateValue < 1)
                                mEtCollegeDurations.setText("" + 1);
                            else if (updateValue > 10)
                                mEtCollegeDurations.setText("" + 10);
                            else
                                mEtCollegeDurations.setText("" + updateValue);

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
                mEtCollegeStart.post(new Runnable() {
                    public void run() {
                        int currentValue = Integer.parseInt(mEtCollegeStart.getText().toString());
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

                                if (updateValue < 15)
                                    mEtCollegeStart.setText("" + 15);
                                else if (updateValue > 25)
                                    mEtCollegeStart.setText("" + 25);
                                else
                                    mEtCollegeStart.setText("" + updateValue);

                                if (mBrokerActivity != null)
                                    audioPlayer.play(mBrokerActivity);
                                else
                                    audioPlayer.play(mActivity);
                                calculateAmount();

                            } else {

                                mApplication.showSnackBar(mEtPresentAge, getResources().getString(R.string.education_calculator_error_college));
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
                        int collegeStart = Integer.parseInt(mEtCollegeStart.getText().toString());
                        int updateValue = 0;


                        if (mCount1 != percentage) {
                            if (percentage > mCount1) {
                                updateValue = (currentValue + 1);
                            } else if (percentage < mCount1) {
                                updateValue = (currentValue - 1);
                            }

                            mCount1 = percentage;
                            if (currentValue < collegeStart || updateValue < collegeStart) {
                                if (updateValue < 1) {
                                    mEtPresentAge.setText("" + 0);
                                } else {
                                    mEtPresentAge.setText("" + updateValue);
                                }

                                if (mBrokerActivity != null)
                                    audioPlayer.play(mBrokerActivity);
                                else
                                    audioPlayer.play(mActivity);
                                calculateAmount();

                            } else {
                                mApplication.showSnackBar(mEtPresentAge, getResources().getString(R.string.education_calculator_invalid_age));


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

    private void initialDialerSetUp() {
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
        tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        ll_calculator_header = view.findViewById(R.id.ll_calculator_header);
        mRelToolbar = view.findViewById(R.id.relToolBar);
        mEtPresentAge = view.findViewById(R.id.edit1);
        mEtCollegeStart = view.findViewById(R.id.edit2);
        mEtCollegeDurations = view.findViewById(R.id.edit3);
        mEtCostPerYear = view.findViewById(R.id.edit4);
        mEtExitingCurpus = view.findViewById(R.id.edit5);
        mEtExpectedRate = view.findViewById(R.id.edit6);
        mEtExpectedInflation = view.findViewById(R.id.edit7);
        tvErrorAge=view.findViewById(R.id.tv_err_age);
        tvErrorClgAge=view.findViewById(R.id.tv_err_clg_age);
        tvErrorDurEdu=view.findViewById(R.id.tv_err_dur_edu);
        tvErrorCorpus=view.findViewById(R.id.tv_err_corpus);
        tvErrorRor=view.findViewById(R.id.tv_err_ror);
        tvErrorRoi=view.findViewById(R.id.tv_err_roi);
        tvErrAmountReq=view.findViewById(R.id.tv_err_cost);
        mScrollView = view.findViewById(R.id.sv_edu);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        mLinerResultPart = view.findViewById(R.id.linerResultPart);
        tvAddress = view.findViewById(R.id.tv_address);
        tvBrokerName = view.findViewById(R.id.tv_broker_name);
        tvWeb = view.findViewById(R.id.tv_web);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_call);
        mTvTitle = view.findViewById(R.id.textView1);
        mTvText2 = view.findViewById(R.id.textView2);
        mTvResult1 = view.findViewById(R.id.tvResult);
        mTvResult2 = view.findViewById(R.id.tvResult2);
        mTvResult3 = view.findViewById(R.id.textView3);
        mTvResult4 = view.findViewById(R.id.textView4);
        mLinerResult = view.findViewById(R.id.linerResult);
        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest4 = view.findViewById(R.id.dialer4);
        mDailerInvest5 = view.findViewById(R.id.dialer5);
        mDailerInvest6 = view.findViewById(R.id.dialer6);
        mDailerInvest7 = view.findViewById(R.id.dialer7);
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

                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_education_cal),
                        true, true, false, false, true, false, false, "");
            } else {

                fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_education_cal),
                        true, false, false, false, true, false, false, "");

            }
            fragToolBar.setCallback(this);
        }
        tvHeaderTitle.setText(getResources().getString(R.string.toolBar_title_education_cal));
    }

    private void clearErrorMessage() {

        tvErrorDurEdu.setText("");
        tvErrorClgAge.setText("");
        tvErrorRor.setText("");
        tvErrorCorpus.setText("");
        tvErrorAge.setText("");
        tvErrorRoi.setText("");
        tvErrAmountReq.setText("");
        tvErrorDurEdu.setVisibility(View.GONE);
        tvErrorClgAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);

    }

    private void setErrCorpusVisibility() {
        tvErrorDurEdu.setVisibility(View.GONE);
        tvErrorClgAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.VISIBLE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrInfRateVisibility() {
        tvErrorDurEdu.setVisibility(View.GONE);
        tvErrorClgAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.VISIBLE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrDurVisibility() {
        tvErrorDurEdu.setVisibility(View.VISIBLE);
        tvErrorClgAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.GONE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrAgeVisibility() {
        tvErrorDurEdu.setVisibility(View.GONE);
        tvErrorClgAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.VISIBLE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrRetAgeVisibility() {
        tvErrorDurEdu.setVisibility(View.GONE);
        tvErrorClgAge.setVisibility(View.VISIBLE);
        tvErrorRor.setVisibility(View.GONE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.VISIBLE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setErrRor() {
        tvErrorDurEdu.setVisibility(View.GONE);
        tvErrorClgAge.setVisibility(View.GONE);
        tvErrorRor.setVisibility(View.VISIBLE);
        tvErrorCorpus.setVisibility(View.GONE);
        tvErrorAge.setVisibility(View.VISIBLE);
        tvErrorRoi.setVisibility(View.GONE);
        tvErrAmountReq.setVisibility(View.GONE);
    }

    private void setTvErrAmountReq() {
        tvErrorDurEdu.setVisibility(View.GONE);
        tvErrorClgAge.setVisibility(View.GONE);
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
        Bundle bundle = new Bundle();
        int duration = Integer.parseInt(mEtCollegeStart.getText().toString()) - Integer.parseInt(mEtPresentAge.getText().toString());
        int id = view.getId();/*    case R.id.ivRight:
                saveFrameLayout();
                break;*//*  case R.id.ivRight2:
                refreshView();
                break;*/
        if (id == R.id.lumpsum_btn) {
            if (mBrokerActivity != null) {
                bundle.putString("type", "coming_from_goal");
                bundle.putString("investment_type", "Lumpsum");
                bundle.putString("ic_invest_route_goal", "Education Planning");
                bundle.putString("Amount", mTvResult1.getText().toString());
                bundle.putInt("duration", duration);
                mBrokerActivity.displayViewOther(64, bundle);
            } else {
                bundle.putString("type", "coming_from_goal");
                bundle.putString("investment_type", "Lumpsum");
                bundle.putString("ic_invest_route_goal", "Education Planning");
                bundle.putString("Amount", mTvResult1.getText().toString());
                bundle.putInt("duration", duration);
                mActivity.displayViewOther(64, bundle);
            }
        } else if (id == R.id.sip_btn) {
            if (mBrokerActivity != null) {
                bundle.putString("type", "coming_from_goal");
                bundle.putString("investment_type", "SIP");
                bundle.putString("ic_invest_route_goal", "Education Planning");
                bundle.putString("Amount", mTvResult2.getText().toString());
                bundle.putInt("duration", duration);
                mBrokerActivity.displayViewOther(64, bundle);
            } else {
                bundle.putString("type", "coming_from_goal");
                bundle.putString("investment_type", "SIP");
                bundle.putString("ic_invest_route_goal", "Education Planning");
                bundle.putString("Amount", mTvResult2.getText().toString());
                bundle.putInt("duration", duration);
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
        mEtCollegeStart.setText("18");
        mEtCollegeDurations.setText("5");
        mEtCostPerYear.setText("500000");
        mEtExitingCurpus.setText("100000");
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
            try {

                if (mEtPresentAge.getText().toString().equals("")) {
                    //   mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_calculator_error_empty_child_age));
                    tvErrorAge.setText(getResources().getString(R.string.education_calculator_error_empty_child_age));
                    setErrAgeVisibility();
                } else if (mEtCollegeStart.getText().toString().equals("")) {
                    //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_empty_college_start_age));
                    tvErrorClgAge.setText(getResources().getString(R.string.education_cal_error_empty_college_start_age));
                    setErrRetAgeVisibility();
                } else if (Integer.parseInt(mEtCollegeStart.getText().toString()) > 25) {
                    //    mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_invalid_college_start_age));
                    tvErrorClgAge.setText(getResources().getString(R.string.education_cal_error_invalid_college_start_age));
                    setErrRetAgeVisibility();
                } else if (Integer.parseInt(mEtPresentAge.getText().toString()) > Integer.parseInt(mEtCollegeStart.getText().toString())) {
                    // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_invalid_child_age));
                    tvErrorAge.setText(getResources().getString(R.string.education_cal_invalid_child_age));
                    setErrAgeVisibility();
                } else if (mEtCollegeDurations.getText().toString().equals("")) {
                    // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_min_college_duration));
                    tvErrorDurEdu.setText(getResources().getString(R.string.education_cal_error_min_college_duration));
                    setErrDurVisibility();
                } else if (Integer.parseInt(mEtCollegeDurations.getText().toString()) > 10) {
                    //   mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_max_college_duration));
                    tvErrorDurEdu.setText(getResources().getString(R.string.education_cal_error_max_college_duration));
                    setErrDurVisibility();
                } else if (mEtExpectedRate.getText().toString().equals("") || mEtExpectedRate.getText().toString().equals("0")) {
                    //  mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_empty_expected_rate));
                    tvErrorRor.setText(getResources().getString(R.string.education_cal_error_empty_expected_rate));
                    setErrRor();
                } else if (Double.parseDouble(mEtExpectedRate.getText().toString()) > 30) {
                    mEtExpectedRate.setText("30.00");
                    // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_expected_max_rate));
                    tvErrorRor.setText(getResources().getString(R.string.education_cal_expected_max_rate));
                    setErrRor();
                } else if (mEtExpectedInflation.getText().toString().equals("")) {
                    // mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_expected_infl_rate));
                    tvErrorRoi.setText(getResources().getString(R.string.education_cal_error_expected_infl_rate));
                    setErrInfRateVisibility();
                } else if (Double.parseDouble(mEtExpectedInflation.getText().toString()) > 30) {
                    mEtExpectedInflation.setText("30.00");
                    tvErrorRoi.setText(getResources().getString(R.string.education_cal_error_min_infl_invalid_rate));
                    setErrInfRateVisibility();
                    //mBrokerActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_min_infl_invalid_rate));
                } else {
                    String rawValue = mEtCostPerYear.getText().toString().replaceAll(",", "");
                    mBrokerActivity.convertIntoCurrencyFormat(rawValue, mEtCostPerYear);

                    String rawValue2 = mEtExitingCurpus.getText().toString().replaceAll(",", "");
                    mBrokerActivity.convertIntoCurrencyFormat(rawValue2, mEtExitingCurpus);

                    try {

                        dblTymLeft = 0;
                        inflammationCost = 0;
                        totalCost = 0;
                        dblExistingGrowth = 0;
                        sav = 0;
                        leftAmount = 0;
                        onetimeinv = 0;
                        monthlyinv = 0;
                        yearlyinv = 0;

                        dblChildAge = Double.parseDouble(mEtPresentAge.getText().toString());
                        dblClgAge = Double.parseDouble(mEtCollegeStart.getText().toString());
                        dlbDuration = Double.parseDouble(mEtCollegeDurations.getText().toString());
                        dblColgCost = Double.parseDouble(mEtCostPerYear.getText().toString().replaceAll(",", ""));
                        dblExpReturnRate = Double.parseDouble(mEtExpectedRate.getText().toString());
                        dblExtInflammation = Double.parseDouble(mEtExpectedInflation.getText().toString());
                        dblExistingCorpus = Double.parseDouble(mEtExitingCurpus.getText().toString().replaceAll(",", ""));

                        dblTymLeft = dblClgAge - dblChildAge;

                        for (int i = 1; i <= dlbDuration; i++) {
                            inflammationCost = dblColgCost * (Math.pow((1 + (dblExtInflammation / 100)), dblTymLeft));
                            dblTymLeft++;
                            //calculation for total cost
                            totalCost = totalCost + inflammationCost;
                        }

                        //Calculation for existing corpus gain after retirement
                        dblExistingGrowth = dblExistingCorpus * (Math.pow((1 + dblExpReturnRate / 100), dblTymLeft));

                        leftAmount = totalCost - dblExistingGrowth;

                        // Calculation for Money need to save aside.
                        double totperiod = dblClgAge - dblChildAge;
                        double nMonths = totperiod * 12;
                        double v = 0;
                        double r = dblExpReturnRate / 100;

                        double m = dblColgCost * dlbDuration;


                        double rt = dblExpReturnRate - dblExtInflammation;

                        double a = Math.pow((1 + rt / 100), dblTymLeft);

                        sav = m / a;

                        onetimeinv = leftAmount / (Math.pow((1 + dblExpReturnRate / 100), totperiod));

                        yearlyinv = (leftAmount * dblExpReturnRate / 100) / ((Math.pow((1 + dblExpReturnRate / 100), totperiod)) - 1);


                        for (int i = 0; i < nMonths; i++) {
                            v = v + Math.pow((1 + r), (nMonths - i) / 12);
                        }

                        monthlyinv = leftAmount / v;


                        String strAmount = "";
                        String[] resultAmount;
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                        long value1 = Math.round(dblExistingGrowth);
                        strAmount = format.format(value1);
                        resultAmount = strAmount.split("\\.", 0);
                        String existingGroth = resultAmount[0];

                        long value2 = Math.round(leftAmount);
                        strAmount = format.format(value2);
                        resultAmount = strAmount.split("\\.", 0);
                        String leftAmount = resultAmount[0];


                        long value3 = Math.round(dblExistingCorpus);
                        strAmount = format.format(value3);
                        resultAmount = strAmount.split("\\.", 0);
                        String curpus_vlaue = resultAmount[0];

                        long value4 = Math.round(totalCost);
                        strAmount = format.format(value4);
                        resultAmount = strAmount.split("\\.", 0);
                        String total_amount_at_study = resultAmount[0];
                        mTvText2.setText(total_amount_at_study);


                        if (onetimeinv > 0) {
                            mLinerResultPart.setVisibility(View.VISIBLE);
                            mTvResult4.setVisibility(View.GONE);
                            mTvResult3.setTextColor(ContextCompat.getColor(mBrokerActivity, R.color.colorGrey_600));
                            mTvResult3.setText(getResources().getString(R.string.education_cal_some_txt));
                        } else {
                            mTvResult3.setText(getResources().getString(R.string.education_cal_congratulation));
                            mTvResult4.setText(getResources().getString(R.string.education_cal_goal_meet));
                            mTvResult3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
                            mLinerResultPart.setVisibility(View.GONE);
                            mTvResult4.setVisibility(View.VISIBLE);
                        }

               /*     if (onetimeinv > 0) {
                        mLinerResultPart.setVisibility(View.VISIBLE);
                        mTvText2.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                        if (dblExistingCorpus > 0)
                            mTvTitle.setText(String.valueOf("Existing corpus " + curpus_vlaue + " will grow to " + existingGroth + " then You need to invest for amount " + leftAmount + " because Amount required of College "));
                        else
                            mTvTitle.setText(String.valueOf("You need to invest for amount " + leftAmount + " because Amount required of College "));

                    } else {
                        if (dblExistingCorpus > 0) {
                            mTvTitle.setText(String.valueOf("Existing corpus " + curpus_vlaue + " will grow to " + existingGroth + " then Amount required at Marriage " + leftAmount + " then no need to invest more amount "));
                        } else {
                            mTvTitle.setText(String.valueOf("Amount required at Marriage " + leftAmount + " then no need to invest more amount "));
                        }
                        mTvText2.setText("Congratulations!");
                        mTvText2.setTextColor(ContextCompat.getColor(mActivity, R.color.green));
                        mLinerResultPart.setVisibility(View.GONE);
                    }
*/

                        long value5 = Math.round(onetimeinv);
                        double totalAmount = Math.round(value5);
                        strAmount = format.format(totalAmount);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvResult1.setText(resultAmount[0]);

                        long value6 = Math.round(monthlyinv);
                        double shortFall = Math.round(value6);
                        strAmount = format.format(shortFall);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvResult2.setText(resultAmount[0]);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            } catch (Exception e) {

            }
        } else {
            try {

                if (mEtPresentAge.getText().toString().equals("")) {
                    mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_calculator_error_empty_child_age));
                    tvErrorAge.setText(getResources().getString(R.string.education_calculator_error_empty_child_age));
                    setErrAgeVisibility();
                } else if (mEtCollegeStart.getText().toString().equals("")) {
                    tvErrorClgAge.setText(getResources().getString(R.string.education_cal_error_empty_college_start_age));
                    setErrRetAgeVisibility();
                    // mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_empty_college_start_age));
                } else if (Integer.parseInt(mEtCollegeStart.getText().toString()) > 25) {
                    tvErrorClgAge.setText(getResources().getString(R.string.education_cal_error_invalid_college_start_age));
                    setErrRetAgeVisibility();
                    // mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_invalid_college_start_age));
                } else if (Integer.parseInt(mEtPresentAge.getText().toString()) > Integer.parseInt(mEtCollegeStart.getText().toString())) {
                    //  mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_invalid_child_age));
                    tvErrorAge.setText(getResources().getString(R.string.education_cal_invalid_child_age));
                    setErrAgeVisibility();
                } else if (mEtCollegeDurations.getText().toString().equals("")) {
                    //  mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_min_college_duration));

                    tvErrorDurEdu.setText(getResources().getString(R.string.education_cal_error_min_college_duration));
                    setErrDurVisibility();
                } else if (Integer.parseInt(mEtCollegeDurations.getText().toString()) > 10) {
                    // mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_max_college_duration));
                    tvErrorDurEdu.setText(getResources().getString(R.string.education_cal_error_max_college_duration));
                    setErrDurVisibility();
                } else if (mEtExpectedRate.getText().toString().equals("") || mEtExpectedRate.getText().toString().equals("0")) {
                    //mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_empty_expected_rate));
                    tvErrorRor.setText(getResources().getString(R.string.education_cal_error_empty_expected_rate));
                    setErrRor();
                } else if (Double.parseDouble(mEtExpectedRate.getText().toString()) > 30) {
                    mEtExpectedRate.setText("30.00");
                    tvErrorRor.setText(getResources().getString(R.string.education_cal_expected_max_rate));
                    setErrRor();
                    // mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_expected_max_rate));
                } else if (mEtExpectedInflation.getText().toString().equals("")) {
                    // mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_expected_infl_rate));
                    tvErrorRoi.setText(getResources().getString(R.string.education_cal_error_expected_infl_rate));
                    setErrInfRateVisibility();
                } else if (Double.parseDouble(mEtExpectedInflation.getText().toString()) > 30) {
                    mEtExpectedInflation.setText("30.00");
                    tvErrorRoi.setText(getResources().getString(R.string.education_cal_error_min_infl_invalid_rate));
                    setErrInfRateVisibility();
                    // mActivity.showCommonDialog(mBrokerActivity, "Not Valid", getResources().getString(R.string.education_cal_error_min_infl_invalid_rate));
                } else {
                    String rawValue = mEtCostPerYear.getText().toString().replaceAll(",", "");
                    mActivity.convertIntoCurrencyFormat(rawValue, mEtCostPerYear);

                    String rawValue2 = mEtExitingCurpus.getText().toString().replaceAll(",", "");
                    mActivity.convertIntoCurrencyFormat(rawValue2, mEtExitingCurpus);

                    try {

                        dblTymLeft = 0;
                        inflammationCost = 0;
                        totalCost = 0;
                        dblExistingGrowth = 0;
                        sav = 0;
                        leftAmount = 0;
                        onetimeinv = 0;
                        monthlyinv = 0;
                        yearlyinv = 0;

                        dblChildAge = Double.parseDouble(mEtPresentAge.getText().toString());
                        dblClgAge = Double.parseDouble(mEtCollegeStart.getText().toString());
                        dlbDuration = Double.parseDouble(mEtCollegeDurations.getText().toString());
                        dblColgCost = Double.parseDouble(mEtCostPerYear.getText().toString().replaceAll(",", ""));
                        dblExpReturnRate = Double.parseDouble(mEtExpectedRate.getText().toString());
                        dblExtInflammation = Double.parseDouble(mEtExpectedInflation.getText().toString());
                        dblExistingCorpus = Double.parseDouble(mEtExitingCurpus.getText().toString().replaceAll(",", ""));

                        dblTymLeft = dblClgAge - dblChildAge;

                        for (int i = 1; i <= dlbDuration; i++) {
                            inflammationCost = dblColgCost * (Math.pow((1 + (dblExtInflammation / 100)), dblTymLeft));
                            dblTymLeft++;
                            //calculation for total cost
                            totalCost = totalCost + inflammationCost;
                        }

                        //Calculation for existing corpus gain after retirement
                        dblExistingGrowth = dblExistingCorpus * (Math.pow((1 + dblExpReturnRate / 100), dblTymLeft));

                        leftAmount = totalCost - dblExistingGrowth;

                        // Calculation for Money need to save aside.
                        double totperiod = dblClgAge - dblChildAge;
                        double nMonths = totperiod * 12;
                        double v = 0;
                        double r = dblExpReturnRate / 100;

                        double m = dblColgCost * dlbDuration;


                        double rt = dblExpReturnRate - dblExtInflammation;

                        double a = Math.pow((1 + rt / 100), dblTymLeft);

                        sav = m / a;

                        onetimeinv = leftAmount / (Math.pow((1 + dblExpReturnRate / 100), totperiod));

                        yearlyinv = (leftAmount * dblExpReturnRate / 100) / ((Math.pow((1 + dblExpReturnRate / 100), totperiod)) - 1);


                        for (int i = 0; i < nMonths; i++) {
                            v = v + Math.pow((1 + r), (nMonths - i) / 12);
                        }

                        monthlyinv = leftAmount / v;


                        String strAmount = "";
                        String[] resultAmount;
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                        long value1 = Math.round(dblExistingGrowth);
                        strAmount = format.format(value1);
                        resultAmount = strAmount.split("\\.", 0);
                        String existingGroth = resultAmount[0];

                        long value2 = Math.round(leftAmount);
                        strAmount = format.format(value2);
                        resultAmount = strAmount.split("\\.", 0);
                        String leftAmount = resultAmount[0];


                        long value3 = Math.round(dblExistingCorpus);
                        strAmount = format.format(value3);
                        resultAmount = strAmount.split("\\.", 0);
                        String curpus_vlaue = resultAmount[0];

                        long value4 = Math.round(totalCost);
                        strAmount = format.format(value4);
                        resultAmount = strAmount.split("\\.", 0);
                        String total_amount_at_study = resultAmount[0];
                        mTvText2.setText(total_amount_at_study);


                        if (onetimeinv > 0) {
                            mLinerResultPart.setVisibility(View.VISIBLE);
                            mTvResult4.setVisibility(View.GONE);
                            if (mBrokerActivity != null) {
                                mTvResult3.setTextColor(ContextCompat.getColor(mBrokerActivity, R.color.colorGrey_600));
                            } else {
                                mTvResult3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGrey_600));
                            }
                            mTvResult3.setText(getResources().getString(R.string.education_cal_some_txt));
                        } else {
                            mTvResult3.setText(getResources().getString(R.string.education_cal_congratulation));
                            mTvResult4.setText(getResources().getString(R.string.education_cal_goal_meet));
                            if (mBrokerActivity != null) {
                                mTvResult3.setTextColor(ContextCompat.getColor(mBrokerActivity, R.color.colorGreen));
                            } else {
                                mTvResult3.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreen));
                            }
                            mLinerResultPart.setVisibility(View.GONE);
                            mTvResult4.setVisibility(View.VISIBLE);
                        }

               /*     if (onetimeinv > 0) {
                        mLinerResultPart.setVisibility(View.VISIBLE);
                        mTvText2.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                        if (dblExistingCorpus > 0)
                            mTvTitle.setText(String.valueOf("Existing corpus " + curpus_vlaue + " will grow to " + existingGroth + " then You need to invest for amount " + leftAmount + " because Amount required of College "));
                        else
                            mTvTitle.setText(String.valueOf("You need to invest for amount " + leftAmount + " because Amount required of College "));

                    } else {
                        if (dblExistingCorpus > 0) {
                            mTvTitle.setText(String.valueOf("Existing corpus " + curpus_vlaue + " will grow to " + existingGroth + " then Amount required at Marriage " + leftAmount + " then no need to invest more amount "));
                        } else {
                            mTvTitle.setText(String.valueOf("Amount required at Marriage " + leftAmount + " then no need to invest more amount "));
                        }
                        mTvText2.setText("Congratulations!");
                        mTvText2.setTextColor(ContextCompat.getColor(mActivity, R.color.green));
                        mLinerResultPart.setVisibility(View.GONE);
                    }
*/

                        long value5 = Math.round(onetimeinv);
                        double totalAmount = Math.round(value5);
                        strAmount = format.format(totalAmount);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvResult1.setText(resultAmount[0]);

                        long value6 = Math.round(monthlyinv);
                        double shortFall = Math.round(value6);
                        strAmount = format.format(shortFall);
                        resultAmount = strAmount.split("\\.", 0);
                        mTvResult2.setText(resultAmount[0]);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            } catch (Exception e) {

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
                if (value < 1)
                    mEtPresentAge.setText("" + 0);
                else if (value > 25)
                    mEtPresentAge.setText("" + 25);
            } else {
                mEtPresentAge.setText("" + 2);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer2) {//    private EditText mEtPresentAge, mEtCollegeStart, mEtCollegeDurations, mEtCostPerYear, mEtExitingCurpus, mEtExpectedRate, mEtExpectedInflation;

            currentValue = mEtCollegeStart.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 15)
                    mEtCollegeStart.setText("" + 15);
                else if (value > 25)
                    mEtCollegeStart.setText("" + 25);

            } else {
                mEtCollegeStart.setText("" + 15);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer3) {
            currentValue = mEtCollegeDurations.getText().toString();
            if (currentValue.length() > 0) {
                int value = Integer.parseInt(currentValue);
                if (value < 1)
                    mEtCollegeDurations.setText("" + 0);
                else if (value > 10)
                    mEtCollegeStart.setText("" + 10);

            } else {
                mEtCollegeDurations.setText("" + 5);
            }
            desableScrollView(motionEvent);

        } else if (i == R.id.dialer4) {
            currentValue = mEtCostPerYear.getText().toString().replaceAll(",", "");
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 50000)
                    mEtCostPerYear.setText("" + 50000);
            } else {
                mEtCostPerYear.setText("" + 50000);
            }


            desableScrollView(motionEvent);

        } else if (i == R.id.dialer5) {
            currentValue = mEtExitingCurpus.getText().toString().replaceAll(",", "");
            if (currentValue.length() > 0) {
                long value = Long.parseLong(currentValue);
                if (value < 1)
                    mEtExitingCurpus.setText("" + 0);

            } else {
                mEtExitingCurpus.setText("" + 1000);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer6) {
            currentValue = mEtExpectedRate.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtExpectedRate.setText("" + 1.00);
                else if (value > 30.00)
                    mEtExpectedRate.setText("" + 30.00);

            } else {
                mEtExpectedRate.setText("" + 1.00);
            }

            desableScrollView(motionEvent);

        } else if (i == R.id.dialer7) {
            currentValue = mEtExpectedInflation.getText().toString();
            if (currentValue.length() > 0) {
                double value = Double.parseDouble(currentValue);
                if (value < 1)
                    mEtExpectedInflation.setText("" + 1.00);
                else if (value > 30.00)
                    mEtExpectedInflation.setText("" + 30.00);

            } else {
                mEtExpectedInflation.setText("" + 1.00);
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
            if (mBrokerActivity != null) {
                if (mBrokerActivity.getCurrentFocus() == mEtPresentAge) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtCollegeStart) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtCollegeDurations) {
                    delayCall();
                } else if (mBrokerActivity.getCurrentFocus() == mEtCostPerYear) {
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
                } else if (mActivity.getCurrentFocus() == mEtCollegeStart) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtCollegeDurations) {
                    delayCall();
                } else if (mActivity.getCurrentFocus() == mEtCostPerYear) {
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

