package investwell.client.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import investwell.utils.AppSession;
import investwell.utils.CustomProgressBar;
import investwell.utils.ProgressItem;

public class GoalAdapterOldVersion extends RecyclerView.Adapter<GoalAdapterOldVersion.ViewHolder> {
    public ArrayList<JSONObject> mDataList;

    private Context mContext;
    private GoalAdapterOldVersion.OnItemClickListener listener;
    private AppSession mSession;

    public GoalAdapterOldVersion(Context context, ArrayList<JSONObject> list, GoalAdapterOldVersion.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_goal_summery_adapter, viewGroup, false);
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
        TextView tvGoalName, tvTargetAmount, tvAchieved, tvComplete, tvProjected, tvShortFall, tvGoalUserName;
        CustomProgressBar mProgressBar;
        LinearLayout mLinerShortFall, mLy_Progress;
        ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            tvGoalName = view.findViewById(R.id.tvGoalName);
            tvTargetAmount = view.findViewById(R.id.tvTargetAmount);
            tvAchieved = view.findViewById(R.id.tvAchieved);
            mProgressBar = view.findViewById(R.id.seekBar0);

            mLy_Progress = view.findViewById(R.id.ly_progress);

            tvComplete = view.findViewById(R.id.tvComplete);
            tvProjected = view.findViewById(R.id.tvProjected);
            tvShortFall = view.findViewById(R.id.tvShortFall);
            mLinerShortFall = view.findViewById(R.id.linerShortFall);
            tvGoalUserName = view.findViewById(R.id.tvGoalUserName);
            ivImage = view.findViewById(R.id.ivImage);

        }

        /*"Bid":"10056 ",
"Cid":"01C02330",
"MasterGoalCode":"2",
"GoalName":"Education",
"Purpose":"SHERYAS GRADUATION",
"StartYear":"2018",
"YearToGet":"2022",
"CurrentAmount":"1600000.00",
"InflationRate":"15.00",
"ExpectedCorpus":"2798410.00",
"CurrentValue":"1360957.09",
"SIPAmt":"0.00",
"ExpectedReturn":"10.00",
"ProjectedValue":"1811433.89",
"ShortFall":"986976.11",
"OneTimeInvestment":"741529.76",
"SIPInvestment":"22883.75"*/

        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            tvGoalName.setText(jsonObject.optString("GoalName"));
            tvGoalUserName.setText(jsonObject.optString("Purpose"));
            double targetAmount = jsonObject.optDouble("ExpectedCorpus");
            double CurrentValue = jsonObject.optDouble("CurrentValue");
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(0);
            if (!jsonObject.has("ProjectedValue")) {
                mLy_Progress.setVisibility(View.GONE);
                tvTargetAmount.setText(jsonObject.optString("totalamount"));
                itemView.setClickable(false);
            } else {
                itemView.setClickable(true);
                mLy_Progress.setVisibility(View.VISIBLE);
                String strAmount = format.format(targetAmount);
                String strCurrentValue = format.format(CurrentValue);
                tvTargetAmount.setText(strAmount);
                tvAchieved.setText(strCurrentValue);
            }
            String goalName = jsonObject.optString("GoalName");
            if (goalName.equalsIgnoreCase("Education")) {
                ivImage.setImageResource(R.mipmap.education);
            } else if (goalName.equalsIgnoreCase("Retirement")) {
                ivImage.setImageResource(R.mipmap.retirement);
            } else if (goalName.equalsIgnoreCase("Marriage")) {
                ivImage.setImageResource(R.mipmap.mariage);
            } else if (goalName.equalsIgnoreCase("House") || goalName.equalsIgnoreCase("Home")) {
                ivImage.setImageResource(R.mipmap.house);
            } else if (goalName.equalsIgnoreCase("Car")) {
                ivImage.setImageResource(R.mipmap.car);
            } else if (goalName.equalsIgnoreCase("Loan")) {
                ivImage.setImageResource(R.mipmap.other);
            } else if (goalName.equalsIgnoreCase("Tour")) {
                ivImage.setImageResource(R.mipmap.vacation);
            } else {
                ivImage.setImageResource(R.mipmap.other);
            }


            mProgressBar.getThumb().mutate().setAlpha(0);
            ArrayList<ProgressItem> progressItemList;
            ProgressItem mProgressItem;

            Double currentAmount = jsonObject.optDouble("CurrentValue");
            Double ExpectedCorpus = jsonObject.optDouble("ExpectedCorpus");
            Double ProjectedValue = jsonObject.optDouble("ProjectedValue");

            Double ShortFall = jsonObject.optDouble("ShortFall");


            Double percentagecomplete = currentAmount * 100 / ExpectedCorpus;
            Double percentageProjected = (ProjectedValue * 100) / ExpectedCorpus;

            Double percentageShortFall = (ShortFall * 100) / ExpectedCorpus;


            DecimalFormat twoDForm = new DecimalFormat("#.00");
            percentagecomplete = Double.valueOf(twoDForm.format(percentagecomplete));
            percentageProjected = Double.valueOf(twoDForm.format(percentageProjected));
            percentageShortFall = Double.valueOf(twoDForm.format(percentageShortFall));

            tvComplete.setText("Completed\n(" + percentagecomplete + "%)");
            tvProjected.setText("Projected\n(" + percentageProjected + "%)");

            if (percentageShortFall < 0) {
                mLinerShortFall.setVisibility(View.GONE);
                //  tvProjected.setText("Projected\n(Excess)");
            } else {
                mLinerShortFall.setVisibility(View.VISIBLE);
                tvShortFall.setText("Shortfall\n(" + percentageShortFall + "%)");
               /* double finalValue = percentageProjected - percentageShortFall;
                finalValue = Double.valueOf(twoDForm.format(finalValue));*/
                //  tvProjected.setText("Projected\n(" + percentageProjected + "%)");

            }

            progressItemList = new ArrayList<ProgressItem>();
            // red span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = percentagecomplete;
            mProgressItem.color = R.color.colorGreen;
            progressItemList.add(mProgressItem);

            // blue span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = (percentageProjected - percentagecomplete);
            mProgressItem.color = R.color.colorOrange;
            progressItemList.add(mProgressItem);

            // green span
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = percentageShortFall;
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
