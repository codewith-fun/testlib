package investwell.common.calculator.fragment;

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
import android.text.Editable;
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


import com.iw.acceleratordemo.R;

import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.common.calculator.utils.AudioPlayer;
import investwell.common.calculator.utils.RoundKnobButton;
import investwell.common.calculator.utils.Singleton;
import investwell.common.calculator.utils.Utils;


public class FragSip extends Fragment implements View.OnClickListener, View.OnTouchListener, RoundKnobButton.RoundKnobButtonListener {
    private final long DELAY = 1000; // milliseconds
    double w_delay = 0;
    double w_tot = 0;
    double amt_inv = 0;
    double nYears = 0, amtD = 0, rorD = 0;
    Singleton m_Inst = Singleton.getInstance();
    private EditText mEtYear, mEtAmount, mEtRate;
    private TextView mTvSipTitle, mTvFutureAmount, mTvTotalInvest, mTvInvestedTimes, mTvWealth, mTvImageTitle, mResulttxt;
    //    private LinearLayout mLinerResult;
    private ImageView mIvImageRight, mIvRefresh, mIvBack;
    private int mAmountCount = 0, mAnnualAmountCount = 0, mYearCount = 0, mReturn = 01;
    private ProgressBar mProgressBar;
    private AudioPlayer audioPlayer;
    private ScrollView mScrollView;
    private RoundKnobButton mDailerInvest1, mDailerInvest2, mDailerInvest3;
    private LinearLayout mLinerFooter;
    private Timer timer = new Timer();
    private MainActivity mActivity;
    private AppApplication mApplication;
    private RelativeLayout mRelToolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_sip, container, false);

        m_Inst.InitGUIFrame(getActivity());
        audioPlayer = new AudioPlayer();
        mActivity = (MainActivity) getActivity();
        mApplication = (AppApplication) getActivity().getApplication();
        mRelToolbar = view.findViewById(R.id.relToolBar);

        mScrollView = view.findViewById(R.id.scrollView);
        mLinerFooter = view.findViewById(R.id.linerFooter);
        mEtAmount = view.findViewById(R.id.edit1);
        mEtYear = view.findViewById(R.id.edit2);
        mEtRate = view.findViewById(R.id.edit3);
        mTvSipTitle = view.findViewById(R.id.textView1);
        mTvFutureAmount = view.findViewById(R.id.textView2);
        mResulttxt = view.findViewById(R.id.textView3);

        mTvTotalInvest = view.findViewById(R.id.tvResult);
        mTvWealth = view.findViewById(R.id.tvResult2);
