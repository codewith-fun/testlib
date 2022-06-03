package investwell.client.fragment.goalbased.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iw.acceleratordemo.R;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.utils.AppSession;

public class FragGoalFolioMenuAdapter extends RecyclerView.Adapter<FragGoalFolioMenuAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private AppSession mSession;
    private MainActivity mActivity;
    JSONObject jsonObject1;
    private FragGoalFolioMenuAdapter.OnItemClickListener listener;


    public FragGoalFolioMenuAdapter(Context context, ArrayList<JSONObject> list ,FragGoalFolioMenuAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        mActivity = (MainActivity)context;
        this.listener = listener;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal_folio_menu, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position,listener);
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
        TextView mTvPercent, mTvSchemeName;
        ImageView mIvDelete;


        public ViewHolder(View view) {
            super(view);
            mTvPercent = view.findViewById(R.id.tvPercent);
            mTvSchemeName = view.findViewById(R.id.tvSchemeName);
            mIvDelete = view.findViewById(R.id.ivDelete);


        }


        public void setItem(final int position, final FragGoalFolioMenuAdapter.OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);

            mTvPercent.setText(jsonObject.optString("Allocation"));
            mTvSchemeName.setText(jsonObject.optString("GoalName"));

            mIvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(position);

                }
            });


        }






    }


}
