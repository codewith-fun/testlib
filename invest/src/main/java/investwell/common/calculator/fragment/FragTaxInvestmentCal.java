package investwell.common.calculator.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import android.widget.TextView;

import com.iw.acceleratordemo.R;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;


public class FragTaxInvestmentCal extends Fragment implements View.OnClickListener , ToolbarFragment.ToolbarCallback {
    private SeekBar mSeekAmount, mSeekSlab;
    private TextView mLumpResult_tv, mSIPResult_tv, minSeekbar, maxSeekbar, mSeekbar_value, mTv_TaxSaving;
    private int mAmount;
    private double mSlab = 0.20;
    private Format mFormat;
    private EditText mMainAmount;
    private String[] resultAmount;
    private DecimalFormat format;
    private ImageView mIvImageRight, mIvRefresh, mIvBack;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private int mSeekAmtDiff = 1000;
    private ToolbarFragment fragToolBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MainActivity){
            this.mActivity=(MainActivity)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_taxinvestment, container, false);
        /*TextView toolbar_title_left = view.findViewById(R.id.toolbar_title_left);
        toolbar_title_left.setText("Tax Investment Calculator");
*/

        mApplication = (AppApplication)mActivity.getApplication();

        mFormat = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        format = new DecimalFormat("##,##,##,###");
setUpToolBar();
//        mLinerResult = view.findViewById(R.id.linerResult);
     /*   view.findViewById(R.id.ivRight2).setOnClickListener(this);*/
        view.findViewById(R.id.ivLeft).setOnClickListener(this);
        view.findViewById(R.id.lumpsum_btn).setOnClickListener(this);
        view.findViewById(R.id.sip_btn).setOnClickListener(this);
//        String strAmount = format.format(nFV);
        mLumpResult_tv = view.findViewById(R.id.resultlump);
        mSIPResult_tv = view.findViewById(R.id.resultsip);
        mSeekAmount = view.findViewById(R.id.sb_amount);
        mSeekbar_value = view.findViewById(R.id.seekbar_value);
        mSeekAmount.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thmb));
        mSeekSlab = view.findViewById(R.id.sb_slab);
        mMainAmount = view.findViewById(R.id.main_value);
        mTv_TaxSaving = view.findViewById(R.id.item_menu_main_tax_saving);
        minSeekbar = view.findViewById(R.id.seekbar_min);
        maxSeekbar = view.findViewById(R.id.seekbar_max);
        minSeekbar.setText(formatvalue(1000));
        maxSeekbar.setText(formatvalue(150000));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mSeekSlab.setMin(1);
//            mSeekSlab.setMax(3);
//            mSeekSlab.setProgress(2);
//            mSeekAmount.setMin(1000);
//            mSeekAmount.setMax(150000);
//            mSeekAmount.setProgress(30000);
//        } else {
        mSeekSlab.setMax(2);
        mSeekSlab.setProgress(1);
        mSeekAmount.setMax(150000 - mSeekAmtDiff);
        mSeekAmount.setProgress(30000 - mSeekAmtDiff);
//        }
//        mMainAmount.setText(String.valueOf(mSeekAmount.getProgress()));

        mMainAmount.setText(format.format(mSeekAmount.getProgress() + mSeekAmtDiff));
        mMainAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                int amt = Integer.parseInt(mMainAmount.getText().toString());
