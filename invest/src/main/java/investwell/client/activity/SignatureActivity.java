package investwell.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;

import investwell.client.fragment.others.ToolbarFragment;
import investwell.common.basic.BaseActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.crop.CropImage;
import investwell.utils.crop.CropImageView;
import investwell.utils.customView.CustomDialog;
import investwell.utils.signature.SignatureView;

public class SignatureActivity extends BaseActivity implements View.OnClickListener, CustomDialog.DialogBtnCallBack{
    private SignatureView signatureView;
    static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_GALLERY_IMAGE = 2;
    private String mBase64Image = "";
    private ProgressDialog mBar;
    private AppSession mSession;
    private String imageURl = "";
    private String mUCC_Code = "", mChequeRequired = "",CommingFrom="";
    private ImageView mImageView;
    private String mFileName;
    private AppApplication mApplication;
    private Button mClearbtn;
    private CustomDialog customDialog;
    private ToolbarFragment fragmentToolBar;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("signUploaded", "no");
        setResult(100, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature_activity);
        mSession = AppSession.getInstance(this);
        mApplication = (AppApplication) getApplication();
        signatureView = findViewById(R.id.signature_view);
        customDialog = new CustomDialog(SignatureActivity.this);
        signatureView.setPenColor(ContextCompat.getColor(this, R.color.colorBlack));
        mImageView = findViewById(R.id.image_view);
        mClearbtn = findViewById(R.id.tvClear);
        findViewById(R.id.tvClear).setOnClickListener(this);
        findViewById(R.id.gallery_button).setOnClickListener(this);
        findViewById(R.id.tvSave).setOnClickListener(this);

        if(getIntent()!=null) {
            mUCC_Code = getIntent().getStringExtra("ucc_code");
            if (getIntent().getStringExtra("chequeRequired") == null) {
                mChequeRequired = "Y";
            } else {
                mChequeRequired = getIntent().getStringExtra("chequeRequired");
            }

            if (getIntent().hasExtra("coming_from")){
                CommingFrom = getIntent().getStringExtra("coming_from");
            }

            getsetData();
        }
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    //Error Content Initializer
    private void errorContentInitializer(View view) {
        viewNoData = view.findViewById(R.id.content_no_data);
        tvErrorMessage = viewNoData.findViewById(R.id.tv_error_message);
        ivErrorImage = viewNoData.findViewById(R.id.iv_error_image);
    }

