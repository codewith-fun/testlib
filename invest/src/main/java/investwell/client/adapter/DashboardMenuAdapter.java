package investwell.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;

import com.iw.acceleratordemo.R;

import java.util.List;

import investwell.utils.customView.CustomTextViewBold;
import investwell.utils.model.FinancialTools;

public class DashboardMenuAdapter extends RecyclerView.Adapter<DashboardMenuAdapter.MyViewHolder> {
    private Context mContext;
    private List<FinancialTools> financialToolList;
    private DashboardMenuListener dashListener;

    public DashboardMenuAdapter(Context context, List<FinancialTools> financialToolList, DashboardMenuListener dashListeners) {
        this.financialToolList = financialToolList;
        mContext = context;
        dashListener = dashListeners;
    }

    @NonNull
    @Override
    public DashboardMenuAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_dashboard_menu, parent, false);

        return new DashboardMenuAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DashboardMenuAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        FinancialTools financialTools = financialToolList.get(position);
        holder.tvFinancialterms.setText(!TextUtils.isEmpty(financialTools.getFinancialTerms()) ? financialTools.getFinancialTerms() : "");
        holder.ivMenuIcons.setImageResource(financialTools.getImgFinancialTools());
        holder.llFinancialTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashListener.onToolsClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return financialToolList.size();
    }

    public interface DashboardMenuListener {
        void onToolsClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewBold tvFinancialterms;
        LinearLayout llFinancialTools;
        ImageView ivMenuIcons;
        public MyViewHolder(View view) {
            super(view);
            tvFinancialterms = view.findViewById(R.id.tv_dashboard_menu_title);
            llFinancialTools=view.findViewById(R.id.ll_menu);
            ivMenuIcons=view.findViewById(R.id.iv_menu_icons);
        }
    }
}
