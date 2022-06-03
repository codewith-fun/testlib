package investwell.client.fragment.help;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import investwell.client.activity.WebViewActivity;
import investwell.client.fragment.nfo.Adapter.NFOAdapter;
import investwell.client.fragment.nfo.Fragments.FragOpen;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;


public class FragHelpContactus extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private String appAddressGoogleLink = "";
    private String email = "";
    private String landLine = "";
    private String phnNo = "";
    private String aboutUs = "";
    private String callBack = "";
    private LinearLayout llWealthManager, llEmailManager, llCallManager, llAddressManager, llWebsiteManager;
    private TextView tvWealthMangerHeading, tvCallManagerHeading, tvCallTitleHeading, tvWealthManagerTitleHeading, tvEmailTitleHeading, tvWebsiteTitleHeading, tvAddressTitleHeading;
    private TextView tvCallManagerDesc;
    private TextView tvEmailManagerDesc;
    private TextView tvWebManagerDesc;
    private TextView tvAddressManagerDesc;
    private TextView tvCallMobile;
    private TextView tvWealthCall, tvWealthEmail;
    private ImageView ivWealthEmail, ivWealthCall, ivCallUser;
    private TextView tvAddressLink;

    private RecyclerView contactusRecycle;
    private ContactUsAdapter contactusAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_help_contactus, container, false);
        mSession = AppSession.getInstance(getActivity());
        initializer(view);
        setUpUi();
        setListeners();

        return view;
    }

    private void initializer(View view) {
        llWealthManager = view.findViewById(R.id.ll_wealth_manager);
        llCallManager = view.findViewById(R.id.ll_call_manager);
        tvCallMobile = view.findViewById(R.id.tvCallMobile);
        llAddressManager = view.findViewById(R.id.ll_address_manager);
        llEmailManager = view.findViewById(R.id.ll_email_manager);
        llWebsiteManager = view.findViewById(R.id.ll_website_manager);
        tvWealthMangerHeading = view.findViewById(R.id.tvWealthMangerHeading);
        tvCallManagerHeading = view.findViewById(R.id.tvCallManagerHeading);
        tvWealthManagerTitleHeading = view.findViewById(R.id.tvWealthManagerTitleHeading);
        tvCallTitleHeading = view.findViewById(R.id.tvCallTitleHeading);
        tvEmailTitleHeading = view.findViewById(R.id.tvEmailTitleHeading);
        tvWebsiteTitleHeading = view.findViewById(R.id.tvWebsiteTitleHeading);
        tvAddressTitleHeading = view.findViewById(R.id.tvAddressTitleHeading);
        tvCallManagerDesc = view.findViewById(R.id.tvCallManagerDesc);
        tvEmailManagerDesc = view.findViewById(R.id.tvEmailManagerDesc);
        tvWebManagerDesc = view.findViewById(R.id.tvWebManagerDesc);
        tvAddressManagerDesc = view.findViewById(R.id.tvAddressManagerDesc);
        tvWealthCall = view.findViewById(R.id.tvWealthCall);
        tvWealthEmail = view.findViewById(R.id.tvWealthEmail);
        ivWealthCall = view.findViewById(R.id.iv_call_wealth_manager);
        ivWealthEmail = view.findViewById(R.id.iv_mail_wealth_manager_desc);
        tvAddressLink = view.findViewById(R.id.tv_address_link);
        ivCallUser = view.findViewById(R.id.ivIconCall);

        contactusRecycle = view.findViewById(R.id.contactus_recycler);

        contactusAdapter = new ContactUsAdapter(getActivity(), new ArrayList<JSONObject>(), FragHelpContactus.this);
        contactusRecycle.setHasFixedSize(true);
        contactusRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        contactusRecycle.setAdapter(contactusAdapter);
        getBrokerDetails();
    }


    @SuppressLint("SetTextI18n")
    private void setUpUi() {
        if (!mSession.getRM().isEmpty() && (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("MyWealthManager")) &&
                investwell.utils.Utils.getConfigData(mSession).optString("MyWealthManager").equalsIgnoreCase("Y"))) {


            if (!TextUtils.isEmpty(mSession.getRMemail())) {
                ivWealthEmail.setVisibility(View.VISIBLE);
                tvWealthEmail.setText(mSession.getRMemail());
                tvWealthEmail.setVisibility(View.VISIBLE);
            } else {
                ivWealthEmail.setVisibility(View.GONE);
                tvWealthEmail.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mSession.getRMmobil())) {
                ivWealthCall.setVisibility(View.VISIBLE);
                tvWealthCall.setText(mSession.getRMmobil());
                tvWealthCall.setVisibility(View.VISIBLE);
            } else {
                ivWealthCall.setVisibility(View.GONE);
                tvWealthCall.setVisibility(View.GONE);

            }
            tvWealthMangerHeading.setVisibility(View.VISIBLE);
            tvWealthMangerHeading.setText(getResources().getString(R.string.help_manager_title));
            tvWealthManagerTitleHeading.setText(mSession.getRM());
            llWealthManager.setVisibility(View.VISIBLE);
        } else {
            llWealthManager.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Landline")) || !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Mobile"))) {
            landLine = Utils.getConfigData(mSession).optString("Landline");
            phnNo = Utils.getConfigData(mSession).optString("Mobile");

            tvCallManagerHeading.setText(getResources().getString(R.string.help_contact_title));
            tvCallTitleHeading.setText(getResources().getString(R.string.help_call_txt));
            if (!TextUtils.isEmpty(landLine) && !TextUtils.isEmpty(phnNo)) {
                tvCallManagerDesc.setText(phnNo);
                tvCallMobile.setText(" , " + landLine);
                tvCallMobile.setVisibility(View.VISIBLE);
            } else if (!TextUtils.isEmpty(landLine) && TextUtils.isEmpty(phnNo)) {

                tvCallMobile.setText(landLine);

            } else if (TextUtils.isEmpty(landLine) && !TextUtils.isEmpty(phnNo)) {
                tvCallManagerDesc.setText(phnNo);
            } else {
                tvCallMobile.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("CallBack"))) {
                callBack = Utils.getConfigData(mSession).optString("CallBack");

            } else {
                callBack = "";


            }
            llCallManager.setVisibility(View.VISIBLE);
        } else {
            llCallManager.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Email"))) {
            email = Utils.getConfigData(mSession).optString("Email");

            tvEmailTitleHeading.setText(getResources().getString(R.string.help_email_title_txt));
            tvEmailManagerDesc.setText(email);
            llEmailManager.setVisibility(View.VISIBLE);
        } else {
            llEmailManager.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Website"))) {
            aboutUs = Utils.getConfigData(mSession).optString("Website");
            tvWebsiteTitleHeading.setText(getResources().getString(R.string.help_website_title_txt));
            tvWebManagerDesc.setText(aboutUs);
            llWebsiteManager.setVisibility(View.VISIBLE);
        } else {
            llWebsiteManager.setVisibility(View.GONE);


        }

        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Address")) || !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("GoogleCoordinates"))) {
            String appAddress = Utils.getConfigData(mSession).optString("Address");
            appAddressGoogleLink = Utils.getConfigData(mSession).optString("GoogleCoordinates");
            tvAddressTitleHeading.setText(getResources().getString(R.string.help_address_title_txt));
            tvAddressManagerDesc.setText(appAddress);
            llAddressManager.setVisibility(View.VISIBLE);
            if (appAddressGoogleLink != null && !TextUtils.isEmpty(Utils.getConfigData(mSession).optString("GoogleCoordinates"))) {
                tvAddressLink.setVisibility(View.VISIBLE);
            } else {
                tvAddressLink.setVisibility(View.GONE);
            }
        } else {
            llAddressManager.setVisibility(View.GONE);

        }
    }

    private void setListeners() {
        ivWealthCall.setOnClickListener(this);
        ivWealthEmail.setOnClickListener(this);
        tvAddressLink.setOnClickListener(this);
        tvCallMobile.setOnClickListener(this);
        tvCallManagerDesc.setOnClickListener(this);
        llWebsiteManager.setOnClickListener(this);
        llEmailManager.setOnClickListener(this);
        ivCallUser.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_address_link) {
            onViewDirectionClick();
        } else if (id == R.id.iv_mail_wealth_manager_desc) {
            onRmEmailIconClick();
        } else if (id == R.id.iv_call_wealth_manager) {
            onRMMobileIconClick();
        } else if (id == R.id.ll_email_manager) {
            onEmailRowClick();
        } else if (id == R.id.ll_website_manager) {
            onWebsiteRowClick();
        } else if (id == R.id.tvCallMobile) {
            onCallRowClick();
        } else if (id == R.id.tvCallManagerDesc) {
            onCallRow2Click();
        } else if (id == R.id.ivIconCall) {
            if (!TextUtils.isEmpty(callBack)) {
                onCallIconClick();
            } else {
                Toast.makeText(getActivity(), "No Callback Number found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onViewDirectionClick() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(appAddressGoogleLink));
        startActivity(intent);

    }

    private void onRmEmailIconClick() {
        String brokerName = "";
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("BrokerName"))) {
            brokerName = Utils.getConfigData(mSession).optString("BrokerName");
        }
        String subject = "Query From " + brokerName;
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] strTo = {mSession.getRMemail()};
        intent.putExtra(Intent.EXTRA_EMAIL, strTo);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setType("message/rfc822");
        intent.setPackage("com.google.android.gm");
        startActivity(intent);
    }

    private void onRMMobileIconClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mSession.getRMmobil()));
        startActivity(intent);
    }

    private void onCallIconClick() {
        Intent inten = new Intent(Intent.ACTION_DIAL);
        inten.setData(Uri.parse("tel:" + callBack));
        startActivity(inten);
    }

    private void onCallRowClick() {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + landLine));
        startActivity(i);
    }

    private void onCallRow2Click() {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + phnNo));
        startActivity(i);
    }

    private void onEmailRowClick() {
        String brokerName = "";
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("BrokerName"))) {
            brokerName = Utils.getConfigData(mSession).optString("BrokerName");
        }
        String recEmail = email; // either set to destination email or leave empty
        String subject = "Query From " + brokerName;
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] strTo = {recEmail};
        intent.putExtra(Intent.EXTRA_EMAIL, strTo);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setType("message/rfc822");
        intent.setPackage("com.google.android.gm");
        startActivity(intent);

    }

    private void onWebsiteRowClick() {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("title", "Our Website");
        intent.putExtra("url", aboutUs);
        startActivity(intent);
    }

    private void getBrokerDetails() {
        String url = Config.Get_Broker_Details;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            //  InvestmentAdvisorName",  "SEBIRegNo", "AdvisoryDivision" , "RegistrationType", "RegistrationValidation"
            //  "Address", "ContactNo" ,  "ContactPersonName", "ContactPersonNo", "ContactPersonEmail", "SEBIAddress"
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    ArrayList<JSONObject> list = new ArrayList<>();
//  {"Status":"True",
//  "ResponseData":[
//  {"InvestmentAdvisorName":"Investwell Self Test..","SEBIRegNo":"INA000015561","AdvisoryDivision":"Operation","RegistrationType":"Non-Individualk",
//  "RegistrationValidation":"Perpetual","Address":"401, Guardian Square, , CTS No. 8\/20, Erandwane, Opposite Nisarg Hotel,
//  Pune 411004","ContactNo":"+91 20 25436844","ContactPersonName":"Mr. Dhruv Lalit Mehta","ContactPersonNo":"+91 22 49120200",
//  "ContactPersonEmail":"dhruv@sapientwealth.co.in","SEBIAddress":"Securities and Exchange Board of India, SEBI Bhavan, Plot No. C4-A, ‘G’ Block,
//  Bandra Kurla Complex, Bandra (East), Mumbai 400051, Maharashtra.","AdvisoryType":"R"},
//  {"InvestmentAdvisorName":"Investwell Self Test..","SEBIRegNo":"INA00098765","AdvisoryDivision":"Operation","RegistrationType":"Individual",
//  "RegistrationValidation":"Perpetual","Address":"401, Guardian Square, , CTS No. 8\/20, Erandwane, Opposite Nisarg Hotel, Pune 411004",
//  "ContactNo":"+91 20 25436844","ContactPersonName":"Mr. Dhruv Lalit Mehta","ContactPersonNo":"+91 22 49120200","ContactPersonEmail":"dhruv@sapientwealth.co.in",
//  "SEBIAddress":"Securities and Exchange Board of India, SEBI Bhavan, Plot No. C4-A, ‘G’ Block, Bandra Kurla Complex, Bandra (East), Bangalore 400051, Karnataka.","AdvisoryType":"B"}
//  ]
//  }
                    try {

                        if (jsonObject.optBoolean("Status")) {
                            JSONArray jsonArray =  jsonObject.getJSONArray("ResponseData");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                list.add(jsonObject1);
                            }

                            contactusAdapter.updateList(list);
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
                    } else if (volleyError instanceof NoConnectionError) {
                    }


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

}