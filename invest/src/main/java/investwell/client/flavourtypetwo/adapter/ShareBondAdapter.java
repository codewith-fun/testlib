package investwell.client.flavourtypetwo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.flavourtypetwo.activity.ShareBondDetailActivity;

public class ShareBondAdapter extends RecyclerView.Adapter<ShareBondAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;


    public ShareBondAdapter(Context context, ArrayList<JSONObject> list) {
        mContext = context;
        mDataList = list;

    }

    @Override
    public ShareBondAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_share_bond, viewGroup, false);
        return new ShareBondAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShareBondAdapter.ViewHolder viewHolder, final int position) {

        viewHolder.setItem(position);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = mDataList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("JsonData", jsonObject.toString());
                Intent intent = new Intent(mContext, ShareBondDetailActivity.class);
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

        TextView mtvUserName, mtvInitialValue, mtvMarketValue, mtvCagr, mtvGain, mtvAbsoluteReturn, mtvWeightedDays;
        ImageView mivNext, gain_arrow, cagr_arrow, return_arrow;
        CardView cvShareBond;

        public ViewHolder(View view) {
            super(view);
            cvShareBond = view.findViewById(R.id.cv_container_share_bond);
            mtvUserName = view.findViewById(R.id.tv_user_portfolio_name);
            mtvInitialValue = view.findViewById(R.id.tv_initial_value);
            mtvMarketValue = view.findViewById(R.id.tv_market_value);
            mtvCagr = view.findViewById(R.id.tv_cagr_value);
            mtvGain = view.findViewById(R.id.tv_gain_value);
            mtvAbsoluteReturn = view.findViewById(R.id.tv_abs_return_value);
            mivNext = view.findViewById(R.id.ivNext);
            return_arrow = view.findViewById(R.id.iv_arrow_proceed);

        }

        public void setItem(final int position) {


            try {
                //  JSONArray jsonArray = new JSONArray(mDataList.get(0).optString("EquityApplicantDetail"));


                JSONObject jsonObject = mDataList.get(position);
                mtvUserName.setText(jsonObject.optString("ApplicantName"));
                mtvInitialValue.setText(jsonObject.optString("InitialValue"));
                mtvMarketValue.setText(jsonObject.optString("CurrentValue"));
                mtvCagr.setText(jsonObject.optString("CAGR"));
                mtvGain.setText(jsonObject.optString("Gain"));
                mtvAbsoluteReturn.setText(jsonObject.optString("AbsoluteReturn"));
                mtvWeightedDays.setText(jsonObject.optString("WeightedDays"));


                if (jsonObject.optString("Gain").contains("-")) {
                    //gain.setTextColor(Color.parseColor("#d01f1f"));
                    /* gain_arrow.setBackgroundResource(R.drawable.menu_down);*/
                    mtvGain.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                } else {

                    /* gain_arrow.setBackgroundResource(R.drawable.menu_up);*/
                    mtvGain.setTextColor(mContext.getResources().getColor(R.color.colorGreen));

                }
                if (jsonObject.optString("CAGR").contains("-")) {

                    /* cagr_arrow.setBackgroundResource(R.drawable.menu_down);*/
                    mtvCagr.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                } else {
                    /*cagr_arrow.setBackgroundResource(R.drawable.menu_up);*/
                    mtvCagr.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                }

                if (jsonObject.optString("AbsoluteReturn").contains("-")) {

                    /*return_arrow.setBackgroundResource(R.drawable.menu_down);*/
                    mtvAbsoluteReturn.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                } else {
                    /*                    return_arrow.setBackgroundResource(R.drawable.menu_up);*/
                    mtvAbsoluteReturn.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                }




            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}



