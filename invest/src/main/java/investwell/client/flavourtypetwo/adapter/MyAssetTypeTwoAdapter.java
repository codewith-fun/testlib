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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppSession;


public class MyAssetTypeTwoAdapter extends RecyclerView.Adapter<MyAssetTypeTwoAdapter.MyViewHolder> {
    private Context mContext;
    public ArrayList<JSONObject> listMyAsset;
    private MyAssetsClickListener investRouteListenerTypeTwo;
    private AppSession mSession;
    private View itemView;

    public MyAssetTypeTwoAdapter(Context context, ArrayList<JSONObject> list, MyAssetsClickListener investRouteListenersTypeTwo) {
        this.listMyAsset = list;
        mContext = context;
        investRouteListenerTypeTwo = investRouteListenersTypeTwo;
        mSession = AppSession.getInstance(mContext);
    }

    @NonNull
    @Override
    public MyAssetTypeTwoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_my_assets_type_two, parent, false);

        return new MyAssetTypeTwoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyAssetTypeTwoAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final JSONObject jsonObject = listMyAsset.get(position);
        if (!TextUtils.isEmpty(jsonObject.optString("AUM"))) {
            holder.tvMyAssetsAmount.setText(jsonObject.optString("AUM"));
        }
        if (!TextUtils.isEmpty(jsonObject.optString("Product"))) {
            holder.tvMyAssetTypes.setText(jsonObject.optString("Product"));
        }
        String productCat = jsonObject.optString("Product");
        if (productCat.contains("Mutual")) {
            holder.tvMyAssetTypes.setTextColor(mContext.getResources().getColor(R.color.colorMutualFund));
            holder.ivMyAssets.setImageResource(R.drawable.ic_my_assets_mf);
            /*   holder.ivMyAssets.setImageResource(R.mipmap.app_icon_r);*/
        } else if (productCat.contains("Equity")) {
            holder.ivMyAssets.setImageResource(R.drawable.ic_my_assets_equity);
            holder.tvMyAssetTypes.setTextColor(mContext.getResources().getColor(R.color.colorEquityShare));
            /* holder.ivMyAssets.setImageResource(R.mipmap.app_icon_r);*/
        } else if (productCat.contains("Life")) {
            holder.ivMyAssets.setImageResource(R.drawable.ic_my_assets_life_insurance);
            holder.tvMyAssetTypes.setTextColor(mContext.getResources().getColor(R.color.colorLifeInsurance));
            /*   holder.ivMyAssets.setImageResource(R.mipmap.app_icon_r);*/
        } else if (productCat.contains("General")) {
            holder.ivMyAssets.setImageResource(R.drawable.ic_my_assets_general_insurance);
            holder.tvMyAssetTypes.setTextColor(mContext.getResources().getColor(R.color.colorGeneralInsurance));
            /*   holder.ivMyAssets.setImageResource(R.mipmap.app_icon_r);*/
        } else if (productCat.contains("Fixed")) {
            holder.ivMyAssets.setImageResource(R.drawable.ic_my_assets_investment);
            holder.tvMyAssetTypes.setTextColor(mContext.getResources().getColor(R.color.colorFixedDeposit));
            /*   holder.ivMyAssets.setImageResource(R.mipmap.app_icon_r);*/
        } else if (productCat.contains("Other")) {
//            holder.ivMyAssets.setImageResource(R.drawable.ic_my_assets_investment);
            holder.tvMyAssetTypes.setTextColor(mContext.getResources().getColor(R.color.colorMutualFund));
            holder.ivMyAssets.setImageResource(R.drawable.ic_other_asset);
        }

        holder.cvMyAssets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                investRouteListenerTypeTwo.onMyAssetsClick(position);
            }
        });

    }
    public void updateList(List<JSONObject> list) {
        listMyAsset.clear();
        listMyAsset.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return listMyAsset.size();
    }

    public interface MyAssetsClickListener {
        void onMyAssetsClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMyAssetTypes, tvMyAssetsAmount;
        ImageView ivMyAssets;
        CardView cvMyAssets;


        public MyViewHolder(View view) {
            super(view);
            tvMyAssetsAmount = view.findViewById(R.id.tv_my_asset_return_value);
            tvMyAssetTypes = view.findViewById(R.id.tv_my_asset_return_type);
            ivMyAssets = view.findViewById(R.id.iv_my_asset);
            cvMyAssets = view.findViewById(R.id.cv_my_assets_container);
        }
    }
}
