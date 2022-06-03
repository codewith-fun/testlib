package investwell.client.flavourtypetwo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import java.util.List;

import investwell.client.flavourtypetwo.model.ServicesTypeTwo;
import investwell.utils.AppSession;

public class HelpServicesAdapter extends RecyclerView.Adapter<HelpServicesAdapter.MyViewHolder> {
    private Context mContext;
    private List<ServicesTypeTwo> servicesTypeTwoList;
    private HelpServicesAdapter.InvestRouteListenerTypeTwo investRouteListenerTypeTwo;
    private AppSession mSession;
    private View itemView;

    public HelpServicesAdapter(Context context, List<ServicesTypeTwo> servicesTypeTwoList, HelpServicesAdapter.InvestRouteListenerTypeTwo investRouteListenersTypeTwo) {
        this.servicesTypeTwoList = servicesTypeTwoList;
        mContext = context;
        investRouteListenerTypeTwo = investRouteListenersTypeTwo;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public HelpServicesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_help_services, parent, false);

        return new HelpServicesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HelpServicesAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ServicesTypeTwo routes = servicesTypeTwoList.get(position);

        holder.tvServicesName.setText(!TextUtils.isEmpty(routes.getServiceName()) ? routes.getServiceName() : "");
        holder.ivServicesIcon.setImageResource(routes.getIvServices());
        holder.cv_my_services_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                investRouteListenerTypeTwo.onRoutesClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return servicesTypeTwoList.size();
    }

    public interface InvestRouteListenerTypeTwo {
        void onRoutesClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvServicesName;
        ImageView ivServicesIcon;
        CardView cv_my_services_container;

        public MyViewHolder(View view) {
            super(view);
            tvServicesName = view.findViewById(R.id.tv_services);
            ivServicesIcon = view.findViewById(R.id.iv_services);
            cv_my_services_container = view.findViewById(R.id.cv_my_services_container);
        }
    }
}

