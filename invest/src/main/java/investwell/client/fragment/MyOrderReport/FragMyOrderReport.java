package investwell.client.fragment.MyOrderReport;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragMyOrderReport extends Fragment {

    private ToolbarFragment fragToolBar;
    private AppSession mSession;
    private AppApplication mApplication;
    private Bundle bundle;
    private String mLastDay = "7D", mTransType, mTransStatus = "ALL", mUcc, mDaysValue=" (Last 7 Days)",InvestorNameValue;
    private RecyclerView mOrderReportRecycle;
    private MyOrderReportAdapter myOrderReportAdapter;
    private FloatingActionButton mFilterIcon;
    private Spinner mMemberSpinner,mLastDaySpinner, mTransTypeSpinner, mTransStatusSpinner;
    private String[] LastDayValue = {"1 Day" ,"3 Days" , "7 Days"},TransTypeValue, TransStatusValue;
    ArrayList<JSONObject> list = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_frag_my_order_report, container, false);
        bundle = getArguments();
        mApplication = (AppApplication) getActivity().getApplication();

        mSession = AppSession.getInstance(getActivity());
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUcc = bundle.getString("ucc_code");
            InvestorNameValue = bundle.getString("InvestorName");
        } else {
            mUcc = mSession.getUCC_CODE();
        }
        setUpToolBar(InvestorNameValue,"(Last 7 Days)");
        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
            mTransType = "A";
            TransTypeValue = new String[]{"All", "Purchase", "Redemption", "Switch", "Triggered"};
            TransStatusValue = new String[]{"All", "Processed", "Authorized", "Pending", "Rejected"};
        } else {
            mTransType = "P";
            TransTypeValue = new String[]{"Purchase", "Sell"};
            TransStatusValue = new String[]{"All", "Valid", "Invalid"};
        }


        mOrderReportRecycle = view.findViewById(R.id.rv_my_order_report);
        mFilterIcon = view.findViewById(R.id.filter_icon);
        mOrderReportRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mOrderReportRecycle.setHasFixedSize(true);
        myOrderReportAdapter = new MyOrderReportAdapter(getActivity(), new ArrayList<JSONObject>());
        mOrderReportRecycle.setAdapter(myOrderReportAdapter);
        getMyOrderReport();
        mFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialog = inflater.inflate(R.layout.my_order_report_dialog, null);

                dialogBuilder.setView(dialog);
                final AlertDialog alertDialog = dialogBuilder.create();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                mMemberSpinner = dialog.findViewById(R.id.member_spinner);
                mLastDaySpinner = dialog.findViewById(R.id.lastDay_spinner);
                mTransTypeSpinner = dialog.findViewById(R.id.type_spinner);
                mTransStatusSpinner = dialog.findViewById(R.id.status_spinner);
                setSpinner(list);



                dialog.findViewById(R.id.apply_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMyOrderReport();
                        alertDialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        setMemberSpinnerData();

        return view;
    }

    private void setUpToolBar(String InvestorName,String day_value) {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(InvestorName+" "+getString(R.string.order_txt)+day_value, true, false, false, false, false, false, false, "");
        }
    }

    private void getMyOrderReport() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.My_Order_Report;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("UCC", mUcc);
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("LastDay", mLastDay);
            jsonObject.put("TranType", mTransType);
            jsonObject.put("TranStatus", mTransStatus);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    ArrayList<JSONObject> list = new ArrayList<>();
                    setUpToolBar(InvestorNameValue,mDaysValue);
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArray = response.optJSONArray("ResponseData");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            list.add(jsonObject1);
                        }
                        myOrderReportAdapter.updateList(list);

                    } else {
                        list.clear();
                        myOrderReportAdapter.updateList(list);
                        mApplication.showSnackBar(mOrderReportRecycle, response.optString("ServiceMSG"));

                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    DialogsUtils.hideProgressBar();
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());

                            mApplication.showSnackBar(mOrderReportRecycle, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        mApplication.showSnackBar(mOrderReportRecycle, getResources().getString(R.string.no_internet));
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setMemberSpinnerData() {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.PROFILE_LIST;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    try {
                        if (response.optBoolean("Status")) {
                            JSONArray araArray = response.getJSONArray("ProfileListDetail");
                            for (int i = 0; i < araArray.length(); i++) {
                                JSONObject object = araArray.getJSONObject(i);
                                list.add(object);
                            }



                        } else {
                            mApplication.showSnackBar(mOrderReportRecycle, response.optString("ServiceMSG"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    DialogsUtils.hideProgressBar();
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        try {
                            JSONObject jsonObject = new JSONObject(error.getMessage());

                            mApplication.showSnackBar(mOrderReportRecycle, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        mApplication.showSnackBar(mOrderReportRecycle, getResources().getString(R.string.no_internet));
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setSpinner(ArrayList<JSONObject> mlist) {
        ArrayList<String> member = new ArrayList<>();
        for (int i = 0; i < mlist.size(); i++) {
            member.add(mlist.get(i).optString("InvestorName"));
        }

        ArrayAdapter typeAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, TransTypeValue);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTransTypeSpinner.setAdapter(typeAdapter);


        ArrayAdapter statusAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, TransStatusValue);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTransStatusSpinner.setAdapter(statusAdapter);


        ArrayAdapter memberAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, member);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMemberSpinner.setAdapter(memberAdapter);


        ArrayAdapter lastDayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_dropdown, LastDayValue);
        lastDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLastDaySpinner.setAdapter(lastDayAdapter);


        mTransTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {

                    case 0:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransType = "A";
                        } else {
                            mTransType = "P";
                        }

                        break;


                    case 1:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransType = "P";
                        } else {
                            mTransType = "R";
                        }
                        break;


                    case 2:
                        mTransType = "R";
                        break;

                    case 3:
                        mTransType = "S";
                        break;

                    case 4:
                        mTransType = "T";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mTransStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {

                    case 0:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransStatus = "ALL";
                        } else {
                            mTransStatus = "ALL";
                        }
                        break;


                    case 1:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransStatus = "A";
                        } else {
                            mTransStatus = "Valid";
                        }
                        break;


                    case 2:
                        if (mSession.getAppType().equalsIgnoreCase("N") || mSession.getAppType().equalsIgnoreCase("DN")) {
                            mTransStatus = "C";
                        } else {
                            mTransStatus = "Invalid";
                        }
                        break;

                    case 3:
                        mTransStatus = "P";
                        break;

                    case 4:
                        mTransStatus = "R";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mMemberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                InvestorNameValue = adapterView.getSelectedItem().toString();
                mUcc = list.get(i).optString("UCC");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mLastDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDaysValue = " (Last "+adapterView.getSelectedItem().toString()+")";
                switch (i){
                    case 0:
                        mLastDay = "1D";
                        break;

                    case 1:
                        mLastDay = "3D";
                        break;

                    case 2:
                        mLastDay = "7D";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}
