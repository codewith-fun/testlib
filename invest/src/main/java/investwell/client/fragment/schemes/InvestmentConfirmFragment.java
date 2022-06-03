package investwell.client.fragment.schemes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.adapter.InvestConfirmAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class InvestmentConfirmFragment extends Fragment implements View.OnClickListener {

    private AppSession mSession;
    private Bundle bundleForConfirm;
    private MainActivity mActivity;
    private BrokerActivity mBrokerActivity;
    private View view;
    private ToolbarFragment fragToolBar;
    private View viewContentTop, viewContentInvest;
    private TextView tvSchemeName, tvSchemeDesc, tv500, tv1000, tv2000, tv5000, tv10000,tvError;
    private ImageView ivScheme;
    private EditText etAmount;
    private RadioGroup rgInvestOption;
    private RadioButton rbSip, rbLumpSum;
    private List<JSONObject> mList;
    private RecyclerView rvSchemes;
    private InvestConfirmAdapter mAdapter;
    private JSONObject jsonObject;
    private int amount = 10000;
    private TextView tvAmountIncrease[] = new TextView[5];
    private int[] btn_id = {R.id.tv_500, R.id.tv_1000, R.id.tv_2000, R.id.tv_5000, R.id.tv_100000};
    private TextView tv_unFocus;
    private String current = "";
    public Button btnProceed;
    private final long DELAY = 1000; // milliseconds
    private Timer timer = new Timer();
    private String investOption = AppConstants.INVEST_VIA_SIP;
    private AppApplication mApplication;
    String oldValue = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mBrokerActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();

        } else if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_investment_confirm, container, false);
        setUpToolBar();
        initializer();
        getDataFromBundle();
        setAdapter();
        setUpUi();
        setListener();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_invest_confirm),
                    true, false, false, false, false, false, false, "");
        }

    }

    private void initializer() {
        bundleForConfirm = getArguments();
        viewContentInvest = view.findViewById(R.id.content_invest_confirm);
        tv500 = viewContentInvest.findViewById(R.id.tv_500);
        tv1000 = viewContentInvest.findViewById(R.id.tv_1000);
        tv2000 = viewContentInvest.findViewById(R.id.tv_2000);
        tv5000 = viewContentInvest.findViewById(R.id.tv_5000);
        tv10000 = viewContentInvest.findViewById(R.id.tv_100000);
        etAmount = viewContentInvest.findViewById(R.id.et_amount);
        rgInvestOption = viewContentInvest.findViewById(R.id.rg_choose_invest_opt);
        rbLumpSum = viewContentInvest.findViewById(R.id.rb_lumpsum);
        rbSip = viewContentInvest.findViewById(R.id.rb_sip);
        viewContentTop = view.findViewById(R.id.content_top_invest_confirm);
        tvSchemeDesc = viewContentTop.findViewById(R.id.tv_scheme_desc);
        tvSchemeName = viewContentTop.findViewById(R.id.tv_scheme);
        ivScheme = viewContentTop.findViewById(R.id.iv_scheme);
        rvSchemes = view.findViewById(R.id.rv_scheme_confirm);
        btnProceed = view.findViewById(R.id.btn_proceed);
        tvError=view.findViewById(R.id.tv_error);
    }

    private void setAdapter() {
        rvSchemes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new InvestConfirmAdapter(getActivity(), btnProceed, new ArrayList<JSONObject>(), new InvestConfirmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("passkey", mSession.getPassKey());
                bundle.putString("excl_code", jsonObject.optString("Exlcode"));
                bundle.putString(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
                bundle.putString("scheme", jsonObject.optString("SchName"));
                bundle.putString("object", jsonObject.toString());
                mActivity.displayViewOther(42, bundle);
            }
        });
        rvSchemes.setAdapter(mAdapter);
    }

    private void getDataFromBundle() {
        if (bundleForConfirm != null) {
            if (!TextUtils.isEmpty(bundleForConfirm.getString("imagePath"))) {
                Picasso.get().load(bundleForConfirm.getString("imagePath")).placeholder(R.mipmap.knowledge_area)
                        .error(R.mipmap.knowledge_area).into(ivScheme);
            } else {
                ivScheme.setImageResource(R.mipmap.knowledge_area);

            }
            if (!TextUtils.isEmpty(bundleForConfirm.getString("basketName"))) {
                tvSchemeName.setText(bundleForConfirm.getString("basketName"));
            } else {
                tvSchemeName.setText(R.string.no_data_found);

            }
            if (!TextUtils.isEmpty(bundleForConfirm.getString("basketDescription"))) {
                tvSchemeDesc.setText(bundleForConfirm.getString("basketDescription"));
            } else {
                tvSchemeDesc.setText(R.string.no_data_found);

            }
            if (!TextUtils.isEmpty(bundleForConfirm.getString("listOfSchemes"))) {
                try {
                    jsonObject = new JSONObject(bundleForConfirm.getString("listOfSchemes"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setUpUi() {
        updateAmount(amount);
        setUpAmountChosenData();
        rbSip.setChecked(true);
        rbLumpSum.setChecked(false);
        rbLumpSum.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGrey_500));
        rbSip.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        try {
            mList = new ArrayList<>();
            if (jsonObject != null) {
                JSONArray araArray = jsonObject.getJSONArray("SchemeDetail");
                for (int i = 0; i < araArray.length(); i++) {
                    JSONObject object = araArray.getJSONObject(i);
                    object.put("amount",0);
                    mList.add(object);
                }
                mAdapter.updateList(mList, amount, investOption);
                Log.e("L1:",mList.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String formatInRupee(int amount) {
        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        String strAmount = format.format(amount);
        String[] resultAmount = strAmount.split("\\.", 0);
        return resultAmount[0];
    }

    @SuppressLint("SetTextI18n")
    private void setUpAmountChosenData() {
        String addSymbol = " + ";
        tv500.setText(addSymbol + formatInRupee(500));
        tv1000.setText(addSymbol + formatInRupee(1000));
        tv2000.setText(addSymbol + formatInRupee(2000));
        tv5000.setText(addSymbol + formatInRupee(5000));
        tv10000.setText(addSymbol + formatInRupee(10000));
    }

    private void setFocus(TextView btn_unfocus, TextView btn_focus) {
        btn_unfocus.setTextColor(getResources().getColor(R.color.colorGrey_400));
        btn_unfocus.setBackground(getResources().getDrawable(R.drawable.bg_amount_unselected));
        btn_focus.setTextColor(getResources().getColor(R.color.colorBlack));
        btn_focus.setBackground(getResources().getDrawable(R.drawable.bg_amount_selected));
        this.tv_unFocus = btn_focus;
    }

    private void setListener() {
        for (int i = 0; i < tvAmountIncrease.length; i++) {
            tvAmountIncrease[i] = (TextView) view.findViewById(btn_id[i]);
            tvAmountIncrease[i].setBackground(getResources().getDrawable(R.drawable.bg_amount_unselected));
            tvAmountIncrease[i].setTextColor(getResources().getColor(R.color.colorGrey_400));
            tvAmountIncrease[i].setOnClickListener(this);
        }
        tv_unFocus = tvAmountIncrease[4];
        tvAmountIncrease[4].setBackground(getResources().getDrawable(R.drawable.bg_amount_selected));
        tvAmountIncrease[4].setTextColor(getResources().getColor(R.color.colorBlack));
        etAmount.addTextChangedListener(textWatcher);
        rgInvestOption.setOnCheckedChangeListener(onCheckedChangeListener);
        btnProceed.setOnClickListener(this);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int pos;
            if (getActivity() != null) {
                pos = rgInvestOption.indexOfChild(getActivity().findViewById(i));
                switch (pos) {
                    case 0:
                        investOption = AppConstants.INVEST_VIA_SIP;
                        rbLumpSum.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGrey_500));
                        rbSip.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                        mAdapter.updateList(mList, amount, investOption);
                        break;
                    case 1:
                        investOption = AppConstants.INVEST_VIA_LUMPSUM;
                        rbLumpSum.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                        rbSip.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGrey_500));
                        mAdapter.updateList(mList, amount, investOption);
                        break;
                }
            }
        }
    };


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
         /*   if (charSequence.toString().equals(current)) {
                amount = 0;
            } else {
                String str = charSequence.toString();
                str = str.replaceAll("[^\\d.]", "");
                if (!TextUtils.isEmpty(str)) {
                    amount = Integer.parseInt(str);
                }
                mAdapter.updateList(mList, amount, investOption);
            }*/

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String value = editable.toString();
            if (!oldValue.equals(value)) {
                oldValue = value;
                delayCall();
            }
        }
    };

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
        String rawValue = etAmount.getText().toString().replaceAll("[^\\d.]", "");
        if (!TextUtils.isEmpty(rawValue)) {
            amount = Integer.parseInt(rawValue);
        }
        if(getActivity()!=null) {
            if (investOption.equalsIgnoreCase(AppConstants.INVEST_VIA_SIP)) {
                if (amount < 2000) {
                    tvError.setText("(Min Investment :" + getString(R.string.rs) + " 2,000)");
                    btnProceed.setEnabled(false);
                    btnProceed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGrey_500));
                    tvError.setVisibility(View.VISIBLE);
                } else {
                    btnProceed.setEnabled(true);
                    btnProceed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.btnPrimaryBackgroundColor));
                    tvError.setVisibility(View.GONE);
                }
            }
            if (investOption.equalsIgnoreCase(AppConstants.INVEST_VIA_LUMPSUM)) {
                if (amount < 5000) {
                    tvError.setText("(Min Investment :" + getString(R.string.rs) + " 5,000)");
                    btnProceed.setEnabled(false);
                    btnProceed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGrey_500));
                    tvError.setVisibility(View.VISIBLE);
                } else {
                    btnProceed.setEnabled(true);
                    btnProceed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.btnPrimaryBackgroundColor));
                    tvError.setVisibility(View.GONE);
                }
            }
        }
        if (mBrokerActivity != null) {
            mBrokerActivity.convertIntoIndianCurrencyFormat(amount, etAmount);
        } else {
            mActivity.convertIntoIndianCurrencyFormat(amount, etAmount);
        }

        mAdapter.updateList(mList, amount, investOption);
    }

    private boolean isValidate() {
        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            mApplication.showSnackBar(etAmount, "Please enter amount");
            return false;
        } else {
            return true;
        }
    }

    private void updateAmount(int amount) {
        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        String strAmount = format.format(amount);
        String[] resultAmount = strAmount.split("\\.", 0);
        etAmount.setText(resultAmount[0]);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_500) {
            amount = amount + 500;
            setFocus(tv_unFocus, tvAmountIncrease[0]);
            updateAmount(amount);
            mAdapter.updateList(mList, amount, investOption);
        } else if (id == R.id.tv_1000) {
            amount = amount + 1000;
            setFocus(tv_unFocus, tvAmountIncrease[1]);
            updateAmount(amount);
            mAdapter.updateList(mList, amount, investOption);
        } else if (id == R.id.tv_2000) {
            amount = amount + 2000;
            setFocus(tv_unFocus, tvAmountIncrease[2]);
            updateAmount(amount);
            mAdapter.updateList(mList, amount, investOption);
        } else if (id == R.id.tv_5000) {
            amount = amount + 5000;
            setFocus(tv_unFocus, tvAmountIncrease[3]);
            updateAmount(amount);
            mAdapter.updateList(mList, amount, investOption);
        } else if (id == R.id.tv_100000) {
            amount = amount + 10000;
            setFocus(tv_unFocus, tvAmountIncrease[4]);
            updateAmount(amount);
            mAdapter.updateList(mList, amount, investOption);
        } else if (id == R.id.btn_proceed) {
            if (isValidate()) {
                Config.BASKET_SCHEMES_LIST.clear();
                Config.BASKET_SCHEMES_LIST.addAll(mList);
                if (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "coming_from_dashborad");
                    mActivity.displayViewOther(36, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "coming_from_dashborad");
                    bundle.putString("investType", investOption);
                    mActivity.displayViewOther(36, bundle);
                }
            }
        }
    }
}