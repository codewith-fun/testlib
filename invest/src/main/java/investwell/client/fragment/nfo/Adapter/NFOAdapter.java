package investwell.client.fragment.nfo.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.nfo.Fragments.FragNewFundOffers;
import investwell.client.fragment.nfo.Fragments.FragOpen;
import investwell.utils.AppSession;

public class NFOAdapter extends RecyclerView.Adapter<NFOAdapter.ViewHolder> {

    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private AppSession mSession;
    private FragNewFundOffers mFrag;
    MainActivity mActivity;


    public NFOAdapter(Context context, ArrayList<JSONObject> mDataList, FragOpen frag,MainActivity mActivity) {
        this.mDataList = mDataList;
        this.mActivity = mActivity;
        this.mContext = context;
        mSession = AppSession.getInstance(mContext);
        this.mFrag = (FragNewFundOffers) frag.getParentFragment();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nfo_item_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final JSONObject object = mDataList.get(position);
        viewHolder.mSchemeName.setText(object.optString("SchemeName"));
        viewHolder.mInvestmentValue.setText(object.optString("LSMinimumAmount"));
        Picasso.get().load(object.optString("AMCLogo")).into(viewHolder.mLogo);

        if (object.optString("NFOStatus").equalsIgnoreCase("Current")){
            viewHolder.mCart.setVisibility(View.VISIBLE);
            viewHolder.mInvestmentBy.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            viewHolder.mInvestmentBy.setText(object.optString("CloseDate"));
            viewHolder.mNfoDate.setText("Close Date");
        }else{
            viewHolder.mCart.setVisibility(View.GONE);
            viewHolder.mInvestmentBy.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            viewHolder.mInvestmentBy.setText(object.optString("OpenDate"));
            viewHolder.mNfoDate.setText("Open Date");
        }


        if (mFrag.mSelectedCartsList.size() > 0) {
            for (int j = 0; j < mFrag.mSelectedCartsList.size(); j++) {
                JSONObject cartObject = mFrag.mSelectedCartsList.get(j);
                if (cartObject.optString("Exlcode").equals(object.optString("Exlcode"))) {
                    viewHolder.mCart.setImageResource(R.mipmap.cart_done);
                    break;
                } else {
                    viewHolder.mCart.setImageResource(R.mipmap.add_cart);
                    viewHolder.mCart.setEnabled(true);
                }
            }

        } else {
            viewHolder.mCart.setEnabled(true);
            viewHolder.mCart.setImageResource(R.mipmap.add_cart);
        }
        viewHolder.mCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandleCart(object,viewHolder);

            }
        });
        if (!TextUtils.isEmpty(investwell.utils.Utils.getConfigData(mSession).optString("NFOAddToCart")) &&
                investwell.utils.Utils.getConfigData(mSession).optString("NFOAddToCart").equalsIgnoreCase("Y") &&(object.optString("NFOStatus").equalsIgnoreCase("Current"))) {
            viewHolder.mCart.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mCart.setVisibility(View.GONE);
        }

        String videoUrl = object.optString("VideoLink");
        if (videoUrl.length()>0) {
            viewHolder.ivVideoPlay.setVisibility(View.VISIBLE);
        }else {
            viewHolder.ivVideoPlay.setVisibility(View.GONE);
        }

        viewHolder.ivVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Exlcode",object.optString("Exlcode"));
                bundle.putString("SchemeName",object.optString("SchemeName"));
                mActivity.displayViewOther(123,bundle);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public void updatelist(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mSchemeName, mInvestmentValue, mInvestmentBy,mNfoDate;
        ImageView mCart, mLogo, ivVideoPlay;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCart = itemView.findViewById(R.id.nfo_cart);
            mSchemeName = itemView.findViewById(R.id.tv_schemename);
            mInvestmentValue = itemView.findViewById(R.id.tv_investment);
            mInvestmentBy = itemView.findViewById(R.id.tv_investmentBy);
            mNfoDate=itemView.findViewById(R.id.nfo_date);
            mLogo = itemView.findViewById(R.id.logo);
            ivVideoPlay = itemView.findViewById(R.id.ivVideoPlay);
        }
    }

    private void HandleCart(JSONObject object, ViewHolder viewHolder) {

        try {
            JSONObject cartobject = new JSONObject();
            cartobject.put("SchName", object.optString("SchemeName"));
            cartobject.put("Scode", object.optString("Scode"));
            cartobject.put("Fcode", object.optString("Fcode"));
            cartobject.put("Exlcode", object.optString("Exlcode"));
            if (mSession.getAddToNFOCartList().contains(cartobject.optString("Exlcode"))) {
                viewHolder.mCart.setImageResource(R.mipmap.add_cart);
                for (int i = 0; i < mFrag.mSelectedCartsList.size(); i++) {
                    JSONObject jsonObject1 = mFrag.mSelectedCartsList.get(i);
                    if (jsonObject1.optString("Exlcode").matches(cartobject.optString("Exlcode"))) {
                        mFrag.mSelectedCartsList.remove(mFrag.mSelectedCartsList.get(i));
                    }
                }
            } else {
                viewHolder.mCart.setImageResource(R.mipmap.cart_done);
                mFrag.mSelectedCartsList.add(cartobject);
                mFrag.mTvCart.setVisibility(View.VISIBLE);
            }

            mSession.setAddToNFOCartList(mFrag.mSelectedCartsList.toString());
            mFrag.fragToolBar.updateNFOCart(true);
            mFrag.fragToolBar.updateCart(false);
            if (mFrag.mSelectedCartsList.size() == 0) {
                mFrag.mTvCart.setVisibility(View.INVISIBLE);
            } else {
                mFrag.mTvCart.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

