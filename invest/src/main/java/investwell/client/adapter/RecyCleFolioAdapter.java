package investwell.client.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;
import investwell.client.activity.MainActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shruti on 8/5/18.
 */

public class RecyCleFolioAdapter extends RecyclerView.Adapter<RecyCleFolioAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private RecyCleFolioAdapter.OnItemClickListener listener;
    private String mType = "";

    public RecyCleFolioAdapter(Context context, ArrayList<JSONObject> list, String type, RecyCleFolioAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_folio_adapter, viewGroup, false);
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

    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bucket_icon, ivArrow;
        TextView tvName, tvUCC, tvLumsumm, tvSIP, tvMandate, tvMyOrder;
        LinearLayout LinerProfileItems;

        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.bucket_icon);
            tvName = view.findViewById(R.id.tvName);
            tvUCC = view.findViewById(R.id.tvUCC);
            tvLumsumm = view.findViewById(R.id.tvLumsumm);
            tvSIP = view.findViewById(R.id.tvSIP);
            ivArrow = view.findViewById(R.id.ivArrow);
            tvMandate = view.findViewById(R.id.tvMandate);
            tvMyOrder = view.findViewById(R.id.tvMyOrder);
            LinerProfileItems = view.findViewById(R.id.LinerProfileItems);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            // String image_path = mDataList.optString("ImageURL");
            tvName.setText(jsonObject.optString("InvestorName"));
            tvUCC.setText(jsonObject.optString("UCC"));

            final MainActivity mainActivity = (MainActivity) mContext;
            tvMandate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ucc_code", jsonObject.optString("UCC"));
                    mainActivity.displayViewOther(37, bundle);
                }
            });

            tvMyOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ucc_code", jsonObject.optString("UCC"));
                    mainActivity.displayViewOther(19, bundle);
                }
            });

            if (mType.equals("show_only_profiles")){
                LinerProfileItems.setVisibility(View.VISIBLE);
                tvSIP.setVisibility(View.GONE);
                tvLumsumm.setVisibility(View.GONE);
            }else{
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position);
                    }
                });

                tvSIP.setVisibility(View.VISIBLE);
                tvLumsumm.setVisibility(View.VISIBLE);
                LinerProfileItems.setVisibility(View.GONE);

                if (!jsonObject.optString("SIPCart").equals("0")) {
                    tvSIP.setVisibility(View.VISIBLE);
                    if (jsonObject.optString("SIPCart").equals("1"))
                        tvSIP.setText(jsonObject.optString("SIPCart") + " Scheme are waiting for payment in SIP Cart");
                    else
                        tvSIP.setText(jsonObject.optString("SIPCart") + " Schemes are waiting for payment in SIP Cart");
                } else {
                    tvSIP.setVisibility(View.GONE);
                }

                if (!jsonObject.optString("LSCart").equals("0")) {
                    tvLumsumm.setVisibility(View.VISIBLE);
                    if (jsonObject.optString("LSCart").equals("1"))
                        tvLumsumm.setText(jsonObject.optString("LSCart") + " Scheme are waiting for payment in Lumpsum Cart");
                    else
                        tvLumsumm.setText(jsonObject.optString("LSCart") + " Schemes are waiting for payment in Lumpsum Cart");
                } else {
                    tvLumsumm.setVisibility(View.GONE);
                }
            }



        }

    }


}
