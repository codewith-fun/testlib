package investwell.client.fragment.goalbased.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;

public class GoalCategoryTypeOneAAdapter extends RecyclerView.Adapter<GoalCategoryTypeOneAAdapter.MyViewHolder> {
    public ArrayList<JSONObject> mGoalCategoryTypeOneAList;
    private Context mContext;
    private AppSession mSession;
    private View itemView;
    private MainActivity mActivity;

    public GoalCategoryTypeOneAAdapter(Context context, ArrayList<JSONObject> goalCategoryTypeOneAList) {
        this.mGoalCategoryTypeOneAList = goalCategoryTypeOneAList;
        mContext = context;

        mSession = AppSession.getInstance(mContext);
        mActivity = (MainActivity) context;
    }

    @NonNull
    @Override
    public GoalCategoryTypeOneAAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_goal_category_type_one_a, parent, false);

        return new GoalCategoryTypeOneAAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GoalCategoryTypeOneAAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final JSONObject object = mGoalCategoryTypeOneAList.get(position);
        holder.tvGoalCategoryOneA.setText(!TextUtils.isEmpty(object.optString("CategoryName")) ? object.optString("CategoryName") : "");
        String path = object.optString("CategoryIcon");
        Picasso.get().load(path).placeholder(R.mipmap.tranparent).into(holder.ivGoalCategoryOneA);

        holder.rlGoalCategoryTypeOneA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoalCategoryClick(object.toString());
            }
        });

        //To change the stroke color
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        GradientDrawable myGrad = (GradientDrawable) holder.rlGoalCategoryImg.getBackground();
        myGrad.setStroke(convertDpToPx(1), currentColor);
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void updateList(List<JSONObject> list) {
        mGoalCategoryTypeOneAList.clear();
        mGoalCategoryTypeOneAList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mGoalCategoryTypeOneAList.size();
    }

    private void onGoalCategoryClick(String cat) {
        Bundle bundle = new Bundle();
        bundle.putString("category_data", cat);
        mActivity.displayViewOther(76, bundle);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalCategoryOneA;
        ImageView ivGoalCategoryOneA;
        RelativeLayout rlGoalCategoryTypeOneA, rlGoalCategoryImg;


        public MyViewHolder(View view) {
            super(view);
            tvGoalCategoryOneA = view.findViewById(R.id.tv_goal_category);
            rlGoalCategoryImg = view.findViewById(R.id.rl_goal_category_img);
            ivGoalCategoryOneA = view.findViewById(R.id.iv_goal_category);
            rlGoalCategoryTypeOneA = view.findViewById(R.id.rl_goal_category);
        }
    }
}
