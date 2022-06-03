package investwell.client.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import investwell.client.activity.AppApplication;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.ResizeWidthAnimation;
import investwell.utils.model.Amount;


public class InvestConfirmAdapter extends RecyclerView.Adapter<InvestConfirmAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private InvestConfirmAdapter.OnItemClickListener listener;
    private AppSession mSession;
    private int amountPercentage = 0;
    private String investOption = "";
    private AppApplication mApplictaion;
    private Button mButtonProceed;
    private boolean isLessAmountFound = false;

    public InvestConfirmAdapter(Context context, Button button, ArrayList<JSONObject> list, InvestConfirmAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        mButtonProceed = button;
        this.listener = listener;
        mApplictaion = (AppApplication) context.getApplicationContext();
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public InvestConfirmAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_new_invest_confirm, viewGroup, false);

        return new InvestConfirmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InvestConfirmAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position, listener);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list, int amount, String invest) {
        mDataList.clear();
        mDataList.addAll(list);
        amountPercentage = amount;
        investOption = invest;
        isLessAmountFound = false;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private String formatInRupee(String amount) {

        Format format = NumberFormat.getNumberInstance(new Locale("en", "in"));
        String str = format.format(Double.parseDouble(amount));
        return str;
    }

    private String calculatePercentage(int amountPercentage, int percent) {
        double amount = Double.parseDouble(String.valueOf(amountPercentage));
        double res = (amount / 100.0f) * percent;
        return String.valueOf(res);
    }

    private int calculateModulo(int amount) {
        int mod = amount % 100;
        int finalAmount = 0;
        if (mod < 50) {
            finalAmount = amount - mod;
        }
        if (mod >= 50) {
            finalAmount = amount + (100 - mod);
        }
        return finalAmount;
    }

    private int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPercentage, tvSchemeName, tvAmount, tvSchemeCat;
        CardView vProgress;
        private TextView tvErrorMsg, tvMinAmountMsg;


        public ViewHolder(View view) {
            super(view);
            tvPercentage = view.findViewById(R.id.tv_percentage);
            tvAmount = view.findViewById(R.id.tv_scheme_amount);
            tvSchemeName = view.findViewById(R.id.tv_scheme_name);
            tvSchemeCat = view.findViewById(R.id.tv_scheme_category);
            vProgress = view.findViewById(R.id.v_progress);
            tvMinAmountMsg = view.findViewById(R.id.tv_min_amount_msg);
            tvErrorMsg = view.findViewById(R.id.tv_error_msg);
        }


        public void setItem(final int position, final InvestConfirmAdapter.OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            boolean animate = true;

            try {
                tvSchemeName.setText(jsonObject.optString("SchName"));
                tvPercentage.setText(jsonObject.optString("AllocatePercentage") + " % allocated");
                int newWidth = 0;
                if (!TextUtils.isEmpty(jsonObject.optString("AllocatePercentage"))) {
                    double width = Double.parseDouble(jsonObject.optString("AllocatePercentage"));
                    newWidth = (int) Math.round(width * 1.5);
                }
                ViewGroup.LayoutParams layoutParams = vProgress.getLayoutParams();
                layoutParams.width = dpToPx(newWidth);
                vProgress.setLayoutParams(layoutParams);
                if (animate) {
                    ResizeWidthAnimation anim = new ResizeWidthAnimation(vProgress, dpToPx(newWidth));
                    anim.setDuration(1200);
                    vProgress.startAnimation(anim);
                } else {
                    newWidth = dpToPx(newWidth);
                    ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) vProgress.getLayoutParams();
                    lp.width = newWidth;
                    vProgress.setLayoutParams(lp);
                }
                tvSchemeCat.setText(jsonObject.optString("Objective"));
                String amountP = calculatePercentage(amountPercentage, Integer.parseInt(jsonObject.optString("AllocatePercentage")));
                double data = Double.parseDouble(amountP);
                int amountFinal = (int) Math.round(data);
                int amountForLastElement = calculateModulo(amountFinal);
                jsonObject.put("amount", amountFinal);
                Log.e("L2:", mDataList.toString());
                int sumOfSips = 0;
                int amount = 0;

                if (investOption.equalsIgnoreCase(AppConstants.INVEST_VIA_SIP)) {
                    if (position != mDataList.size() - 1) {

                        amount = calculateModulo(amountFinal);
                        tvAmount.setText(formatInRupee(String.valueOf(amount)));
                    }

                    if (position == mDataList.size() - 1) {
                        for (int i = 0; i < mDataList.size() - 1; i++) {
                            Log.e("Value Amount:", mDataList.get(i).optString("amount"));
                            sumOfSips += Integer.parseInt(mDataList.get(i).optString("amount"));

                        }
                        int lastElementAmount = amountPercentage - sumOfSips;
                        amount = calculateModulo(lastElementAmount);
                        jsonObject.put("amount", amount);
                        tvAmount.setText(formatInRupee(String.valueOf(amount)));
                    }
                    try {
                        tvMinAmountMsg.setText("SIP Minimum amount : " + formatInRupee(String.valueOf(Double.parseDouble(jsonObject.optString("SIPMinimumAmount").replaceAll(",", "")))));
                        tvMinAmountMsg.setVisibility(View.VISIBLE);

                        if (amount < Integer.parseInt(jsonObject.optString("SIPMinimumAmount").replaceAll(",", ""))) {
                            tvErrorMsg.setText("The amount is less then Min. SIP amount");
                            tvErrorMsg.setVisibility(View.VISIBLE);
                            isLessAmountFound = true;

                        } else {
                            tvErrorMsg.setText("");
                            tvErrorMsg.setVisibility(View.GONE);

                        }
                    } catch (Exception e) {

                    }finally {
                        if (isLessAmountFound){
                            mButtonProceed.setEnabled(false);
                            mButtonProceed.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrey_500));
                        }else{

                            mButtonProceed.setEnabled(true);
                            mButtonProceed.setBackgroundColor(ContextCompat.getColor(mContext, R.color.btnPrimaryBackgroundColor));
                        }
                    }
                }
                if (investOption.equalsIgnoreCase(AppConstants.INVEST_VIA_LUMPSUM)) {
                    amount = amountFinal;
                    tvAmount.setText(formatInRupee(String.valueOf(amountFinal)));
                    try {
                        tvMinAmountMsg.setText("Lumpsum Minimum amount : " + formatInRupee(String.valueOf(Double.parseDouble(jsonObject.optString("LumpsumMinimumAmount").replaceAll(",", "")))));
                        tvMinAmountMsg.setVisibility(View.VISIBLE);
                        if (amount < Integer.parseInt(jsonObject.optString("LumpsumMinimumAmount").replaceAll(",", ""))) {
                            tvErrorMsg.setText("The amount is less then Min. Lumpsum amount");
                            tvErrorMsg.setVisibility(View.VISIBLE);

                            isLessAmountFound = true;
                        } else {
                            tvErrorMsg.setText("");
                            tvErrorMsg.setVisibility(View.GONE);

                        }
                    } catch (Exception e) {
                    }finally {
                        if (isLessAmountFound){
                            mButtonProceed.setEnabled(false);
                            mButtonProceed.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrey_500));
                        }else{

                            mButtonProceed.setEnabled(true);
                            mButtonProceed.setBackgroundColor(ContextCompat.getColor(mContext, R.color.btnPrimaryBackgroundColor));
                        }
                    }
                }
                //tvErrorMsg.setText("The amount is less then Min. Lumpsum amount");
                //tvErrorMsg.setVisibility(View.VISIBLE);

                List<Amount> list = new ArrayList<>();
                for (int i = 0; i < mDataList.size(); i++) {
                    Amount amount1 = new Amount();
                    amount1.setAmount(String.valueOf(amount));
                    list.add(amount1);
                }
                mApplictaion.setAmountList(list);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


}
