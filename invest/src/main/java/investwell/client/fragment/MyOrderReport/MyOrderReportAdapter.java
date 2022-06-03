package investwell.client.fragment.MyOrderReport;

import android.content.Context;
import android.graphics.Typeface;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyOrderReportAdapter extends RecyclerView.Adapter<MyOrderReportAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;

    public MyOrderReportAdapter(Context context, ArrayList<JSONObject> list) {
        mContext = context;
        mDataList = list;
    }

    @Override
    public MyOrderReportAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_report, viewGroup, false);
        return new MyOrderReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyOrderReportAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position);


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvestorName, tvSchemeName, tvFolioNo, tvDate, tvTransacType,
                tvTransacStatus, tvTransacAmount, tvOrderNo, tvMemberRequest, tvRejectedReason;

        public ViewHolder(View view) {
            super(view);
            tvInvestorName = view.findViewById(R.id.tv_investor_name);
            tvSchemeName = view.findViewById(R.id.tv_scheme_name);
            tvFolioNo = view.findViewById(R.id.tv_folio_no);
            tvDate = view.findViewById(R.id.tv_date);
            tvTransacType = view.findViewById(R.id.tv_transac_type);
            tvTransacStatus = view.findViewById(R.id.tv_transac_status);
            tvTransacAmount = view.findViewById(R.id.tv_transac_amount);
            tvOrderNo = view.findViewById(R.id.tv_order_no);
            tvMemberRequest = view.findViewById(R.id.tv_member_remark);
            tvRejectedReason = view.findViewById(R.id.tv_rejected_reason);

        }

        public void setItem(final int position) {
            try {
                final JSONObject jsonObject1 = mDataList.get(position);

                tvInvestorName.setText(jsonObject1.optString("ClientName"));
                tvSchemeName.setText(jsonObject1.optString("SchemeName"));
                tvFolioNo.setText("Folio No. " + jsonObject1.optString("FolioNo"));
                tvDate.setText(jsonObject1.optString("TransactionDate"));
                if (jsonObject1.optString("SubTranType").isEmpty()) {
                    tvTransacType.setText(jsonObject1.optString("TranType"));
                } else {
                    tvTransacType.setText(jsonObject1.optString("TranType") + " | " + jsonObject1.optString("SubTranType"));
                }
                tvTransacStatus.setText(jsonObject1.optString("TranStatus"));
                tvTransacStatus.setTypeface(null, Typeface.ITALIC);
                String input = jsonObject1.optString("Amount");
                boolean isFound = input.indexOf("-") != -1 ? true : false; //true
                if (isFound) {
                    input = input.substring(0, 1) + mContext.getString(R.string.Rs) + " " + input.substring(1, input.length());
                    tvTransacAmount.setText(input);
                } else {
                    tvTransacAmount.setText(mContext.getString(R.string.Rs) + " " + jsonObject1.optString("Amount"));
                }

                if (jsonObject1.optString("Amount").contains("-")) {
                    //viewHolder.tvTransacAmount.getText().toString().replace("-","");
                    tvTransacAmount.setTextColor(mContext.getResources().getColor(R.color.colorNegativeValues));

                } else {
                    tvTransacAmount.setTextColor(mContext.getResources().getColor(R.color.colorPositiveValues));

                }
                tvOrderNo.setText("Order No. " + jsonObject1.optString("OrderNo"));
                tvMemberRequest.setText(jsonObject1.optString("MemberRemark"));
                tvRejectedReason.setText(jsonObject1.optString("RejectedReason"));


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

}