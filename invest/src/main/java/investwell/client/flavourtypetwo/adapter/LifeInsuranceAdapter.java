package investwell.client.flavourtypetwo.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;

public class LifeInsuranceAdapter extends RecyclerView.Adapter<LifeInsuranceAdapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;
    private MainActivity mActivity;

    public LifeInsuranceAdapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public LifeInsuranceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_insurance_item, parent, false);
        LifeInsuranceAdapter.MyViewHolder vh = new LifeInsuranceAdapter.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final LifeInsuranceAdapter.MyViewHolder holder, final int position) {
        mActivity = (MainActivity) context;
        DecimalFormat myFormatter = new DecimalFormat("##,##,###.00");
        final JSONObject jsonObject1 = jsonObject.get(position);
        holder.Applicant_Name.setText(jsonObject1.optString("Applicant"));
        holder.policy_number.setText("Policy No."+jsonObject1.optString("PolicyNo"));
        holder.insurance_type.setText(jsonObject1.optString("InsType"));
        holder.sum_assured.setText(jsonObject1.optString("SumInsured"));
        holder.premium.setText(jsonObject1.optString("Amount"));
        holder.due_date.setText(jsonObject1.optString("NextPremium"));
        holder.mTvRemarks.setText(jsonObject1.optString("Remarks"));

        if (holder.due_date.getText().toString().equalsIgnoreCase("Fully Paid")){
            holder.due_date.setTextColor(Color.parseColor("#5DB64C"));
        }else{

            holder.due_date.setTextColor(Color.parseColor("#000000"));
        }

        if (jsonObject1.optString("Remarks").isEmpty()){
            holder.mTvRemarks.setVisibility(View.GONE);
            holder.view_line2.setVisibility(View.GONE);
        }else{
            holder.mTvRemarks.setVisibility(View.VISIBLE);
            holder.view_line2.setVisibility(View.VISIBLE);
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

        TextView Applicant_Name, policy_number,insurance_type,sum_assured,premium,due_date,mTvRemarks;
        View view_line2;

        public MyViewHolder(View view) {
            super(view);

            Applicant_Name = view.findViewById(R.id.applicant_name);
            policy_number = view.findViewById(R.id.policy_number);
            insurance_type = view.findViewById(R.id.insurance_type);
            sum_assured = view.findViewById(R.id.sum_assured);
            premium = view.findViewById(R.id.premium);
            due_date = view.findViewById(R.id.due_date);
            mTvRemarks = view.findViewById(R.id.tvremarks);
            view_line2 = view.findViewById(R.id.view_line2);





        }
    }
}
