package investwell.client.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FolioItemListAdapter extends RecyclerView.Adapter<FolioItemListAdapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    private JSONObject jsonObject1;
    Context context;


    public FolioItemListAdapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @NonNull
    @Override
    public FolioItemListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_folio_transaction, parent, false);
        FolioItemListAdapter.MyViewHolder vh = new FolioItemListAdapter.MyViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(final FolioItemListAdapter.MyViewHolder holder, final int position) {

        jsonObject1 = jsonObject.get(position);
        /* holder.TvScheme.setText(jsonObject1.optString("SchemeName"));*/
        holder.TvTrans.setText(jsonObject1.optString("TranDate"));
        String navPlaceHolder="NAV : ";
        String navValue=jsonObject1.optString("Nav");
        String navData=navPlaceHolder+navValue;
        holder.Tvnav.setText(navData);

        if (jsonObject1.optString("Amount").contains("-")){
            holder.TvAmount.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.TvAmount.setText(context.getString(R.string.rs)+jsonObject1.optString("Amount").replace("-",""));
        }else{
            holder.TvAmount.setText(context.getString(R.string.rs)+jsonObject1.optString("Amount"));
            holder.TvAmount.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        holder.TvScheme.setText(jsonObject1.optString("Type"));
        String unitsPlaceHolder="Units : ";
        String unitsValue=jsonObject1.optString("Units");
        String units=unitsPlaceHolder+unitsValue;
        holder.TvUnit.setText(units);


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

        TextView TvScheme,TvTrans,Tvnav,TvAmount,TvType,TvUnit;

        public MyViewHolder(View view) {
            super(view);

            TvScheme = view.findViewById(R.id.tv_scheme);
            TvTrans = view.findViewById(R.id.tv_date);
            Tvnav = view.findViewById(R.id.tv_nav_amount);
            TvAmount = view.findViewById(R.id.tv_amount);
            /* TvType = view.findViewById(R.id.type);*/
            TvUnit = view.findViewById(R.id.tv_units);


        }
    }


}
