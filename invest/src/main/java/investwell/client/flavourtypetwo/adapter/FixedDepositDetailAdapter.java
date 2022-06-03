package investwell.client.flavourtypetwo.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FixedDepositDetailAdapter extends RecyclerView.Adapter<FixedDepositDetailAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;


    public FixedDepositDetailAdapter(Context context, ArrayList<JSONObject> list) {
        mContext = context;
        mDataList = list;

    }

    @NonNull
    @Override
    public FixedDepositDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_fixed_deposit_detail, viewGroup, false);
        return new FixedDepositDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FixedDepositDetailAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.mtvSchemeName.setText(mDataList.get(position).optString("SchemeName"));
        viewHolder.mtvStartDate.setText(mDataList.get(position).optString("InvestmentDate"));
        viewHolder.mtvAmount.setText(mDataList.get(position).optString("FDAmount"));
        viewHolder.mtvPeriod.setText(mDataList.get(position).optString("Period")+"M");
        viewHolder.mtvRoR.setText("@"+mDataList.get(position).optString("RateOfReturn")+"%");

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mtvSchemeName, mtvStartDate, mtvAmount,mtvPeriod,mtvRoR;



        public ViewHolder(View view) {
            super(view);

            mtvSchemeName = view.findViewById(R.id.tv_user_portfolio_name);
            mtvStartDate = view.findViewById(R.id.tv_start_date_value);
            mtvAmount = view.findViewById(R.id.tv_amount_value);
            mtvPeriod = view.findViewById(R.id.tv_period_value);
            mtvRoR = view.findViewById(R.id.tv_years_of_compounding);


        }


    }


}
