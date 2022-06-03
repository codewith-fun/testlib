package investwell.client.fragment.divident;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterDividend extends RecyclerView.Adapter<AdapterDividend.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private int next_year, year;

    public AdapterDividend(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public AdapterDividend.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dividend_list_item, parent, false);
        AdapterDividend.MyViewHolder vh = new AdapterDividend.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final AdapterDividend.MyViewHolder holder, final int position) {
        mActivity = (MainActivity) context;
        final JSONObject jsonObject1 = jsonObject.get(position);

        holder.Applicant_Name.setText(jsonObject1.optString("ApplicantName"));
        holder.GrndTotal.setText(context.getString(R.string.rs) + jsonObject1.optString("GrandTotal"));
        JSONArray array = jsonObject1.optJSONArray("DividendSummaryDetail");
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {

            JSONObject jsonObject2 = array.optJSONObject(i);
            list.add(jsonObject2);
        }
        holder.dividend_scheme_adapter.updateList(list, jsonObject1.optString("ApplicantName"), year, next_year);

     /*   holder.iv_down.setOnClickListener(new View.OnClickListener() {
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

    public void updateList(List<JSONObject> list, int Year, int Next_year) {

        jsonObject.clear();
        jsonObject.addAll(list);
        year = Year;
        next_year = Next_year;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Applicant_Name, GrndTotal;
        RecyclerView listview;
        /*ImageView iv_down;*/
        AdapterDividendScheme dividend_scheme_adapter;


        public MyViewHolder(View view) {
            super(view);

            Applicant_Name = view.findViewById(R.id.applicant_name);
            GrndTotal = view.findViewById(R.id.GrndTotal);
            listview = view.findViewById(R.id.listview);
            /*iv_down = view.findViewById(R.id.iv_down);*/

            listview.setHasFixedSize(true);
            listview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            dividend_scheme_adapter = new AdapterDividendScheme(context, new ArrayList<JSONObject>());
            listview.setAdapter(dividend_scheme_adapter);


        }
    }
}
