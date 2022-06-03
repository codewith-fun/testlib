package investwell.client.fragment.requestservice;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;

public class FragServiceOption extends Fragment {


    LinearLayout btncheque, btnchangesportfolio, btnportfolioreport, btncollectdoc, btncall, btnmeet, btnreportissue, btnothers;
    private String URL = "http://ws.investwell.in/WSGenerateForms";
    ProgressDialog mBar;
    ServiceRequest serviceRequest;
    public static boolean servicepage;
    AppApplication mConfig;
    AppSession appSession;


    public FragServiceOption() {

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
        serviceRequest.tvToolBarTitle.setText(getResources().getString(R.string.toolBar_title_service_request));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_service_options, container, false);
        serviceRequest = (ServiceRequest) getActivity();
        btncheque = rootView.findViewById(R.id.cheque);
        btnchangesportfolio = rootView.findViewById(R.id.portfolio_change);
        btnportfolioreport = rootView.findViewById(R.id.portfolio_report);
        btncollectdoc = rootView.findViewById(R.id.document);
        btncall = rootView.findViewById(R.id.call_me);
        btnmeet = rootView.findViewById(R.id.meet_me);
        btnreportissue = rootView.findViewById(R.id.report_issue);
        btnothers = rootView.findViewById(R.id.others);
        btncheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(AppConstants.APP_BID)) {
                    vollyreqst("COLLECT_CHEQUE");
                } else {
                    servicepage = true;
                    mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_empty_login));
                }


            }
        });
        btnchangesportfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(AppConstants.APP_BID)) {
                    vollyreqst("PORTFOLIO_CHANGE");
                } else {
                    servicepage = true;
                    mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_empty_login));

                }


            }
        });
        btnportfolioreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(AppConstants.APP_BID)) {
                    vollyreqst("PORTFOLIO_REPORT");
                } else {
                    servicepage = true;
                    mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_empty_login));

                }


            }
        });
        btncollectdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(AppConstants.APP_BID)) {
                    vollyreqst("COLLECT_DOC");
                } else {
                    servicepage = true;
                    mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_empty_login));

                }


            }
        });
        btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(AppConstants.APP_BID)) {
                    vollyreqst("CALL_REQUEST");
                } else {
                    servicepage = true;
                    mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_empty_login));

                }


            }
        });
        btnmeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(AppConstants.APP_BID)) {
                    vollyreqst("MEETING_REQUEST");
                } else {
                    servicepage = true;
                    mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_empty_login));

                }


            }
        });
        btnreportissue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(AppConstants.APP_BID)) {
                    vollyreqst("REPORT_ISSUE");
                } else {
                    servicepage = true;
                    mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_empty_login));

                }


            }
        });

        btnothers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_not_available));


            }
        });
        return rootView;
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

                            FragServiceForm serviceForm = new FragServiceForm();
                            Bundle bundle = new Bundle();
                            bundle.putString("data", object.toString());
                            serviceForm.setArguments(bundle);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, serviceForm)
                                        .addToBackStack(null).commit();
                            }

                        } else {
                            mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.error_try_again));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.no_internet));
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                            try {
                                JSONObject jsonObject = new JSONObject(volleyError.getMessage());
                                mConfig.showSnackBar(btnchangesportfolio, jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (error instanceof NoConnectionError)
                            mConfig.showSnackBar(btnchangesportfolio, getResources().getString(R.string.no_internet));
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


}
