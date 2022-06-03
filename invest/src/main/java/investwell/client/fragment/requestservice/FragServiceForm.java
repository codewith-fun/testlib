package investwell.client.fragment.requestservice;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import investwell.client.activity.AppApplication;
import investwell.client.adapter.RecycleViewAdapterCheckbox;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;


public class FragServiceForm extends Fragment {

    ServiceRequest serviceRequest;
    private RecycleViewAdapterCheckbox mAdapterCheckbox, mAdapterPayment;

    private ArrayList<String> mcheckbox_item = new ArrayList<>();
    private ArrayList<String> mPayment_item = new ArrayList<>();

    private String URL = " http://ws.investwell.in/WSGetTimeSlots";
    private String SubmitUrl = "http://ws.investwell.in/WSFormSubmit";
    ProgressDialog mBar;
    Button btnsubmit;
    String mFormName, mTime, mDetailCode, mServiceCode;
    ArrayList<JSONObject> list;
    static TextView date;
    String mNote, mDate;
    int mEmailReq, mTimeReq = 0, mDateReq = 0;
    LinearLayout lytime, lydate;
    AppApplication mConfig;
    AppSession appSession;

    public FragServiceForm() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ServiceRequest) {
            this.serviceRequest = (ServiceRequest) context;
            appSession = AppSession.getInstance(serviceRequest);
            mConfig = (AppApplication) serviceRequest.getApplication();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        serviceRequest.tvToolBarTitle.setText(mConfig.aServiceName + " Request");
        mEmailReq = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service_form, container, false);

        serviceRequest = (ServiceRequest) getActivity();
        vollyreqst("PORTFOLIO_REPORT");
        date = rootView.findViewById(R.id.tx_date);
        String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        date.setText(date_n);
        System.out.println("date===========" + date_n);
        ImageButton ib_cal = (ImageButton) rootView.findViewById(R.id.edit_cal);
        lytime = rootView.findViewById(R.id.ly_time);
        lydate = rootView.findViewById(R.id.ser_lydate);
        final EditText mEt_Note = (EditText) rootView.findViewById(R.id.service_note);
        ib_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dfrag = new DatePickerFrafment();
                dfrag.show(getActivity().getFragmentManager(), "Date Picker");
            }
        });

        mConfig = new AppApplication();
        mFormName = mConfig.aServiceName;

        btnsubmit = (Button) rootView.findViewById(R.id.form_submit);
        RecyclerView recyclerView = rootView.findViewById(R.id.time_recycleview);
        RecyclerView recyclerView1 = rootView.findViewById(R.id.payment_recycleview);


        mAdapterCheckbox = new RecycleViewAdapterCheckbox(new ArrayList<JSONObject>(), getActivity());
        recyclerView.setAdapter(mAdapterCheckbox);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // update();

        mAdapterPayment = new RecycleViewAdapterCheckbox(new ArrayList<JSONObject>(), getActivity());
        recyclerView1.setAdapter(mAdapterPayment);
        recyclerView1.setNestedScrollingEnabled(false);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
        // update1();

        Bundle bundle = getArguments();
        if (bundle.containsKey("data")) {
            list = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(bundle.getString("data"));
                JSONArray jarray = object.getJSONArray("Data");
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jdataarray = jarray.getJSONObject(i);
                    if (i == 0 && jdataarray.has("name")) {

                        mConfig.aServiceName = jdataarray.optString("name");
                        mServiceCode = jdataarray.optString("pk");
                        mTimeReq = jdataarray.optInt("isReqTime");
                        mDateReq = jdataarray.optInt("isReqDate");

                        serviceRequest.tvToolBarTitle.setText(jdataarray.optString("name"));

                        System.out.println("=====================" + mConfig.aServiceName);
                        /*System.out.println("2222222222222222222222222" + serviceRequest.title);*/
                    }
                    list.add(jdataarray);
                }

                Collections.sort(list, new SortData());
                mAdapterPayment.updatelist(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mTimeReq == 0) {
            lytime.setVisibility(View.GONE);
        } else if (mTimeReq == 1) {
            lytime.setVisibility(View.VISIBLE);
        }


        if (mDateReq == 0) {
            lydate.setVisibility(View.GONE);
        } else if (mDateReq == 1) {
            lydate.setVisibility(View.VISIBLE);
        }
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datacheck();
                if (mTimeReq == 1 && mTime == null) {


                    mConfig.showSnackBar(mEt_Note,"Select Suitable Time for you");
                } else if (mDetailCode == null) {
                    mConfig.showSnackBar(mEt_Note,"Please Select Your Service Type");

                } else {
                    mNote = mEt_Note.getText().toString();
                    mDate = date.getText().toString();

                /*mBar = ProgressDialog.show(getActivity(), "Requesting", "Please wait....", true, false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mBar.dismiss();
                        Toast.makeText(getActivity(), "Submit", Toast.LENGTH_LONG).show();

                    }
                }, 3000);*/

                    sendingdata();
//                saveCheckedData();
                }
            }
        });
        return rootView;
    }


    private void datacheck() {

        for (int i = 0; i < mAdapterPayment.mDataList.size(); i++) {
            if (mAdapterPayment.mHashValue.get(i)) {

                JSONObject getvalue = mAdapterPayment.mDataList.get(i);
                String checkeddetailcode = getvalue.optString("detail_code");
                if (i == 0 || mDetailCode == null) {
                    mDetailCode = checkeddetailcode;
                } else {
                    mDetailCode = mDetailCode + "|" + checkeddetailcode;
                }
            }
        }

        for (int i = 0; i < mAdapterCheckbox.mDataList.size(); i++) {
            if (mAdapterCheckbox.mHashMap.get(i).equals(true)) {

                JSONObject getvalue = mAdapterCheckbox.mDataList.get(i);
                String checkedtime = getvalue.optString("timeSlot");

                if (i == 0 || mTime == null) {
                    mTime = checkedtime;
                } else {
                    mTime = mTime + ", " + checkedtime;
                }
            }
        }

    }

    private void sendingdata() {

        mBar = ProgressDialog.show(getActivity(), "Sending Request", "Please wait...", true, false);
        mBar.show();
        JSONObject submit_object = null;
        try {
            submit_object = new JSONObject();
            submit_object.put("bid", AppConstants.APP_BID);
             submit_object.put("client_id", appSession.getCID());
            submit_object.put("app_service_type_pk", mServiceCode);
            submit_object.put("notes", mNote);
            submit_object.put("service_date", mDate);
            submit_object.put("service_requested_datetime", "");
            submit_object.put("is_email_forwarded", mEmailReq);
            submit_object.put("detailCode", mDetailCode);
            submit_object.put("timeSlot", mTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Result=================" + submit_object);

        JsonObjectRequest submit_rqst = new JsonObjectRequest(Request.Method.POST, SubmitUrl, submit_object,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mBar.dismiss();

                        if (response.optString("Status").equals("True")) {


                            String response_status = response.optString("Data");


                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Status")
                                    .setMessage(response_status)
                                    .setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FragServiceOption serviceOption = new FragServiceOption();

                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, serviceOption)
                                            .commit();

                                }

                            });
                            builder.show();


//                            Toast.makeText(getActivity(), (CharSequence) response_status, Toast.LENGTH_SHORT).show();


                        } else if (response.optString("Status").equals("False")) {
                            String response_status = response.optString("Data");


                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Status")
                                    .setMessage(response_status)
                                    .setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FragServiceOption serviceOption = new FragServiceOption();

                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, serviceOption)
                                            .commit();

                                }

                            });
                            builder.show();

