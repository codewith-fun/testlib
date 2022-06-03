package investwell.client.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;

public class IndividualPortfolioAdapter extends RecyclerView.Adapter<IndividualPortfolioAdapter.MyViewHolder> {


    private ArrayList<JSONObject> jsonObject1;
    private String CID;
    private AppSession mSession;
    Context context;
    private String Name;
    private MainActivity mAvtivity;

    public IndividualPortfolioAdapter(Context context, ArrayList<JSONObject> jsonObject1, String CID) {

        this.context = context;
        this.jsonObject1 = jsonObject1;
        this.CID = CID;
        mSession = AppSession.getInstance(context);
    }

    @NonNull
    @Override
    public IndividualPortfolioAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_individual_portfolio, parent, false);

        IndividualPortfolioAdapter.MyViewHolder vh = new IndividualPortfolioAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(IndividualPortfolioAdapter.MyViewHolder holder, final int position) {

        mAvtivity = (MainActivity) context;
        final JSONObject jsonObject = jsonObject1.get(position);

        mSession = AppSession.getInstance(context);

        DecimalFormat myFormatter = new DecimalFormat("##,##,###.00");
        DecimalFormat decimalFormat = new DecimalFormat("##.00");
        if (jsonObject.optString("Gain").contains("-")) {
            holder.tvGain.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.tvGain.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_down, 0);
        } else {
            holder.tvGain.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.tvGain.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_up, 0);

        }
        if (jsonObject.optString("CAGR").contains("-")) {
            holder.tvReturn.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.tvReturn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_down, 0);
        } else {
            holder.tvReturn.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.tvReturn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.menu_up, 0);
        }


        holder.tvFolio.setVisibility(View.VISIBLE);
        holder.tvSchemeName.setText(jsonObject.optString("SchemeName"));
        holder.tvPurchaseCost.setText(context.getString(R.string.Rs) + jsonObject.optString("InitialValue"));
        holder.tvMarketValue.setText(context.getString(R.string.Rs) + jsonObject.optString("CurrentValue"));
        String input = jsonObject.optString("Gain");
        boolean isFound = input.indexOf("-") != -1 ? true : false; //true
        if (isFound) {
            input = input.substring(0, 1) + context.getString(R.string.Rs) + " " + input.substring(1, input.length());
            holder.tvGain.setText(input);
        } else {
            holder.tvGain.setText(context.getString(R.string.Rs) + " " + jsonObject.optString("Gain"));
        }


        holder.tvReturn.setText(jsonObject.optString("CAGR") + "%");
        holder.tvFolio.setText(jsonObject.optString("FolioNo") + " " + "(" + "Folio" + ")");
        final Bundle bundle = new Bundle();


        holder.tvSchemeName.setOnClickListener(new View.OnClickListener() {
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
                if (!jsonObject.optString("Objective").equalsIgnoreCase("Debt FD")) {
                    mAvtivity.displayViewOther(47, bundle);
                }


            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

                if (!jsonObject.optString("Objective").equalsIgnoreCase("Debt FD") &&
                        !jsonObject.optString("Objective").equalsIgnoreCase("Equity Shares")) {
                    mAvtivity.displayViewOther(42, bundle);
                }

            }
        });
        holder.tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("UCC", jsonObject.optString("UCC"));
                bundle.putString("Bid", AppConstants.APP_BID);
                bundle.putString("Fcode", jsonObject.optString("FCode"));
                bundle.putString("Scode", jsonObject.optString("SCode"));
                bundle.putString("FolioNo", jsonObject.optString("FolioNo"));
                bundle.putString("ExcelCode", jsonObject.optString("Exlcode"));
                bundle.putString("Passkey", mSession.getPassKey());
                bundle.putString("applicant_name", Name);
                bundle.putString("colorBlue", jsonObject.optString("SchemeName"));
                bundle.putString("purchase_cost", jsonObject.optString("Unit"));
                bundle.putString("market_position", jsonObject.optString("CurrentValue"));
                mAvtivity.displayViewOther(30, bundle);
            }
        });

        holder.tvRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("UCC", jsonObject.optString("UCC"));
                bundle.putString("Bid", AppConstants.APP_BID);
                bundle.putString("Fcode", jsonObject.optString("FCode"));
                bundle.putString("Scode", jsonObject.optString("SCode"));
                bundle.putString("FolioNo", jsonObject.optString("FolioNo"));
                bundle.putString("ExcelCode", jsonObject.optString("Exlcode"));
                bundle.putString("Passkey", mSession.getPassKey());
                bundle.putString("applicant_name", Name);
                bundle.putString("colorBlue", jsonObject.optString("SchemeName"));
                bundle.putString("purchase_cost", jsonObject.optString("Unit"));
                bundle.putString("market_position", jsonObject.optString("CurrentValue"));
                mAvtivity.displayViewOther(33, bundle);

            }
        });
        holder.tvPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionChooser(jsonObject, bundle);
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

    public void showTransactionChooser(final JSONObject jsonObject, final Bundle bundle) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mAvtivity);
        LayoutInflater inflater = (LayoutInflater) mAvtivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_trans_chooser, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        TextView tvTitle = dialogView.findViewById(R.id.tv_header_question);
        TextView tvMessage = dialogView.findViewById(R.id.tv_lumpsum);
        TextView tvSIP = dialogView.findViewById(R.id.tv_sip);
        ImageView ivLumpsum = dialogView.findViewById(R.id.iv_lumpsum);
        ImageView ivSip = dialogView.findViewById(R.id.iv_sip);
        tvTitle.setText("How would you like to invest ?");
        tvMessage.setText("Lumpsum");
        tvSIP.setText("SIP");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        ivLumpsum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                bundle.putString("UCC", jsonObject.optString("UCC"));
                bundle.putString("Bid", AppConstants.APP_BID);
                bundle.putString("Fcode", jsonObject.optString("FCode"));
                bundle.putString("Scode", jsonObject.optString("SCode"));
                bundle.putString("FolioNo", jsonObject.optString("FolioNo"));
                bundle.putString("ExcelCode", jsonObject.optString("Exlcode"));
                bundle.putString("Passkey", mSession.getPassKey());
                bundle.putString("applicant_name", Name);
                bundle.putString("colorBlue", jsonObject.optString("SchemeName"));
                bundle.putString("purchase_cost", jsonObject.optString("Unit"));
                bundle.putString("market_position", jsonObject.optString("CurrentValue"));
                mAvtivity.displayViewOther(28, bundle);
            }
        });
        ivSip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                bundle.putString("UCC", jsonObject.optString("UCC"));
                bundle.putString("Bid", AppConstants.APP_BID);
                bundle.putString("Fcode", jsonObject.optString("FCode"));
                bundle.putString("Scode", jsonObject.optString("SCode"));
                bundle.putString("FolioNo", jsonObject.optString("FolioNo"));
                bundle.putString("ExcelCode", jsonObject.optString("Exlcode"));
                bundle.putString("Passkey", mSession.getPassKey());
                bundle.putString("applicant_name", Name);
                bundle.putString("colorBlue", jsonObject.optString("SchemeName"));
                bundle.putString("purchase_cost", jsonObject.optString("Unit"));
                bundle.putString("market_position", jsonObject.optString("CurrentValue"));
                mAvtivity.displayViewOther(29, bundle);
            }
        });


        alertDialog.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSchemeName, tvPurchaseCost, tvMarketValue, tvGain, tvReturn, tvFolio, tvPurchase, tvSwitch, tvRedeem;


        public MyViewHolder(View view) {
            super(view);

            tvSchemeName = view.findViewById(R.id.tv_scheme_name);
            tvPurchaseCost = view.findViewById(R.id.tv_purchase_cost_value);
            tvMarketValue = view.findViewById(R.id.tv_market_value);
            tvGain = view.findViewById(R.id.textView38);
            tvReturn = view.findViewById(R.id.tv_return_value);
            tvFolio = view.findViewById(R.id.tv_folio_no);
            tvPurchase = view.findViewById(R.id.tv_purchase);
            tvRedeem = view.findViewById(R.id.tv_redeem);
            tvSwitch = view.findViewById(R.id.tv_switch);


        }
    }
}
