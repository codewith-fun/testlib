package investwell.client.fragment.profile;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.flavourtypetwo.activity.MainActivityTypeTwo;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.common.applock.PinLockActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.CircleTransform;
import investwell.utils.Config;
import investwell.utils.RoundedImageView;
import investwell.utils.Utils;
import investwell.utils.crop.CropImage;
import investwell.utils.crop.CropImageView;
import investwell.utils.customView.CustomButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static investwell.common.applock.AppLockOptionActivity.CUSTOM_PIN_LOCK_CODE;
import static investwell.common.applock.AppLockOptionActivity.DEFAULT_LOCK_CODE;

public class FragProfileSettings extends Fragment implements View.OnClickListener {
    private AppSession mSession;
    private BrokerActivity mBrokerActivity;
    private MainActivity mMainActivity;
    private AppApplication mApplication;
    private TextView mTvVersion, mTvLockScreen, mTvUsername, mLastFetch, mLastTransaction,
            mLastNAV, mTvLangSelected, mTvChangeImage;
    private int count = 0;
    public ImageView mProfileImage;
    private ToolbarFragment fragToolBar;
    private LinearLayout mllAppInfo;
    private String shareLink = "";
    private TextView tvCloseAccDesc, tvLogoutDesc;

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

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_setting), true, false, false, false, false, false, false, "");
            if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&

                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B")) {
                fragToolBar.setToolBarColor(getActivity(), ContextCompat.getColor(getActivity(), R.color.colorPrimary));

            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);
        mllAppInfo = view.findViewById(R.id.llAppInfo);
        setUpToolBar();
        if (mMainActivity != null) {

            fragToolBar.hasSecondryToolBarVisibility(true);
            mllAppInfo.setVisibility(View.VISIBLE);
        } else {
            mBrokerActivity.setMainVisibility(this, null);
            fragToolBar.hasSecondryToolBarVisibility(false);
            mllAppInfo.setVisibility(View.GONE);
        }


        mTvUsername = view.findViewById(R.id.card_user_name);
        tvCloseAccDesc = view.findViewById(R.id.tv_close_acc_desc);
        tvLogoutDesc = view.findViewById(R.id.tv_logout_acc_desc);
        tvLogoutDesc.setText("Logout From " + getResources().getString(R.string.app_name) + " App");
        tvCloseAccDesc.setText("Clear your profile on this device");
        TextView tvName = view.findViewById(R.id.et_name);
        mLastFetch = view.findViewById(R.id.last_fetch);
        mLastTransaction = view.findViewById(R.id.last_transaction);
        mLastNAV = view.findViewById(R.id.last_nav);
        mTvLangSelected = view.findViewById(R.id.tv_selected_lang);
        mTvChangeImage = view.findViewById(R.id.tvChange);


        mProfileImage = view.findViewById(R.id.profile_icon);
        mProfileImage.setOnClickListener(this);

        if ((mSession.getLoginType().equals("Broker") || mSession.getLoginType().equals("SubBroker") || mSession.getLoginType().equals("RM") || mSession.getLoginType().equalsIgnoreCase("Zone")
                || mSession.getLoginType().equalsIgnoreCase("Region")
                || mSession.getLoginType().equalsIgnoreCase("Branch"))) {
            mTvUsername.setText(mSession.getBrokerFullName());

        } else if (!mSession.getFullName().isEmpty()) {
            mTvUsername.setText(mSession.getFullName());
        } else {
            mTvUsername.setText(getResources().getString(R.string.incomplete_profile_dear_investor_txt));
        }
        if (mSession.getLoginType().equals("Broker")) {
            mTvChangeImage.setVisibility(View.INVISIBLE);
        }


        if (mSession.getHasLoging() && mSession.getImageRawData().length() > 0) {
            tvName.setVisibility(View.GONE);
            String path = mSession.getImageRawData();
            Picasso.get().load(path).error(R.mipmap.profileplaceholder).transform(new CircleTransform()).into(mProfileImage);
        } else {
            String name = mTvUsername.getText().toString();
            if (!name.equals("")) {
                String usertext = "";
                String[] n = name.split(" ", 2);
                for (int i = 0; i < n.length; i++) {
                    usertext = usertext + n[i].charAt(0);
                }
                tvName.setVisibility(View.VISIBLE);
                tvName.setText(usertext);
            }
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 3B"))) {
            view.findViewById(R.id.help_layout).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.help_layout).setVisibility(View.GONE);
        }

        view.findViewById(R.id.screen_lock_layout).setOnClickListener(this);
        view.findViewById(R.id.help_layout).setOnClickListener(this);
        view.findViewById(R.id.change_password_layout).setOnClickListener(this);
        view.findViewById(R.id.share_layout).setOnClickListener(this);
        view.findViewById(R.id.rate_layout).setOnClickListener(this);
        view.findViewById(R.id.logout_layout).setOnClickListener(this);
        view.findViewById(R.id.about_layout).setOnClickListener(this);
        view.findViewById(R.id.privacy_layout).setOnClickListener(this);
        view.findViewById(R.id.rl_selected_language).setOnClickListener(this);
        view.findViewById(R.id.rl_close_acc_layout).setOnClickListener(this);
        mTvVersion = view.findViewById(R.id.app_version);
        mTvLockScreen = view.findViewById(R.id.tvLockType);


        versionName();
        setCurrentFetchTime();
        setting_updateView();
        setUpUiVisibility(view);
        if (!TextUtils.isEmpty(mSession.getDefaultAppLang())) {
            mTvLangSelected.setText(mSession.getDefaultAppLang());
        }
        setUpShareMessage(view);
        return view;
    }

    private void setUpUiVisibility(View view) {
        if (Utils.getConfigData(mSession).optString("ShareText").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.share_layout).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.share_layout).setVisibility(View.GONE);
        }
        if (Utils.getConfigData(mSession).optString("RateRequired").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.rate_layout).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.rate_layout).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("Vernacular")) &&
                Utils.getConfigData(mSession).optString("Vernacular").equalsIgnoreCase("Y")) {
            view.findViewById(R.id.rl_selected_language).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.rl_selected_language).setVisibility(View.GONE);
        }


    }

    private void setUpShareMessage(View view) {
        String primaryShareMsg = Utils.getConfigData(mSession).optString("ShareTextMessage");
        String androidLink = "";
        String iosLink = "";
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("AndroidLink"))) {
            androidLink = "Android:  " + Utils.getConfigData(mSession).optString("AndroidLink");

        } else {
            androidLink = "";
        }
        if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("IOSLink"))) {
            iosLink = "iOS:  " + Utils.getConfigData(mSession).optString("IOSLink");

        } else {
            iosLink = "";
        }

        if (!TextUtils.isEmpty(androidLink) && !TextUtils.isEmpty(iosLink)) {
            shareLink = primaryShareMsg + "\n\n" + androidLink + "\n\n" + iosLink;
        } else if (TextUtils.isEmpty(androidLink) && !TextUtils.isEmpty(iosLink)) {
            shareLink = primaryShareMsg + "\n\n" + iosLink;
        } else if (!TextUtils.isEmpty(androidLink) && TextUtils.isEmpty(iosLink)) {
            shareLink = primaryShareMsg + "\n\n" + androidLink;
        } else {
            shareLink = primaryShareMsg;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.share_layout) {/*  String pckg= "";
                if(mMainActivity.getApplicationContext()!=null) {
                   pckg=mMainActivity.getApplicationContext().getPackageName();
                }else{
                  pckg=mBrokerActivity.getApplicationContext().getPackageName();
                }*/
            Intent intnt = new Intent(Intent.ACTION_SEND);
            intnt.setType("text/plain");
            if (intnt != null) {
                intnt.putExtra(Intent.EXTRA_TEXT, shareLink);
                startActivity(intnt);
            }
        } else if (id == R.id.rate_layout) {
            Uri uriUrl = Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
            Intent intentt = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(intentt);
        } else if (id == R.id.screen_lock_layout) {
            if (mSession.getHasAppLockEnable()) {
                if (mSession.getAppLockType().equalsIgnoreCase("default")) {
                    KeyguardManager km = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
                    if (km.isKeyguardSecure()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Intent i = km.createConfirmDeviceCredentialIntent("Use your Screen lock pattern to Login", "");
                            startActivityForResult(i, DEFAULT_LOCK_CODE);
                        }
                    } else {
                        mSession.setHasAppLockEnable(false);
                        if (mBrokerActivity != null) {
                            mApplication.showCommonDailog(mBrokerActivity, getActivity(), false, getString(R.string.Error), getString(R.string.text_no_screen_lock), "message", false, true);
                        } else {
                            mApplication.showCommonDailog(mMainActivity, getActivity(), false, getString(R.string.Error), getString(R.string.text_no_screen_lock), "message", false, true);
                        }
                    }
                } else if (mSession.getAppLockType().equalsIgnoreCase("pin")) {
                    Intent intent = new Intent(getActivity(), PinLockActivity.class);
                    intent.putExtra("type", "verifyFromSetting");
                    startActivityForResult(intent, CUSTOM_PIN_LOCK_CODE);
                }

            } else {
                if (mBrokerActivity != null) {
                    mBrokerActivity.displayViewOther(56, null);
                } else {
                    mMainActivity.displayViewOther(56, null);
                }
            }
        } else if (id == R.id.help_layout) {
            if (mBrokerActivity != null) {
                mBrokerActivity.displayViewOther(57, null);
            } else {
                mMainActivity.displayViewOther(57, null);
            }


                /*  case R.id.about_layout:
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("title", "About Us");
                intent.putExtra("url", getString(R.string.app_link_about_us));
                startActivity(intent);
                break;

            case R.id.privacy_layout:
                Intent intent_privcy = new Intent(getActivity(), WebViewActivity.class);
                intent_privcy.putExtra("title", "Privacy Policy");
                intent_privcy.putExtra("url", getString(R.string.app_link_privacy_policy));
                startActivity(intent_privcy);
                break;*/
        } else if (id == R.id.change_password_layout) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMainActivity != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "changePassword");
                        //  mActivity.displayViewOther(38, bundle);
                        mMainActivity.displayViewOther(38, bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "changePassword");
                        //  mActivity.displayViewOther(38, bundle);
                        mBrokerActivity.displayViewOther(38, bundle);
                    }

                }
            }, 200);
        } else if (id == R.id.logout_layout) {//                showLogOutAlert();
            showLogoutDailog(getActivity(), "Logout From " + getResources().getString(R.string.app_name), getString(R.string.alert_dialog_logout_desc_txt));
        } else if (id == R.id.rl_close_acc_layout) {//                showLogOutAlert();
            showCloseAccount(getActivity(), "Close account from on this device", getString(R.string.close_acc_desc_txt));
        } else if (id == R.id.rl_selected_language) {
            if (mBrokerActivity != null) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("type", "changeLanguage");
                mBrokerActivity.displayViewOther(92, bundle1);
            } else {
                Bundle bundle1 = new Bundle();
                bundle1.putString("type", "changeLanguage");
                mMainActivity.displayViewOther(92, bundle1);
            }
        } else if (id == R.id.profile_icon) {
            if (mSession.getLoginType().equalsIgnoreCase("Broker")) {
            } else {
                CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(getActivity(), 300);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK && requestCode == DEFAULT_LOCK_CODE)) {
            mSession.setHasAppLockEnable(true);

            if (mBrokerActivity != null) {
                mBrokerActivity.displayViewOther(56, null);
            } else {
                mMainActivity.displayViewOther(56, null);
            }
        } else if ((resultCode == RESULT_CANCELED && requestCode == DEFAULT_LOCK_CODE)) {

        } else if ((resultCode == RESULT_CANCELED && requestCode == CUSTOM_PIN_LOCK_CODE)) {

        } else if (requestCode == CUSTOM_PIN_LOCK_CODE) {
            if (mBrokerActivity != null) {
                mBrokerActivity.displayViewOther(56, null);
            } else {
                mMainActivity.displayViewOther(56, null);
            }
        } else if (requestCode == 300) {
            CropImage.ActivityResult imageResult = CropImage.getActivityResult(data);
            try {
                Uri selectedImage = imageResult.getUri();
                if (selectedImage != null) {
                    Bitmap gallery_bit = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    mProfileImage.setImageBitmap(RoundedImageView.getCroppedBitmap(gallery_bit, 150));
                    convertToDataUri(gallery_bit);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void convertToDataUri(Bitmap signatureBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byteArray = baos.toByteArray();
        String b64String = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        String dataUri = "data:image/png;base64," + b64String;
        saveInformation(dataUri);

    }

    private void saveInformation(String dataUri) {
        final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.UPLOAD_PROFILE_PIC;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstants.KEY_BROKER_ID, AppConstants.APP_BID);
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("UCC", mSession.getUCC_CODE());
            jsonObject.put("Cid", "" + mSession.getCID());
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
                        mSession.setImageRawData(object.optString("FileName"));


                    } else {

                        mApplication.showSnackBar(mProfileImage, object.optString("ServiceMSG"));
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
                        mApplication.showSnackBar(mProfileImage, jsonObject.optString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    mApplication.showSnackBar(mProfileImage, getResources().getString(R.string.no_internet));
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }


    private void setting_updateView() {
        if (mSession.getAppLockType().equalsIgnoreCase("default")) {
            mTvLockScreen.setText(getResources().getString(R.string.profile_phone_active));
            mTvLockScreen.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
        } else if (mSession.getAppLockType().equalsIgnoreCase("pin")) {
            mTvLockScreen.setText(getResources().getString(R.string.profile_pin_active));
            mTvLockScreen.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
        } else if (mSession.getAppLockType().equalsIgnoreCase("nothing")) {
            mTvLockScreen.setText(getResources().getString(R.string.profile_none));
            mTvLockScreen.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
        }
    }

    private void versionName() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            final String version = pInfo.versionName;
            mTvVersion.setText("v" + version);
            mTvVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = count + 1;
                    if (count > 3) {
                        //  mTvVersion.setText("Version: " + version + " & Build: V3");
                        count = 0;
                    }
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showLogoutDailog(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        CardView linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.msg_txt);

        tvTitle.setText(title);
        tvMessage.setText(message);

        CustomButton tvOk = dialogView.findViewById(R.id.tvOk);
        tvOk.setText("Log out");
        CustomButton tvcancel = dialogView.findViewById(R.id.cancel);
        tvcancel.setText("Cancel");
        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (getActivity() != null) {
                    mApplication.clearChacheSession();
                    if (mSession.getLoginType().equalsIgnoreCase("Broker") || mSession.getLoginType().equalsIgnoreCase("SubBroker") ||
                            mSession.getLoginType().equalsIgnoreCase("RM")
                            || mSession.getLoginType().equalsIgnoreCase("Zone")
                            || mSession.getLoginType().equalsIgnoreCase("Region")
                            || mSession.getLoginType().equalsIgnoreCase("Branch")) {
                        mBrokerActivity.notificationCount();
                    } else {
                        mMainActivity.notificationCount();
                    }
                    if (!TextUtils.isEmpty(Utils.getConfigData(mSession).optString("APPType")) &&
                            (Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2B") ||
                                    Utils.getConfigData(mSession).optString("APPType").equalsIgnoreCase("Type 2A"))) {
                        Intent intent = new Intent(getActivity(), MainActivityTypeTwo.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }


            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void showCloseAccount(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        CardView linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.msg_txt);

        tvTitle.setText(title);
        tvMessage.setText(message);

        CustomButton tvOk = dialogView.findViewById(R.id.tvOk);
        tvOk.setText("Close");
        CustomButton tvcancel = dialogView.findViewById(R.id.cancel);
        tvcancel.setText("Cancel");
        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (getActivity() != null) {
                    mSession.clear();
                    getActivity().finish();
                }


            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void setCurrentFetchTime() {

        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date result = new Date(mSession.get_current_time());
        mLastFetch.setText(simple.format(result));
        mLastTransaction.setText(AppApplication.last_transaction_update);
        mLastNAV.setText(AppApplication.last_nav_update);

    }
}

