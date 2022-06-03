package investwell.client.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Scheme_List_Adapter extends RecyclerView.Adapter<Scheme_List_Adapter.MyViewHolder> {
    public ArrayList<JSONObject> jsonObject;
    private Context mcontext;
    private AppSession mSession;
    private MainActivity mActivity;
private AppApplication mApplication;

    public Scheme_List_Adapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.mcontext = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public Scheme_List_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        Scheme_List_Adapter.MyViewHolder vh = new Scheme_List_Adapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final JSONObject jsonObject1 = jsonObject.get(position);
        final JSONObject jsonObject = new JSONObject();
        mActivity = (MainActivity)mcontext;
        mSession = AppSession.getInstance(mcontext);
        holder.name.setText(jsonObject1.optString("SchemeName"));
        holder.cat.setText(jsonObject1.optString("Category"));

        /*holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mcontext, holder.name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/


        holder.ly_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    jsonObject.put("SchName", jsonObject1.optString("SchemeName"));
                    jsonObject.put("Scode", jsonObject1.optString("SchemeCode"));
                    jsonObject.put("Fcode", jsonObject1.optString("FCode"));
                    jsonObject.put("Exlcode", jsonObject1.optString("ExlCode"));

                }catch (Exception e){

                }


                Bundle bundle = new Bundle();
                bundle.putString("passkey", AppSession.getInstance(mcontext).getPassKey());
                bundle.putString("excl_code", jsonObject1.optString("ExlCode"));
                bundle.putString("bid", AppConstants.APP_BID);
                bundle.putString("scheme", jsonObject1.optString("SchemeName"));
                bundle.putString("type", "scheme");
                bundle.putString("object", jsonObject.toString());


                mActivity.displayViewOther(42, bundle);


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
        TextView name, cat;
        LinearLayout ly_item;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            cat = view.findViewById(R.id.name_cat);
            ly_item = view.findViewById(R.id.search_item);

        }
    }
}
