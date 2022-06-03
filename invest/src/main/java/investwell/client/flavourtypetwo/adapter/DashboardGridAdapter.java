package investwell.client.flavourtypetwo.adapter;

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

public class DashboardGridAdapter extends RecyclerView.Adapter<DashboardGridAdapter.MyViewHolder> {
    private Context mContext;
    public ArrayList<JSONObject> sectionArrayList;
    private DashboardGridClickListener otherServicesListener;
    private AppSession mSession;
    private View itemView;

    public DashboardGridAdapter(Context context, ArrayList<JSONObject> list, DashboardGridClickListener otherServicesListeners) {
        this.sectionArrayList = list;
        mContext = context;
        otherServicesListener = otherServicesListeners;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public DashboardGridAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_grid_section, parent, false);

        return new DashboardGridAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DashboardGridAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
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
                otherServicesListener.onDashboardGridServicesClick(position);
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

    public interface DashboardGridClickListener {
        void onDashboardGridServicesClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvOtherTypes;
        ImageView ivOtherServices;
        LinearLayout llOtherServices;


        public MyViewHolder(View view) {
            super(view);
            tvOtherTypes = view.findViewById(R.id.tv_grid_service_types);
            ivOtherServices = view.findViewById(R.id.iv_grid_services);
            llOtherServices = view.findViewById(R.id.ll_other_services_container);
        }
    }
}

