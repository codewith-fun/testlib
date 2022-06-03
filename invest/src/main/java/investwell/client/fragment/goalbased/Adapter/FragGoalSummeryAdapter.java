package investwell.client.fragment.goalbased.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;
import investwell.utils.AppSession;
import investwell.utils.CustomProgressBar;
import investwell.utils.ProgressItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by binesh on 8/5/18.
 */

public class FragGoalSummeryAdapter extends RecyclerView.Adapter<FragGoalSummeryAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;

    private Context mContext;
    private OnItemClickListener listener;
    private AppSession mSession;
    private long mShortfalValue = 0;

    public FragGoalSummeryAdapter(Context context, ArrayList<JSONObject> list, OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_goals_adapter, viewGroup, false);
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
        TextView tvGoalName, tvTargetAmount, tvComplete, tvProjected, tvShortFall,
                tvGoalTarget, tvCurrentValue, tvShortfal, tvShortTitle,mTvTargetTilte;
        CustomProgressBar mProgressBar;
        LinearLayout mLinerShortFall, mLy_Progress, mLy_Projected, mLy_Completed;
        ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            tvGoalName = view.findViewById(R.id.tvGoalName);
            tvGoalTarget = view.findViewById(R.id.tvGoalTarget);
            ivImage = view.findViewById(R.id.ivImage);
            mTvTargetTilte = view.findViewById(R.id.tvTargetTitle);
            tvTargetAmount = view.findViewById(R.id.tv_targetamount);
            tvCurrentValue = view.findViewById(R.id.tv_currentamount);
            tvShortfal = view.findViewById(R.id.tv_shortfalamount);
            mProgressBar = view.findViewById(R.id.seekBar0);
            mLy_Progress = view.findViewById(R.id.ly_progress);
            tvShortTitle = view.findViewById(R.id.tv_shorttitle);
            tvComplete = view.findViewById(R.id.tvComplete);
            tvProjected = view.findViewById(R.id.tvProjected);
            tvShortFall = view.findViewById(R.id.tvShortFall);
            mLinerShortFall = view.findViewById(R.id.ll_ShortFall);
            mLy_Projected = view.findViewById(R.id.ll_Projected);
            mLy_Completed = view.findViewById(R.id.ll_completed);


        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            tvGoalName.setText(jsonObject.optString("GoalName"));
            tvTargetAmount.setText(mContext.getString(R.string.rs) + jsonObject.optString("ExpectedCorpus"));
            String curtval = jsonObject.optString("CurrentValue");
            if (curtval.equals("") || Long.valueOf(curtval.replaceAll(",", "")) < 0) {
                tvCurrentValue.setText(mContext.getString(R.string.rs) + " 0");
            } else {
                tvCurrentValue.setText(mContext.getString(R.string.rs) + jsonObject.optString("CurrentValue"));
            }

            tvShortfal.setText(mContext.getString(R.string.rs) + jsonObject.optString("ShortFall"));
            String shortfalll = jsonObject.optString("ShortFall").replace(",", "");
            if (!shortfalll.equalsIgnoreCase(""))
                mShortfalValue = Long.valueOf(shortfalll);
            if (mShortfalValue < 0) {
                tvShortTitle.setText(mContext.getResources().getString(R.string.goal_summary_surplus));
                tvShortfal.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                tvShortfal.setText(mContext.getString(R.string.rs) + jsonObject.optString("ShortFall").replace("-", ""));
            } else {
                tvShortTitle.setText("Shortfall");
                tvShortfal.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                tvShortfal.setText(mContext.getString(R.string.rs) + jsonObject.optString("ShortFall"));
            }

            double inflat = jsonObject.optDouble("Inflation");
            if (inflat > 0) {
                mTvTargetTilte.setText(mContext.getResources().getString(R.string.goal_summary_inflation_target_amnt));
            } else {
                mTvTargetTilte.setText(mContext.getResources().getString(R.string.goal_summary_target_amnt));
            }


            String target[] = jsonObject.optString("TargetDate").split("-");
            tvGoalTarget.setText(jsonObject.optString("GoalCategory") + " (Required in " + target[2] + ")");
            String path_goalpic = jsonObject.optString("GoalPic");
            if (!path_goalpic.equalsIgnoreCase(""))
                Picasso.get().load(path_goalpic).placeholder(R.mipmap.tranparent).into(ivImage);
            Double completion = jsonObject.optDouble("CompletionPercentage");
            Double projected = jsonObject.optDouble("ProjectedPercentage");
            Double shortfal = jsonObject.optDouble("ShortfallPercentage");


            if (shortfal > 0) {
                mLinerShortFall.setVisibility(View.VISIBLE);
            } else {
                mLinerShortFall.setVisibility(View.GONE);
            }

            if (projected > 0)
                mLy_Projected.setVisibility(View.VISIBLE);
            else
                mLy_Projected.setVisibility(View.GONE);



            if (completion > 0)
                mLy_Completed.setVisibility(View.VISIBLE);
            else
                mLy_Completed.setVisibility(View.GONE);

            mProgressBar.getThumb().mutate().setAlpha(0);
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;
            tvComplete.setText("Completed\n(" + completion + "%)");
            tvProjected.setText("Projected\n(" + projected + "%)");
            tvShortFall.setText("Shortfall\n(" + shortfal + "%)");


            progressItemList = new ArrayList<ProgressItem>();
            // colorRed span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = completion;
            mProgressItem.color = R.color.colorGreen;
            progressItemList.add(mProgressItem);

            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = projected - completion;
            mProgressItem.color = R.color.colorOrange;
            progressItemList.add(mProgressItem);

            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = shortfal;
            mProgressItem.color = R.color.colorRed;
            progressItemList.add(mProgressItem);


            mProgressBar.initData(progressItemList);
            mProgressBar.invalidate();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }

    }


}