//                            Toast.makeText(getActivity(), response_status, Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mBar.dismiss();

                mConfig.showSnackBar(lytime, getResources().getString(R.string.no_internet));
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(volleyError.getMessage());
                        mConfig.showSnackBar(lytime, jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (error instanceof NoConnectionError)
                    mConfig.showSnackBar(lytime, getResources().getString(R.string.no_internet));
                mBar.dismiss();

            }
        }) {
        };


        submit_rqst.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(submit_rqst);

    }


    private void saveCheckedData() {
        for (int i = 0; i < mAdapterPayment.mDataList.size(); i++) {
            if (mAdapterPayment.mHashValue.get(i)) {

                JSONObject getvalue = mAdapterPayment.mDataList.get(i);
                String checkeddetailcode = getvalue.optString("detail_code");
                if (i == 0) {
                    mDetailCode = checkeddetailcode;
                } else {
                    mDetailCode = mDetailCode + "|" + checkeddetailcode;
                }
            }
        }

        for (int i = 0; i < mAdapterCheckbox.mDataList.size(); i++) {
            if (mAdapterCheckbox.mHashMap.get(i)) {

                JSONObject getvalue = mAdapterCheckbox.mDataList.get(i);
                String checkedtime = getvalue.optString("timeSlot");
                if (i == 0) {
                    mTime = checkedtime;
                } else {
                    mTime = mTime + " , " + checkedtime;
                }
            }
        }

        System.out.println("Selected Time" + mTime);
        System.out.println("Selected Codes" + mDetailCode);
    }


    public static class DatePickerFrafment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


            return datePickerDialog;

        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            date = (TextView) getActivity().findViewById(R.id.tx_date);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chooseDate = cal.getTime();

            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String formatedDate = dateFormat.format(chooseDate);
            date.setText(formatedDate);

            System.out.println("formated date=================" + formatedDate);
        }
    }


    public void vollyreqst(final String action) {
        mBar = ProgressDialog.show(getActivity(), null, "Please wait....", true, false);
        mBar.show();
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put("bid", AppConstants.APP_BID);
            object.put("code", action);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        mBar.dismiss();


                        if (object.optString("Status").equals("True")) {

                            ArrayList<JSONObject> timelist = new ArrayList<>();
                            try {
                                JSONArray timearray = object.getJSONArray("Data");
                                for (int i = 0; i < timearray.length(); i++) {
                                    JSONObject timedata = timearray.getJSONObject(i);
                                    timelist.add(timedata);

                                }
                                mAdapterCheckbox.updateView(timelist);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            mConfig.showSnackBar(lytime,getResources().getString(R.string.error_try_again));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        mConfig.showSnackBar(lytime, getResources().getString(R.string.no_internet));
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                            try {
                                JSONObject jsonObject = new JSONObject(volleyError.getMessage());
                                mConfig.showSnackBar(lytime, jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (error instanceof NoConnectionError)
                            mConfig.showSnackBar(lytime, getResources().getString(R.string.no_internet));
                        mBar.dismiss();
                    }
                }) {

        };

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);


    }

    private class SortData implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            String a = "0";
            String b = "0";
            a = ((JSONObject) o1).optString("group_name");
            b = ((JSONObject) o2).optString("group_name");

            /*SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            try {
                Date date1 = formatter.parse(a);
                Date date2 = formatter.parse(b);
                a = "" + date1.getTime();
                b = "" + date2.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            int ia = 0, ib = 0;
            int nza = 0, nzb = 0;
            char ca, cb;
            int result;

            while (true) {
                // only count the number of zeroes leading the last number compared
                nza = nzb = 0;

                ca = charAt(a, ia);
                cb = charAt(b, ib);

                // skip over leading spaces or zeros
                while (Character.isSpaceChar(ca) || ca == '0') {
                    if (ca == '0') {
                        nza++;
                    } else {
                        // only count consecutive zeroes
                        nza = 0;
                    }

                    ca = charAt(a, ++ia);
                }

                while (Character.isSpaceChar(cb) || cb == '0') {
                    if (cb == '0') {
                        nzb++;
                    } else {
                        // only count consecutive zeroes
                        nzb = 0;
                    }

                    cb = charAt(b, ++ib);
                }

                // process run of digits
                if (Character.isDigit(ca) && Character.isDigit(cb)) {
                    if ((result = compareRight(a.substring(ia), b.substring(ib))) != 0) {
                        return result;
                    }
                }

                if (ca == 0 && cb == 0) {
                    // The strings compare the same. Perhaps the caller
                    // will want to call strcmp to break the tie.
                    return nza - nzb;
                }

                if (ca < cb) {
                    return -1;
                } else if (ca > cb) {
                    return +1;
                }

                ++ia;
                ++ib;
            }
        }

        private char charAt(String s, int i) {
            if (i >= s.length()) {
                return 0;
            } else {
                return s.charAt(i);
            }
        }

        int compareRight(String a, String b) {
            int bias = 0;
            int ia = 0;
            int ib = 0;

            // The longest run of digits wins. That aside, the greatest
            // value wins, but we can't know that it will until we've scanned
            // both numbers to know that they have the same magnitude, so we
            // remember it in BIAS.
            for (; ; ia++, ib++) {
                char ca = charAt(a, ia);
                char cb = charAt(b, ib);

                if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
                    return bias;
                } else if (!Character.isDigit(ca)) {
                    return -1;
                } else if (!Character.isDigit(cb)) {
                    return +1;
                } else if (ca < cb) {
                    if (bias == 0) {
                        bias = -1;
                    }
                } else if (ca > cb) {
                    if (bias == 0)
                        bias = +1;
                } else if (ca == 0 && cb == 0) {
                    return bias;
                }
            }
        }
    }

}
