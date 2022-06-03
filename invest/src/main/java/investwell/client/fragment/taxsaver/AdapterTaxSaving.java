package investwell.client.fragment.taxsaver;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterTaxSaving extends RecyclerView.Adapter<AdapterTaxSaving.MyViewHolder> {
    public ArrayList<JSONObject> jsonObject;
    Context context;
    private int mLastPosition = -1;
    public AdapterTaxSaving(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @NonNull
    @Override
    public AdapterTaxSaving.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tax_saving_item_list, parent, false);
        AdapterTaxSaving.MyViewHolder vh = new AdapterTaxSaving.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final AdapterTaxSaving.MyViewHolder holder, final int position) {

        Animation animation = AnimationUtils.loadAnimation(context, (position > mLastPosition) ? R.anim.left_from_right : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        mLastPosition = position;
        if (position == 1) {
            holder.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Rect rect = new Rect();
                    holder.itemView.getGlobalVisibleRect(rect);
                }
            });
        }
        final JSONObject jsonObject1 = jsonObject.get(position);

        holder.client_name.setText(jsonObject1.optString("Applicant"));
        holder.amount.setText(context.getString(R.string.Rs)+jsonObject1.optString("TotalAmount"));
        JSONArray array = jsonObject1.optJSONArray("ELSSSummarySchemeWise");
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0 ; i<array.length();i++){
            JSONObject jsonObject2 = array.optJSONObject(i);
            list.add(jsonObject2);

        }
        holder.taxsaving_scheme_adapter.updateList(list,jsonObject1.optString("Applicant"));
        if (jsonObject1.optString("Amount").contains("-")){
            //viewHolder.tvTransacAmount.getText().toString().replace("-","");
            holder.amount.setTextColor(context.getResources().getColor(R.color.colorNegativeValues));
        }else{
            holder.amount.setTextColor(Color.parseColor("#43ce41"));

        }

       /* holder.iv_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.listview.getVisibility() == View.VISIBLE) {
                    holder.listview.setVisibility(View.GONE);
                    holder.iv_down.setImageResource(R.drawable.down_arrow);
                } else {

                    holder.listview.setVisibility(View.VISIBLE);
                    holder.iv_down.setImageResource(R.drawable.up_arrow);
                }


            }
        });*/


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

        TextView client_name, colorBlue, folio, date,  amount;

        RecyclerView listview;
        AdapterTaxSavingScheme taxsaving_scheme_adapter;


        public MyViewHolder(View view) {
            super(view);

            client_name = view.findViewById(R.id.client_name);
            colorBlue = view.findViewById(R.id.colorBlue);
            folio = view.findViewById(R.id.folio);
            date = view.findViewById(R.id.date);
            amount = view.findViewById(R.id.TotalAmount);

            listview = view.findViewById(R.id.listview);

            listview.setHasFixedSize(true);
            listview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            taxsaving_scheme_adapter = new AdapterTaxSavingScheme(context, new ArrayList<JSONObject>());
            listview.setAdapter(taxsaving_scheme_adapter);

        }
    }
}
