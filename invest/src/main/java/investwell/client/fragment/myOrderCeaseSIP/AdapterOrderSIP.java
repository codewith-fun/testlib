package investwell.client.fragment.myOrderCeaseSIP;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class AdapterOrderSIP extends RecyclerView.Adapter<AdapterOrderSIP.ViewHolder> {
    public ArrayList<JSONObject> mDataList;
    private Context mContext;
    private OnItemClickListener listener;
    private AppApplication mApplication;
    private AppSession mSession;
    private RequestQueue requestQueue;
    private Bundle bundle;
    private MainActivity mMainActivity;

    public AdapterOrderSIP(Context context, ArrayList<JSONObject> list, OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        this.listener = listener;
        this.mMainActivity = (MainActivity) context;
        mSession = AppSession.getInstance(mContext);
        mApplication = (AppApplication) mContext.getApplicationContext();

        this.mMainActivity = (MainActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_my_order_sip, viewGroup, false);
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvestorValue, tv_scheme_value, tvAmount, tvUnits, tvStatus, tvOrderNo, tvIIN, tvDate, tv_UccIin_lable, tv_regdate, tv_unit_label;
        Button mCeaseBtn;
        LinearLayout cease_ll, regdate_ll;
        View v_divider;

        public ViewHolder(View view) {
            super(view);
            tv_scheme_value = view.findViewById(R.id.tv_scheme_value);
            tvInvestorValue = view.findViewById(R.id.tv_investor_value);
            tvAmount = view.findViewById(R.id.tv_amount);
            tvUnits = view.findViewById(R.id.tv_units);
            tv_unit_label = view.findViewById(R.id.unit_label);
            tvStatus = view.findViewById(R.id.tv_status);
            tvOrderNo = view.findViewById(R.id.tv_orderno);
            tvIIN = view.findViewById(R.id.tv_iin);
            tvDate = view.findViewById(R.id.tv_date);
            mCeaseBtn = view.findViewById(R.id.scheme_cease_btn);
            cease_ll = view.findViewById(R.id.cease_ll);
            tv_UccIin_lable = view.findViewById(R.id.uccIin_lable);

            v_divider= view.findViewById(R.id.v_divider);
            regdate_ll = view.findViewById(R.id.regdate_ll);
            tv_regdate = view.findViewById(R.id.tv_regdate);

        }

        public void setItem(final int position, final OnItemClickListener listener) {
            if (AppSession.getInstance(mContext).getAppType().equalsIgnoreCase("N") || AppSession.getInstance(mContext).getAppType().equalsIgnoreCase("DN")) {
                mCeaseBtn.setVisibility(View.GONE);
                cease_ll.setVisibility(View.GONE);
                tv_UccIin_lable.setText("IIN");
            } else {
                mCeaseBtn.setVisibility(View.VISIBLE);
                cease_ll.setVisibility(View.VISIBLE);
                tv_UccIin_lable.setText("UCC");
            }
            try {
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                format.setMinimumFractionDigits(0);
                format.setMaximumFractionDigits(0);
                final JSONObject jsonObject = mDataList.get(position);
                tvAmount.setText(jsonObject.optString("Amount"));
                if(jsonObject.has("TrxnStatus")) {
                    tvInvestorValue.setText(!TextUtils.isEmpty(jsonObject.optString("InvestorName")) ? jsonObject.optString("InvestorName") : "N/A");
                    tv_scheme_value.setText(!TextUtils.isEmpty(jsonObject.optString("FromScheme")) ? jsonObject.optString("FromScheme") : "N/A");
                    tvIIN.setText(jsonObject.optString("CustomerID"));
                    if(jsonObject.optString("TrxnStatus").equals("SIP-Processed")) {
                        tvStatus.setText(jsonObject.optString("TrxnStatus"));
                        cease_ll.setVisibility(View.VISIBLE);
                        mCeaseBtn.setVisibility(View.VISIBLE);
                    } else if(jsonObject.optString("TrxnStatus").equals("SIP-Matured"))
                        tvStatus.setText(jsonObject.optString("TrxnStatus"));
                    else if(jsonObject.optString("TrxnStatus").equals("SIP-Ceased"))
                        tvStatus.setText(jsonObject.optString("TrxnStatus"));

                    tvOrderNo.setText(jsonObject.optString("AutoTranxNo"));
                    tvDate.setText(jsonObject.optString("ToDate").substring(0, 11));
                    tvUnits.setVisibility(View.INVISIBLE);
                    tv_unit_label.setVisibility(View.INVISIBLE);
                    v_divider.setVisibility(View.VISIBLE);
                    regdate_ll.setVisibility(View.VISIBLE);
                    tv_regdate.setText(jsonObject.optString("FromDate"));

                } else {
                    tvUnits.setVisibility(View.VISIBLE);
                    tv_unit_label.setVisibility(View.VISIBLE);
                    tvInvestorValue.setText(!TextUtils.isEmpty(jsonObject.optString("ClientName")) ? jsonObject.optString("ClientName") : "N/A");
                    tv_scheme_value.setText(!TextUtils.isEmpty(jsonObject.optString("SchemeName")) ? jsonObject.optString("SchemeName") : "N/A");
                    tvUnits.setText(jsonObject.optString("Units"));
                    tvStatus.setText(jsonObject.optString("TranStatus"));
                    tvOrderNo.setText(jsonObject.optString("OrderNo"));
                    tvIIN.setText(jsonObject.optString("ClientCode"));
                    tvDate.setText(jsonObject.optString("TransactionDate").substring(0, 11));
                    v_divider.setVisibility(View.GONE);
                    regdate_ll.setVisibility(View.GONE);
                }

                mCeaseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ceaseSIP(jsonObject);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public void updateList(List<JSONObject> list, String type) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void ceaseSIP(JSONObject jsonObjectData){
        //System.out.println("-Clicked Object---"+jsonObjectData);
        final ProgressDialog mBar = ProgressDialog.show(mContext, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.Cease_SIP;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("OnlineOption", mSession.getAppType());
            if (AppSession.getInstance(mContext).getAppType().equalsIgnoreCase("N") || AppSession.getInstance(mContext).getAppType().equalsIgnoreCase("DN")) {
                jsonObject.put("AutoTransactionNo", jsonObjectData.optString("AutoTranxNo"));
                jsonObject.put("AutoTransactionType", jsonObjectData.optString("TranType"));
                jsonObject.put("UCCIIN", jsonObjectData.optString("CustomerID"));
            } else {
                jsonObject.put("AutoTransactionNo", jsonObjectData.optString(""));
                jsonObject.put("AutoTransactionType", jsonObjectData.optString("TranType"));
                jsonObject.put("UCCIIN", jsonObjectData.optString("CustomerID"));
                jsonObject.put("DPCFlag", jsonObjectData.optString("DPC"));
                jsonObject.put("DPTrans", jsonObjectData.optString("DPTrans"));
                jsonObject.put("EUIN", jsonObjectData.optString("EUIN"));
                jsonObject.put("EUINDecl", jsonObjectData.optString("EUINDecl"));
                jsonObject.put("FirstOrder", jsonObjectData.optString("FirstOrder"));
                jsonObject.put("FolioNo", jsonObjectData.optString("FolioNo"));

                jsonObject.put("MemberCode", jsonObjectData.optString("MemberCode"));
                jsonObject.put("SIPRegDate", jsonObjectData.optString("SIPRegDate"));
                jsonObject.put("SIPRegNo", jsonObjectData.optString("SIPRegNo"));
                jsonObject.put("SchemeCode", jsonObjectData.optString("SchemeCode"));
                jsonObject.put("SubBrokerARNCode", jsonObjectData.optString("SubBrokerARNCode"));
                jsonObject.put("Amount", jsonObjectData.optString("Amount"));
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject resObject) {
                    mBar.dismiss();
                    try {
                        if (resObject.optString("Status").equals("True")) {
                            showResultDialog(resObject.optString("ServiceMSG"), resObject.optString("Status"));
                        } else {
                            showResultDialog(resObject.optString("ServiceMSG"), resObject.optString("Status"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mBar.dismiss();
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());
                            Toast.makeText(mMainActivity,jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError) {
                        Toast.makeText(mMainActivity, mApplication.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
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
            requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void showResultDialog(String msg, String status) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mMainActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mMainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.do_later_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView editText = (TextView) dialogView.findViewById(R.id.textMsg);
        editText.setText(msg);
        AlertDialog alertDialog = dialogBuilder.create();
        TextView tvOk = (TextView) dialogView.findViewById(R.id.textOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (status.equals("True"))
                    mMainActivity.getSupportFragmentManager().popBackStack();
            }
        });
        alertDialog.show();

    }

}

 //  NSE - Scheme click - Runnig CEase butn
 //  parama  =
       // BID, Passkey, UCCIIN, OnlineOption, AutoTransactionNo, AutoTransactionType
 //  success.... ok back screen ( Alert box )

// BSE - Scheme click - SIP
  // Param =
  // BID, Passkey, UCCIIN, OnlineOption, AutoTransactionNo, AutoTransactionType
   //"AutoTransactionNo" : ""