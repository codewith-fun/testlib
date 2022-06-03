package investwell.client.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.activity.WebViewActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragMendateListAdapter extends RecyclerView.Adapter<FragMendateListAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;

    private Context mContext;
    private FragMendateListAdapter.OnItemClickListener listener;
    private AppSession mSession;
    private AppApplication mApplication;
    private MainActivity mActivity;
    String fileName = "";
    public FragMendateListAdapter(Context context, ArrayList<JSONObject> list, FragMendateListAdapter.OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);
        mActivity = (MainActivity)mContext;
        mApplication = (AppApplication) mActivity.getApplication();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_mendate_list_adapter, viewGroup, false);
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
        TextView tvBankName, tvAccount, tvAmount, tvMandateCode, tvStatus, tvDate, tvAccountType;
        ImageView mIvNatch, mIvUpload;
        LinearLayout mLvUpload;
        CardView cServices;

        public ViewHolder(View view) {
            super(view);
            tvBankName = view.findViewById(R.id.tvBankName);
            tvAccount = view.findViewById(R.id.tvAccount);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvMandateCode = view.findViewById(R.id.tvMandateCode);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvDate = view.findViewById(R.id.tvDate);
            tvAccountType = view.findViewById(R.id.tvAccountType);
            cServices = view.findViewById(R.id.services_card);
            mLvUpload = view.findViewById(R.id.lvUpload);
            mIvUpload = view.findViewById(R.id.ivUpload);
            mIvNatch = view.findViewById(R.id.ivNach);

        }

        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            tvBankName.setText(jsonObject.optString("BankName"));
            tvAccount.setText("A/c No. " + jsonObject.optString("AccountNo"));
            tvAmount.setText(mContext.getString(R.string.Rs)+jsonObject.optString("Amount"));
            tvMandateCode.setText(jsonObject.optString("MandateCode"));
            tvAccountType.setText(jsonObject.optString("AccountType"));

            if (!jsonObject.optString("ApprovedDate").equals(""))
                tvDate.setText(mContext.getString(R.string.valid_txt)+jsonObject.optString("ApprovedDate"));
            else
                tvDate.setText(mContext.getString(R.string.valid_txt)+jsonObject.optString("EndDate"));

            if (jsonObject.optString("MandateType").equalsIgnoreCase("N")){
                mIvNatch.setVisibility(View.VISIBLE);
            }else{
                mIvNatch.setVisibility(View.GONE);
            }
            if (jsonObject.optString("MandateType").equalsIgnoreCase("X")) {
                mLvUpload.setVisibility(View.VISIBLE);
            }else{
                mLvUpload.setVisibility(View.GONE);
            }

            cServices.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
            mIvNatch.setOnClickListener(view -> verifyNatch());

            mLvUpload.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("UCC", jsonObject.optString("UCC"));
                bundle.putString("UniqueRefNo", jsonObject.optString("UniqueRefNo"));
                bundle.putString("MandateID", jsonObject.optString("MandateCode"));
                mActivity.displayViewOther(116, bundle);
            });
        }

        //------------------verifyNatch----------------------------
        private void verifyNatch(){
            DialogsUtils.showProgressBar(mContext,false);
            String url = Config.ENach;
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
                jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
                jsonObject.put("UCC",mSession.getUCC_CODE());
                jsonObject.put("OnlineOption",mSession.getAppType());
                jsonObject.put("MandateCode",tvMandateCode.getText().toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DialogsUtils.hideProgressBar();

                        if (response.optString("Status").equalsIgnoreCase("True")){

                           Intent mIntent = new Intent(mContext, WebViewActivity.class);
                            mIntent.putExtra("title", "E-NACH");
                            mIntent.putExtra("url", response.optString("ServiceMSG"));
                            mActivity.startActivity(mIntent);

                        }else{
                            mApplication.showCommonDailog(mActivity, mContext, false, mContext.getString(R.string.fatca_instruction), response.optString("ServiceMSG"), "message",false,true);

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogsUtils.hideProgressBar();
                    }
                });
                 jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });

                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                requestQueue.add(jsonObjectRequest);
            }catch (Exception e){
                e.printStackTrace();
            }


        }

    }


}
