package investwell.client.flavourTypeThree;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.nav.fragment.FragNavScheme;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;

public class NavSchemeSearchAdapter extends RecyclerView.Adapter<NavSchemeSearchAdapter.ViewHolder> {

    public ArrayList<JSONObject> mDataList;
    private MainActivity mActivity;
    private Context mContext;
    private AppSession mSession;
    private FragNavScheme mFrag;

    public NavSchemeSearchAdapter(Context context, ArrayList<JSONObject> mDataList, FragNavScheme frag) {
        this.mDataList = mDataList;
        this.mContext = context;
        mFrag = (FragNavScheme) frag;
        mSession = AppSession.getInstance(mContext);
        mActivity = (MainActivity) mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nav_scheme_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final JSONObject object = mDataList.get(position);
        viewHolder.mSchemeName.setText(object.optString("SchemeName"));
        viewHolder.mSchemeDate.setText(object.optString("NAVDate"));
        viewHolder.mSchemeValue.setText(object.optString("NAV"));
        Picasso.get().load(object.optString("AMCLogo")).into(viewHolder.mLogo);
  /*      if (position % 2 == 0) {
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrey_200));
        } else {
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        }*/

        if (mFrag.mSelectedCartsList.size() > 0) {
            for (int j = 0; j < mFrag.mSelectedCartsList.size(); j++) {
                JSONObject cartObject = mFrag.mSelectedCartsList.get(j);
                if (cartObject.optString("Exlcode").equals(object.optString("ExlCode"))) {
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
            public void onClick(View v) {

                try {
                    JSONObject cartobject = new JSONObject();
                    cartobject.put("SchName", object.optString("SchemeName"));
                    cartobject.put("Scode", object.optString("Scode"));
                    cartobject.put("Fcode", object.optString("Fcode"));
                    cartobject.put("Exlcode", object.optString("ExlCode"));
                    if (mSession.getAddToCartList().contains(cartobject.optString("Exlcode"))) {
                        // Toast.makeText(mContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                        viewHolder.mCart.setImageResource(R.mipmap.add_cart);
                        for (int i = 0; i < mFrag.mSelectedCartsList.size(); i++) {
                            JSONObject jsonObject1 = mFrag.mSelectedCartsList.get(i);
                            if (jsonObject1.optString("Exlcode").matches(cartobject.optString("Exlcode"))) {
                                mFrag.mSelectedCartsList.remove(mFrag.mSelectedCartsList.get(i));
                            }
                        }
                    } else {
                        //  Toast.makeText(mContext, "Successfully Added", Toast.LENGTH_SHORT).show();
                        viewHolder.mCart.setImageResource(R.mipmap.cart_done);
                        mFrag.mSelectedCartsList.add(cartobject);
                        mFrag.mTvCart.setVisibility(View.VISIBLE);
                    }

                    mFrag.mTvCart.setText("" + mFrag.mSelectedCartsList.size());
                    mSession.setAddToCartList(mFrag.mSelectedCartsList.toString());
                    if (mFrag.mSelectedCartsList.size() == 0) {
                        mFrag.mTvCart.setVisibility(View.INVISIBLE);
                    } else {
                        mFrag.mTvCart.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    System.out.println("Cart Error.   " + e);
                }

            }
        });


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("SchName", object.optString("SchemeName"));
                    jsonObject.put("Scode", object.optString("Scode"));
                    jsonObject.put("Fcode", object.optString("Fcode"));
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
        TextView mSchemeName, mSchemeValue, mSchemeDate;
        ImageView mCart, mLogo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCart = itemView.findViewById(R.id.nav_cart);
            mSchemeDate = itemView.findViewById(R.id.tv_schemedate);
            mSchemeValue = itemView.findViewById(R.id.tv_scheme_value);
            mSchemeName = itemView.findViewById(R.id.tv_schemename);
            mLogo = itemView.findViewById(R.id.logo);
        }
    }
}

