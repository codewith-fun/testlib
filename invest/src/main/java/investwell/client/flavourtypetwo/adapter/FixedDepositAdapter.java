package investwell.client.flavourtypetwo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import investwell.client.flavourtypetwo.activity.FixedDepositDetailActivity;

public class FixedDepositAdapter extends RecyclerView.Adapter<FixedDepositAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;


    public FixedDepositAdapter(Context context, ArrayList<JSONObject> list) {
        mContext = context;
        mDataList = list;

    }

    @NonNull
    @Override
    public FixedDepositAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_fixed_deposit, viewGroup, false);
        return new FixedDepositAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FixedDepositAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        viewHolder.mtvUserName.setText(mDataList.get(position).optString("InvestorName"));
        viewHolder.mtvAmount.setText(mDataList.get(position).optString("FDAmount"));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("JsonData", mDataList.get(position).optJSONArray("InvestorFDDetail").toString());
                Intent intent = new Intent(mContext, FixedDepositDetailActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

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

        TextView mtvUserName, mtvAmount;


        public ViewHolder(View view) {
            super(view);

            mtvUserName = view.findViewById(R.id.tv_user_portfolio_name);
            mtvAmount = view.findViewById(R.id.tv_fixed_amount_value);


        }


    }


}

