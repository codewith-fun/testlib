package investwell.client.fragment.goalbased.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;

import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;
    private MainActivity mActivity;
private AppApplication mApplication;


    public SchemeAdapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.goalbased_scheme_item_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String rs="Rs";
        mActivity = (MainActivity) context;
        final JSONObject jsonObject1 = jsonObject.get(position);
        holder.colorBlue.setText(jsonObject1.optString("SchName"));

        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
    //   String strAmount = format.format(jsonObject1.optDouble("Amount"));
      //  String[] resultAmount = strAmount.split("\\.", 0);
        //holder.amount.setText(resultAmount[0]);

        String[] resultAmount = jsonObject1.optString("Amount").split("\\.", 0);
        holder.amount.setText("Rs."+resultAmount[0]);



        //     holder.amount.setText(jsonObject1.optString("Amount"));
//      String amount = holder.amount.getText().toString().replace(",", "").replace(context.getString(R.string.rs), "");
//        amount.replace(",", "").replace(context.getString(R.string.rs), "")
        holder.first_year_txt.setText(String.format("%.2f", jsonObject1.optDouble("Performance1Year")));
        holder.third_year_txt.setText(String.format("%.2f", jsonObject1.optDouble("Performance3Year")));
        holder.fifth_year_txt.setText(String.format("%.2f", jsonObject1.optDouble("Performance5Year")));
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

        private TextView colorBlue, amount, first_year_txt, third_year_txt, fifth_year_txt;


        public MyViewHolder(View view) {
            super(view);

            colorBlue = view.findViewById(R.id.colorBlue);
            amount = view.findViewById(R.id.amount);
            first_year_txt = view.findViewById(R.id.first_year_txt);
            third_year_txt = view.findViewById(R.id.third_year_txt);
            fifth_year_txt = view.findViewById(R.id.fifth_year_txt);


        }
    }
}


