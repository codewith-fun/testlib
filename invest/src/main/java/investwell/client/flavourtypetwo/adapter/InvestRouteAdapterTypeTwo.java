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

import com.iw.acceleratordemo.R;

import java.util.List;

import investwell.client.flavourtypetwo.model.InvestRouteTypeTwo;
import investwell.utils.AppSession;

public class InvestRouteAdapterTypeTwo extends RecyclerView.Adapter<InvestRouteAdapterTypeTwo.MyViewHolder> {
    private Context mContext;
    private List<InvestRouteTypeTwo> investMentRouteList;
    private InvestRouteListenerTypeTwo investRouteListenerTypeTwo;
    private AppSession mSession;
    private View itemView;

    public InvestRouteAdapterTypeTwo(Context context, List<InvestRouteTypeTwo> investMentRouteList, InvestRouteListenerTypeTwo investRouteListenersTypeTwo) {
        this.investMentRouteList = investMentRouteList;
        mContext = context;
        investRouteListenerTypeTwo = investRouteListenersTypeTwo;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public InvestRouteAdapterTypeTwo.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_invest_routes_type_two, parent, false);

        return new InvestRouteAdapterTypeTwo.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InvestRouteAdapterTypeTwo.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        InvestRouteTypeTwo routes = investMentRouteList.get(position);

        holder.tvRouteDesc.setText(!TextUtils.isEmpty(routes.getInvestRouteDesc()) ? routes.getInvestRouteDesc() : "");
        holder.tvHeader.setText(!TextUtils.isEmpty(routes.getInvestRouteName()) ? routes.getInvestRouteName() : "");
        holder.ivRouteIcon.setImageResource(routes.getIvRouteIcons());
        holder.llRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                investRouteListenerTypeTwo.onRoutesClick(position);
            }
        });
        int lastRow = investMentRouteList.size() - 1;
        if (position == lastRow) {
            holder.v_routes_divider.setVisibility(View.GONE);
        } else {
            holder.v_routes_divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return investMentRouteList.size();
    }

    public interface InvestRouteListenerTypeTwo {
        void onRoutesClick(int position);
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
