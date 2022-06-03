package investwell.client.fragment.taxsaver;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterTaxSavingScheme extends RecyclerView.Adapter<AdapterTaxSavingScheme.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;
    private MainActivity mActivity;
private AppApplication mApplication;
    private String  applicant_name;



    public AdapterTaxSavingScheme(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @NonNull
    @Override
    public AdapterTaxSavingScheme.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dividend_scheme_list_item, parent, false);
        AdapterTaxSavingScheme.MyViewHolder vh = new AdapterTaxSavingScheme.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final AdapterTaxSavingScheme.MyViewHolder holder, final int position) {
        mActivity = (MainActivity) context;
        final JSONObject jsonObject1 = jsonObject.get(position);

        holder.Scheme_name.setText(jsonObject1.optString("SchemeName"));
        holder.Total_value.setText(context.getString(R.string.rs)+jsonObject1.optString("Amount"));
        final  JSONArray jsonArray  = jsonObject1.optJSONArray("ELSSTransactionDetail");


        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("applicant_name",applicant_name);
                bundle.putString("colorBlue",jsonObject1.optString("SchemeName"));
                bundle.putString("data",jsonArray.toString());
                mActivity.displayViewOther(68,bundle);

            }
        });
    }

    @Override
    public int getItemCount() {

        return jsonObject.size();

    }

    public void updateList(List<JSONObject> list, String Applicant) {

        jsonObject.clear();
        jsonObject.addAll(list);
        applicant_name = Applicant;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Scheme_name, Total_value;
        RelativeLayout main_layout;



        public MyViewHolder(View view) {
            super(view);

            Scheme_name = view.findViewById(R.id.colorBlue);
            Total_value = view.findViewById(R.id.total_value);
            main_layout = view.findViewById(R.id.rl_dashboard_parent_container);




        }
    }
}