//                mMainAmount.setText(amt);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* try {


                    int i = Integer.parseInt(s.toString().replaceAll(",", ""));
                    if (i < 1000) {
                        Toast.makeText(getActivity(), getString(R.string.rs) + i + " is not Acceptable\nAmount shouldn't be lower than " + getString(R.string.rs) + "1,000", Toast.LENGTH_SHORT).show();
                    } else if (i > 150000) {
                        Toast.makeText(getActivity(), getString(R.string.rs) + i + " is not Acceptable\nAmount shouldn't be heiger than " + getString(R.string.rs) + "1,50,000", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {

                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
//                mTv_TaxSaving.setText(String.valueOf(mTv_TaxSaving.getText().toString().replaceAll(",", "")));
                int i = Integer.parseInt(s.toString().replaceAll(",", ""));
                if (s.toString().length() < 4) {

                    mMainAmount.setText("1000");

                }
                if (i < 1000) {

                    mApplication.showSnackBar(mSeekAmount,"Amount should be lower than " + getString(R.string.rs) + "1,000");
                } else if (i > 150000) {
                    mApplication.showSnackBar(mSeekAmount,"Amount should be heiger than " + getString(R.string.rs) + "1,50,000");
                }
                mSeekAmount.setProgress(i - mSeekAmtDiff);
            }
        });
        mSeekSlab.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double progress_value = 0.20;
                switch (progress) {
                    case 0:
                        progress_value = 0.05;
                        break;
                    case 1:
                        progress_value = 0.20;
                        break;
                    case 2:
                        progress_value = 0.30;
                        break;
                }
                calculate(progress_value, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = progress + mSeekAmtDiff;
                mSeekbar_value.setText(mFormat.format(i));
                calculate(0, i);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                mSIPResult_tv.setText(progresresult);
            }
        });
        mSlab = 0.20;
        mAmount = mSeekAmount.getProgress() + mSeekAmtDiff;
        calculate(0, 0);

        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_education_cal),true, true, false, false,true,false,false,"");
        }
        fragToolBar.setCallback(this);
    }

    private void calculate(double slab_value, int amount_value) {

        mMainAmount.setText(format.format(mSeekAmount.getProgress() + mSeekAmtDiff));
        mMainAmount.setSelection(mMainAmount.getText().length());
        if (!(slab_value == 0)) {
            mSlab = slab_value;
        } else if (!(amount_value == 0)) {
            mAmount = amount_value;
        }
        double i = mAmount * mSlab;
        double s_value = i * 0.04;
        int saving = ((int) Math.round(s_value + i));
        mTv_TaxSaving.setText(formatvalue(saving));
        mSIPResult_tv.setText(formatvalue(mAmount / 12));
        mLumpResult_tv.setText(formatvalue(mAmount));

        mTv_TaxSaving.setText(String.valueOf(mTv_TaxSaving.getText().toString().replaceAll(".##", "")));
    }

    private String formatvalue(int value) {
        long l_value = Math.round(value);
        String s = mFormat.format(l_value);
        String[] result = s.split("\\.", 0);
        return result[0];
    }

    private void refreshView() {


        mSeekSlab.setProgress(1);
        mSeekAmount.setProgress(30000 - mSeekAmtDiff);

        mMainAmount.setText(format.format(mSeekAmount.getProgress() + mSeekAmtDiff));
        mSlab = 0.20;
        mAmount = mSeekAmount.getProgress() + mSeekAmtDiff;
        calculate(0, 0);
    }


    @Override
    public void onClick(View view) {

        int duration = 1;
        Bundle bundle = new Bundle();
        /*if (view.getId() == R.id.ivRight) {
//            saveFrameLayout();
        }*//* else if (view.getId() == R.id.ivRight2) {
            refreshView();
        } *//*else */if (view.getId() == R.id.ivLeft) {
           mActivity.getSupportFragmentManager().popBackStack();
        } else if (view.getId() == R.id.lumpsum_btn) {
            bundle.putString("type", "coming_from_goal");
            bundle.putString("investment_type", "Lumpsum");
            bundle.putString("ic_invest_route_goal", "Tax Investment");
            bundle.putString("Amount", mLumpResult_tv.getText().toString());
            bundle.putString("ELSS", "Y");
            bundle.putInt("duration", duration);
            mActivity.displayViewOther(64, bundle);
            System.out.println(bundle.toString());

        } else if (view.getId() == R.id.sip_btn) {
            bundle.putString("type", "coming_from_goal");
            bundle.putString("investment_type", "SIP");
            bundle.putString("ic_invest_route_goal", "Tax Investment");
            bundle.putString("ELSS", "Y");
            bundle.putString("Amount", mSIPResult_tv.getText().toString());
            bundle.putInt("duration", duration);
            mActivity.displayViewOther(64, bundle);
        }

    }

    @Override
    public void onToolbarItemClick(View view) {
        if (view.getId() == R.id.iv_share) {
        }
    }
}
