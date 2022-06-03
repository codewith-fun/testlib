package investwell.client.adapter;

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


public class FragLumsumOrderStatusAdapter extends RecyclerView.Adapter<FragLumsumOrderStatusAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private FragLumsumOrderStatusAdapter.OnItemClickListener listener;

    public FragLumsumOrderStatusAdapter(Context context, ArrayList<JSONObject> list, FragLumsumOrderStatusAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_order_status_adapter, viewGroup, false);
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
        TextView schemes_name, tvStatus, tvDescription, tvMessage;


        public ViewHolder(View view) {
            super(view);
            schemes_name = view.findViewById(R.id.tv_scheme_name);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvDescription = view.findViewById(R.id.description);
            tvMessage = view.findViewById(R.id.tvMessage);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            JSONObject jsonObject = mDataList.get(position);
            schemes_name.setText(jsonObject.optString("SchemeName"));
            if (jsonObject.optString("Remark").contains("FAILED")){
                tvStatus.setText(mContext.getResources().getString(R.string.lumpsum_order_status_no_order_placed));
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                tvMessage.setVisibility(View.GONE);
            }else{
                tvStatus.setText(mContext.getResources().getString(R.string.lumpsum_order_status_desc));
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                tvMessage.setVisibility(View.GONE);
            }

            tvDescription.setText(jsonObject.optString("Remark"));

         /*   itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);

                }
            });*/
        }

    }


}
