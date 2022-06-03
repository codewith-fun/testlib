package investwell.client.fragment.taxsaver;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterTaxSavingDetail extends RecyclerView.Adapter<AdapterTaxSavingDetail.MyViewHolder> {

    public ArrayList<JSONObject> jsonObject;
    Context context;

    public AdapterTaxSavingDetail(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public AdapterTaxSavingDetail.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_tax_saving_detail, parent, false);
        AdapterTaxSavingDetail.MyViewHolder vh = new AdapterTaxSavingDetail.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(AdapterTaxSavingDetail.MyViewHolder holder, final int position) {


        final JSONObject jsonObject1 = jsonObject.get(position);

        holder.transDate.setText(jsonObject1.optString("TranDate"));
        holder.amount.setText(context.getString(R.string.rs)+(jsonObject1.optString("Amount")));
        holder.type.setText("("+jsonObject1.optString("Type")+")");
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

        TextView transDate, amount,type;


        public MyViewHolder(View view) {
            super(view);

            transDate = view.findViewById(R.id.transDate);
            amount = view.findViewById(R.id.amount);
            type = view.findViewById(R.id.type);

        }
    }
}

