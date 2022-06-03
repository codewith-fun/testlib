package investwell.client.fragment.portfolio;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Utils;

public class AdapterPortfolio extends RecyclerView.Adapter<AdapterPortfolio.MyViewHolder> {


    Context context;
    private ArrayList<JSONObject> jsonObjects;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private View v;

    public AdapterPortfolio(Context context, ArrayList<JSONObject> jsonObjects) {

        this.context = context;
        this.jsonObjects = jsonObjects;
        mSession=AppSession.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1C")) {

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_portfolio_one_c, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_portfolio, parent, false);
        }

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        mActivity = (MainActivity) context;
        mApplication = (AppApplication) mActivity.getApplication();
        final JSONObject jsonObject = jsonObjects.get(position);
        mSession = AppSession.getInstance(context);

        if (jsonObject.optString("Gain").contains("-")) {
            holder.gain.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.gain_arrow.setBackgroundResource(R.drawable.menu_down);
        } else {
            holder.gain.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.gain_arrow.setBackgroundResource(R.drawable.menu_up);

        }
        if (jsonObject.optString("CAGR").contains("-")) {
            holder.cagr.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.cagr_arrow.setBackgroundResource(R.drawable.menu_down);
        } else {
            holder.cagr.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.cagr_arrow.setBackgroundResource(R.drawable.menu_up);
        }

        holder.applicant_name.setText(jsonObject.optString("ApplicantName"));
        holder.purchase_cost.setText(context.getString(R.string.Rs) + jsonObject.optString("InitialVal"));
        holder.market_value.setText(context.getString(R.string.Rs) + jsonObject.optString("CurrentVal"));
        String input = jsonObject.optString("Gain");
        boolean isFound = input.indexOf("-") != -1 ? true : false; //true
        if (isFound) {
            input = input.substring(0, 1) + context.getString(R.string.Rs)+" " + input.substring(1, input.length());
            holder.gain.setText(input);
        }else{
            holder.gain.setText(context.getString(R.string.Rs)+" "+jsonObject.optString("Gain"));
        }

        holder.cagr.setText(jsonObject.optString("CAGR") + "%");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("applicant_name", jsonObject.optString("ApplicantName"));
                bundle.putString("cid", jsonObject.optString(AppConstants.CUSTOMER_ID));
                bundle.putString("purchase_cost", jsonObject.optString("InitialVal"));
                bundle.putString("market_position", jsonObject.optString("CurrentVal"));
                bundle.putString("gain", jsonObject.optString("Gain"));
                bundle.putString("cagr", jsonObject.optString("CAGR"));
                bundle.putString("passkey", mSession.getPassKey());
                bundle.putString("bid", AppConstants.APP_BID);
                mActivity.displayViewOther(46, bundle);
                /*          mApplication.showSnackBar(v,mActivity.getResources().getString(R.string.work_under_development));*/

            }
        });


    }

    @Override
    public int getItemCount() {

        return jsonObjects.size();
    }

    public void updateList(List<JSONObject> list) {

        jsonObjects.clear();
        jsonObjects.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView applicant_name, purchase_cost, market_value, gain, cagr;
        ImageView next_arrow;
        ImageView gain_arrow, cagr_arrow;



        public MyViewHolder(View view) {
            super(view);

            applicant_name = view.findViewById(R.id.applicant_name);
            purchase_cost = view.findViewById(R.id.purchase_cost);
            market_value = view.findViewById(R.id.tv_market_value);
            gain = view.findViewById(R.id.gain);
            cagr = view.findViewById(R.id.cagr);
            next_arrow = view.findViewById(R.id.next_arrow);
            gain_arrow = view.findViewById(R.id.gain_arrow);
            cagr_arrow = view.findViewById(R.id.cagr_arrow);


        }
    }
}
