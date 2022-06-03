package investwell.client.fragment.help;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.fragment.nfo.Adapter.NFOAdapter;
import investwell.client.fragment.nfo.Fragments.FragNewFundOffers;
import investwell.client.fragment.nfo.Fragments.FragOpen;
import investwell.utils.AppSession;

public class ContactUsAdapter extends RecyclerView.Adapter<ContactUsAdapter.ViewHolder> {

    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private AppSession mSession;



    public ContactUsAdapter(Context context, ArrayList<JSONObject> mDataList, FragHelpContactus frag) {
        this.mDataList = mDataList;
        this.mContext = context;
        mSession = AppSession.getInstance(mContext);

    }



    @NonNull
    @Override
    public ContactUsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_help_contactus_item, viewGroup, false);
        return new ContactUsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactUsAdapter.ViewHolder viewHolder, int position) {

        final ArrayList<JSONObject> list = new ArrayList<>();
        final JSONObject jsonObject1 = mDataList.get(position);

        String advisoryType = jsonObject1.optString("AdvisoryType" );
        if ( advisoryType.equals("R")){
            viewHolder.tvSebiLabel.setText("SEBI Registration Details");
        }
        if ( advisoryType.equals("B")){
            viewHolder.tvSebiLabel.setText("AMFI Registration Details");
        }

        if (advisoryType.equals("") || advisoryType.equalsIgnoreCase("null")){
            viewHolder.ll_sebilabel.setVisibility(View.GONE);
        }

        if (!jsonObject1.optString("InvestmentAdvisorName").isEmpty()) {
            viewHolder.ll_BokerName.setVisibility(View.VISIBLE);
            viewHolder.tvBrokerName.setText(jsonObject1.optString("InvestmentAdvisorName"));
        }

        if (!jsonObject1.optString("SEBIRegNo").isEmpty()) {
            viewHolder.ll_SEBIRegNo.setVisibility(View.VISIBLE);
            viewHolder.tvSEBIRegNo.setText(jsonObject1.optString("SEBIRegNo"));
        }
        if (!jsonObject1.optString("AdvisoryDivision").isEmpty()) {
            viewHolder.ll_AdvisoryDivision.setVisibility(View.VISIBLE);
            viewHolder.tvAdvisoryDivision.setText(jsonObject1.optString("AdvisoryDivision"));
        }
        if (!jsonObject1.optString("RegistrationType").isEmpty()) {
            viewHolder.ll_RegistrationType.setVisibility(View.VISIBLE);
            viewHolder.tvRegistrationType.setText(jsonObject1.optString("RegistrationType"));
        }

        if (!jsonObject1.optString("RegistrationValidation").isEmpty()) {
            viewHolder.ll_RegistrationValidation.setVisibility(View.VISIBLE);
            viewHolder.tvRegistrationValidation.setText(jsonObject1.optString("RegistrationValidation"));
        }
        if (!jsonObject1.optString("Address").isEmpty()) {
            viewHolder.ll_Address.setVisibility(View.VISIBLE);
            viewHolder.tvAddress.setText(jsonObject1.optString("Address"));
        }

        if (!jsonObject1.optString("ContactNo").isEmpty()) {
            viewHolder.ll_ContactNo.setVisibility(View.VISIBLE);
            viewHolder.tvContactNo.setText(jsonObject1.optString("ContactNo"));
        }

        if (!jsonObject1.optString("ContactPersonName").isEmpty()) {
            viewHolder.ll_ContactPersonName.setVisibility(View.VISIBLE);
            viewHolder.tvContactPersonName.setText(jsonObject1.optString("ContactPersonName"));
        }

        if (!jsonObject1.optString("ContactPersonNo").isEmpty() || !jsonObject1.optString("ContactPersonEmail").isEmpty()) {
            viewHolder.ll_ContactPersonNo.setVisibility(View.VISIBLE);
            String contact = jsonObject1.optString("ContactPersonNo");
            contact+="; "+ jsonObject1.optString("ContactPersonEmail");
            viewHolder.tvContactPersonNo.setText(contact);
        }


        if (!jsonObject1.optString("SEBIAddress").isEmpty()) {
            viewHolder.ll_SEBIAddress.setVisibility(View.VISIBLE);
            viewHolder.tvSEBIAddress.setText(jsonObject1.optString("SEBIAddress"));
        }

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


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBrokerName, tvSEBIRegNo,tvAdvisoryDivision,tvRegistrationType,tvRegistrationValidation,tvAddress,tvContactNo,tvContactPersonName,tvContactPersonNo,tvSEBIAddress,tvSebiLabel;

        private LinearLayout ll_BokerName, ll_SEBIRegNo, ll_AdvisoryDivision, ll_RegistrationType, ll_RegistrationValidation, ll_Address,
                ll_ContactPersonName, ll_ContactNo, ll_ContactPersonNo,ll_SEBIAddress,ll_sebilabel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSebiLabel = itemView.findViewById(R.id.tvSebiLabel);
            tvBrokerName = itemView.findViewById(R.id.tvBrokerName);
            tvSEBIRegNo = itemView.findViewById(R.id.tvSEBIRegNo);
            tvAdvisoryDivision = itemView.findViewById(R.id.tvAdvisoryDivision);

            tvRegistrationType = itemView.findViewById(R.id.tvRegistrationType);
            tvRegistrationValidation = itemView.findViewById(R.id.tvRegistrationValidation);
            tvAddress = itemView.findViewById(R.id.tvAddress);

            tvContactNo = itemView.findViewById(R.id.tvContactNo);
            tvContactPersonName = itemView.findViewById(R.id.tvContactPersonName);
            tvContactPersonNo = itemView.findViewById(R.id.tvContactPersonNo);
            tvSEBIAddress = itemView.findViewById(R.id.tvSEBIAddress);

            ll_BokerName = itemView.findViewById(R.id.ll_BokerName);
            ll_SEBIRegNo = itemView.findViewById(R.id.ll_SEBIRegNo);
            ll_AdvisoryDivision = itemView.findViewById(R.id.ll_AdvisoryDivision);
            ll_RegistrationType = itemView.findViewById(R.id.ll_RegistrationType);
            ll_RegistrationValidation = itemView.findViewById(R.id.ll_RegistrationValidation);
            ll_Address = itemView.findViewById(R.id.ll_Address);
            ll_ContactNo = itemView.findViewById(R.id.ll_ContactNo);
            ll_ContactPersonName = itemView.findViewById(R.id.ll_ContactPersonName);
            ll_ContactPersonNo = itemView.findViewById(R.id.ll_ContactPersonNo);
            ll_SEBIAddress = itemView.findViewById(R.id.ll_SEBIAddress);
            ll_sebilabel = itemView.findViewById(R.id.ll_sebilabel);




        }
    }

}

