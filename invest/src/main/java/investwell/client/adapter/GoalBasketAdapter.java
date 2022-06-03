package investwell.client.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.ResizeWidthAnimation;

public class GoalBasketAdapter extends RecyclerView.Adapter<GoalBasketAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private OnItemClickListener listener;
    private AppSession mSession;

    public GoalBasketAdapter(Context context, ArrayList<JSONObject> list, GoalBasketAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public GoalBasketAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal_based_basket, viewGroup, false);
        return new GoalBasketAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GoalBasketAdapter.ViewHolder viewHolder, final int position) {
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

    private int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSchemeName, tvSchemeCategory, tvPercentage, yearOne, yearTwo, yearThree;
        View vProgress;

        public ViewHolder(View view) {
            super(view);
            tvSchemeName = view.findViewById(R.id.tv_scheme_name);
            yearOne = view.findViewById(R.id.yearOne);
            yearTwo = view.findViewById(R.id.yearTwo);
            yearThree = view.findViewById(R.id.yearThree);
            tvPercentage = view.findViewById(R.id.tv_percentage);
            tvSchemeName = view.findViewById(R.id.tv_scheme_name);
            tvSchemeCategory = view.findViewById(R.id.tv_scheme_category);
            vProgress = view.findViewById(R.id.v_progress);
        }


        public void setItem(final int position, final GoalBasketAdapter.OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            boolean animate = true;
            try {
                String image_path = jsonObject.optString("AMCLogo");
                tvSchemeName.setText(jsonObject.optString("SchName"));
                yearOne.setText(String.format("%.2f", jsonObject.optDouble("Performance1Year")));
                yearTwo.setText(String.format("%.2f", jsonObject.optDouble("Performance3Year")));
                yearThree.setText(String.format("%.2f", jsonObject.optDouble("Performance5Year")));
                tvPercentage.setText(jsonObject.optString("AllocatePercentage") + " %");
                int newWidth = 0;
                if (!TextUtils.isEmpty(jsonObject.optString("AllocatePercentage"))) {
                    double width = Double.parseDouble(jsonObject.optString("AllocatePercentage"));
                    newWidth = (int) Math.round(width * 1.5);
                }
                ViewGroup.LayoutParams layoutParams = vProgress.getLayoutParams();
                layoutParams.width = dpToPx(newWidth);
                vProgress.setLayoutParams(layoutParams);
                if (animate) {
                    ResizeWidthAnimation anim = new ResizeWidthAnimation(vProgress, dpToPx(newWidth));
                    anim.setDuration(1200);
                    vProgress.startAnimation(anim);
                } else {
                    newWidth = dpToPx(newWidth);
                    ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) vProgress.getLayoutParams();
                    lp.width = newWidth;
                    vProgress.setLayoutParams(lp);
                }
                tvSchemeCategory.setText(jsonObject.optString("Objective"));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


}
