package investwell.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Utils;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.MyViewHolder> {


    Context context;
    private ArrayList<JSONObject> jsonObject;
    private int mLastPosition = -1;
    private MainActivity mActivity;
    private AppSession mSession;

    public WatchListAdapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;
        mSession=AppSession.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.watch_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        if (position % 2 == 1) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.itemView.setBackgroundColor(Color.parseColor("#000000"));
            }else{
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }

        } else {

            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.itemView.setBackgroundColor(Color.parseColor("#212121"));
            }else{
                holder.itemView.setBackgroundColor(Color.parseColor("#FAFAFA"));

            }

        }
        mActivity = (MainActivity) context;
        mSession = AppSession.getInstance(context);
        final JSONObject jsonObject1 = jsonObject.get(position);

        Animation animation = AnimationUtils.loadAnimation(context, (position > mLastPosition) ? R.anim.item_animation_fall_down : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        mLastPosition = position;
        if (position == 1) {
            holder.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Rect rect = new Rect();
                    holder.itemView.getGlobalVisibleRect(rect);
                }
            });
        }
        holder.colorBlue.setText(jsonObject1.optString("SchName"));
        holder.nav.setText(" "+jsonObject1.optString("NAV"));
        holder.change.setText(jsonObject1.optString("Change")+" %");
        if (holder.change.getText().toString().contains("-")) {

            holder.change.setTextColor(Color.RED);
        } else {

            holder.change.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("passkey", AppSession.getInstance(context).getPassKey());
                bundle.putString("bid", AppConstants.APP_BID);
                bundle.putString("excl_code", jsonObject1.optString("Exlcode"));
                bundle.putString("scheme", jsonObject1.optString("SchName"));
                bundle.putString("object", jsonObject1.toString());
                mActivity.displayViewOther(42, bundle);

            }
        });
    }

    @Override
    public int getItemCount() {

        return jsonObject.size();
    }


    public void updateList(List<JSONObject> list) {

        jsonObject.clear();
        jsonObject.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView colorBlue, nav, change;


        public MyViewHolder(View view) {
            super(view);

            colorBlue = view.findViewById(R.id.colorBlue);
            nav = view.findViewById(R.id.nav);
            change = view.findViewById(R.id.tv_change);

        }
    }
}