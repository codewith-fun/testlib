package investwell.client.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.Utils;

/**
 * Created by shruti on 8/5/18.
 */

public class RecycleGoalAdapter extends RecyclerView.Adapter<RecycleGoalAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private RecycleGoalAdapter.OnItemClickListener listener;
    private View view;
    private AppSession mSession;

    public RecycleGoalAdapter(Context context, ArrayList<JSONObject> list, RecycleGoalAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_recycle_goal, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_goal_adapter, viewGroup, false);
        }

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
        ImageView bucket_icon;
        TextView basketname;


        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.bucket_icon);
            basketname = view.findViewById(R.id.tv_scheme_name);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            try {
                JSONObject jsonObject = mDataList.get(position);
                String img_url = jsonObject.optString("ImageURL");
                basketname.setText(jsonObject.optString("BasketName"));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position);
                    }
                });

                Picasso.get().load(img_url).placeholder(R.mipmap.knowledge_area)
                        .error(R.mipmap.knowledge_area).into(bucket_icon);

            } catch (Exception e) {

            }


        }

    }


}
