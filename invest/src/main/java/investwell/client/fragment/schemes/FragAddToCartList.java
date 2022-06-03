package investwell.client.fragment.schemes;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
import java.util.Objects;

import investwell.client.activity.AppApplication;
import investwell.client.activity.LoginActivity;
import investwell.client.activity.MainActivity;
import investwell.client.activity.SignUpActivity;
import investwell.client.adapter.RecycleCartListAdapter;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.Utils;
import investwell.utils.customView.CustomButton;


public class FragAddToCartList extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private List<JSONObject> mCartList;
    private RecycleCartListAdapter mAdapter;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private investwell.utils.customView.CustomButton create_acnt_btn;
    private RelativeLayout mRelativeMain;
    private LinearLayout mLinerEmpty;
    private Bundle bundle;
    private ToolbarFragment toolbarFragment;
    private JSONArray araArray;
    private boolean direct = false;
    private String mUCC = "", ActiveStatus = "";
    private int ArraySize = 0;
    private String AlreadyUser = "false";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_to_cart_list, container, false);

        mCartList = new ArrayList<>();

        setUpToolBar();


        RecyclerView recyclerView = view.findViewById(R.id.rv_cart_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_singles = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager_singles);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

        mLinerEmpty = view.findViewById(R.id.linerEmpty);
        mRelativeMain = view.findViewById(R.id.relMain);

        create_acnt_btn = view.findViewById(R.id.create_acnt_btn);
        if (mSession.getHasLoging()) {
            create_acnt_btn.setText(R.string.lumpsum_order_now_btn_txt);
        }else{
            create_acnt_btn.setText("Sign-In");
        }
        create_acnt_btn.setOnClickListener(this);
        bundle = getArguments();
        mUCC = mSession.getUCC_CODE();

        mAdapter = new RecycleCartListAdapter(mActivity, mRelativeMain, mLinerEmpty, new ArrayList<JSONObject>(), position -> {
            try {
                JSONObject jsonObject = mAdapter.mDataList.get(position);
                /*Intent intent = new Intent(mActivity, FragRecomendedAddToCart.class);
                mDataList.put("isSelected", true);
                intent.putExtra("AllData", mAdapter.mDataList.toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

     /*   if (mSession.getHasLoging() && mSession.getImageUploaded().equals("Y") ) {
            create_acnt_btn.setText("Order Now");
        } else if (mSession.getUCC_CODE().isEmpty() || mSession.getUCC_CODE().equalsIgnoreCase("NA")){
            create_acnt_btn.setText("Create Investment Account");
        }else{
            //Change by RAJ KUmar on 24/10/2018
            create_acnt_btn.setText("Order Now");
        }
        */

        view.findViewById(R.id.scheme_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = new Bundle();
                MainActivity mainActivity = (MainActivity) mActivity;
                mainActivity.displayViewOther(41, bundle1);
                // startActivity(new Intent(mActivity,TopSchemeFragment.class));
            }
        });

        getProfileList();
        recyclerView.setAdapter(mAdapter);
        setItemToList();
        return view;
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_my_cart), true, false, false, false, false, false, false, "");
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
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_acnt_btn) {
            if (mSession.getHasLoging()) {
                try {
                    if (mSession.getUserType().equalsIgnoreCase("RM")) {
                        JSONArray jsonArray = new JSONArray(mSession.getTransactionPermission());
                        JSONObject jsonObject = jsonArray.optJSONObject(0);

                        if (jsonObject.optString("AdditionalPurchase").equalsIgnoreCase("Y")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "coming_from_dashborad");
                            if (ArraySize > 1 || ActiveStatus.equalsIgnoreCase("NO")) {
                                mActivity.displayViewOther(36, bundle);
                            } else {
                                bundle.putString("ucc_code", mUCC);
                                mActivity.displayViewOther(11, bundle);
                            }
                        } else {
                            mApplication.showCommonDailog(mActivity, getActivity(), false, getString(R.string.dialog_title_permission_denied), getString(R.string.dialog_permission_denied_message_for_RM), "message", false, true);
                        }
                    } else if (mSession.getHasLoging() && mSession.getLoginType().equals("Prospects")) {
                        showMessageDialog(mActivity, "Message", "Investment Profile is required to make this transaction", "Cancel", "Create Profile", "pros");

                    } else {
                        if (mSession.getHasLoging()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "coming_from_dashborad");
                            if (ArraySize > 1 || ActiveStatus.equalsIgnoreCase("NO")) {
                                mActivity.displayViewOther(36, bundle);
                            } else {
                                bundle.putString("ucc_code", mUCC);
                                mActivity.displayViewOther(11, bundle);
                            }
                        } else {
                            //   showDialog(getString(R.string.no_investment_profile), getString(R.string.no_investment_txt));
                            showInvestDialog(mActivity, getString(R.string.no_investment_profile), getString(R.string.no_investment_txt));
                            // mApplication.showCommonDailog(mActivity, getActivity(), false, getString(R.string.no_investment_profile), getString(R.string.no_investment_txt), "message", false, true);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));

            }
        }


    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showInvestDialog(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        CardView linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.msg_txt);
        ImageView ivClose=dialogView.findViewById(R.id.ivCloseBtn);
        ivClose.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        tvMessage.setText(message);

        CustomButton tvOk = dialogView.findViewById(R.id.tvOk);
        CustomButton tvcancel = dialogView.findViewById(R.id.cancel);
        tvOk.setText("Sign In");
        tvcancel.setText("Sign Up");
        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
            tvcancel.setVisibility(View.VISIBLE);
        } else {
            tvcancel.setVisibility(View.GONE);
        }

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                alertDialog.dismiss();



            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                alertDialog.dismiss();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();


    }
    private void showDialog(String titile, String message) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.no_investment_profile_dialog);

        TextView mTvTitle = dialog.findViewById(R.id.tvTitle);
        TextView mTvMessage = dialog.findViewById(R.id.tvMessage);

        mTvTitle.setText(titile);
        mTvMessage.setText(message);
        if (Utils.getConfigData(mSession).optString("OnBoarding").equalsIgnoreCase("Y")) {
            dialog.findViewById(R.id.tvSignup).setVisibility(View.VISIBLE);
        } else {
            dialog.findViewById(R.id.tvSignup).setVisibility(View.GONE);
        }
        dialog.findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                //  getActivity().finish();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tvSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                //   getActivity().finish();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.ivCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();


    }


    private void setItemToList() {
        try {
            JSONArray jsonArray = new JSONArray(mSession.getAddToCartList());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                mCartList.add(jsonObject);
                mAdapter.updateList(mCartList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (mCartList.size() == 0) {
                mLinerEmpty.setVisibility(View.VISIBLE);
                mRelativeMain.setVisibility(View.GONE);
            } else {
                mLinerEmpty.setVisibility(View.GONE);
                mRelativeMain.setVisibility(View.VISIBLE);
            }
        }
    }


    private void getProfileList() {


        String url = Config.PROFILE_LIST;
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Cid", mSession.getCID());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.optString("Status").equalsIgnoreCase("True")) {
                            araArray = response.getJSONArray("ProfileListDetail");
                            ArraySize = araArray.length();
                            if (ArraySize == 1) {
                                ActiveStatus = araArray.optJSONObject(0).optString("ActiveStatus");
                            }
                            mUCC = araArray.optJSONObject(0).optString("UCC");
                        } else {
                            ArraySize = 0;
                        }
                       /* if (araArray.getJSONObject(0).optString("ActiveStatus").equalsIgnoreCase("NO")){
                            direct = false;
                        }else {
                            direct = true;
                        }
*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {


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

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


