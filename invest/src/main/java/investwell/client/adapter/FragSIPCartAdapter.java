package investwell.client.adapter;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;


/**
 * Created by shruti on 8/5/18.
 */

public class FragSIPCartAdapter extends RecyclerView.Adapter<FragSIPCartAdapter.ViewHolder> {
    public ArrayList<JSONObject> mDataList;

    private Context mContext;
    private OnItemClickListener listener;
    private AppSession mSession;
    private TextView tvTotalView, mTvNothing;
    private CheckBox mCheckBox, mcheckBoxUntil_cancel;
    private String mUcc_Code = "", change = "True";
    private CardView mPaymentView;
    private ToolbarFragment mToolbarFragment;
    private String FolioNumber;


    public FragSIPCartAdapter(Context context, ArrayList<JSONObject> list, TextView tvTotal, CheckBox checkBox, CardView paymentView, CheckBox checkBox2, TextView tvNothing, String uccCode, ToolbarFragment toolbarFragment, OnItemClickListener listener) {
        mContext = context;
        mDataList = list;
        mUcc_Code = uccCode;
        tvTotalView = tvTotal;
        mTvNothing = tvTotal;
        this.listener = listener;
        mCheckBox = checkBox;
        mTvNothing = tvNothing;
        mPaymentView = paymentView;
        mcheckBoxUntil_cancel = checkBox2;
        mSession = AppSession.getInstance(mContext);
        mToolbarFragment = toolbarFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frag_sip_cart_adapter, viewGroup, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView delete_btn, mIvEditFolio;
        TextView schemeNmae, mTVFolioNo;
        EditText etAmount, installments;
        RelativeLayout mRlFolio;
        CheckBox mUntilCancelled;
        LinearLayout ll_install;

        private Spinner mSpDate, mSpMonth;

        public ViewHolder(View view) {
            super(view);
            schemeNmae = view.findViewById(R.id.colorBlue);
            etAmount = view.findViewById(R.id.amount);
            delete_btn = view.findViewById(R.id.delete_btn);
            installments = view.findViewById(R.id.installments);
            mRlFolio = view.findViewById(R.id.RlFolio);
            mTVFolioNo = view.findViewById(R.id.tvFolioNo);
            mIvEditFolio = view.findViewById(R.id.ivEditFolio);
            mUntilCancelled = view.findViewById(R.id.cb_until_cancel);
            ll_install = view.findViewById(R.id.ll_install);
            mSpDate = view.findViewById(R.id.spDate);
            mSpMonth = view.findViewById(R.id.spMonth);
        }

        public void setItem(final int position, final OnItemClickListener listener) {
            final JSONObject jsonObject = mDataList.get(position);
            JSONArray jsonArray = jsonObject.optJSONArray("ExistingFolio");
            String FolioNo = jsonArray.optJSONObject(0).optString("FolioNo");
            final ArrayList<String> Foliolist = new ArrayList<>();
            Foliolist.add("New");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                Foliolist.add(jsonObject1.optString("FolioNo"));
            }
            schemeNmae.setText(jsonObject.optString("Scheme"));
            installments.setText(jsonObject.optString("Installment"));
            if (jsonObject.optBoolean("untilCancel")) {
                mUntilCancelled.setChecked(true);
                installments.setEnabled(false);
                ll_install.setVisibility(View.INVISIBLE);
                installments.setText("");
            } else {
                mUntilCancelled.setChecked(false);
                installments.setEnabled(true);
                ll_install.setVisibility(View.VISIBLE);
                installments.setText("12");
            }
            String decimalAmount = jsonObject.optString("Amount");
         /*   String strAmount = "";
            if (decimalAmount.contains(".")) {
                int Amount = new Double(decimalAmount).intValue();
                strAmount = "" + Amount;
            } else {
                strAmount = "" + decimalAmount;
            }*/
            if (decimalAmount.equalsIgnoreCase("0.0")) {
                etAmount.setHint(decimalAmount);
            } else {
                etAmount.setText(decimalAmount);

            }
            // calculate all amount for
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
      /*              String userInputStr = s.toString();

                    if (userInputStr.length()>0) {*/
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
                }
                /* }*/
            });


            installments.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String userInputStr = charSequence.toString();
                    if (!userInputStr.equals("")) {
                        try {
                            int installments = Integer.parseInt(charSequence.toString());
                            jsonObject.put("Installment", "" + installments);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            String Dates = jsonObject.optString("sipdates");
            List<String> DateList = Arrays.asList(Dates.split(","));
            ArrayList<String> daysList = new ArrayList<>();
            for (int i = 0; i < DateList.size(); i++) {
                DecimalFormat formatter = new DecimalFormat("00");
                int data = Integer.parseInt(DateList.get(i));
                String formatDay = formatter.format(data);
                daysList.add(formatDay);
            }

            if (daysList.size() == 0) {
                DecimalFormat formatter = new DecimalFormat("00");
                int data = 0;
                for (int i = 1; i < 29; i++) {
                    data = i;
                }
                String formatDay = formatter.format(data);
                daysList.add(formatDay);
            }
            setDate(mSpDate, mSpMonth, daysList, jsonObject);
            setMonth(mSpDate, mSpMonth, daysList, jsonObject);

 /*           String dateListStr = jsonObject.optString("sipdates");
            String[] dateList = null;
            if (dateListStr.contains(",")) {
                dateList = jsonObject.optString("sipdates").split(",");
            } else {
                String dateStr = "";
                for (int i = 1; i < 29; i++) {
                    if (i == 0)
                        dateStr = "" + (new DecimalFormat("00").format(i));
                    else {
                        dateStr = dateStr + "," + (new DecimalFormat("00").format(i));
                    }
                }
                dateList = dateStr.split(",");
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, dateList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sipdate_spinner.setAdapter(dataAdapter);
            sipdate_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String selected = sipdate_spinner.getSelectedItem().toString();
                        jsonObject.put("selectedDate", selected);
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });*/


      /*      if (change.equalsIgnoreCase("True")) {
                installments.setEnabled(false);
            } else {
                installments.setEnabled(true);
            }*/

            mUntilCancelled.setOnClickListener(v -> {
                if (jsonObject.optBoolean("untilCancel")) {
                    try {
                        mDataList.get(position).put("untilCancel", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mUntilCancelled.setChecked(false);
                    installments.setEnabled(true);
                    installments.setText("12");
                    ll_install.setVisibility(View.VISIBLE);

                } else {
                    try {
                        mDataList.get(position).put("untilCancel", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mUntilCancelled.setChecked(true);
                    installments.setEnabled(false);
                    installments.setText("");
                    ll_install.setVisibility(View.INVISIBLE);
                }
                notifyDataSetChanged();
            });


            if (FolioNo.isEmpty()) {
                mRlFolio.setVisibility(View.GONE);
                mTVFolioNo.setText("New");
                mIvEditFolio.setVisibility(View.INVISIBLE);
                mIvEditFolio.setEnabled(false);
            } else {
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
                            FolioNumber = adapterView.getSelectedItem().toString();
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

    private void setDate(Spinner spDay,Spinner spMonthYear, ArrayList<String> daysList, JSONObject jsonObject) {
        try {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item2, daysList);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown2);
            spDay.setAdapter(dataAdapter);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String[] date = null;
            if (mSession.getAppType().equals(mContext.getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                calendar.add(Calendar.DATE, 7);
              String  mDateAfter7days = simpleDateFormat.format(calendar.getTime());
                date = mDateAfter7days.split("/");
            } else {
                String todayDate = simpleDateFormat.format(calendar.getTime());
                date = todayDate.split("/");
            }


            int spinnerPosition = 0;
            if (daysList.contains(date[0])) {
                spinnerPosition = dataAdapter.getPosition(date[0]);
                spDay.setSelection(spinnerPosition);
            } else {
                for (int i = 0; i < daysList.size(); i++) {
                    int valueDate = Integer.parseInt(date[0]);
                    if (Integer.parseInt(daysList.get(i)) >= valueDate) {
                        spinnerPosition = i;
                        spDay.setSelection(spinnerPosition);
                        break;
                    } else {
                        spinnerPosition = 0;
                        spDay.setSelection(spinnerPosition);
                    }
                }
            }


            /*Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayDate = simpleDateFormat.format(calendar.getTime());
            String[] date = todayDate.split("-");

            int spinnerPosition = 0;
            if (daysList.contains(date[2])) {
                spinnerPosition = dataAdapter.getPosition(date[2]);
                spDay.setSelection(spinnerPosition);
            } else {
                for (int i = 0; i < daysList.size(); i++) {
                    int valueDate = Integer.parseInt(date[2]);
                    if (Integer.parseInt(daysList.get(i)) >= valueDate) {
                        spinnerPosition = i;
                        spDay.setSelection(spinnerPosition);
                        break;
                    } else {
                        spinnerPosition = 0;
                        spDay.setSelection(spinnerPosition);
                    }
                }
            }*/

            spDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String day = spDay.getSelectedItem().toString();
                        String monthYear = spMonthYear.getSelectedItem().toString();
                        String completeDate = day + " " + monthYear;
                        SimpleDateFormat monthParse = new SimpleDateFormat("dd MMM yyyy");
                        SimpleDateFormat monthDisplay = new SimpleDateFormat("dd/MM/yyyy");
                        String selected = monthDisplay.format(monthParse.parse(completeDate));
                        jsonObject.put("selectedDate", selected);
                    } catch (Exception e) {
                        String error = e.getLocalizedMessage();
                        System.out.println(error);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {
        }

    }

    private void setMonth(Spinner spDay,Spinner spMonthYear, ArrayList<String> daysList, JSONObject jsonObject) {
        try {
            List<String> monthyearList = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy");
            Calendar calendar = Calendar.getInstance();

            if (mSession.getAppType().equals(mContext.getResources().getString(R.string.apptype_n)) || mSession.getAppType().equalsIgnoreCase("DN")) {
                calendar.add(Calendar.DATE, 8);
                String firstMonth = simpleDateFormat.format(calendar.getTime());
                monthyearList.add(firstMonth);
            }else{
                String firstMonth = simpleDateFormat.format(calendar.getTime());
                monthyearList.add(firstMonth);
            }

            calendar.add(Calendar.MONTH, 1);
            String secondMonth = simpleDateFormat.format(calendar.getTime());
            monthyearList.add(secondMonth);

            calendar.add(Calendar.MONTH, 1);
            String thirdMonth = simpleDateFormat.format(calendar.getTime());
            monthyearList.add(thirdMonth);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item2, monthyearList);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown2);
            spMonthYear.setAdapter(dataAdapter);

            spMonthYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String day = spDay.getSelectedItem().toString();
                        String monthYear = spMonthYear.getSelectedItem().toString();
                        String completeDate = day + " " + monthYear;
                        SimpleDateFormat monthParse = new SimpleDateFormat("dd MMM yyyy");
                        SimpleDateFormat monthDisplay = new SimpleDateFormat("dd/MM/yyyy");
                        String selected = monthDisplay.format(monthParse.parse(completeDate));
                        jsonObject.put("selectedDate", selected);
                    } catch (Exception e) {
                        String error = e.getLocalizedMessage();
                        System.out.println(error);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {

        }
    }


    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }


    public void update_List(String until_cancel) {
        if (until_cancel.equalsIgnoreCase("Y")) {
            change = "True";
        } else {
            change = "False";
        }
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
                            mcheckBoxUntil_cancel.setVisibility(View.GONE);
                            mTvNothing.setVisibility(View.VISIBLE);
                            title = mContext.getString(R.string.text_sip_title);
                        } else {
                            title = mContext.getString(R.string.text_sip_title) + "(" + mDataList.size() + ")";
                        }

                        try {
                            int netPrice = 0;
                            for (int j = 0; j < mDataList.size(); j++) {
                                final JSONObject jsonObject2 = mDataList.get(j);
                                int pricePerItem = jsonObject2.getInt("Amount");
                                netPrice = netPrice + pricePerItem;
                            }
                            tvTotalView.setText("" + netPrice);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (mDataList.size() == 0)
                            mCheckBox.setVisibility(View.GONE);
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


    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    private void deleteItem(final String srno) {
        String url = Config.DELETE_SIP;
        JSONObject params = new JSONObject();

        try {
            params.put("UCC", mUcc_Code);
            params.put("Bid", AppConstants.APP_BID);
            params.put("Srno", srno);
            params.put("Passkey", mSession.getPassKey());
            params.put("OnlineOption", mSession.getAppType());
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