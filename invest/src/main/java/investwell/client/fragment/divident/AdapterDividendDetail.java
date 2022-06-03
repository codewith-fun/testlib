package investwell.client.fragment.divident;

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

public class AdapterDividendDetail extends RecyclerView.Adapter<AdapterDividendDetail.MyViewHolder> {

    public ArrayList<JSONObject> jsonObject;
    Context context;

    public AdapterDividendDetail(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public AdapterDividendDetail.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_dividend_detail, parent, false);
        AdapterDividendDetail.MyViewHolder vh = new AdapterDividendDetail.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(AdapterDividendDetail.MyViewHolder holder, final int position) {


        final JSONObject jsonObject1 = jsonObject.get(position);

        holder.div_date.setText(jsonObject1.optString("DivDate"));
        holder.div_payout.setText(context.getString(R.string.rs)+(jsonObject1.optString("DivPayout")));
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

     TextView div_date, div_payout;


        public MyViewHolder(View view) {
            super(view);

            div_date = view.findViewById(R.id.div_date);
            div_payout = view.findViewById(R.id.div_payout);

        }
    }
}
