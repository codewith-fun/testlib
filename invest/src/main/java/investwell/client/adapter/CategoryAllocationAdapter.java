package investwell.client.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryAllocationAdapter extends RecyclerView.Adapter<CategoryAllocationAdapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;

    public CategoryAllocationAdapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public CategoryAllocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryallocationitems, parent, false);
        CategoryAllocationAdapter.MyViewHolder vh = new CategoryAllocationAdapter.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final CategoryAllocationAdapter.MyViewHolder holder, final int position) {
        final JSONObject jsonObject1 = jsonObject.get(position);
        holder.mCategory.setText(jsonObject1.optString("Objective"));
        holder.mValue.setText(context.getResources().getString(R.string.rs)+""+jsonObject1.optString("Amount"));
        holder.mPercentage.setText(jsonObject1.optString("HoldingPercentage"));
        holder.mCategory.setSelected(true);
        if (position == jsonObject.size() - 1) {
            holder.mCategory.setTextColor(Color.BLACK);
            holder.mValue.setTextColor(Color.BLACK);
            holder.mValue.setTextColor(Color.BLACK);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");
            holder.mCategory.setTypeface(font);
            holder.mValue.setTypeface(font);
            holder.mPercentage.setTypeface(font);
            holder.mValue.setText(jsonObject1.optString("Amount"));
        }

    }

    @Override
    public int getItemCount() {

        return jsonObject.size();

    }

    public void  updateList(List<JSONObject> list) {

        jsonObject.clear();
        jsonObject.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mCategory,mValue,mPercentage;


        public MyViewHolder(View view) {
            super(view);

            mCategory = view.findViewById(R.id.category);
            mValue = view.findViewById(R.id.tv_market_value);
            mPercentage = view.findViewById(R.id.percent);



        }
    }
}