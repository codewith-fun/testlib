package investwell.client.fragment.mysip;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by binesh on 8/5/18.
 */

public class AdapterSipStpSwp extends RecyclerView.Adapter<AdapterSipStpSwp.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private AdapterSipStpSwp.OnItemClickListener listener;

    public AdapterSipStpSwp(Context context, ArrayList<JSONObject> list, AdapterSipStpSwp.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_sip_stp_swp, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position, listener);


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvestorValue, tvAmountValue;

        public ViewHolder(View view) {
            super(view);
            tvAmountValue = view.findViewById(R.id.tv_amount_value);
            tvInvestorValue = view.findViewById(R.id.tv_investor_value);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            try {
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                format.setMinimumFractionDigits(0);
                format.setMaximumFractionDigits(0);

                final JSONObject jsonObject = mDataList.get(position);
                tvInvestorValue.setText(!TextUtils.isEmpty(jsonObject.optString("InvestorName")) ? jsonObject.optString("InvestorName") : "N/A");
                String totalAmount = jsonObject.optString("TotalAmount");
                boolean isFound = totalAmount.indexOf("-") != -1 ? true : false; //true
                if (isFound) {
                    totalAmount = totalAmount.substring(0, 1) + mContext.getString(R.string.Rs)+" " + totalAmount.substring(1, totalAmount.length());
                    tvAmountValue.setText(totalAmount);
                }else{
                    tvAmountValue.setText(mContext.getString(R.string.Rs)+" "+jsonObject.optString("TotalAmount"));
                    
                }

                //int totalAmount = jsonObject.optInt("TotalAmount");
                //String strTotalAmount = format.format(totalAmount);



            } catch (Exception e) {
                e.printStackTrace();
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });



        }

    }


    public void updateList(List<JSONObject> list, String type) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

}
