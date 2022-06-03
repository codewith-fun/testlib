package investwell.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.model.FinancialTools;

import java.util.List;

public class FinancialToolAdapter extends RecyclerView.Adapter<FinancialToolAdapter.MyViewHolder> {
    private Context mContext;
    private List<FinancialTools> financialToolList;
    private FinancialToolListener financialToolListener;
    private AppSession mSession;
private  View itemView;
    public FinancialToolAdapter(Context context, List<FinancialTools> financialToolList, FinancialToolListener financialToolListeners) {
        this.financialToolList = financialToolList;
        mContext = context;
        financialToolListener = financialToolListeners;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public FinancialToolAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row_financial_tools_type_a, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row_financial_tools, parent, false);
        }
        return new FinancialToolAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FinancialToolAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        FinancialTools financialTools = financialToolList.get(position);
        holder.tvFinancialterms.setText(!TextUtils.isEmpty(financialTools.getFinancialTerms()) ? financialTools.getFinancialTerms() : "");
        holder.tvFinancialterms.setCompoundDrawablesWithIntrinsicBounds(0, financialTools.getImgFinancialTools(), 0, 0);
        holder.llFinancialTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                financialToolListener.onToolsClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return financialToolList.size();
    }

    public interface FinancialToolListener {
        void onToolsClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFinancialterms;
        LinearLayout llFinancialTools;

        public MyViewHolder(View view) {
            super(view);
            tvFinancialterms = view.findViewById(R.id.tv_dashboard_finance_term);
            llFinancialTools = view.findViewById(R.id.ll_tools);
        }
    }
}
