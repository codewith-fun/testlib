package investwell.client.adapter;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Transaction_Adapter extends RecyclerView.Adapter<Transaction_Adapter.MyViewHolder> {

    // String[] ApplicantName, tvSchemeName, tvFolioNo, tvDate, tvTransacType, tvTransacAmount;

    public ArrayList<JSONObject> jsonObject;
    Context context;
    private int mLastPosition = -1;

    public Transaction_Adapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_transactions, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

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

        holder.tvInvestorName.setText(jsonObject1.optString("Applicant"));
        holder.tvSchemeName.setText(jsonObject1.optString("SchemeName"));
        holder.tvFolioNo.setText( "Folio No. " + jsonObject1.optString("FolioNo"));
        holder.tvDate.setText(jsonObject1.optString("TranDate"));
        holder.tvTransacType.setText(" | " + jsonObject1.optString("Type"));
        String input = jsonObject1.optString("Amount");
        boolean isFound = input.indexOf("-") != -1 ? true : false; //true
        if (isFound) {
            input = input.substring(0, 1) + context.getString(R.string.Rs)+" " + input.substring(1, input.length());
            holder.tvTransacAmount.setText(input);
        }else{
            holder.tvTransacAmount.setText(context.getString(R.string.Rs)+" "+jsonObject1.optString("Amount"));
        }

        if (jsonObject1.optString("Amount").contains("-")) {
            //viewHolder.tvTransacAmount.getText().toString().replace("-","");
            holder.tvTransacAmount.setTextColor(context.getResources().getColor(R.color.colorNegativeValues));

        } else {
            holder.tvTransacAmount.setTextColor(context.getResources().getColor(R.color.colorPositiveValues));

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

        TextView tvInvestorName, tvSchemeName, tvFolioNo, tvDate, tvTransacType, tvTransacAmount;


        public MyViewHolder(View view) {
            super(view);

            tvInvestorName = view.findViewById(R.id.tv_investor_name);
            tvSchemeName = view.findViewById(R.id.tv_scheme_name);
            tvFolioNo = view.findViewById(R.id.tv_folio_no);
            tvDate = view.findViewById(R.id.tv_date);
            tvTransacType = view.findViewById(R.id.tv_transac_type);
            tvTransacAmount = view.findViewById(R.id.tv_transac_amount);


        }
    }
}