package investwell.client.fragment.payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.PaymentNewActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppSession;


public class FragPrePayment extends Fragment implements View.OnClickListener {
    private ArrayList<JSONObject> mPendingOrderList;
    private ArrayList<JSONObject> mSuccessOrderList;
    private RecyclerView mPrePaymentRecycle;
    private Bundle bundle;
    private AppSession mSession;
    private String mUcc;
    private AdapterPrePayment mAdapter;
    private TextView mTvTotalView;
    private JSONObject jsonObject;
    private MainActivity mActivity;
    private View view;
    private String mType = "Pending";
    private AppApplication mApplication;
    private LinearLayout linerButtom;
    private TextView mTvNothing;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mApplication = (AppApplication) mActivity.getApplication();        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        view = inflater.inflate(R.layout.fragment_pre_payment, container, false);
        setInitializer();

        getDataFromBundle();
        setRecylerAdapter();
        filterPrePaymentData();
        setListener();
        return view;
    }

    private void setInitializer() {
        mActivity.setMainVisibility(this, null);
        bundle = getArguments();
        mSession = AppSession.getInstance(mActivity);
        mPrePaymentRecycle = (view).findViewById(R.id.prepayment_recycle);
        mTvTotalView = view.findViewById(R.id.total_value);
        linerButtom = view.findViewById(R.id.linerButtom);
        mTvNothing = view.findViewById(R.id.tvNothing);
    }

    private void setListener() {

        view.findViewById(R.id.place_order).setOnClickListener(this);


    }

    private void setRecylerAdapter() {
        mPrePaymentRecycle.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mAdapter = new AdapterPrePayment(mActivity, new ArrayList<JSONObject>(), mUcc, new AdapterPrePayment.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                jsonObject = mAdapter.mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("singleObject", jsonObject.toString());
            }

            @Override
            public void onRefreshClick(int position) {
                //getTransactOrderList();
            }
        });
        mPrePaymentRecycle.setAdapter(mAdapter);
    }

    private void getDataFromBundle() {
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUcc = bundle.getString("ucc_code");
        } else {
            mUcc = mSession.getUCC_CODE();
        }

        if (bundle != null && bundle.containsKey("type")) {
            mType = bundle.getString("type");
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.back_arrow) {
            mActivity.getSupportFragmentManager().popBackStack();
        } else if (id == R.id.place_order) {
            boolean isAmountZero = false;
            for (int i = 0; i < mAdapter.mDataList.size(); i++) {
                JSONObject jsonObject = mAdapter.mDataList.get(i);
                int amount = jsonObject.optInt("Amount");
                if (amount == 0) {
                    isAmountZero = true;
                    mApplication.showSnackBar(mTvTotalView, getResources().getString(R.string.pre_payment_error_empty_amount));
                    break;
                }
            }

            if (mAdapter.mDataList.size() == 0) {
                mApplication.showSnackBar(mTvTotalView, getResources().getString(R.string.pre_payment_error_empty_cart));
            } else if (isAmountZero) {
                mApplication.showSnackBar(mTvTotalView, getResources().getString(R.string.pre_payment_error_empty_amount));
            } else {
                placeOrder();

            }
        }


    }


    private void placeOrder() {
        Bundle bundle = getArguments();
        Intent intent = new Intent(mActivity, PaymentNewActivity.class);
        intent.putExtra("ucc_code", mUcc);
        intent.putExtra("type", bundle.getString("call_from"));
        startActivityForResult(intent, 500);

        // mActivity.displayViewOther(84,bundle);
    }

  private void filterPrePaymentData(){
      try {
          DialogsUtils.hideProgressBar();

          if (FragPrePaymentHome.resonsePrePaymentData.optString("Status").equalsIgnoreCase("True")) {
              JSONArray jsonArray = FragPrePaymentHome.resonsePrePaymentData.optJSONArray("TransactOrderList");
              if (mType.equalsIgnoreCase("pending")) {
                  mPendingOrderList = new ArrayList<>();
                  linerButtom.setVisibility(View.VISIBLE);

                  for (int i = 0; i < jsonArray.length(); i++) {
                      JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                      if (jsonObject1.optString("PaymentStatus").equalsIgnoreCase("Pending")){
                          mPendingOrderList.add(jsonObject1);
                      }
                  }

                  int totalPrice = 0;
                  for (int i = 0; i < mPendingOrderList.size(); i++) {
                      totalPrice += Integer.parseInt(mPendingOrderList.get(i).optString("Amount").replaceAll(",", ""));
                  }
                  Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));

                  String num = format.format(new BigDecimal(String.valueOf(totalPrice)));
                  mTvTotalView.setText(num.replace(".00", ""));

                  if (mPendingOrderList.size()>0){
                      mAdapter.updateList(mPendingOrderList, mType);
                      mTvNothing.setVisibility(View.GONE);
                  }else{
                      mTvNothing.setVisibility(View.VISIBLE);
                  }

                  for (int i = 0; i < mPendingOrderList.size(); i++) {
                      if (TextUtils.isEmpty(mPendingOrderList.get(i).optString("BSEOrderNo")) || mPendingOrderList.get(i).optString("BSEOrderNo") == null) {
                          view.findViewById(R.id.place_order).setEnabled(false);
                          view.findViewById(R.id.place_order).setClickable(false);
                      } else {
                          view.findViewById(R.id.place_order).setEnabled(true);
                          view.findViewById(R.id.place_order).setClickable(true);
                      }
                  }
              }else {
                  linerButtom.setVisibility(View.GONE);
                  mSuccessOrderList = new ArrayList<>();
                  for (int i = 0; i < jsonArray.length(); i++) {
                      JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                      if (!jsonObject1.optString("PaymentStatus").equalsIgnoreCase("Pending")){
                          mSuccessOrderList.add(jsonObject1);
                      }
                  }

                  if (mSuccessOrderList.size()>0){
                      mAdapter.updateList(mSuccessOrderList, mType);
                      mTvNothing.setVisibility(View.GONE);
                  }else{
                      mTvNothing.setVisibility(View.VISIBLE);
                  }

              }



          } else {
              Toast.makeText(mActivity, FragPrePaymentHome.resonsePrePaymentData.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
}
