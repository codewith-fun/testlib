package investwell.client.fragment.nfo.Fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Objects;

import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.client.activity.SignUpActivity;
import investwell.client.fragment.nfo.Adapter.NFOcartAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomButton;


public class FragNFOCart extends Fragment implements View.OnClickListener {
    public ToolbarFragment fragToolBar;
    private AppSession mSession;
    private MainActivity mActivity;
    private RecyclerView mNFOcartRecycle;
    private NFOcartAdapter nfOcartAdapter;
    private List<JSONObject> mCartList;
    private String AlreadyUser = "false";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_nfocart, container, false);
        mCartList = new ArrayList<>();
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();
        setUpToolBar();
        view.findViewById(R.id.create_acnt_btn).setOnClickListener(this);
        mNFOcartRecycle = view.findViewById(R.id.nfo_cart_recycle);
        mNFOcartRecycle.setHasFixedSize(true);
        mNFOcartRecycle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        nfOcartAdapter = new NFOcartAdapter(getActivity(), new ArrayList<JSONObject>());
        mNFOcartRecycle.setAdapter(nfOcartAdapter);
        setItemToList();
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.nfo_text), true, false, false, false, false, false,false,"");


        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showMessageDialog(Context context, String title, String message, String mBtnNeg, String mBtnPos, String mNavTo) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.msg_txt);

        tvTitle.setText(title);
        tvMessage.setText(message);

        CustomButton tvOk = dialogView.findViewById(R.id.tvOk);
        CustomButton tvcancel = dialogView.findViewById(R.id.cancel);
        tvcancel.setText(mBtnNeg);
        tvOk.setText(mBtnPos);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                if (mNavTo.equalsIgnoreCase("withoutLogin")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));

                }else if(mNavTo.equalsIgnoreCase("pros")){
                    createInvestmentProfile();
                }


            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        alertDialog.setCancelable(false);
        alertDialog.show();


    }
    public void onClick(View view) {
        if (view.getId() == R.id.create_acnt_btn) {
            if (!mSession.getHasLoging()) {
                showMessageDialog(mActivity, "Message", "Please login to make this transaction", "Cancel", "Login", "withoutLogin");
            } else if (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects")) {
                // callInvestNowApi("lumsum");
                showMessageDialog(mActivity, "Message", "Investment Profile is required to make this transaction", "Cancel", "Create Profile", "pros");

            } else {
                Bundle bundle = new Bundle();
                bundle.putString("type", "coming_from_nfo");
                mActivity.displayViewOther(36, bundle);
            }
        }
    }
    private void createInvestmentProfile() {
        if (mSession.getHasLoging()) {
            Bundle bundle = new Bundle();
            AlreadyUser = "true";
            bundle.putString("AlreadyUser", AlreadyUser);
            mActivity.displayViewOther(5, bundle);


        } else {
            startActivity(new Intent(getActivity(), SignUpActivity.class));
        }
    }
    private void callInvestNowApi(final String type) {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = "";
        if (type.equals("lumsum")) {
            url = Config.INSERT_INTO_LUMPSUM;
        } else {
            url = Config.INSERT_INTO_SIP;
        }
        JSONObject jsonParam = new JSONObject();

        try {
            String FCode = "";
            String SCode = "";

            if (Config.BASKET_SCHEMES_LIST.size() > 0) {
                for (int i = 0; i < Config.BASKET_SCHEMES_LIST.size(); i++) {
                    JSONObject jsonObject = Config.BASKET_SCHEMES_LIST.get(i);

                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                    }

                }

            } else {
                JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (i == 0) {
                        FCode = jsonObject.optString("Fcode");
                        SCode = jsonObject.optString("Scode");
                    } else {
                        FCode = FCode + "|" + jsonObject.optString("Fcode");
                        SCode = SCode + "|" + jsonObject.optString("Scode");
                    }
                }
            }


            jsonParam.put("UCC", mSession.getUCC_CODE());
            jsonParam.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonParam.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonParam.put("Fcode", FCode);
            jsonParam.put("Scode", SCode);
            jsonParam.put("FolioNo", "0|0|0|0|0");
            jsonParam.put("Reinvest", "Z|Z|Z|Z|Z");
            jsonParam.put("Amount", "0|0|0|0|0");
            jsonParam.put("Installment", "0|0|0|0|0");
            jsonParam.put(AppConstants.PASSKEY, mSession.getPassKey());
            //jsonParam.put("BasketCode", mSession.getSelectedBasketCode());
            jsonParam.put("BasketCode", "00");
            jsonParam.put("OnlineOption", mSession.getAppType());

            System.out.println("LUMPSUM" + jsonParam);


        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        mSession.setAddToCartList("");
                        // mSession.setSelectedBasketCode("");
                        Config.BASKET_SCHEMES_LIST.clear();
                        //mActivity.removeAllStack();

                        if (type.equals("lumsum")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mSession.getUCC_CODE());
                            mActivity.displayViewOther(14, bundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("ucc_code", mSession.getUCC_CODE());
                            mActivity.displayViewOther(13, bundle);
                        }


                    } else {
                        Toast.makeText(mActivity, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, volleyError -> {
            mBar.dismiss();

            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                try {
                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(error.getMessage()));
                    Toast.makeText(mActivity, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (volleyError instanceof NoConnectionError)
                Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);
    }

    private void setItemToList() {
        try {
            JSONArray jsonArray = new JSONArray(mSession.getAddToNFOCartList());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                mCartList.add(jsonObject);
                nfOcartAdapter.updateList(mCartList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } /*finally {
            if (mCartList.size() == 0) {
                mLinerEmpty.setVisibility(View.VISIBLE);
                mRelativeMain.setVisibility(View.GONE);
            } else {
                mLinerEmpty.setVisibility(View.GONE);
                mRelativeMain.setVisibility(View.VISIBLE);
            }*/
        }
    }

