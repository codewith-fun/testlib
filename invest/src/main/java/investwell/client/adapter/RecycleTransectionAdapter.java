package investwell.client.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;
import investwell.utils.model.AddTrans;

/**
 * Created by shruti on 8/5/18.
 */

public class RecycleTransectionAdapter extends RecyclerView.Adapter<RecycleTransectionAdapter.MyViewHolder> {
    private Context mContext;
    private List<AddTrans> financialToolList;
    private OnItemClickListener itemClickListener;
    private AppSession mSession;
    private View itemView;
    private MainActivity mainActivity;

    public RecycleTransectionAdapter(Context context, List<AddTrans> financialToolList, OnItemClickListener financialToolListeners) {
        this.financialToolList = financialToolList;
        mContext = context;
        itemClickListener = financialToolListeners;
        mSession = AppSession.getInstance(mContext);
        mainActivity = (MainActivity) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_trensection_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final AddTrans financialTools = financialToolList.get(position);

        viewHolder.tvFinancialterms.setText(!TextUtils.isEmpty(financialTools.getFinancialTerms()) ? financialTools.getFinancialTerms() : "");
        viewHolder.tvFinancialterms.setCompoundDrawablesWithIntrinsicBounds(0, financialTools.getImgFinancialTools(), 0, 0);
        viewHolder.llFinancialTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (financialTools.getFinancialTerms().equalsIgnoreCase(mContext.getResources().getString(R.string.add_tranc_additional_purchase_txt))) {
                    mainActivity.displayViewOther(28, financialTools.getBundle());

                }
                if (financialTools.getFinancialTerms().equalsIgnoreCase(mContext.getResources().getString(R.string.add_tranc_sip_txt))) {
                    mainActivity.displayViewOther(29, financialTools.getBundle());
                }
                if (financialTools.getFinancialTerms().equalsIgnoreCase(mContext.getResources().getString(R.string.add_tranc_switch_txt))) {
                    mainActivity.displayViewOther(30, financialTools.getBundle());
                }
                if (financialTools.getFinancialTerms().equalsIgnoreCase(mContext.getResources().getString(R.string.add_tranc_swp_txt))) {

                    mainActivity.displayViewOther(31, financialTools.getBundle());

                }
                if (financialTools.getFinancialTerms().equalsIgnoreCase(mContext.getResources().getString(R.string.add_tranc_stp_stxt))) {
                    mainActivity.displayViewOther(32, financialTools.getBundle());

                }
                if (financialTools.getFinancialTerms().equalsIgnoreCase(mContext.getResources().getString(R.string.add_tranc_redeem_txt))) {
                    mainActivity.displayViewOther(33, financialTools.getBundle());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return financialToolList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFinancialterms;
        LinearLayout llFinancialTools;

        public MyViewHolder(View view) {
            super(view);
            tvFinancialterms = view.findViewById(R.id.tv_trans);
            llFinancialTools = view.findViewById(R.id.ll_add_trans);
        }


    }


}
