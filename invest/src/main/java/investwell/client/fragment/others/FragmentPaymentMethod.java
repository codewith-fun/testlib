package investwell.client.fragment.others;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import investwell.client.activity.MainActivity;
import investwell.client.activity.WebViewActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragmentPaymentMethod extends Fragment {

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private String Bseurl = "", mUcc = "";
    private AppSession mSession;
    private Bundle bundle;
    private MainActivity mainActivity;
    private AppCompatRadioButton mRbNetbanking, mRbBse;
    private ToolbarFragment toolbarFragment;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        mSession = AppSession.getInstance(mainActivity);
        mainActivity.setMainVisibility(this, null);
        setUpToolBar();
        bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUcc = bundle.getString("ucc_code");
        } else {
            mUcc = mSession.getUCC_CODE();
        }

        getBSEData();
        mRbNetbanking = view.findViewById(R.id.rb_direct_netbanking);
        mRbBse = view.findViewById(R.id.rb_bse);


        view.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mRbNetbanking.isChecked() == true && mRbBse.isChecked() == false) {

                   /* Intent intent = new Intent(mainActivity, PaymentNewActivity.class);
                    intent.putExtra("ucc_code", mUcc);
                    intent.putExtra("type", bundle.getString("call_from"));
                    startActivityForResult(intent, 500);*/
                    mainActivity.displayViewOther(84, bundle);


                } else if (mRbBse.isChecked() == true && mRbNetbanking.isChecked() == false) {

                    Intent intent = new Intent(mainActivity, WebViewActivity.class);
                    intent.putExtra("url", Bseurl);
                    intent.putExtra("title", "Payment");

                    startActivity(intent);
                }

            }
        });


        mRbBse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRbBse.setButtonDrawable(R.drawable.checked_circle);
                mRbNetbanking.setButtonDrawable(R.drawable.blank_circle);
                mRbNetbanking.setChecked(false);
                mRbBse.setChecked(true);

            }
        });

        mRbNetbanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRbNetbanking.setButtonDrawable(R.drawable.checked_circle);
                mRbBse.setButtonDrawable(R.drawable.blank_circle);
                mRbBse.setChecked(false);
                mRbNetbanking.setChecked(true);


            }
        });
        return view;
    }

    private void setUpToolBar() {
        toolbarFragment
                = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment
                != null) {
            toolbarFragment
                    .setUpToolBar(getResources().getString(R.string.Pre_Payment_Method_toolbar_title), true, false, false, false, false, false, false, "");
        }
    }


    private void getBSEData() {
        DialogsUtils.showProgressBar(mainActivity, false);
        final String url = Config.BSE_Gateway;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", mUcc);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    if (response.optString("Status").equalsIgnoreCase("True")) {
                        Bseurl = response.optString("ServiceMSG");
                    } else {
                        Toast.makeText(mainActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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
            requestQueue = Volley.newRequestQueue(mainActivity);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}