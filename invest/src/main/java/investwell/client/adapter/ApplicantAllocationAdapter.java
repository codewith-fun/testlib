package investwell.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;

public class ApplicantAllocationAdapter extends RecyclerView.Adapter<ApplicantAllocationAdapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;

    public ApplicantAllocationAdapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public ApplicantAllocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryallocationitems, parent, false);
        ApplicantAllocationAdapter.MyViewHolder vh = new ApplicantAllocationAdapter.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ApplicantAllocationAdapter.MyViewHolder holder, final int position) {
        final MainActivity mActivity = (MainActivity) context;
        final JSONObject jsonObject1 = jsonObject.get(position);
        holder.mCategory.setText(jsonObject1.optString("Name"));
        holder.mValue.setText(context.getResources().getString(R.string.rs) + "" + jsonObject1.optString("MarketValue"));
        holder.mPercentage.setText(jsonObject1.optString("HoldingPercentage"));

        holder.mCategory.setSelected(true);
        holder.mCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("cid", jsonObject1.optString("Cid"));
                mActivity.displayViewOther(46, bundle);

            }
        });
        if (position == jsonObject.size() - 1) {
            holder.mCategory.setTextColor(Color.BLACK);
            holder.mValue.setTextColor(Color.BLACK);
            holder.mValue.setTextColor(Color.BLACK);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");
            holder.mCategory.setTypeface(font);
            holder.mValue.setTypeface(font);
            holder.mPercentage.setTypeface(font);
            holder.mValue.setText(jsonObject1.optString("MarketValue"));

        }
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

        TextView mCategory, mValue, mPercentage;


        public MyViewHolder(View view) {
            super(view);

            mCategory = view.findViewById(R.id.category);
            mValue = view.findViewById(R.id.tv_market_value);
            mPercentage = view.findViewById(R.id.percent);


        }
    }
}
