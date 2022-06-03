package investwell.client.fragment.fundpicks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.google.android.material.card.MaterialCardView;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;


import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.fundpicks.fragments.FundPicksFragment;
import investwell.client.fragment.fundpicks.fragments.FragFundScheme;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static investwell.client.fragment.fundpicks.fragments.FundPicksFragment.mCatFunds;


public class FundItemSchemeAdapter extends RecyclerView.Adapter<FundItemSchemeAdapter.ViewHolder> {


    public ArrayList<JSONObject> mDataList;

    public HashMap<Integer, Boolean> mHashValue;
    private Context mContext;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private int mLastPosition = -1;
    private AppSession mSession;
    private FundPicksFragment mFragFund;

    public FundItemSchemeAdapter(ArrayList<JSONObject> mDataList, Context context, FragFundScheme fragFundScheme) {
        this.mDataList = mDataList;
        mContext = context;
        mHashValue = new HashMap<>();
        mFragFund = (FundPicksFragment) fragFundScheme.getParentFragment();
        mSession = AppSession.getInstance(mContext);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_fundscheme, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        final JSONObject dataobject = mDataList.get(i);
        mActivity = (MainActivity) mContext;
        final JSONObject jsonObject = new JSONObject();
        holder.schemename.setText(dataobject.optString("SchemeName"));
        Picasso.get().load(dataobject.optString("AMCLogo")).into(holder.mLogo);
        switch (mCatFunds) {
            case 0:
                holder.tv_value.setText(dataobject.optString("Return7Day"));
                break;
            case 1:
                holder.tv_value.setText(dataobject.optString("Return15Day"));
                break;
            case 2:
                holder.tv_value.setText(dataobject.optString("Return30Day"));
                break;
            case 3:
                holder.tv_value.setText(dataobject.optString("Return3Month"));
                break;
            case 4:
                holder.tv_value.setText(dataobject.optString("Return6Month"));
                break;
            case 5:
                holder.tv_value.setText(dataobject.optString("Return1Year"));
                break;
            case 6:
                holder.tv_value.setText(dataobject.optString("Return2Year"));
                break;
            case 7:
                holder.tv_value.setText(dataobject.optString("Return3Year"));
                break;
            case 8:
                holder.tv_value.setText(dataobject.optString("Return5Year"));
                break;
            case 9:
                holder.tv_value.setText(dataobject.optString("Return10Year"));
                break;
            default:
                holder.tv_value.setText(dataobject.optString("Return1Year"));
                break;
        }

        if (i % 2 == 1) {
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.cvParentFundScheme.setBackgroundColor(Color.parseColor("#000000"));
            }else{
                holder.cvParentFundScheme.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }

        } else {

            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 1D")) {
                holder.cvParentFundScheme.setBackgroundColor(Color.parseColor("#212121"));
            }else{
                holder.cvParentFundScheme.setBackgroundColor(Color.parseColor("#FAFAFA"));

            }

        }

        float value = Float.valueOf(String.valueOf(holder.tv_value.getText()));
        if (value > 0) {
            holder.tv_value.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
            holder.tv_value.setText(value + "%");
        } else if (value < 0) {
            holder.tv_value.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            holder.tv_value.setText(value + "%");
        } else {
            holder.tv_value.setText("---");
        }
        if (mFragFund.mSelectedCartsList.size() > 0) {
            for (int j = 0; j < mFragFund.mSelectedCartsList.size(); j++) {
                JSONObject cartObject = mFragFund.mSelectedCartsList.get(j);
                if (cartObject.optString("Exlcode").equals(dataobject.optString("Exlcode"))) {
                    holder.mCart.setEnabled(false);
                    holder.mCart.setImageResource(R.mipmap.cart_done);
                    break;
                } else {
                    holder.mCart.setEnabled(true);
                }
            }

        } else {
            holder.mCart.setEnabled(true);
        }

        holder.mCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject cartobject = new JSONObject();
                    cartobject.put("SchName", dataobject.optString("SchemeName"));
                    cartobject.put("Scode", dataobject.optString("Scode"));
                    cartobject.put("Fcode", dataobject.optString("Fcode"));
                    cartobject.put("Exlcode", dataobject.optString("Exlcode"));
//                    intent.putExtra("object", mDataList.toString())
                    if (mSession.getAddToCartList().contains(cartobject.optString("Exlcode"))) {
                        // Toast.makeText(mContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                        holder.mCart.setImageResource(R.mipmap.add_cart);
                        for (int i = 0; i < mFragFund.mSelectedCartsList.size(); i++) {
                            JSONObject jsonObject1 = mFragFund.mSelectedCartsList.get(i);
                            if (jsonObject1.optString("Exlcode").matches(cartobject.optString("Exlcode"))) {
                                mFragFund.mSelectedCartsList.remove(mFragFund.mSelectedCartsList.get(i));
                            }
                        }
                    } else {
                        //  Toast.makeText(mContext, "Successfully Added", Toast.LENGTH_SHORT).show();
                        holder.mCart.setImageResource(R.mipmap.cart_done);
                        mFragFund.mSelectedCartsList.add(cartobject);
                        mFragFund.mTvCart.setVisibility(View.VISIBLE);
                    }
                    mFragFund.mTvCart.setText("" + mFragFund.mSelectedCartsList.size());
                    mSession.setAddToCartList(mFragFund.mSelectedCartsList.toString());
                    mFragFund.fragToolBar.updateCart(true);
                    if (mFragFund.mSelectedCartsList.size() == 0) {
                        mFragFund.mTvCart.setVisibility(View.INVISIBLE);
                    } else {
                        mFragFund.mTvCart.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    System.out.println("Cart Error.   " + e);
                }

            }
        });


        holder.schemename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    jsonObject.put("SchName", dataobject.optString("SchemeName"));
                    jsonObject.put("Scode", dataobject.optString("Scode"));
                    jsonObject.put("Fcode", dataobject.optString("Fcode"));
                    jsonObject.put("Exlcode", dataobject.optString("Exlcode"));

                } catch (Exception e) {
e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putString("passkey", AppSession.getInstance(mContext).getPassKey());
                bundle.putString("excl_code", dataobject.optString("Exlcode"));
                bundle.putString("bid", AppConstants.APP_BID);
                bundle.putString("scheme", dataobject.optString("SchemeName"));
                bundle.putString("type", "scheme");
                bundle.putString("object", jsonObject.toString());
                mActivity.displayViewOther(42, bundle);

                //   context.startActivity(intent);

            }
        });
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("FundPicksAddToCart")) &&
                Utils.getConfigData(mSession).optString("FundPicksAddToCart").equalsIgnoreCase("Y")) {
            holder.mCart.setVisibility(View.VISIBLE);
        } else {
            holder.mCart.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView schemename, tv_value;

        ImageView mCart, mLogo;
        ConstraintLayout cvParentFundScheme;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            schemename = itemView.findViewById(R.id.tv_scheme_title);
            tv_value = itemView.findViewById(R.id.tv_default);
            mCart = itemView.findViewById(R.id.iv_top_scheme_add_cart);
            mLogo = itemView.findViewById(R.id.logo);
cvParentFundScheme=itemView.findViewById(R.id.cv_fund_picks);
        }
    }


    public void updatelist(ArrayList<JSONObject> itemlist) {
        mDataList.clear();
        mDataList.addAll(itemlist);
        for (int i = 0; i < mDataList.size(); i++) {
            mHashValue.put(i, false);
        }
        notifyDataSetChanged();
    }

}
