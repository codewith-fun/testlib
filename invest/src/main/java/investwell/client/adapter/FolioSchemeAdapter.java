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

import investwell.client.activity.MainActivity;

public class FolioSchemeAdapter extends RecyclerView.Adapter<FolioSchemeAdapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    String FolioNo;
    Context context;
    private MainActivity mActivity;
    private JSONObject jsonObject1;

    public FolioSchemeAdapter(Context context, ArrayList<JSONObject> jsonObject, String FolioNo) {
        this.context = context;
        this.jsonObject = jsonObject;
        this.FolioNo = FolioNo;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_folio_scheme_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);


        return vh;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        mActivity = (MainActivity) context;
        jsonObject1 = jsonObject.get(position);

        holder.colorBlue.setText(jsonObject1.optString("SchemeName"));
        holder.banalce_unit.setText(jsonObject1.optString("BalanceUnits"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Bundle bundle = new Bundle();
                bundle.putString("tvFolioNo", FolioNo);
                bundle.putString("scode", jsonObject.get(position).optString("Scode"));

                mActivity.displayViewOther(78, bundle);*/
            }
        });

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

        TextView colorBlue, banalce_unit;


        public MyViewHolder(View view) {
            super(view);


            colorBlue = view.findViewById(R.id.colorBlue);
            banalce_unit = view.findViewById(R.id.banalce_unit);

        }
    }


}