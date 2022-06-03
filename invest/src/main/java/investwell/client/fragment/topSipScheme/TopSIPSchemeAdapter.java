package investwell.client.fragment.topSipScheme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.topScheme.TopSchemeFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Utils;

public class TopSIPSchemeAdapter extends RecyclerView.Adapter<TopSIPSchemeAdapter.ViewHolder> {

    private static final String TAG = "SchemePerformanceadapte";

    public ArrayList<JSONObject> mDataList;
    public HashMap<Integer, Boolean> mHashValue;
    private Context mContext;
    private int mLastPosition = -1;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private TopSIPSchemeFragment mFragTopScheme;
    private AppSession mSession;


    public TopSIPSchemeAdapter(ArrayList<JSONObject> mDataList, Context context, TopSIPSchemeFragment fragTopScheme) {
        this.mDataList = mDataList;
        mContext = context;
        mHashValue = new HashMap<>();
        mSession = AppSession.getInstance(mContext);
        mActivity = (MainActivity) mContext;
        mFragTopScheme = (TopSIPSchemeFragment) fragTopScheme;
    }

    @NonNull
    @Override
    public TopSIPSchemeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_top_scheme, viewGroup, false);
        TopSIPSchemeAdapter.ViewHolder holder = new TopSIPSchemeAdapter.ViewHolder(view);
        return holder;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final TopSIPSchemeAdapter.ViewHolder holder, int position) {

        final JSONObject object = mDataList.get(position);
        final JSONObject jsonObject = new JSONObject();
        holder.tvTitle.setText(object.optString("SchemeName"));
/*        holder.tvDefault_yr.setText("(" + TopSchemeFragment.TopInvest + ")");*/
        Picasso.get().load(object.optString("AMCLogo")).into(holder.mLogo);
        String default_value = object.optString(TopSchemeFragment.mTopTime);


        if (mFragTopScheme.mSelectedCartsList.size() > 0) {
            for (int j = 0; j < mFragTopScheme.mSelectedCartsList.size(); j++) {
                JSONObject cartObject = mFragTopScheme.mSelectedCartsList.get(j);
                if (object.optString("ExlCode").equalsIgnoreCase(cartObject.optString("Exlcode"))) {
                    holder.mCart.setEnabled(false);
                    holder.mCart.setImageResource(R.mipmap.cart_done);
                    break;
                } else {
                    holder.mCart.setImageResource(R.mipmap.add_cart);
                    holder.mCart.setEnabled(true);
                }
                /*if (cartObject.optString("Exlcode").equals(object.optString("ExlCode"))) {
                    holder.mCart.setEnabled(false);
                    holder.mCart.setImageResource(R.mipmap.cart_done);
                } else {
                    holder.mCart.setImageResource(R.mipmap.add_cart);
                    holder.mCart.setEnabled(true);
                }*/
            }

        } else {
            holder.mCart.setEnabled(true);
            holder.mCart.setImageResource(R.mipmap.add_cart);
        }
        holder.mCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject cartobject = new JSONObject();
                    cartobject.put("SchName", object.optString("SchemeName"));
                    cartobject.put("Scode", object.optString("SchemeCode"));
                    cartobject.put("Fcode", object.optString("AMCCode"));
                    cartobject.put("Exlcode", object.optString("ExlCode"));
//                    intent.putExtra("object", mDataList.toString());


                    if (mSession.getAddToCartList().contains(cartobject.optString("Exlcode"))) {
                        // Toast.makeText(mContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                        holder.mCart.setImageResource(R.mipmap.add_cart);
                        for (int i = 0; i < mFragTopScheme.mSelectedCartsList.size(); i++) {
                            JSONObject jsonObject1 = mFragTopScheme.mSelectedCartsList.get(i);
                            if (jsonObject1.optString("Exlcode").matches(cartobject.optString("Exlcode"))) {
                                mFragTopScheme.mSelectedCartsList.remove(mFragTopScheme.mSelectedCartsList.get(i));
                            }
                        }
                    } else {
                        //  Toast.makeText(mContext, "Successfully Added", Toast.LENGTH_SHORT).show();
                        holder.mCart.setImageResource(R.mipmap.cart_done);
                        mFragTopScheme.mSelectedCartsList.add(cartobject);
                        mFragTopScheme.mTvCart.setVisibility(View.VISIBLE);
                    }

                    //   mFragTopScheme.mTvCart.setText("" + mFragTopScheme.mSelectedCartsList.size());
                    mSession.setAddToCartList(mFragTopScheme.mSelectedCartsList.toString());
                    mFragTopScheme.fragToolBar.updateCart(true);
                    if (mFragTopScheme.mSelectedCartsList.size() == 0) {
                        mFragTopScheme.mTvCart.setVisibility(View.INVISIBLE);
                    } else {
                        mFragTopScheme.mTvCart.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
e.printStackTrace();
                }
            }
        });
        /*    holder.ratingBar.setRating(Float.parseFloat(object.optString("Fundrate")));*/
        holder.tvDefault.setText(default_value + "%");

        if (Float.valueOf(default_value) > 0) {
            holder.tvDefault.setTextColor(Color.parseColor("#329D00"));
        } else if (Float.valueOf(default_value) < 0) {
            holder.tvDefault.setTextColor(Color.parseColor("#C91717"));
        } else {
            holder.tvDefault.setTextColor(Color.parseColor("#000000"));
        }


        holder.mCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    jsonObject.put("SchName", object.optString("SchemeName"));
                    jsonObject.put("Scode", object.optString("SchemeCode"));
                    jsonObject.put("Fcode", object.optString("AMCCode"));
                    jsonObject.put("Exlcode", object.optString("ExlCode"));

                } catch (Exception e) {

                }

                Bundle bundle = new Bundle();
                bundle.putString("passkey", AppSession.getInstance(mContext).getPassKey());
                bundle.putString("excl_code", object.optString("ExlCode"));
                bundle.putString("bid", AppConstants.APP_BID);
                bundle.putString("scheme", object.optString("SchemeName"));
                bundle.putString("type", "scheme");
                bundle.putString("object", jsonObject.toString());
                mActivity.displayViewOther(42, bundle);

                //   mContext.startActivity(intent);

            }
        });


        mLastPosition = position;
        if (position == 1) {
            holder.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Rect rect = new Rect();
                    holder.itemView.getGlobalVisibleRect(rect);
                }
            });
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("TopSchemeAddToCart")) &&
                Utils.getConfigData(mSession).optString("TopSchemeAddToCart").equalsIgnoreCase("Y")) {
            holder.llTopSchemeCart.setVisibility(View.VISIBLE);
        } else {
            holder.llTopSchemeCart.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updatelist(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        for (int i = 0; i < mDataList.size(); i++) {
            mHashValue.put(i, false);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout bottomLayout, llTopSchemeCart;
        ImageView downArrow;
        TextView tvTitle;
        ConstraintLayout mCardview;
        TextView tvCorpus, tvDefault, tvDefault_yr;
        /*  RatingBar ratingBar;*/
        TextView tvYr1, tvYr2, tvYr3, tvYr5, tvYr10, tvSi;
        TextView tvD15, tvD30, tvM3, tvM6, tv_title;
        ImageView mCart, mLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCart = itemView.findViewById(R.id.iv_top_scheme_add_cart);
            mLogo = itemView.findViewById(R.id.logo);
            mCardview = itemView.findViewById(R.id.cv_fund_picks);

            downArrow = itemView.findViewById(R.id.iv_down);
            tvTitle = itemView.findViewById(R.id.tv_title);

            /*    ratingBar = itemView.findViewById(R.id.ratingBar);*/
            tvDefault = itemView.findViewById(R.id.tv_default);


            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

}