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

import com.iw.acceleratordemo.R;

import java.util.List;

import investwell.utils.AppSession;
import investwell.utils.Utils;
import investwell.utils.customView.CustomTextViewBold;
import investwell.utils.customView.CustomTextViewLight;
import investwell.utils.customView.CustomTextViewRegular;
import investwell.utils.model.InvestmentRoutes;

public class InvestmentRouteAdapter extends RecyclerView.Adapter<InvestmentRouteAdapter.MyViewHolder> {
    private Context mContext;
    private List<InvestmentRoutes> investMentRouteList;
    private InvestmentRouteListener investmentRouteListener;
    private AppSession mSession;
    private View itemView;

    public InvestmentRouteAdapter(Context context, List<InvestmentRoutes> investMentRouteList, InvestmentRouteListener investmentRouteListeners) {
        this.investMentRouteList = investMentRouteList;
        mContext = context;
        investmentRouteListener = investmentRouteListeners;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1A")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row_investment_route_two, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row_investment_routes, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        InvestmentRoutes routes = investMentRouteList.get(position);
        holder.tvRouteName.setText(!TextUtils.isEmpty(routes.getRouteName()) ? routes.getRouteName() : "");
        holder.tvRouteDesc.setText(!TextUtils.isEmpty(routes.getRouteDesc()) ? routes.getRouteDesc() : "");
        holder.tvHeader.setText(!TextUtils.isEmpty(routes.getRouteMiniHeader()) ? routes.getRouteMiniHeader() : "");
        holder.ivRouteIcon.setImageResource(routes.getRouteIcon());
        holder.llRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                investmentRouteListener.onRoutesClick(position);
            }
        });
        switch (position) {
            case 0:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("GoalModuleV2")) &&
                        Utils.getConfigData(mSession).optString("GoalModuleV2").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 1:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Accelator")) &&
                        Utils.getConfigData(mSession).optString("Accelator").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 2:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("FundPicks")) &&
                        Utils.getConfigData(mSession).optString("FundPicks").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 3:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("TopSchemes")) &&
                        Utils.getConfigData(mSession).optString("TopSchemes").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 4:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("SearchAMC")) &&
                        Utils.getConfigData(mSession).optString("SearchAMC").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 5:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("NFO")) &&
                        Utils.getConfigData(mSession).optString("NFO").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 6:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("FlavourOfMonth")) &&
                        Utils.getConfigData(mSession).optString("FlavourOfMonth").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 7:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("JustSaveReq")) &&
                        Utils.getConfigData(mSession).optString("JustSaveReq").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);

                }
                break;
            case 8:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("SimplySave")) &&
                        Utils.getConfigData(mSession).optString("SimplySave").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);
                }
                break;
            case 9:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("ServiceRequest")) &&
                        Utils.getConfigData(mSession).optString("ServiceRequest").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);
                }
                break;
            case 10:
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("UploadExistingCAS")) &&
                    Utils.getConfigData(mSession).optString("UploadExistingCAS").equalsIgnoreCase("Y")) {
                holder.llRoutes.setVisibility(View.VISIBLE);

            } else {
                holder.llRoutes.setVisibility(View.GONE);
            }

                break;
            case 11:
                if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("TransferHolding")) &&
                        Utils.getConfigData(mSession).optString("TransferHolding").equalsIgnoreCase("Y")) {
                    holder.llRoutes.setVisibility(View.VISIBLE);

                } else {
                    holder.llRoutes.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return investMentRouteList.size();
    }

    public interface InvestmentRouteListener {
        void onRoutesClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewRegular tvHeader;
        ImageView ivRouteIcon;
        LinearLayout llRoutes;
        CustomTextViewBold tvRouteName;
        private CustomTextViewRegular tvRouteDesc;

        public MyViewHolder(View view) {
            super(view);
            tvHeader = view.findViewById(R.id.tv_dashboard_route_header_desc);
            tvRouteDesc = view.findViewById(R.id.tv_dashboard_route_desc);
            tvRouteName = view.findViewById(R.id.tv_route);
            ivRouteIcon = view.findViewById(R.id.iv_dashboard_route_icon);
            llRoutes = view.findViewById(R.id.ll_route_container);
        }
    }
}
