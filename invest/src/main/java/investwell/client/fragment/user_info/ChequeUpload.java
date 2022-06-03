package investwell.client.fragment.user_info;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;

import investwell.client.activity.AccountConfActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.crop.CropImage;
import investwell.utils.crop.CropImageView;
import investwell.utils.customView.CustomButton;


public class ChequeUpload extends Fragment implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1,REQUEST_GALLERY_IMAGE = 2;
    private ImageView mImageView,mIvChequePreview;
    private AppSession mSession;
    private String mBase64Image;
    private String mUCC_Code = "",FileName = "",mComingFrom="";
    private ProgressDialog mBar;
    private LinearLayout mimage_layout,mbutton_layout ;
    private Bundle bundle;
    private MainActivity mActivity;
    private ToolbarFragment fragmentToolBar;
    private AppApplication mApplication;
    private CustomButton mTvReupload;
    private Uri mCropImageUri;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity = (MainActivity) getActivity();
            mSession = AppSession.getInstance(getActivity());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_cheque_upload, container, false);
        mApplication = (AppApplication) getActivity().getApplication();
        setUpToolBar();
        mImageView = view.findViewById(R.id.cheque_image);
        mIvChequePreview = view.findViewById(R.id.ivChequePreview);
        bundle = getArguments();
        mimage_layout = view.findViewById(R.id.image_layout);
        mbutton_layout = view.findViewById(R.id.button_layout);
        mTvReupload = view.findViewById(R.id.tvReupload);
        view.findViewById(R.id.tvReupload).setOnClickListener(this);
        view.findViewById(R.id.tvSave).setOnClickListener(this);
        view.findViewById(R.id.gallery_btn).setOnClickListener(this);

        if (bundle!=null && bundle.containsKey("ucc_code")){
            mUCC_Code = bundle.getString("ucc_code");
            FileName = bundle.getString("file1");
            if(bundle.containsKey("coming_from"))
                mComingFrom=bundle.getString("coming_from");
        }else if (bundle!=null && bundle.containsKey("AllData")){
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("AllData"));
                mUCC_Code = jsonObject.optString("ucc_code");
                mComingFrom=jsonObject.optString("coming_from");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            mUCC_Code = mSession.getUCC_CODE();
        }
        // getSetData();

        return view;
    }

    private void getSetData(){
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(getActivity());
    }

    private void setUpToolBar() {
        fragmentToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragmentToolBar != null) {
            fragmentToolBar.setUpToolBar(getResources().getString(R.string.investment_profile_upload_cheque_txt),true, false, false, false,false,false,false,"");
        }
    }
    @SuppressLint("NewApi")
    public void onSelectImageClick() {
        if (CropImage.isExplicitCameraPermissionRequired(mActivity)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(mActivity);
        }
    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.back_arrow) {
            mActivity.displayViewOther(0, null);
        } else if (id == R.id.tvReupload) {//  CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(mActivity);
            CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(mActivity);
            // onSelectImageClick();
        } else if (id == R.id.tvSave) {
            if (mImageView.getDrawable() == null) {
                Toast.makeText(getActivity(), "Upload Image First", Toast.LENGTH_SHORT).show();

            } else {
                saveData();
            }
        } else if (id == R.id.gallery_btn) {
            CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(mActivity);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mimage_layout.setVisibility(View.VISIBLE);
        mbutton_layout.setVisibility(View.GONE);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mimage_layout.setVisibility(View.VISIBLE);
                mbutton_layout.setVisibility(View.GONE);
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    Uri selectedImage = result.getUri();
                    if (selectedImage != null) {
                        Bitmap gallery_bit = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), selectedImage);
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
                        }else{
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
                        mBase64Image = b64String;
                        mImageView.setImageBitmap(gallery_bit);
                        mIvChequePreview.setVisibility(View.GONE);
                        mTvReupload.setText(R.string.Reupload);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else  {
                mApplication.showSnackBar(mImageView, getResources().getString(R.string.main_activity_crop_error));

            }
        }

        else  {

            mApplication.showSnackBar(mImageView, getResources().getString(R.string.main_activity_crop_error));
        }



    }

    public void saveData() {
        mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        bundle.putString("ucc_code",mUCC_Code);

        String url = Config.Cheque_Upload;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put("ImageString", "data:image/png;base64,"+mBase64Image);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("OnlineOption", mSession.getAppType());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mBar.dismiss();
                    if (response.optString("Status").equalsIgnoreCase("TRUE")) {
                        if (bundle!=null ){
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();

                           /* if (mComingFrom.equalsIgnoreCase("profile_list_cheque")||mComingFrom.equalsIgnoreCase("incomplete_profile")){
                                Intent intent = new Intent(getActivity(), AccountConfActivity.class);
                                intent.putExtra("coming_from",mComingFrom);
                                startActivity(intent);
                            }else{
                                NextStep(response.optString("FileName"));
                            }*/

                        }
                    } else {

                        mApplication.showCommonDailog(mActivity, getActivity(), false, "Error", response.optString("ServiceMSG"), "message",false,true);
                    }



                }
            }, error -> {
                mBar.dismiss();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Server_Error), error.getLocalizedMessage(), "message",false,true);
                } else{
                    mApplication.showCommonDailog(mActivity, getActivity(), false, getResources().getString(R.string.Error), getResources().getString(R.string.no_internet), "message",false,true);
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

    private void NextStep(String ChequeFileName){
        mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.SIGNATURE_UPLOAD_2;
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", mUCC_Code);
            jsonObject.put("FileName",FileName);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("OnlineOption", mSession.getAppType());
            jsonObject.put("ChequeFileName",ChequeFileName);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    mBar.dismiss();
                    if (response.optString("Status").equalsIgnoreCase("True")){

                        if (bundle!=null ){
                            Intent intent = new Intent(getActivity(), AccountConfActivity.class);
                            intent.putExtra("coming_from",mComingFrom);
                            startActivity(new Intent(getActivity(), AccountConfActivity.class));
                        }else {

                            startActivity(new Intent(getActivity(), AccountConfActivity.class));
                        }
                    }else{
                        mApplication.showCommonDailog(mActivity, getActivity(), false, "Server Response", response.optString("ServiceMSG"), "message",false,true);

                    }
                }
            }, error -> mBar.dismiss());
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

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
