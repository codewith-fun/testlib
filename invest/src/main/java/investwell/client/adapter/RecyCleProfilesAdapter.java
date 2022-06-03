package investwell.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shruti on 8/5/18.
 */

public class RecyCleProfilesAdapter extends RecyclerView.Adapter<RecyCleProfilesAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private String comingFrom;
    private Context mContext;
    private OnItemClickListener listener;
    private String mType = "";
    private AppSession mSession ;
    public RecyCleProfilesAdapter(Context context, ArrayList<JSONObject> list, String type, OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mType = type;
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        // a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(500);
        v.startAnimation(a);
    }


    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        // a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(1000);
        v.startAnimation(a);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_profile_adapter, viewGroup, false);
       mSession= AppSession.getInstance(mContext);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.setItem(position, listener);
        if (comingFrom.equalsIgnoreCase("show_only_profiles")) {
            viewHolder.ivArrow.setVisibility(View.GONE);
            viewHolder.ivUserProfile.setVisibility(View.VISIBLE);

            /*viewHolder.cvParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.LinerProfileItems.getVisibility() == View.VISIBLE) {
                        collapse(viewHolder.LinerProfileItems);
                        viewHolder.ivArrow.setBackgroundResource(R.mipmap.ic_down_arrow);
                        viewHolder.LinerProfileItems.setVisibility(View.GONE);
                        viewHolder.divider.setVisibility(View.GONE);

                    } else {
                        viewHolder.ivArrow.setBackgroundResource(R.mipmap.ic_up_arrow);
                        expand(viewHolder.LinerProfileItems);
                        viewHolder.LinerProfileItems.setVisibility(View.VISIBLE);
                        viewHolder.divider.setVisibility(View.VISIBLE);

                    }
                }
            });*/
        } else {
            viewHolder.ivArrow.setVisibility(View.GONE);
            viewHolder.ivUserProfile.setVisibility(View.GONE);


        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list, String type) {
        mDataList.clear();
        mDataList.addAll(list);
        comingFrom = type;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(int position,View view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bucket_icon, ivArrow,ivDot,ivUserProfile,ivmyOrderReport;
        LinearLayout tvAOF, tvCheque, tvMandate, tvFatca, llMyOrders, llProfileDetail;
        TextView tvMyOrder, tvUCC, tvName,tvStatus, tvSIP, tvLumsumm, tvHolding, tvJointName;
        LinearLayout LinerProfileItems,llParent;
        RelativeLayout mrlCard;
        View divider;
        CardView cvParent;

        public ViewHolder(View view) {
            super(view);
            bucket_icon = view.findViewById(R.id.bucket_icon);
            tvName = view.findViewById(R.id.tvName);
            ivDot = view.findViewById(R.id.ivDot);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvUCC = view.findViewById(R.id.tvUCC);
            tvLumsumm = view.findViewById(R.id.tvLumsumm);
            tvSIP = view.findViewById(R.id.tvSIP);
            ivArrow = view.findViewById(R.id.ivArrow);
            ivmyOrderReport = view.findViewById(R.id.ivmyOrderReport);
            ivUserProfile = view.findViewById(R.id.ivUserProfile);
            tvMandate = view.findViewById(R.id.tvMandate);
            tvMyOrder = view.findViewById(R.id.tvMyOrder);
            divider = view.findViewById(R.id.divider);
            LinerProfileItems = view.findViewById(R.id.LinerProfileItems);
            tvFatca = view.findViewById(R.id.tvFatca);
            llParent=view.findViewById(R.id.ll_card_parent);
            tvHolding = view.findViewById(R.id.tvHolding);
            tvAOF = view.findViewById(R.id.tvAOF);
            cvParent = view.findViewById(R.id.cv_parent);
            llProfileDetail = view.findViewById(R.id.profile_detail);
            tvCheque = view.findViewById(R.id.tvCheque);
            llMyOrders = view.findViewById(R.id.ll_order);
            tvJointName = view.findViewById(R.id.tvJointName);
            mrlCard = view.findViewById(R.id.rlCard);


        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            final Bundle bundle = new Bundle();
            bundle.putString("ucc_code", jsonObject.optString("UCC"));
            bundle.putString("comming_from", "profile_list");
            if (jsonObject.optString("ActiveStatus").equalsIgnoreCase("N")){
                ivDot.setBackgroundResource(R.drawable.circle_image_red);
            }else{
                ivDot.setBackgroundResource(R.drawable.circle_image_green);
            }
            // String image_path = mDataList.optString("ImageURL");
            tvName.setText(jsonObject.optString("InvestorName"));
            tvUCC.setText(" " + "|" + "  " + jsonObject.optString("UCC"));
            tvHolding.setText(jsonObject.optString("HoldingType") + "  ");

            String jointName1 = jsonObject.optString("Joint1Name");
            if (jointName1.isEmpty()){
                tvJointName.setVisibility(View.GONE);
            }else {
                tvJointName.setVisibility(View.VISIBLE);
                tvJointName.setText(jointName1 + " " + jsonObject.optString("Joint2Name"));
            }


            if (jsonObject.optString("DocUploadStatus").equalsIgnoreCase("Y")) {

                tvAOF.setEnabled(false);
                tvCheque.setEnabled(true);

                tvAOF.setVisibility(View.GONE);
                tvCheque.setVisibility(View.VISIBLE);

            } else {

                tvAOF.setEnabled(true);
                tvCheque.setEnabled(true);

                tvAOF.setVisibility(View.VISIBLE);
                tvCheque.setVisibility(View.VISIBLE);

            }



            /*if (jsonObject.optString("FatcaStatus").equalsIgnoreCase("Y")) {
                tvFatca.setEnabled(false);
                tvFatca.setVisibility(View.GONE);
            } else {

                tvFatca.setEnabled(true);
                tvFatca.setVisibility(View.VISIBLE);
            }*/

            final MainActivity mainActivity = (MainActivity) mContext;
            tvMandate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mainActivity.displayViewOther(37, bundle);

                   // mainActivity.displayViewOther(105, bundle);
                }
            });

            llMyOrders.setVisibility(View.GONE);
            ivmyOrderReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("InvestorName",jsonObject.optString("InvestorName"));
                    bundle.putString("ucc_code", jsonObject.optString("UCC"));
                    mainActivity.displayViewOther(110, bundle);
                }
            });

            tvFatca.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ProfileDetail(jsonObject.optString("UCC"));
                }
            });
            llProfileDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("coming_from","Investment Profile");
                    bundle.putString("ucc_code", jsonObject.optString("UCC"));
                    mainActivity.displayViewOther(82, bundle);
                }
            });
         /*   ivArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (LinerProfileItems.getVisibility() == View.VISIBLE) {
                        collapse(LinerProfileItems);
                        ivArrow.setBackgroundResource(R.mipmap.ic_down_arrow);
                        LinerProfileItems.setVisibility(View.GONE);
                        divider.setVisibility(View.GONE);

                    } else {
                        ivArrow.setBackgroundResource(R.mipmap.ic_up_arrow);
                        expand(LinerProfileItems);
                        LinerProfileItems.setVisibility(View.VISIBLE);
                        divider.setVisibility(View.VISIBLE);

                    }

                }
            });*/

            ivUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bundle.putString("InvestorName",jsonObject.optString("InvestorName"));
                    bundle.putString("allData",jsonObject.toString());
                    bundle.putString("cid",jsonObject.optString("Cid"));
                    bundle.putString("ucc_code", jsonObject.optString("UCC"));
                    mainActivity.displayViewOther(16,bundle);
                }
            });


            tvCheque.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                   /* bundle.putString("ucc_code", jsonObject.optString("UCC"));
                    mainActivity.displayViewOther(54, bundle);*/

                    MainActivity mActivity = (MainActivity)mContext;
                    bundle.putString("AllData", jsonObject.toString());
                    bundle.putString("coming_from","profile_list_cheque");
                    mActivity.displayViewOther(94, bundle);
                }
            });
            tvAOF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                   /* Intent intent = new Intent(mContext, SignatureActivity.class);
                    intent.putExtra("ucc_code", jsonObject.optString("UCC"));
                    intent.putExtra("comming_from", "profile_list");
                    intent.putExtra("chequeRequired", "NA");
                    ((Activity) mContext).startActivityForResult(intent, 100);*/

                   MainActivity mActivity = (MainActivity)mContext;
                    bundle.putString("AllData", jsonObject.toString());
                    bundle.putString("coming_from","profile_list_signature");
                    mActivity.displayViewOther(93, bundle);

                    //mActivity.displayViewOther(104, bundle);


                }
            });

            if (mType.equals("show_only_profiles")) {
                //  LinerProfileItems.setVisibility(View.VISIBLE);
                tvSIP.setVisibility(View.GONE);
                tvLumsumm.setVisibility(View.GONE);
                ivDot.setVisibility(View.VISIBLE);
                if (jsonObject.optString("ActiveStatus").equalsIgnoreCase("N")){
                    ivDot.setBackgroundResource(R.drawable.circle_image_red);
                    ivmyOrderReport.setVisibility(View.GONE);
                }else{
                    ivDot.setBackgroundResource(R.drawable.circle_image_green);
                    ivmyOrderReport.setVisibility(View.VISIBLE);
                }
            } else {
                ivDot.setVisibility(View.VISIBLE);
                cvParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position,llParent);
                    }
                });
                if(mSession.getAppType().equals(mContext.getResources().getString(R.string.apptype_n))|| mSession.getAppType().equalsIgnoreCase("DN")){
                    llMyOrders.setVisibility(View.GONE);
                    tvCheque.setVisibility(View.VISIBLE);
                }else {
                    llMyOrders.setVisibility(View.VISIBLE);
                    tvCheque.setVisibility(View.VISIBLE);
                }
                tvSIP.setVisibility(View.GONE);
                tvLumsumm.setVisibility(View.GONE);
                //   LinerProfileItems.setVisibility(View.GONE);

                if (mType.equals("transfer_holding")) {
                    tvSIP.setVisibility(View.GONE);
                    tvLumsumm.setVisibility(View.GONE);
                } else {


                    tvSIP.setVisibility(View.VISIBLE);
                    if (!jsonObject.optString("ActiveOrder").equals("0") && !jsonObject.optString("ActiveOrder").isEmpty()) {
                        tvSIP.setText("Pending payment of " + jsonObject.optString("ActiveOrder") + " active order");
                        tvSIP.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                    } else {
                        tvSIP.setText(mContext.getResources().getString(R.string.no_active_order));
                        tvSIP.setTextColor(Color.RED);
                        llMyOrders.setVisibility(View.GONE);
                    }
                    tvStatus.setVisibility(View.VISIBLE);
                    if (!mType.equals("brokerActivitySearch")) {
                        if (jsonObject.optString("ActiveStatus").equalsIgnoreCase("N")) {
                            tvStatus.setText("INACTIVE");
                            tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                            ivDot.setBackgroundResource(R.drawable.circle_image_red);
                            cvParent.setEnabled(false);
                            //   mrlCard.setBackgroundColor(mContext.getResources().getColor(R.color.colorGrey_200));
                        } else {
                            tvStatus.setText("ACTIVE");
                            tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                            ivDot.setBackgroundResource(R.drawable.circle_image_green);
                            cvParent.setEnabled(true);
                            //  mrlCard.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));

                        }
                    }




                   /* if (!mDataList.optString("SIPCart").equals("0")) {
                        tvSIP.setVisibility(View.VISIBLE);
                        if (mDataList.optString("SIPCart").equals("1"))
                            tvSIP.setText(mDataList.optString("SIPCart") + " Scheme are waiting for payment in SIP Cart");
                        else
                            tvSIP.setText(mDataList.optString("SIPCart") + " Schemes are waiting for payment in SIP Cart");
                    } else {
                        tvSIP.setVisibility(View.GONE);
                    }

                    if (!mDataList.optString("LSCart").equals("0")) {
                        tvLumsumm.setVisibility(View.VISIBLE);
                        if (mDataList.optString("LSCart").equals("1"))
                            tvLumsumm.setText(mDataList.optString("LSCart") + " Scheme are waiting for payment in Lumpsum Cart");
                        else
                            tvLumsumm.setText(mDataList.optString("LSCart") + " Schemes are waiting for payment in Lumpsum Cart");
                    } else {
                        tvLumsumm.setVisibility(View.GONE);
                    }*/
                }


            }


        }


    }

    public void ProfileDetail (final String Ucc){

        final MainActivity mainActivity = (MainActivity) mContext;
        try {
            String url = Config.Profile_Detail;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey",mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC",Ucc);
            jsonObject.put("OnlineOption",mSession.getAppType());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (response.optString("Status").equalsIgnoreCase("True")){

                        JSONObject jsonObject1 = response.optJSONObject("ProfileDetail");
                        Bundle bundle = new Bundle();
                        bundle.putString("ucc_code", Ucc);
                        bundle.putString("UserAllData",jsonObject1.toString());
                        mainActivity.displayViewOther(86, bundle);
                       /* if(mSession.getAppType().equalsIgnoreCase("B")|| mSession.getAppType().equalsIgnoreCase("DB")) {
                            mainActivity.displayViewOther(58, bundle);
                        }
                        else if(mSession.getAppType().equals(getResources().getString(R.string.apptype_n))|| mSession.getAppType().equalsIgnoreCase("DN")){
                            mainActivity.displayViewOther(86, bundle);
                        }*/
                    }else{
                        Toast.makeText(mContext, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(jsonObjectRequest);


        }catch (Exception e){
            e.printStackTrace();
        }



    }
}

