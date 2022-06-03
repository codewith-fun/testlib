package investwell.client.adapter;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class RecycleSchemesAdapter extends RecyclerView.Adapter<RecycleSchemesAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private RecycleSchemesAdapter.OnItemClickListener listener;
    private int mLastPosition = -1;
    private View view;
    private AppSession mSession;
    public RecycleSchemesAdapter(Context context, ArrayList<JSONObject> list, RecycleSchemesAdapter.OnItemClickListener listener) {
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_recommended_schemes_1a, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_recom_schmes_adapter, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position, listener);
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > mLastPosition) ? R.anim.left_from_right : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        mLastPosition = position;
        if (position == 1) {
            viewHolder.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Rect rect = new Rect();
                    viewHolder.itemView.getGlobalVisibleRect(rect);
                }
            });
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bucket_icon;
        TextView basketname, description;


        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.bucket_icon);
            basketname = view.findViewById(R.id.tv_scheme_name);
            description = view.findViewById(R.id.description);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            JSONObject jsonObject = mDataList.get(position);
            String image_path = jsonObject.optString("ImageURL");
            basketname.setText(jsonObject.optString("BasketName"));
            description.setText(jsonObject.optString("BasketDescription"));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });

            try {

                Picasso.get().load(image_path).placeholder(R.mipmap.knowledge_area)
                        .error(R.mipmap.knowledge_area).into(bucket_icon);

            } catch (Exception e) {

            }
        }

    }


}
