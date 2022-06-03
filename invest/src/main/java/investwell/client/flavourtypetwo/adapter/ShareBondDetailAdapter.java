package investwell.client.flavourtypetwo.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShareBondDetailAdapter extends RecyclerView.Adapter<ShareBondDetailAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;


    public ShareBondDetailAdapter(Context context, ArrayList<JSONObject> list) {
        mContext = context;
        mDataList = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_share_bond_detail, viewGroup, false);
        return new ShareBondDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShareBondDetailAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.mtvSchemeName.setText(mDataList.get(position).optString("SchemeName"));
        viewHolder.mtvMarketValue.setText(mDataList.get(position).optString("CurrentValue"));
        viewHolder.mtvPurchaseCost.setText(mDataList.get(position).optString("PurchaseValue"));
        viewHolder.mtvBalanceUnit.setText(mDataList.get(position).optString("BalanceUnit"));
        viewHolder.mtvGain.setText(mDataList.get(position).optString("Gain"));
        viewHolder.mtvAbsRtrn.setText(mDataList.get(position).optString("AbsoluteReturn"));

        if (mDataList.get(position).optString("Gain").contains("-")) {

            viewHolder.mtvGain.setTextColor(mContext.getResources().getColor(R.color.colorRed));
        } else {


            viewHolder.mtvGain.setTextColor(mContext.getResources().getColor(R.color.colorGreen));

        }


        if (mDataList.get(position).optString("AbsoluteReturn").contains("-")) {

            /*    viewHolder.return_arrow.setBackgroundResource(R.drawable.menu_down);*/
            viewHolder.mtvAbsRtrn.setTextColor(mContext.getResources().getColor(R.color.colorRed));
        } else {
            /*          viewHolder.return_arrow.setBackgroundResource(R.drawable.menu_up);*/
            viewHolder.mtvAbsRtrn.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
        }

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
        TextView mtvSchemeName, mtvMarketValue, mtvPurchaseCost, mtvBalanceUnit, mtvGain, mtvAbsRtrn;
        /*        ImageView gain_arrow, return_arrow;*/

        public ViewHolder(View view) {
            super(view);
            mtvSchemeName = view.findViewById(R.id.tv_user_portfolio_name);
            mtvMarketValue = view.findViewById(R.id.tv_market_value);
            mtvPurchaseCost = view.findViewById(R.id.tv_purchase_cost);
            mtvBalanceUnit = view.findViewById(R.id.tv_balance_unit_value);
            mtvGain = view.findViewById(R.id.tv_gain_value);
            mtvAbsRtrn = view.findViewById(R.id.tv_abs_return_value);


        }


    }


}