    //Display Connection TimeOut Content
    private void displayConnectionTimeOut() {
        viewNoData.setVisibility(View.VISIBLE);
        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.error_connection_timeout);
    }

    //Display Server Error Content
    private void displayServerErrorMessage(VolleyError error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error.getLocalizedMessage());
    }
    //Display Server Error Content
    private void displayServerMessage(String error) {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_data_found);
        tvErrorMessage.setText(error);
    }
    //Display Network Error Content
    private void displayNoInternetMessage() {
        viewNoData.setVisibility(View.VISIBLE);

        ivErrorImage.setImageResource(R.drawable.bg_no_interent);
        tvErrorMessage.setText(R.string.no_internet);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }



    private void getsetData(){

        if (getIntent().getStringExtra("onscreen").equalsIgnoreCase("true")){
            mClearbtn.setText("Clear");
            signatureView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
        }else{
            signatureView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            mClearbtn.setText("Reupload");
            CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);


        }

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tvClear) {
            if (getIntent().getStringExtra("onscreen").equalsIgnoreCase("true")) {
                signatureView.clearCanvas();
                mImageView.setImageDrawable(null);
                mImageView.setVisibility(View.GONE);
                signatureView.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        } else if (id == R.id.tvSave) {
            if (mImageView.getVisibility() == View.VISIBLE) {
                saveFileOnServer();
            } else {
                saveImage();
            }
        } else if (id == R.id.gallery_button) {
            CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }

    }


    private void saveImage() {
        try {
            Bitmap bitmap = signatureView.getSignatureBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] byteArray = baos.toByteArray();
            String b64String = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            // mBase64Image = b64String;
            mBase64Image = "data:image/png;base64," + b64String;
            saveFileOnServer();
        } catch (Exception e) {

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mImageView.setVisibility(View.VISIBLE);
                signatureView.setVisibility(View.GONE);
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                try {
                    Uri selectedImage = result.getUri();
                    if (selectedImage != null) {
                        Bitmap gallery_bit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        // gallery_bit = Bitmap.createScaledBitmap(gallery_bit, 1200, 1200, true);
                        int height = gallery_bit.getHeight();
                        int width = gallery_bit.getWidth();
                        int newHeight = 0;
                        int newWidth = 0;
                        if (height > 5000 || width > 5000) {
                            newHeight = height / 100 * 20;
                            newWidth = width / 100 * 20;
                        } else if (height > 4000 || width > 4000) {
                            newHeight = height / 100 * 25;
                            newWidth = width / 100 * 25;
                        } else if (height > 4000 || width > 3500) {
                            newHeight = height / 100 * 30;
                            newWidth = width / 100 * 30;
                        } else if (height > 3500 || width > 3000) {
                            newHeight = height / 100 * 40;
                            newWidth = width / 100 * 40;
                        } else if (height > 3000 || width > 2500) {
                            newHeight = height / 100 * 45;
                            newWidth = width / 100 * 45;
                        } else if (height > 2500 || width > 2000) {
                            newHeight = height / 100 * 50;
                            newWidth = width / 100 * 50;
                        } else if (height > 2500 || width > 1500) {
                            newHeight = height / 100 * 55;
                            newWidth = width / 100 * 55;
                        } else if (height > 2000 || width > 1500) {
                            newHeight = height / 100 * 60;
                            newWidth = width / 100 * 60;
                        } else {
                            newHeight = height;
                            newWidth = width;
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        if (newHeight > 0 && newWidth > 0) {
                            gallery_bit = Bitmap.createScaledBitmap(gallery_bit, newWidth, newHeight, true);

                            // if (newHeight > 1000 || width > 1000)
                            gallery_bit.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        } else {
                            gallery_bit.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                        }

                        height = gallery_bit.getHeight();
                        width = gallery_bit.getWidth();
                        System.out.println(height + width);

                        byte[] byteArray = baos.toByteArray();
                        String b64String = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                        mBase64Image = "data:image/png;base64," + b64String;
                        mImageView.setImageBitmap(gallery_bit);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                mApplication.showSnackBar(mImageView, getResources().getString(R.string.main_activity_crop_error));
            }
        } else {

            mApplication.showSnackBar(mImageView, getResources().getString(R.string.main_activity_crop_error));
        }

    }


    private void saveFileOnServer() {
        mBar = ProgressDialog.show(SignatureActivity.this, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.SIGNATURE_UPLOAD_1;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put("ImageString", "" + mBase64Image);
            jsonObject.put(AppConstants.PASSKEY, mSession.getPassKey());
            jsonObject.put("OnlineOption", mSession.getAppType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optString("Status").equals("True")) {

                        mFileName = object.optString("FileName");
                        mApplication.showSnackBar(mImageView, object.optString("ServiceMSG"));
                        Intent intent = getIntent();
                        intent.putExtra("signUploaded", "yes");
                        intent.putExtra("ucc_code", mUCC_Code);
                        intent.putExtra("chequeRequired", mChequeRequired);
                        intent.putExtra("File1",mFileName);
                        intent.putExtra("coming_from",CommingFrom);
                        setResult(100, intent);
                        finish();
                       // NextStep(intent);

                    } else {
/*
                        Toast.makeText(SignatureActivity.this, object.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
*/
                        mApplication.showSnackBar(mImageView, object.optString("ServiceMSG"));
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
                        mApplication.showSnackBar(mImageView, jsonObject.optString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)

                    mApplication.showSnackBar(mImageView, getResources().getString(R.string.no_internet));
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

        RequestQueue requestQueue = Volley.newRequestQueue(SignatureActivity.this);
        requestQueue.add(jsonObjectRequest);
    }


    private void NextStep(final Intent intent){
        mBar = ProgressDialog.show(SignatureActivity.this, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.SIGNATURE_UPLOAD_2;
        JSONObject jsonObject = new JSONObject();
        String FileName = intent.getStringExtra("File1");
        if (intent.getStringExtra("chequeRequired").equalsIgnoreCase("Y")){
            mBar.dismiss();
            setResult(100, intent);
            finish();
        }else{
            try{
                jsonObject.put("Bid", AppConstants.APP_BID);
                jsonObject.put("ucc_code", mUCC_Code);
                jsonObject.put("FileName",FileName);
                jsonObject.put("Passkey", mSession.getPassKey());
                jsonObject.put("OnlineOption", mSession.getAppType());
                jsonObject.put("ChequeFileName","");

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mBar.dismiss();
                        if (response.optString("Status").equalsIgnoreCase("True")){

                            setResult(100, intent);
                            finish();
                        }else{

                              mApplication.showSnackBar(mImageView,response.optString("ServiceMSG"));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mBar.dismiss();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            mApplication.showCommonDailog(SignatureActivity.this, SignatureActivity.this, false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message", false, true);
                        } else {
                            mApplication.showCommonDailog(SignatureActivity.this, SignatureActivity.this, false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message", false, true);
                        }
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

                RequestQueue requestQueue = Volley.newRequestQueue(SignatureActivity.this);
                requestQueue.add(jsonObjectRequest);

            }catch (Exception e){
                e.printStackTrace();
            }

        }




    }
    @Override
    public void onDialogBtnClick(View view) {

    }
}