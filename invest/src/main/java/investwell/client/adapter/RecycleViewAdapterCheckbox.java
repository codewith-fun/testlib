package investwell.client.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import investwell.client.activity.AppApplication;

public class RecycleViewAdapterCheckbox extends RecyclerView.Adapter<RecycleViewAdapterCheckbox.ViewHolder> {
    private static final String TAG = "RecycleViewAdapterCheck";

    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    AppApplication mConfig;
    public HashMap<Integer, Boolean> mHashMap, mHashValue;

    public RecycleViewAdapterCheckbox(ArrayList<JSONObject> mch_item, Context context) {
        mDataList = mch_item;
        mContext = context;
        mHashMap = new HashMap<>();
        mHashValue = new HashMap<>();
        mConfig = new AppApplication();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView htv_detail_name, htv_detail_group;
        RelativeLayout rl_checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.ch_item);
            htv_detail_name = itemView.findViewById(R.id.detail_code);
            htv_detail_group = itemView.findViewById(R.id.detail_group);
            rl_checkbox = itemView.findViewById(R.id.parent_chitem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_checkbox_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final JSONObject object = mDataList.get(position);

        if (object.has("group_name")) {

            holder.htv_detail_name.setText(object.optString("detail_code"));
            holder.checkBox.setText(object.optString("detail_name"));
            holder.htv_detail_group.setText(object.optString("group_name"));

            if (position == 0 && object.has("group_name")) {

                mConfig.aServiceName = object.optString("name");
//                serviceRequest.title.setText(object.optString("name"));

                System.out.println("====================="+position+ mConfig.aServiceName);
            }

            if (position > 0) {
                JSONObject preObject = mDataList.get(position - 1);

                if (object.optString("group_name").equalsIgnoreCase(preObject.optString("group_name"))) {
                    holder.htv_detail_group.setVisibility(View.GONE);
                }
            } else {
                holder.htv_detail_group.setVisibility(View.VISIBLE);
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        } else if (object.has("timeSlot")) {
            holder.htv_detail_group.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            holder.htv_detail_group.setText("Time");
            holder.htv_detail_name.setVisibility(View.GONE);
            holder.checkBox.setText(object.optString("timeSlot"));
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (object.has("group_name")) {
                    if (mHashValue.get(position)) {
                        mHashValue.put(position, false);
                    } else {
                        mHashValue.put(position, true);
                    }
                } else if (object.has("timeSlot")) {
                    if (mHashMap.get(position)) {
                        mHashMap.put(position, false);

                    } else {
                        mHashMap.put(position, true);
                    }
                }
            }
        });


        System.out.println("bbb Name:====================="+position + mConfig.aServiceName);

    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updatelist(ArrayList<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        for (int i = 0; i < mDataList.size(); i++) {
            mHashValue.put(i, false);
        }
        notifyDataSetChanged();
    }

    public void updateView(ArrayList<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        for (int i = 0; i < mDataList.size(); i++) {
            mHashMap.put(i, false);
        }
        notifyDataSetChanged();

    }
}
