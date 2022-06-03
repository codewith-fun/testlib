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

public class DashboardTypeOneCardAdapter extends RecyclerView.Adapter<DashboardTypeOneCardAdapter.MyViewHolder> {
    private Context mContext;
    public ArrayList<JSONObject> sectionArrayList;
    private EquityListenerTypeOne equityListener;
    private AppSession mSession;
    private View itemView;

    public DashboardTypeOneCardAdapter(Context context, ArrayList<JSONObject> sectionArrayList, EquityListenerTypeOne investRouteListenersTypeTwo) {
        this.sectionArrayList = sectionArrayList;
        mContext = context;
        equityListener = investRouteListenersTypeTwo;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public DashboardTypeOneCardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_investment_route_two, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row_investment_routes, parent, false) ;
        }


        return new DashboardTypeOneCardAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DashboardTypeOneCardAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final JSONObject jsonObject = sectionArrayList.get(position);

        holder.tvHeader.setText(!TextUtils.isEmpty(jsonObject.optString("ChildTitle")) ? jsonObject.optString("ChildTitle") : "");
        holder.tvRouteDesc.setText(!TextUtils.isEmpty(jsonObject.optString("ChildBrief")) ? jsonObject.optString("ChildBrief") : "");
        String childType = "";
        String childCode = "";
        if (!TextUtils.isEmpty(jsonObject.optString("ChildType"))) {
            childType = jsonObject.optString("ChildType");
        }
        if (childType.equalsIgnoreCase("Module")) {
            if (!TextUtils.isEmpty(jsonObject.optString("ChildCode"))) {
                childCode = jsonObject.optString("ChildCode");
                if (childCode.equalsIgnoreCase("FundPicks")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_fund_pick);
                }

                if (childCode.equalsIgnoreCase("TopSchemes")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                }
                if (childCode.equalsIgnoreCase("SearchAMC")) {


                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_amc);
                }
                if (childCode.equalsIgnoreCase("CalculatorRequired")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_top_performer);
                }

                if (childCode.equalsIgnoreCase("GoalModuleV2")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_goal);
                }
                if (childCode.equalsIgnoreCase("Accelator")) {
                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_expertise);
                }

                if (childCode.equalsIgnoreCase("NFO")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_nfo);
                }

                if (childCode.equalsIgnoreCase("UploadCAS")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_track_old);
                }
                if (childCode.equalsIgnoreCase("FlavourOfMonth")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_flavour);

                }
                if (childCode.equalsIgnoreCase("TransferHolding")) {
                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_transfer_holding);
                }
                if (childCode.equalsIgnoreCase("SimplySave")) {

                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_simply_save);

                }
                if (childCode.equalsIgnoreCase("JustSaveReq")) {
                    holder.ivRouteIcon.setImageResource(R.mipmap.ic_invest_route_just_save);
                }
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                        Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
                    if (childCode.equalsIgnoreCase("SIPCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip_1a);
                    }
                    if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip_1a);
                    }
                    if (childCode.equalsIgnoreCase("EducationCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education_1a);
                    }
                    if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage_1a);
                    }
                    if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement_1a);

                    }
                    if (childCode.equalsIgnoreCase("LumpsumCalculator")) {
                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum_1a);

                    }

                }else{
                    if (childCode.equalsIgnoreCase("SIPCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_sip);
                    }
                    if (childCode.equalsIgnoreCase("SIPDelayCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_cost_delay_sip);
                    }
                    if (childCode.equalsIgnoreCase("EducationCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_education);
                    }
                    if (childCode.equalsIgnoreCase("MarriageCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_marriage);
                    }
                    if (childCode.equalsIgnoreCase("RetirementCalculator")) {

                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_retirement);

                    }
                    if (childCode.equalsIgnoreCase("LumpsumCalculator")) {
                        holder.ivRouteIcon.setImageResource(R.mipmap.ic_calculator_lumpsum);

                    }
                }
            }
        }
        if (childType.equalsIgnoreCase("Custom")) {
            if (!TextUtils.isEmpty(jsonObject.optString("ImageFile"))) {
                Picasso.get().load(jsonObject.optString("ImageFile")).error(R.mipmap.profileplaceholder).into(holder.ivRouteIcon);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equityListener.onEquityItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionArrayList.size();
    }

    public interface EquityListenerTypeOne {
        void onEquityItemClick(int position);
    }

    public void updateSectionsTypeList(List<JSONObject> list) {

        sectionArrayList.clear();
        sectionArrayList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        ImageView ivRouteIcon;
        LinearLayout llRoutes;
        TextView tvRouteDesc;
        View v_routes_divider;

        public MyViewHolder(View view) {
            super(view);
            tvHeader = view.findViewById(R.id.tv_route);
            tvRouteDesc = view.findViewById(R.id.tv_dashboard_route_desc);
            ivRouteIcon = view.findViewById(R.id.iv_dashboard_route_icon);
          /*  llRoutes = view.findViewById(R.id.ll_invest);
            v_routes_divider = view.findViewById(R.id.v_divider_routes);*/
        }
    }
}
