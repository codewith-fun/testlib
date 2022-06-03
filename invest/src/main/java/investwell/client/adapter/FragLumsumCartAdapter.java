package investwell.client.adapter;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;


/**
 * Created by shruti on 8/5/18.
 */

public class FragLumsumCartAdapter extends RecyclerView.Adapter<FragLumsumCartAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;

    private Context mContext;
    private OnItemClickListener listener;
    private AppSession mSession;
    private TextView tvTotalView, mTvNothing;
    private String mUCC_Code = "";
    private CardView mPaymentView;
    private ToolbarFragment mToolbarFragment;
    String FolioNumber;

    public FragLumsumCartAdapter(Context context, ArrayList<JSONObject> list, TextView tvTotal, String ucc, CardView paymentView, TextView tvNothing, ToolbarFragment toolbarFragment, OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        tvTotalView = tvTotal;
        this.listener = listener;
        mSession = AppSession.getInstance(mContext);
        mUCC_Code = ucc;

        mTvNothing = tvNothing;
        mPaymentView = paymentView;
        mToolbarFragment = toolbarFragment;
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText etAmount;
        ImageView delete_btn,mIvEditFolio;
        TextView schemeNmae, min_value,mTVFolioNo;
        RelativeLayout mRlFolio;


        public ViewHolder(View view) {
            super(view);
            schemeNmae = view.findViewById(R.id.colorBlue);
            etAmount = view.findViewById(R.id.amount);
            delete_btn = view.findViewById(R.id.delete_btn);
            min_value = view.findViewById(R.id.min_value);
            mRlFolio = view.findViewById(R.id.RlFolio);
            mTVFolioNo = view.findViewById(R.id.tvFolioNo);
            mIvEditFolio = view.findViewById(R.id.ivEditFolio);
        }


        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            JSONArray jsonArray = jsonObject.optJSONArray("ExistingFolio");
            String FolioNo = jsonArray.optJSONObject(0).optString("FolioNo");
            final ArrayList<String> Foliolist = new ArrayList<>();
            Foliolist.add("New");
            for (int i = 0; i <jsonArray.length() ; i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                Foliolist.add(jsonObject1.optString("FolioNo"));
            }
            schemeNmae.setText(jsonObject.optString("Scheme"));
            String decimalAmount = jsonObject.optString("Amount");
            String strAmount = "";
            if (decimalAmount.contains(".")) {
                int Amount = new Double(decimalAmount).intValue();
                strAmount = "" + Amount;
            } else {
                strAmount = "" + decimalAmount;
            }
            etAmount.setText("" + strAmount);

            if (position == (mDataList.size() - 1)) {
                int netPrice = 0;
                for (int j = 0; j < mDataList.size(); j++) {
                    final JSONObject jsonObject2 = mDataList.get(j);
                    int pricePerItem = jsonObject2.optInt("Amount");
                    netPrice = netPrice + pricePerItem;
                }
                tvTotalView.setText("" + netPrice);
            }

            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String userInputStr = s.toString();

                    /*  if (!userInputStr.equals("")) {*/
                    try {
                        jsonObject.put("Amount", s.toString());
                        int netPrice = 0;
                        for (int j = 0; j < mDataList.size(); j++) {
                            final JSONObject jsonObject2 = mDataList.get(j);
                            int pricePerItem = jsonObject2.optInt("Amount");
                            netPrice = netPrice + pricePerItem;
                        }
                        tvTotalView.setText("" + netPrice);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }/*}*/
            });

            if (FolioNo.isEmpty()){
                mRlFolio.setVisibility(View.GONE);
                mTVFolioNo.setText("New");
                mIvEditFolio.setVisibility(View.INVISIBLE);
                mIvEditFolio.setEnabled(false);
            }else{
                mRlFolio.setVisibility(View.VISIBLE);
                mTVFolioNo.setText(FolioNo);
                mIvEditFolio.setVisibility(View.VISIBLE);
                mIvEditFolio.setEnabled(true);
            }

            mIvEditFolio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.foliolist_dialog);
                    Spinner mFolioSpinner = dialog.findViewById(R.id.folioSpinner);
                    ArrayAdapter spinner_value = new ArrayAdapter(mContext, R.layout.spinner_item, Foliolist);
                    spinner_value.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mFolioSpinner.setAdapter(spinner_value);
                    dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });



                    mFolioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            FolioNumber =  adapterView.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    dialog.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mTVFolioNo.setText(FolioNumber);
                            dialog.dismiss();
                        }
                    });



                    dialog.show();
                    dialog.setCancelable(false);

                }
            });

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDailog(jsonObject, position);
                }
            });
        }

    }



    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    private void showDailog(final JSONObject object, final int position) {
        CustomDialog customDialog = new CustomDialog(new CustomDialog.DialogBtnCallBack() {
            @Override
            public void onDialogBtnClick(View view) {
                int id = view.getId();
                if (id == R.id.btDone) {
                    if (mDataList.size() > 0) {
                        mDataList.remove(position);
                        String title = "";
                        if (mDataList.size() == 0) {
                            mPaymentView.setVisibility(View.GONE);
                            mTvNothing.setVisibility(View.VISIBLE);
                            title = mContext.getString(R.string.text_lumpsum_title);
                        } else {
                            title = mContext.getString(R.string.text_lumpsum_title) + "(" + mDataList.size() + ")";
                        }

                        int netPrice = 0;
                        for (int j = 0; j < mDataList.size(); j++) {
                            final JSONObject jsonObject2 = mDataList.get(j);
                            int pricePerItem = jsonObject2.optInt("Amount");
                            netPrice = netPrice + pricePerItem;
                        }
                        tvTotalView.setText("" + netPrice);
                        notifyDataSetChanged();
                        deleteItem(object.optString("srno"));

                        if (mToolbarFragment != null) {
                            mToolbarFragment.setUpToolBar(title, true, false, false, false, false, false, false, "");
                        }
                    }
                } else if (id == R.id.btCalcel) {
                }
            }
        });

        customDialog.showDialog(mContext, mContext.getResources().getString(R.string.alert_dialog_confirmation_header_txt),
                mContext.getResources().getString(R.string.alert_dialog_delete_scheme_txt),
                mContext.getResources().getString(R.string.alert_dialog_yes_btn_txt),
                mContext.getResources().getString(R.string.alert_dialog_btn_no_txt),
                true, true);
    }


    private void deleteItem(final String srno) {
        String url = Config.DELETE_LUMSUM;
        JSONObject params = new JSONObject();

        try {
            params.put("UCC", mUCC_Code);
            params.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            params.put("Srno", srno);
            params.put(AppConstants.PASSKEY, mSession.getPassKey());
            params.put("OnlineOption",mSession.getAppType());
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
    }



}
