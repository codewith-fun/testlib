package investwell.client.fragment.InvestInExistingSchemes.Adpter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.InvestInExistingSchemes.Fragment.ExistingInvestmentSchemeFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;


public class ExistingInvestmentAdapter extends RecyclerView.Adapter<ExistingInvestmentAdapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;
    ExistingInvestmentSchemeFragment mexistingInvestmentSchemeFragment;
    private AppSession mSession;

    public ExistingInvestmentAdapter(Context context, ArrayList<JSONObject> jsonObject, ExistingInvestmentSchemeFragment existingInvestmentSchemeFragment) {

        this.context = context;
        this.jsonObject = jsonObject;
        this.mexistingInvestmentSchemeFragment = existingInvestmentSchemeFragment;

    }

    @Override
    public ExistingInvestmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.existing_investment_item, parent, false);
        ExistingInvestmentAdapter.MyViewHolder vh = new ExistingInvestmentAdapter.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ExistingInvestmentAdapter.MyViewHolder holder, final int position) {
        mSession = AppSession.getInstance(context);
      final MainActivity mActivity = (MainActivity)context;
        final JSONObject jsonObject1 = jsonObject.get(position);
        holder.mSchemeName.setText(jsonObject1.optString("SchemeName"));
        holder.mName.setText(jsonObject1.optString("FirstName"));
        holder.mInvestment.setText(context.getString(R.string.Rs)+jsonObject1.optString("InvestedAmount"));
        holder.mFolio.setText(jsonObject1.optString("FolioNo"));



        if (mexistingInvestmentSchemeFragment.mSelectedCartsList.size() > 0) {
            for (int j = 0; j < mexistingInvestmentSchemeFragment.mSelectedCartsList.size(); j++) {
                JSONObject cartObject = mexistingInvestmentSchemeFragment.mSelectedCartsList.get(j);
                if (jsonObject1.optString("Exlcode").equalsIgnoreCase(cartObject.optString("Exlcode"))&&
                        jsonObject1.optString("FolioNo").replaceAll("\\\\","").equalsIgnoreCase(cartObject.optString("FolioNo").replaceAll("\\\\",""))) {
                    //  holder.mIvCart.setEnabled(false);
                    holder.mIvCart.setImageResource(R.mipmap.cart_done);
                    break;
                } else {
                    holder.mIvCart.setImageResource(R.mipmap.add_cart);
                    // holder.mIvCart.setEnabled(true);
                }

            }

        } else {
            //  holder.mIvCart.setEnabled(true);
            holder.mIvCart.setImageResource(R.mipmap.add_cart);
        }

       final Bundle bundle = new Bundle();
        bundle.putString("Passkey",mSession.getPassKey());
        bundle.putString("Bid", AppConstants.APP_BID);
        bundle.putString("applicant_name", jsonObject1.optString("FirstName"));
        bundle.putString("UCC", jsonObject1.optString("UCC"));
        bundle.putString("Fcode", jsonObject1.optString("Fcode"));
        bundle.putString("Scode", jsonObject1.optString("Scode"));
        bundle.putString("ExcelCode", jsonObject1.optString("Exlcode"));
        bundle.putString("FolioNo", jsonObject1.optString("FolioNo"));
        bundle.putString("colorBlue", jsonObject1.optString("SchemeName"));
        bundle.putString("purchase_cost", jsonObject1.optString("InitialValue"));
        bundle.putString("market_position", jsonObject1.optString("CurrentValue"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.purchase_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.findViewById(R.id.tvLumpsum).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.displayViewOther(28,bundle );
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.tvSip).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.displayViewOther(29,bundle);
                        dialog.dismiss();

                    }
                });

                dialog.setCancelable(true);
                dialog.show();


            }
        });


      /*  holder.mIvCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject cartobject = new JSONObject();
                    cartobject.put("SchName", jsonObject1.optString("SchemeName"));
                    cartobject.put("Scode", jsonObject1.optString("Scode"));
                    cartobject.put("Fcode", jsonObject1.optString("Fcode"));
                    cartobject.put("Exlcode", jsonObject1.optString("Exlcode"));
                    cartobject.put("FolioNo",jsonObject1.optString("FolioNo"));
                  //  cartobject.put("Applicant",jsonObject1.optString(""))


                    if (mSession.getAddToCartList().contains(cartobject.optString("Exlcode"))&&
                            mSession.getAddToCartList().replaceAll("\\\\","").contains(cartobject.optString("FolioNo").replaceAll("\\\\",""))) {
                        // Toast.makeText(mContext, "Successfully Removed", Toast.LENGTH_SHORT).show();
                        holder.mIvCart.setImageResource(R.mipmap.add_cart);
                        for (int i = 0; i < mexistingInvestmentSchemeFragment.mSelectedCartsList.size(); i++) {
                            JSONObject jsonObject1 = mexistingInvestmentSchemeFragment.mSelectedCartsList.get(i);
                            if ((jsonObject1.optString("Exlcode").matches(cartobject.optString("Exlcode")))
                                    && (jsonObject1.optString("FolioNo").matches(cartobject.optString("FolioNo")))) {
                                mexistingInvestmentSchemeFragment.mSelectedCartsList.remove(mexistingInvestmentSchemeFragment.mSelectedCartsList.get(i));
                            }
                        }

                    } else {
                        //  Toast.makeText(mContext, "Successfully Added", Toast.LENGTH_SHORT).show();
                        holder.mIvCart.setImageResource(R.mipmap.cart_done);
                        mexistingInvestmentSchemeFragment.mSelectedCartsList.add(cartobject);

                    }

                    //   mexistingInvestmentSchemeFragment.mTvCart.setText("" + mexistingInvestmentSchemeFragment.mSelectedCartsList.size());
                    mSession.setAddToCartList(mexistingInvestmentSchemeFragment.mSelectedCartsList.toString());
                    mexistingInvestmentSchemeFragment.fragToolBar.updateCart(true);


                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });*/


    }

    @Override
    public int getItemCount() {

        return jsonObject.size();

    }

    public void  updateList(List<JSONObject> list) {

        jsonObject.clear();
        jsonObject.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView mSchemeName,mInvestment,mFolio,mName;
        ImageView mIvCart;


        public MyViewHolder(View view) {
            super(view);

            mSchemeName = view.findViewById(R.id.tv_schemename);
            mName = view.findViewById(R.id.tv_name);
            mInvestment = view.findViewById(R.id.tv_investment);
            mFolio = view.findViewById(R.id.tv_folio);
            mIvCart = view.findViewById(R.id.cart);



        }
    }
}
