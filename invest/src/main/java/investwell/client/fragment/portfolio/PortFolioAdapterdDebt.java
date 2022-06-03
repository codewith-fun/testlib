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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Utils;

public class PortFolioAdapterdDebt extends RecyclerView.Adapter<PortFolioAdapterdDebt.MyViewHolder> {


    private ArrayList<JSONObject> jsonObject1;
    private String CID;
    private AppSession mSession;
    Context context;
    private String Name;
    private MainActivity mAvtivity;

    public PortFolioAdapterdDebt(Context context, ArrayList<JSONObject> jsonObject1, String CID) {

        this.context = context;
        this.jsonObject1 = jsonObject1;
        this.CID = CID;
        mSession = AppSession.getInstance(context);
    }

    @NonNull
    @Override
    public PortFolioAdapterdDebt.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1C")) {

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_portfolio_client_one_c, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_portfolio_client, parent, false);
        }
        PortFolioAdapterdDebt.MyViewHolder vh = new PortFolioAdapterdDebt.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PortFolioAdapterdDebt.MyViewHolder holder, final int position) {

        mAvtivity = (MainActivity) context;
        final JSONObject jsonObject = jsonObject1.get(position);

        mSession = AppSession.getInstance(context);

        DecimalFormat myFormatter = new DecimalFormat("##,##,###.00");
        DecimalFormat decimalFormat = new DecimalFormat("##.00");
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


        holder.folio.setVisibility(View.VISIBLE);
        holder.applicant_name.setText(jsonObject.optString("SchemeName"));
        holder.purchase_cost.setText(context.getString(R.string.Rs) + jsonObject.optString("InitialValue"));
        holder.market_value.setText(context.getString(R.string.Rs) + jsonObject.optString("CurrentValue"));
        String input = jsonObject.optString("Gain");
        boolean isFound = input.indexOf("-") != -1 ? true : false; //true
        if (isFound) {
            input = input.substring(0, 1) + context.getString(R.string.Rs) + " " + input.substring(1, input.length());
            holder.gain.setText(input);
        } else {
            holder.gain.setText(context.getString(R.string.Rs) + " " + jsonObject.optString("Gain"));
        }


        holder.cagr.setText(jsonObject.optString("CAGR") + "%");
        holder.folio.setText(jsonObject.optString("FolioNo"));
        final Bundle bundle = new Bundle();


        holder.top_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                bundle.putString("applicant_name", Name);
                bundle.putString("colorBlue", jsonObject.optString("SchemeName"));
                bundle.putString("cid", CID);
                bundle.putString("purchase_cost", jsonObject.optString("InitialValue"));
                bundle.putString("market_position", jsonObject.optString("CurrentValue"));
                bundle.putString("dividend", jsonObject.optString("Dividend"));
                bundle.putString("gain", jsonObject.optString("Gain"));
                bundle.putString("folio", jsonObject.optString("FolioNo"));
                bundle.putString("cagr", jsonObject.optString("CAGR"));
                bundle.putString("holding", jsonObject.optString("Holdingdays"));
                bundle.putString("unit", jsonObject.optString("Unit"));
                bundle.putString("absreturn", jsonObject.optString("AbsoluteReturn"));
                bundle.putString("fund_code", jsonObject.optString("FCode"));
                bundle.putString("scheme_code", jsonObject.optString("SCode"));
                bundle.putString("Exlcode", jsonObject.optString("Exlcode"));
                bundle.putString("Objective", jsonObject.optString("Objective"));
                bundle.putString("passkey", mSession.getPassKey());
                bundle.putString("bid", AppConstants.APP_BID);
                bundle.putString("UCC", jsonObject.optString("UCC"));
                if (!jsonObject.optString("Objective").equalsIgnoreCase("Debt FD")){
                    mAvtivity.displayViewOther(47, bundle);
                }



            }
        });

        holder.applicant_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                bundle.putString("applicant_name", Name);
                bundle.putString("colorBlue", jsonObject.optString("SchemeName"));
                bundle.putString("cid", CID);
                bundle.putString("purchase_cost", jsonObject.optString("InitialValue"));
                bundle.putString("market_position", jsonObject.optString("CurrentValue"));
                bundle.putString("gain", jsonObject.optString("Gain"));
                bundle.putString("folio", jsonObject.optString("FolioNo"));
                bundle.putString("cagr", jsonObject.optString("CAGR"));
                bundle.putString("holding", jsonObject.optString("Holdingdays"));
                bundle.putString("unit", jsonObject.optString("Unit"));
                bundle.putString("absreturn", jsonObject.optString("AbsoluteReturn"));
                bundle.putString("fund_code", jsonObject.optString("FCode"));
                bundle.putString("scheme_code", jsonObject.optString("SCode"));
                bundle.putString("Exlcode", jsonObject.optString("Exlcode"));
                bundle.putString("Objective", jsonObject.optString("Objective"));
                bundle.putString("passkey", mSession.getPassKey());
                bundle.putString("bid", AppConstants.APP_BID);
                bundle.putString("UCC", jsonObject.optString("UCC"));
                bundle.putString("excl_code", jsonObject.optString("Exlcode"));
                bundle.putString("colorBlue", jsonObject.optString("SchemeName"));
                bundle.putString("passkey", mSession.getPassKey());
                bundle.putString("Bid", AppConstants.APP_BID);
                bundle.putString("comming_from", "ic_bottombar_portfolio_inactive");
                bundle.putString("object", jsonObject.toString());

                if (!jsonObject.optString("Objective").equalsIgnoreCase("Debt FD")&&
                        !jsonObject.optString("Objective").equalsIgnoreCase("Equity Shares")){
                    mAvtivity.displayViewOther(42, bundle);
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return jsonObject1.size();
    }

    public void updateList(List<JSONObject> list, String name) {

        jsonObject1.clear();
        jsonObject1.addAll(list);
        Name = name;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView applicant_name, purchase_cost, market_value, gain, cagr, folio;
        ImageView next_arrow, gain_arrow, cagr_arrow;

        LinearLayout top_linear_layout;


        public MyViewHolder(View view) {
            super(view);

            applicant_name = view.findViewById(R.id.applicant_name);
            purchase_cost = view.findViewById(R.id.purchase_cost);
            market_value = view.findViewById(R.id.tv_market_value);
            gain = view.findViewById(R.id.gain);
            cagr = view.findViewById(R.id.cagr);
            folio = view.findViewById(R.id.folio);
            next_arrow = view.findViewById(R.id.next_arrow);
            gain_arrow = view.findViewById(R.id.gain_arrow);
            cagr_arrow = view.findViewById(R.id.cagr_arrow);
            top_linear_layout = view.findViewById(R.id.top_linear_layout);


        }
    }
}
