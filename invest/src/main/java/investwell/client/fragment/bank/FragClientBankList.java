package investwell.client.fragment.bank;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.crop.CropImage;



public class FragClientBankList extends Fragment implements View.OnClickListener {
    private ProgressDialog mBar;
    private RecycleClientBankListAdapter mAdapter;
    private AppSession mSession;
    private MainActivity mActivity;
    private AppApplication mApplication;
    private TextView mTvNothing;
    private String mType = "";
    private investwell.utils.customView.CustomButton mCreateButton;
    private String mUCC_Code = "";
    private ToolbarFragment fragToolBar;
    private boolean isMandate = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mActivity);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }


    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            if (!isMandate){
                mCreateButton.setVisibility(View.VISIBLE);
            fragToolBar.setUpToolBar(getResources().getString(R.string.add_bank_text),
                    true, false, false, false, false, false, false, "");
        }else {
                mCreateButton.setVisibility(View.GONE);
                fragToolBar.setUpToolBar(getResources().getString(R.string.select_bank_text),
                        true, false, false, false, false, false, false, "");
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_client_bank_list, container, false);


        mCreateButton = view.findViewById(R.id.create_acnt_btn);
        mCreateButton.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("ucc_code")) {
            mUCC_Code = bundle.getString("ucc_code");
            isMandate = bundle.getBoolean("isMandate");
        }
        setUpToolBar();

        RecyclerView recycleView = view.findViewById(R.id.recycleView);
        mTvNothing = view.findViewById(R.id.tvNothing);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new RecycleClientBankListAdapter(getActivity(), new ArrayList<JSONObject>(), mType, new RecycleClientBankListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String bankName,accountNo,ifscCode;
                if (mSession.getAppType().equalsIgnoreCase("N")
                        || mSession.getAppType().equalsIgnoreCase("DN")){

                    Bundle bundle = new Bundle();
                    JSONObject jsonObject = mAdapter.mDataList.get(position);
                    bankName = jsonObject.optString("BankName");
                    accountNo = jsonObject.optString("AccountNo");
                    ifscCode = jsonObject.optString("IFSCCode");

                    bundle.putString("ucc_code", mUCC_Code);
                    bundle.putString("BankName", bankName);
                    bundle.putString("AccountNo", accountNo);
                    bundle.putString("IFSCCode", ifscCode);

                    mActivity.displayViewOther(12, bundle);
                }

              /*  Bundle bundle = new Bundle();
                bundle.putString("type", "coming_from_dashborad");
                bundle.putString("ucc_code", jsonObject.optString("UCC"));
                mActivity.displayViewOther(11, bundle);*/


            }
        });
        recycleView.setAdapter(mAdapter);

        getBankList();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create_acnt_btn) {
            Bundle bundle = new Bundle();
            bundle.putString("ucc_code", mUCC_Code);
            mActivity.displayViewOther(118, bundle);
        }
    }

    private void getBankList() {
        final ProgressDialog mBar = ProgressDialog.show(mActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.Bank_detail;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    mBar.dismiss();
                    try {
                        List<JSONObject> mBankList = new ArrayList();
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            JSONArray GetNSEBankList = jsonObject.optJSONArray("GetNSEBankList");
                            for (int i = 0; i < GetNSEBankList.length(); i++) {
                                JSONObject jsonObject1 = GetNSEBankList.optJSONObject(i);
                                if (!jsonObject1.optString("BankStatus").equalsIgnoreCase("Not Activated")){
                                    mBankList.add(jsonObject1);
                                }

                                Log.i("allbanks", "onResponse: activated"+jsonObject1);
                            }
                            mAdapter.updateList(mBankList);

                        } else {
                            if (jsonObject.optString("ServiceMSG").equalsIgnoreCase("Invalid Passkey")) {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "invalidPasskey", false, true);
                            } else {
                                mApplication.showCommonDailog(mActivity, mActivity, false, "Error", jsonObject.optString("ServiceMSG"), "message", false, true);
                            }
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
                        JSONObject jsonObject12 = new JSONObject(error.getMessage());
                        Toast.makeText(mActivity, jsonObject12.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 108) {
            CropImage.ActivityResult imageResult = CropImage.getActivityResult(data);
            try {
                Uri selectedImage = imageResult.getUri();
                if (selectedImage != null) {
                    Bitmap gallery_bit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    gallery_bit.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] byteArray = baos.toByteArray();
                    String b64String = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    String dataUri = "data:image/jpeg;base64," + b64String;
                    uploadFile( dataUri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void uploadFile(String dataUri) {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.UPLOAD_FILE_NSE_BANK;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            if (mActivity.mBankObjectOnBankList!=null){
                jsonObject.put("BankCode", mActivity.mBankObjectOnBankList.optString("BankCode"));
                jsonObject.put("AccountNo", mActivity.mBankObjectOnBankList.optString("AccountNo"));
            }

            jsonObject.put("IIN", mUCC_Code);
            jsonObject.put("ImageString", dataUri);


        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        Toast.makeText(getActivity(), object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
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
                    JSONObject jsonObject1 = new JSONObject(error.getMessage());
                    Toast.makeText(mActivity, jsonObject1.optString("error"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (volleyError instanceof NoConnectionError) {
                Toast.makeText(mActivity, mApplication.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(jsonObjectRequest);

    }

}
