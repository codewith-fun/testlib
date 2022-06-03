package investwell.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.Utils;

public class DashboardTypeOneSliderAdapter extends RecyclerView.Adapter<DashboardTypeOneSliderAdapter.MyViewHolder> {
    private Context mContext;
    public ArrayList<JSONObject> sectionArrayList;
    private SliderTypeListener otherServicesListener;
    private AppSession mSession;
    private View itemView;

    public DashboardTypeOneSliderAdapter(Context context, ArrayList<JSONObject> list, SliderTypeListener otherServicesListeners) {
        this.sectionArrayList = list;
        mContext = context;
        otherServicesListener = otherServicesListeners;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public DashboardTypeOneSliderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_other_services_type_2b, parent, false);

        return new DashboardTypeOneSliderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DashboardTypeOneSliderAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final JSONObject jsonObject = sectionArrayList.get(position);
        holder.tvOtherTypes.setText(!TextUtils.isEmpty(jsonObject.optString("ChildTitle")) ? jsonObject.optString("ChildTitle") : "");

        String childType = "";
        String childCode = "";
        if (!TextUtils.isEmpty(jsonObject.optString("ChildType"))) {
            childType = jsonObject.optString("ChildType");
        }
        if (childType.equalsIgnoreCase("Module")) {
            if (!TextUtils.isEmpty(jsonObject.optString("ChildCode"))) {
                childCode = jsonObject.optString("ChildCode");
                if (childCode.equalsIgnoreCase("FundPicks")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                }

                if (childCode.equalsIgnoreCase("TopSchemes")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_top_performer);
                }
                if (childCode.equalsIgnoreCase("SearchAMC")) {


                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_amc);
                }
                if (childCode.equalsIgnoreCase("CalculatorRequired")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_top_performer);
                }

                if (childCode.equalsIgnoreCase("GoalModuleV2")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_goal);
                }
                if (childCode.equalsIgnoreCase("Accelator")) {
                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_expertise);
                }

                if (childCode.equalsIgnoreCase("NFO")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_nfo);
                }

                if (childCode.equalsIgnoreCase("UploadCAS")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_track_old);
                }
                if (childCode.equalsIgnoreCase("FlavourOfMonth")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_flavour);

                }  if (childCode.equalsIgnoreCase("TransferHolding")) {
                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                }  if (childCode.equalsIgnoreCase("SimplySave")) {

                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_simply_save);

                }  if (childCode.equalsIgnoreCase("JustSaveReq")) {
                    holder.ivOtherServices.setImageResource(R.mipmap.ic_invest_route_just_save);
                }
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                    if (childCode.equalsIgnoreCase("SIPCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_sip_1a);
                    }
                    if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                    }
                    if (childCode.equalsIgnoreCase("EducationCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_education_1a);
                    }
                    if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                    }
                    if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_retirement_1a);

                    }
                    if (childCode.equalsIgnoreCase("LumpsumCalculator")) {
                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);

                    }

                }else{
                    if (childCode.equalsIgnoreCase("SIPCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_sip);
                    }
                    if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                    }
                    if (childCode.equalsIgnoreCase("EducationCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_education);
                    }
                    if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_marriage);
                    }
                    if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_retirement);

                    }
                    if (childCode.equalsIgnoreCase("LumpsumCalculator")) {
                        holder.ivOtherServices.setImageResource(R.mipmap.ic_calculator_lumpsum);

                    }
                }

            }
        }
        if (childType.equalsIgnoreCase("Custom")) {
            if (!TextUtils.isEmpty(jsonObject.optString("ImageFile"))) {
                Picasso.get().load(jsonObject.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(holder.ivOtherServices);
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otherServicesListener.onOtherServicesClick(position);
            }
        });
    }

    public void updateSectionsTypeList(List<JSONObject> list) {

        sectionArrayList.clear();
        sectionArrayList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    public interface SliderTypeListener {
        void onOtherServicesClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvOtherTypes;
        ImageView ivOtherServices;
        LinearLayout llOtherServices;


        public MyViewHolder(View view) {
            super(view);
            tvOtherTypes = view.findViewById(R.id.tv_other_service_types);
            ivOtherServices = view.findViewById(R.id.iv_other_services);
            llOtherServices = view.findViewById(R.id.ll_other_services_container);
        }
    }
}

