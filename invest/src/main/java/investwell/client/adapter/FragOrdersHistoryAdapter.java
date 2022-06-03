package investwell.client.adapter;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.iw.acceleratordemo.R;
import investwell.utils.AppSession;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shruti on 8/5/18.
 */

public class FragOrdersHistoryAdapter extends RecyclerView.Adapter<FragOrdersHistoryAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;

    private Context mContext;
    private FragOrdersHistoryAdapter.OnItemClickListener listener;
    private AppSession mSession;

    private int mLastPosition = -1;
    public FragOrdersHistoryAdapter(Context context, ArrayList<JSONObject> list, FragOrdersHistoryAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_order_history_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

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
        TextView schemes, remark, order_number, date, tvAmount;

        public ViewHolder(View view) {
            super(view);
            schemes = view.findViewById(R.id.scheme);

            order_number = view.findViewById(R.id.order_number);
            date = view.findViewById(R.id.date);
            tvAmount = view.findViewById(R.id.tvAmount);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            schemes.setText(jsonObject.optString("Sname"));
            order_number.setText(jsonObject.optString("OrderNo"));
            tvAmount.setText(jsonObject.optString("Amount"));
          /*  if ( mDataList.optString("BSERemark").contains("ORD CONF")) {
                remark.setText("Order Processed Successfully");
            } else {
                remark.setText("Order Not Processed");
            }*/

            date.setText("Date. " + jsonObject.optString("TransactionDate"));

        }

    }


}
