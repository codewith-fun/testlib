package investwell.client.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.utils.Utils;

public class MyFolioAdapter extends RecyclerView.Adapter<MyFolioAdapter.MyViewHolder>{


    public ArrayList<JSONObject> mDataList;

    Context context;
    private MainActivity mActivity;
    public MyFolioAdapter(Context context, ArrayList<JSONObject> jsonObject) {
        this.context = context;
        this.mDataList = jsonObject;

    }

    @NonNull
    @Override
    public MyFolioAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_folio_detail, parent, false);
        MyFolioAdapter.MyViewHolder vh = new MyFolioAdapter.MyViewHolder(v);



        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull final MyFolioAdapter.MyViewHolder holder, final int position) {


        mActivity = (MainActivity) context;
        final ArrayList<JSONObject> list = new ArrayList<>();
        final JSONObject jsonObject1 = mDataList.get(position);

        holder.tvSchemName.setText(jsonObject1.optString("AMCName"));
        holder.tvFolio.setText(!TextUtils.isEmpty(jsonObject1.optString("FolioNo")) ? jsonObject1.optString("FolioNo") : "N/A");
        holder.tvInvestor.setText(!TextUtils.isEmpty(Utils.convertStringFirstTextCaps(jsonObject1.optString("ApplicantName"))) ? jsonObject1.optString("ApplicantName") : "N/A");
        holder.tvUCC.setText(!TextUtils.isEmpty(jsonObject1.optString("UCC")) ? jsonObject1.optString("UCC") : "N/A");
        holder.tvHolding.setText(!TextUtils.isEmpty(jsonObject1.optString("MOH")) ? jsonObject1.optString("MOH") : "N/A");

        JSONArray jsonArray = jsonObject1.optJSONArray("SchemeList");
        holder.tvSchemeNo.setText(""+jsonArray.length());
        for (int i = 0 ; i<jsonArray.length();i++){
            JSONObject obj = jsonArray.optJSONObject(i);
            list.add(obj);
        }

        if (jsonArray.length()==1){
            holder.tvscheme.setText("Scheme");
        }else{
            holder.tvscheme.setText("Schemes");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("data", jsonObject1.toString());
                bundle.putString("applicant_name",holder.tvInvestor.getText().toString());
                bundle.putString("pan",jsonObject1.optString("FirstPAN"));
                bundle.putString("folio_no",holder.tvFolio.getText().toString());
                bundle.putString("holding",holder.tvHolding.getText().toString());
                bundle.putString("all_data",list.toString());
                mActivity.displayViewOther(87,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {

        return mDataList.size();

    }

    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }





    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFolio,tvUCC,tvSchemName, tvInvestor, tvHolding,tvSchemeNo,tvscheme;




        public MyViewHolder(View view) {
            super(view);


            tvFolio = view.findViewById(R.id.tv_folio_value);
            tvUCC = view.findViewById(R.id.tv_ucc_value);
            tvSchemName = view.findViewById(R.id.tvSchemName);
            tvInvestor = view.findViewById(R.id.tv_investor_value);
            tvHolding = view.findViewById(R.id.tv_holding_value);
            tvSchemeNo = view.findViewById(R.id.tvSchemeNo);
            tvscheme = view.findViewById(R.id.tvscheme);
        }
    }


}