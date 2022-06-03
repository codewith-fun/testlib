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

public class DashboardTwoEquityItemAdapter extends RecyclerView.Adapter<DashboardTwoEquityItemAdapter.MyViewHolder> {
    private Context mContext;
    public ArrayList<JSONObject> sectionArrayList;
    private EquityItemListenerTypeTwo equityListener;
    private AppSession mSession;
    private View itemView;

    public DashboardTwoEquityItemAdapter(Context context, ArrayList<JSONObject> sectionArrayList, EquityItemListenerTypeTwo investRouteListenersTypeTwo) {
        this.sectionArrayList = sectionArrayList;
        mContext = context;
        equityListener = investRouteListenersTypeTwo;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public DashboardTwoEquityItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_invest_routes_type_two, parent, false);

        return new DashboardTwoEquityItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DashboardTwoEquityItemAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final JSONObject jsonObject = sectionArrayList.get(position);

        holder.tvHeader.setText(!TextUtils.isEmpty(jsonObject.optString("ChildTitle")) ? jsonObject.optString("ChildTitle") : "");

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

    public interface EquityItemListenerTypeTwo {
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
            tvHeader = view.findViewById(R.id.tv_home_element_header);
            tvRouteDesc = view.findViewById(R.id.tv_home_element_desc);
            ivRouteIcon = view.findViewById(R.id.iv_home_element);
            llRoutes = view.findViewById(R.id.ll_invest);
            v_routes_divider = view.findViewById(R.id.v_divider_routes);
        }
    }
}
