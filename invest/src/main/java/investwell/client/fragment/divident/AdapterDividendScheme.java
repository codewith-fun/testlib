package investwell.client.fragment.divident;

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
import investwell.utils.AppConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterDividendScheme extends RecyclerView.Adapter<AdapterDividendScheme.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private String  applicant_name;
    private int year,next_year;


    public AdapterDividendScheme(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @NonNull
    @Override
    public AdapterDividendScheme.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dividend_scheme_list_item, parent, false);
        AdapterDividendScheme.MyViewHolder vh = new AdapterDividendScheme.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final AdapterDividendScheme.MyViewHolder holder, final int position) {
        mActivity = (MainActivity) context;
        final JSONObject jsonObject1 = jsonObject.get(position);

        holder.Scheme_name.setText(jsonObject1.optString("SchemeName"));
        holder.Total_value.setText(context.getString(R.string.rs)+jsonObject1.optString("Total"));

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("applicant_name",applicant_name);
                bundle.putString("colorBlue",jsonObject1.optString("SchemeName"));
                bundle.putString("fcode",jsonObject1.optString("Fcode"));
                bundle.putString("scode",jsonObject1.optString("Scode"));
                bundle.putString("div_payout",jsonObject1.optString("DivPayout"));
                bundle.putString("div_reinvest",jsonObject1.optString("DivReinvest"));
                bundle.putString("cid",jsonObject1.optString(AppConstants.CUSTOMER_ID));
                bundle.putString("year",String.valueOf(year));
                bundle.putString("next_year",String.valueOf(next_year));
                mActivity.displayViewOther(45,bundle);

              //  Toast.makeText(context, ""+jsonObject1.optString("Fcode"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {

        return jsonObject.size();

    }

    public void updateList(List<JSONObject> list, String ApplicantName, int Year, int Next_year) {

        jsonObject.clear();
        jsonObject.addAll(list);
        applicant_name=ApplicantName;
        year = Year;
        next_year=Next_year;
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

