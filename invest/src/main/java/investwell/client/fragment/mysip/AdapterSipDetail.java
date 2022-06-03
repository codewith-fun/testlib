package investwell.client.fragment.mysip;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterSipDetail extends RecyclerView.Adapter<AdapterSipDetail.MyViewHolder> {


    Context context;
    private ArrayList<JSONObject> schemeDetailList;
    private JSONObject schemeJson;
    private String mcomingFrom;


    public AdapterSipDetail(Context context, ArrayList<JSONObject> jsonObject, String comingFrom) {
        this.context = context;
        this.schemeDetailList = jsonObject;
        this.mcomingFrom = comingFrom;

    }

    @Override
    public AdapterSipDetail.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sip, parent, false);


        AdapterSipDetail.MyViewHolder vh = new AdapterSipDetail.MyViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(final AdapterSipDetail.MyViewHolder holder, final int position) {
        schemeJson = schemeDetailList.get(position);
        holder.tvSchemeValue.setText(!TextUtils.isEmpty(schemeJson.optString("SchemeName")) ? schemeJson.optString("SchemeName") : "N/A");
        holder.tvFolioValue.setText(!TextUtils.isEmpty(schemeJson.optString("Foliono")) ? schemeJson.optString("Foliono") : "N/A");
      //  holder.tvInstallmentValue.setText(!TextUtils.isEmpty(schemeJson.optString("StartDate")) ? schemeJson.optString("StartDate") : "N/A");
        holder.tvEndDateValue.setText(!TextUtils.isEmpty(schemeJson.optString("EndDate")) ? schemeJson.optString("EndDate") : "N/A");
        holder.tvSipValue.setText(!TextUtils.isEmpty(schemeJson.optString("Amount")) ? schemeJson.optString("Amount") : "N/A");
        holder.tvSchemeValue.setVisibility(View.VISIBLE);
        holder.llRowTwo.setVisibility(View.VISIBLE);
        holder.dividendTwo.setVisibility(View.VISIBLE);
        holder.tvEndDatePlaceholder.setVisibility(View.VISIBLE);
        holder.tvEndDateValue.setVisibility(View.VISIBLE);
        holder.rlMiddleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mcomingFrom.equalsIgnoreCase("systemeticTransaction")) {
                    // showHelpDialog();
                }
            }
        });

        if (mcomingFrom.equalsIgnoreCase("systemeticTransaction")){
            holder.tv_installment_placeholder.setText(R.string.date);
            holder.tvInstallmentValue.setText(!TextUtils.isEmpty(schemeJson.optString("TranDate")) ? schemeJson.optString("TranDate") : "N/A");

            holder.tvEndDatePlaceholder.setVisibility(View.GONE);
            holder.tvEndDateValue.setVisibility(View.GONE);
        }else{
            holder.tvInstallmentValue.setText(!TextUtils.isEmpty(schemeJson.optString("StartDate")) ? schemeJson.optString("StartDate") : "N/A");

        }
    }

    @Override
    public int getItemCount() {

        return schemeDetailList.size();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showHelpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_help, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.setCancelable(true);
        alertDialog.show();


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSchemeValue, tvFolioValue, tvInstallmentValue, tvEndDateValue, tvSipValue, tvEndDatePlaceholder,tv_installment_placeholder;
        private RelativeLayout rlMiddleView;
        private LinearLayout llRowTwo;
        private View dividendTwo;

        public MyViewHolder(View view) {
            super(view);
            tv_installment_placeholder = view.findViewById(R.id.tv_installment_placeholder);

            tvEndDatePlaceholder = view.findViewById(R.id.tv_endDate_placeholder);
            tvEndDateValue = view.findViewById(R.id.tv_endDate_value);
            tvSchemeValue = view.findViewById(R.id.tv_scheme_value);
            tvFolioValue = view.findViewById(R.id.tv_folio_value);
            tvInstallmentValue = view.findViewById(R.id.tv_installment_value);
            tvSipValue = view.findViewById(R.id.tv_sip_value);
            rlMiddleView=view.findViewById(R.id.rl_middle_view);
            llRowTwo=view.findViewById(R.id.ll_row_two);
            dividendTwo=view.findViewById(R.id.dividend_two);
        }
    }
}
