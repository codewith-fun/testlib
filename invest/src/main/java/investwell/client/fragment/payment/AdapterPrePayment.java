package investwell.client.fragment.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;


public class
AdapterPrePayment extends RecyclerView.Adapter<AdapterPrePayment.ViewHolder> {
    public ArrayList<JSONObject> mDataList;

    private Context mContext;
    private OnItemClickListener listener;
    private AppSession mSession;

    private String mUCC_Code = "";
    private int netval = 0;
    private String mType = "";

    public AdapterPrePayment(Context context, ArrayList<JSONObject> list, String ucc, OnItemClickListener listener) {
        mContext = context;
        mDataList = list;

        this.listener = listener;
        mSession = AppSession.getInstance(mContext);
        mUCC_Code = ucc;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_lumpsum_cart_adapter, viewGroup, false);
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

    public void updateList(List<JSONObject> list, String type) {
        mDataList.clear();
        mDataList.addAll(list);
        netval = 0;
        mType = type;
        notifyDataSetChanged();
    }

    public void showDailog(final JSONObject object, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dailog_comman, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        TextView tvCancel = dialogView.findViewById(R.id.tvCancel);
        TextView tvOk = dialogView.findViewById(R.id.tvOk);
        tvTitle.setText(mContext.getResources().getString(R.string.alert_dialog_confirmation_header_txt));
        tvMessage.setText(mContext.getResources().getString(R.string.alert_dialog_delete_scheme_txt));
        tvCancel.setText(mContext.getResources().getString(R.string.alert_dialog_btn_no_txt));
        tvOk.setText(mContext.getResources().getString(R.string.alert_dialog_yes_btn_txt));

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataList.remove(position);
                try {
                    int netPrice = 0;
                    for (int j = 0; j < mDataList.size(); j++) {
                        final JSONObject jsonObject2 = mDataList.get(j);
                        int pricePerItem = jsonObject2.getInt("Amount");
                        netPrice = netPrice + pricePerItem;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }

                netval = 0;
                notifyDataSetChanged();

                deleteItem(object.optString("UniqueNo"));
                listener.onRefreshClick(position);
                alertDialog.dismiss();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void deleteItem(final String UniqueNo) {
        String url = Config.Delete_Pending_Order;
        JSONObject params = new JSONObject();

        try {
            params.put("UCC", mUCC_Code);
            params.put("Bid", AppConstants.APP_BID);
            params.put("UniqueNo", UniqueNo);
            params.put("Passkey", mSession.getPassKey());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                try {
                    if (object.optString("Status").equals("True")) {
                        //Toast.makeText(mContext, "Deleted data....", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        Toast.makeText(mContext, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onRefreshClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText etAmount;
        ImageView delete_btn,refresh_btn;
        TextView schemeNmae, min_value,tv_auth_scheme;
        RelativeLayout RlFolio;


        public ViewHolder(View view) {
            super(view);
            schemeNmae = view.findViewById(R.id.colorBlue);
            etAmount = view.findViewById(R.id.amount);
            delete_btn = view.findViewById(R.id.delete_btn);
            min_value = view.findViewById(R.id.min_value);
            RlFolio = view.findViewById(R.id.RlFolio);
            refresh_btn=view.findViewById(R.id.refresh_btn);
            tv_auth_scheme=view.findViewById(R.id.tv_auth_scheme);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
          /*  String[] arr = String.valueOf(jsonObject.optDouble("Amount")).split("\\.");
            int value = Integer.parseInt(arr[0]);
            etAmount.setText(String.valueOf(value));*/

            RlFolio.setVisibility(View.GONE);
//            netval = netval + value;
//            try {
//                jsonObject.put("Amount", "" + value);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }



            schemeNmae.setText(jsonObject.optString("Sname"));
            schemeNmae.setSelected(true);
            String decimalAmount = jsonObject.optString("Amount");
            String strAmount = "";
            if (decimalAmount.contains(".")) {
                int Amount = new Double(decimalAmount).intValue();
                strAmount = "" + Amount;
            } else {
                strAmount = "" + decimalAmount;
            }
            etAmount.setText("" + strAmount);
            etAmount.setEnabled(false);
            etAmount.setBackground(null);
            if (position == (mDataList.size() - 1)) {
                int netPrice = 0;
                for (int j = 0; j < mDataList.size(); j++) {
                    final JSONObject jsonObject2 = mDataList.get(j);
                    int pricePerItem = jsonObject2.optInt("Amount");
                    netPrice = netPrice + pricePerItem;
                }

            }
            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String userInputStr = charSequence.toString();
           /*         if (!userInputStr.equals("")) {
                        try {
                            int user_enter_price = Integer.parseInt(charSequence.toString());
                            jsonObject.put("Amount", "" + user_enter_price);
                            int netPrice = 0;
                            for (int j = 0; j < mDataList.size(); j++) {
                                final JSONObject jsonObject2 = mDataList.get(j);
                                int pricePerItem = jsonObject2.getInt("Amount");
                                netPrice = netPrice + pricePerItem;
                            }

                            tvTotalView.setText("" + netPrice);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvTotalView.setText("");
                        }
                    } else {
                        try {
                            jsonObject.put("Amount", "" + 0);
                            tvTotalView.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }*/
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        jsonObject.put("Amount", editable.toString());
                        int netPrice = 0;
                        for (int j = 0; j < mDataList.size(); j++) {
                            final JSONObject jsonObject2 = mDataList.get(j);
                            int pricePerItem = jsonObject2.optInt("Amount");
                            netPrice = netPrice + pricePerItem;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
             if((!TextUtils.isEmpty(jsonObject.optString("BSEOrderNo")) && (jsonObject.optString("BSEOrderNo")!=null)) && !jsonObject.optString("BSEOrderNo").equalsIgnoreCase("null")){
                refresh_btn.setVisibility(View.GONE);
                tv_auth_scheme.setVisibility(View.GONE);
            }else {
                refresh_btn.setVisibility(View.VISIBLE);
                tv_auth_scheme.setVisibility(View.VISIBLE);
            }
            refresh_btn.setOnClickListener(v -> {
                listener.onRefreshClick(position);
            });
            delete_btn.setOnClickListener(v -> showDailog(jsonObject, position));
        }

    }
}

