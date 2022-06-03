package investwell.client.fragment.factsheet.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.Utils;


public class ScoreAnalysisAdapter extends RecyclerView.Adapter<ScoreAnalysisAdapter.MyViewHolder> {

    ArrayList<JSONObject> mDataList;
    Context context;
    private String mDataType = "";
    private AppSession appSession;
    public ScoreAnalysisAdapter(Context context, ArrayList<JSONObject> list) {
        this.context = context;
        mDataList = list;
        appSession=AppSession.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_factsheet_holding, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        JSONObject jsonObject = mDataList.get(position);

        if (mDataType.equalsIgnoreCase("topHolding")) {
            holder.score.setText(jsonObject.optString("Holdings"));
            holder.asset.setText(jsonObject.optString("NetAsset"));
        } else {
            holder.score.setText(jsonObject.optString("SectorRating"));
            holder.asset.setText(jsonObject.optString("NetAsset"));
        }

        if (position % 2 == 0) {
            if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                    Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.rlFactsheetHolding.setBackgroundColor(ContextCompat.getColor(context, R.color.darkColorCardBackground));

            }
            else {
                holder.rlFactsheetHolding.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey_100));

            }
        } else {
            if (!TextUtils.isEmpty(Utils.getConfigData(appSession).optString("APPType")) &&
                    Utils.getConfigData(appSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.rlFactsheetHolding.setBackgroundColor(ContextCompat.getColor(context, R.color.darkColorPrimaryDark));
            }else {
                holder.rlFactsheetHolding.setBackgroundColor(ContextCompat.getColor(context, R.color.lightDialogBackground));

            }
        }

    }

    @Override
    public int getItemCount() {

        return mDataList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView score, asset;
        RelativeLayout rlFactsheetHolding;

        public MyViewHolder(View itemView) {
            super(itemView);
            rlFactsheetHolding = itemView.findViewById(R.id.rl_parent_fact_sheet_holding);
            score = itemView.findViewById(R.id.score);
            asset = itemView.findViewById(R.id.asset);


        }
    }

    public void updateList(List<JSONObject> list, String type) {
        mDataList.clear();
        mDataList.addAll(list);
        mDataType = type;
        notifyDataSetChanged();
    }

}
