package investwell.client.fragment.mandate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;

import androidx.fragment.app.Fragment;
import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.crop.CropImage;
import investwell.utils.crop.CropImageView;

public class FragMandateUpload extends Fragment {
    private AppSession mSession;
    private BrokerActivity mBrokerActivity;
    private MainActivity mMainActivity;
    private AppApplication mApplication;
    private String fileName, UniqueRefNo, MandateID, UCC;
    private Bundle bundle;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(mBrokerActivity);
        } else if (context instanceof MainActivity) {
            this.mMainActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mMainActivity);
            mApplication = (AppApplication) mMainActivity.getApplication();
            mMainActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mMainActivity);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.frag_mandate_upload, container, false);
        mApplication = (AppApplication) getActivity().getApplication();
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();
        UniqueRefNo = bundle.getString("UniqueRefNo");
        MandateID = bundle.getString("MandateID");
        UCC = bundle.getString("UCC");
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(getActivity(), 600);
        return view;
    }
    //------------------------------------------------------------------

    public void saveMandateFile( String UniqueRefNo, String dataUri) {
        final ProgressDialog mBar = ProgressDialog.show(mMainActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.Mandate_UPLOAD_PART1;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", UCC);
            jsonObject.put("OnlineOption", mSession.getAppType());
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
                        uploadMandateFile( UniqueRefNo,  object.optString("FileName") );
                    } else {
                        Toast.makeText(mMainActivity, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mBar.dismiss();
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        Toast.makeText(mMainActivity,jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(mMainActivity, mApplication.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mMainActivity);
        requestQueue.add(jsonObjectRequest);

    }

    public void uploadMandateFile( String UniqueRefNo, String savedFileName) {
        String url = Config.Mandate_UPLOAD_PART2;
        JSONObject jsonObject = new JSONObject();
        final ProgressDialog mBar = ProgressDialog.show(mMainActivity, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        try {
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", UCC);
            jsonObject.put("FileName", savedFileName);
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("UniqueRefNo", UniqueRefNo);
            jsonObject.put("MandateID", MandateID);

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {
                        showResultDialog(object.optString("ServiceMSG"));
                    } else {
                        showResultDialog(object.optString("ServiceMSG"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mBar.dismiss();
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        Toast.makeText(mMainActivity,jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError) {
                    Toast.makeText(mMainActivity, mApplication.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(mMainActivity);
        requestQueue.add(jsonObjectRequest);
    }

    public void showResultDialog(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mMainActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mMainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.do_later_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView editText = (TextView) dialogView.findViewById(R.id.textMsg);
        editText.setText(msg);
        AlertDialog alertDialog = dialogBuilder.create();
        TextView tvOk = (TextView) dialogView.findViewById(R.id.textOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                mMainActivity.getSupportFragmentManager().popBackStack();
            }
        });
        alertDialog.show();

    }
    //--------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 600) {
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
                    saveMandateFile (UniqueRefNo, dataUri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