//        mLinerResult = view.findViewById(R.id.linerResult);

        mIvBack = view.findViewById(R.id.ivLeft);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mTvImageTitle = view.findViewById(R.id.tvImageTitle);
        mTvInvestedTimes = view.findViewById(R.id.textView6);

       /* TextViewtoolbar_title_left = view.findViewById(R.id.toolbar_title_left);
        toolbar_title_left.setText("SIP Calculator");*/





        mDailerInvest1 = view.findViewById(R.id.dialer1);
        mDailerInvest2 = view.findViewById(R.id.dialer2);
        mDailerInvest3 = view.findViewById(R.id.dialer3);
        mDailerInvest1.RoundKnobButton2(getActivity(), 0, m_Inst.Scale(110), m_Inst.Scale(110));
        mDailerInvest2.RoundKnobButton2(getActivity(), 0, m_Inst.Scale(110), m_Inst.Scale(110));
        mDailerInvest3.RoundKnobButton2(getActivity(), 0, m_Inst.Scale(110), m_Inst.Scale(110));

        mResulttxt.setText("To achieve this Goal you must start Investment of \u20B9 " + mEtAmount.getText().toString() + " Monthly SIP Today.");

        mDailerInvest1.setOnTouchListener(this);
        mDailerInvest2.setOnTouchListener(this);
        mDailerInvest3.setOnTouchListener(this);


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

                            audioPlayer.play(getActivity());
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

                            audioPlayer.play(getActivity());
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
                            else if (updateValue > 30.00)
                                mEtRate.setText("" + 30.00);
                            else
                                mEtRate.setText("" + updateValue);

                            audioPlayer.play(getActivity());
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


    @Override
    public void onStateChange(boolean newstate) {

    }

    @Override
    public void onRotate(int percentage) {

    }

    @Override
    public void onClick(View view) {
       /* if (view.getId() == R.id.ivRight) {
            saveFrameLayout();
        }*/ /*else if (view.getId() == R.id.ivRight2) {
            refreshView();
        }*/ /*else*/ if (view.getId() == R.id.ivLeft) {
            getActivity().getSupportFragmentManager().popBackStack();
        }

    }

    private void refreshView() {
        mDailerInvest1.setRotorPercentage(0);
        mDailerInvest2.setRotorPercentage(0);
        mDailerInvest3.setRotorPercentage(0);
        mEtAmount.setText("1000");
        mEtYear.setText("10");
        mEtRate.setText("10.00");
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
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getActivity().getCurrentFocus();

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


        if (mEtAmount.getText().toString().replaceAll(",", "").replaceAll(",", "").equals("") || mEtAmount.getText().toString().replaceAll(",", "").equals("0")) {
            mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.lumsum_cal_error_empty_sip_amnt));
        } else if ((Long.parseLong(mEtAmount.getText().toString().replaceAll(",", ""))) < 500) {
            mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.sip_delay_error_minn_amnt));
        } else if (mEtYear.getText().toString().equals("") || mEtYear.getText().toString().equals("0")) {
            mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.lumspum_cal_error_duration));
        } else if (Integer.parseInt(mEtYear.getText().toString()) > 50) {
            mEtYear.setText("50");
            mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.sip_error_max_dur));
        } else if (mEtRate.getText().toString().equals("") || mEtRate.getText().toString().equals("0")) {
            mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.lumpsum_cal_error_ror));
        } else if (Double.parseDouble(mEtRate.getText().toString()) > 30) {
            mEtRate.setText("30.00");
            mActivity.showCommonDialog(getActivity(), "Not Valid", getResources().getString(R.string.error_max_rorxx));
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
                double inrateD = Double.parseDouble("0");


                double r = rorD / 100;
                double nMonths = (nYears * 12);
                double v = 0;


                for (int i = 0; i < nMonths; i++) {
                    amt_inv = amt_inv + amtD;
                    v = v + Math.pow((1 + r), (nMonths - i) / 12);
                }

                w_tot = amtD * v;

//                mLinerResult.setVisibility(View.VISIBLE);
                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));


                String strAmount = format.format(amtD);
                String[] resultAmount = strAmount.split("\\.", 0);

                mTvSipTitle.setText("Monthly SIP of " + resultAmount[0] + " for " + ((int) nYears) + " years at " + (rorD) + "% annual return will grow to ");

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


//                int percentage = (int) ((amt_inv * 100) / w_tot);
//                mProgressBar.setProgress(percentage);
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


    public void saveFrameLayout() {
        mRelToolbar.setVisibility(View.GONE);
        mTvImageTitle.setVisibility(View.VISIBLE);
        mLinerFooter.setVisibility(View.VISIBLE);
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
        int totalHeight = mScrollView.getChildAt(0).getHeight();
        int totalWidth = mScrollView.getChildAt(0).getWidth();
        Bitmap bitmap = getBitmapFromView(mScrollView, totalHeight, totalWidth);

        try {
            Uri imageUri = Utils.saveImage(bitmap, getActivity());

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/*");
            waIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            mTvImageTitle.setVisibility(View.GONE);
            mLinerFooter.setVisibility(View.GONE);
            mRelToolbar.setVisibility(View.VISIBLE);
            mScrollView.destroyDrawingCache();
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

    private class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void afterTextChanged(Editable editable) {


            if (getActivity().getCurrentFocus() == mEtAmount) {
                delayCall();
            } else if (getActivity().getCurrentFocus() == mEtYear) {
                delayCall();
            } else if (getActivity().getCurrentFocus() == mEtRate) {
                delayCall();
            }

            mResulttxt.setText("To achieve this Goal you must start Investment of \u20B9 " + mEtAmount.getText().toString() + " Monthly SIP Today.");

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

    }


}
