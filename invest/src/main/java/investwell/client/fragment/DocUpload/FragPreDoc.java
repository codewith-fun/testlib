package investwell.client.fragment.DocUpload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.util.Objects;

import investwell.client.activity.AccountConfActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.factsheet.Utils.DialogsUtils;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;


public class FragPreDoc extends Fragment implements View.OnClickListener {

    private Bundle mBundle;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private AppSession mSession;
    public ToolbarFragment fragToolBar;
    private TextView mTvMessage, mTvContinue;
    private JSONObject object;
    private String mUcc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_pre_doc, container, false);
        mActivity = (MainActivity) getActivity();
        mBundle = getArguments();
        mSession = AppSession.getInstance(getActivity());
        setUpToolBar();
        setData(view);
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.investment_profile_pre_doc_txt), true, false, false, false, false, false, false, "");

        }
    }


    private void setData(View view) {
        try {
            object = new JSONObject(mBundle.getString("AllData"));
            mTvMessage = view.findViewById(R.id.tvMessage);
            view.findViewById(R.id.tvContinue).setOnClickListener(this);
            String message = object.optString("ServiceMSG");
            mTvMessage.setText(message);
            if (object.has("UCC")){
                mUcc = object.optString("UCC");
            }else{
                mUcc = mSession.getUCC_CODE();;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onClick(View view) {
        if (view.getId() == R.id.tvContinue) {
            try {

                if (mBundle.containsKey("comingFrom")) {
                    if (Utils.getConfigData(mSession).optString("SignRequired").equalsIgnoreCase("Y")) {
                        mBundle.putString("ucc_code", mUcc);
                        mActivity.displayViewOther(124, mBundle);
                        // mActivity.displayViewOther(93, mBundle);
                    } else {
                        startActivity(new Intent(getActivity(), AccountConfActivity.class));
                    }
                } else {

                    checkIINDialog(object);
                }

                   /* Intent intent = new Intent(mActivity, SignatureActivity.class);
                    intent.putExtra("ucc_code", object.optString("UCC"));
                    intent.putExtra("chequeRequired", object.optString("ChequRequired"));
                    intent.putExtra("onscreen", "true");
                    startActivityForResult(intent, 100);*/


            } catch (Exception e) {

            }
        }

    }


    public void checkIINDialog(final JSONObject object) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_common_application, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        CustomButton btDone = dialogView.findViewById(R.id.btDone);
        btDone.setText(R.string.alert_dialog_yes_btn_txt);
        CustomButton btCalcel = dialogView.findViewById(R.id.btCalcel);
        btCalcel.setText(R.string.alert_dialog_btn_no_txt);
        tvTitle.setText(R.string.fatca_instruction);
        tvMessage.setText(R.string.iin_verify_txt);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        btDone.setVisibility(View.VISIBLE);
        btCalcel.setVisibility(View.VISIBLE);


        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                checkIIN(object);
            }
        });
        btCalcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void checkIIN(final JSONObject object) {
        DialogsUtils.showProgressBar(getActivity(), false);
        String url = Config.IIN_Check;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey",mSession.getPassKey());
            jsonObject.put("UCC",mUcc);
            jsonObject.put("OnlineOption",mSession.getAppType());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DialogsUtils.hideProgressBar();
                    if (response.optString("Status").equalsIgnoreCase("True")){
                        if (Utils.getConfigData(mSession).optString("SignRequired").equalsIgnoreCase("Y")) {
                            //mActivity.displayViewOther(93, mBundle);
                            mBundle.putString("ucc_code", mUcc);
                            mActivity.displayViewOther(124, mBundle);
                        } else {
                            startActivity(new Intent(getActivity(), AccountConfActivity.class));
                        }
                    }else{
                        mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), response.optString("ServiceMSG"), "message",false,true);
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
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
