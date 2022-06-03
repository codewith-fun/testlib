package investwell.client.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.schemes.FragBucket;
import investwell.client.fragment.schemes.FragRecomendedAddToCart;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextViewBold;
import investwell.utils.customView.CustomTextViewRegular;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shruti on 8/5/18.
 */

public class RecycleBucketAddToCartAdapter extends RecyclerView.Adapter<RecycleBucketAddToCartAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private FragRecomendedAddToCart mFragBasketAddToCart;
    private Context mContext;
    private RecycleBucketAddToCartAdapter.OnItemClickListener listener;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;

    public RecycleBucketAddToCartAdapter(Context context, FragBucket fragBucket, ArrayList<JSONObject> list, RecycleBucketAddToCartAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);
        mFragBasketAddToCart = ((FragRecomendedAddToCart) fragBucket.getParentFragment());


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_bucket_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position, listener);


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bucket_icon, ivAddToCart;
        CustomTextViewRegular schemeNmae;
        CustomButton tvAddLog;
        CustomTextViewBold yearOne, yearTwo, yearThree;

        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.iv_scheme_logo);
            schemeNmae = view.findViewById(R.id.tv_scheme_name);
            yearOne = view.findViewById(R.id.yearOne);
            yearTwo = view.findViewById(R.id.yearTwo);
            yearThree = view.findViewById(R.id.yearThree);
            ivAddToCart = view.findViewById(R.id.iv_add_to_cart);

        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            try {
                String image_path = jsonObject.optString("AMCLogo");
                schemeNmae.setText(jsonObject.optString("SchName"));
                yearOne.setText(String.format("%.2f", jsonObject.optDouble("Performance1Year")));
                yearTwo.setText(String.format("%.2f", jsonObject.optDouble("Performance3Year")));
                yearThree.setText(String.format("%.2f", jsonObject.optDouble("Performance5Year")));


                if (mFragBasketAddToCart.mSelectedCartsList.size() > 0) {
                    for (int i = 0; i < mFragBasketAddToCart.mSelectedCartsList.size(); i++) {
                        JSONObject cartObject = mFragBasketAddToCart.mSelectedCartsList.get(i);
                        if (cartObject.optString("Exlcode").equals(jsonObject.optString("Exlcode"))) {

                            ivAddToCart.setEnabled(false);
                            ivAddToCart.setImageResource(R.mipmap.cart_done);
                        } else {
                            ivAddToCart.setImageResource(R.mipmap.add_cart);
                            ivAddToCart.setEnabled(true);
                        }
                    }

                } else {

                    ivAddToCart.setImageResource(R.mipmap.add_cart);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position);
                    }
                });

                ivAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ivAddToCart.setEnabled(false);
                        mFragBasketAddToCart.mSelectedCartsList.add(jsonObject);
                        mFragBasketAddToCart.mTvCart.setVisibility(View.VISIBLE);
                        mFragBasketAddToCart.mTvCart.setText("" + mFragBasketAddToCart.mSelectedCartsList.size());
                        mSession.setAddToCartList(mFragBasketAddToCart.mSelectedCartsList.toString());
                        ivAddToCart.setImageResource(R.mipmap.cart_done);


                    }
                });


                Picasso.get().load(image_path).placeholder(R.mipmap.knowledge_area)
                        .error(R.mipmap.knowledge_area).into(bucket_icon);
            } catch (Exception e) {


            }
        }


    }


}
