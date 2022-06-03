package investwell.client.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class PaymentNewActivity extends Activity {

    public static boolean done = false;
    private WebView webview;
    private Button Dashboard;
    private TextView heading;
    private AppSession mSession;
    private ProgressDialog mBar;
    private String mUCC_Code = "";
    private String mCallFrom = "";
    private AppApplication mApplication;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mApplication = (AppApplication) getApplication();
        heading = (TextView) findViewById(R.id.heading);
        mSession = AppSession.getInstance(this);
        heading.setText(getResources().getString(R.string.payment_toolbar_title));

        mUCC_Code = getIntent().getStringExtra("ucc_code");
        mCallFrom = getIntent().getStringExtra("type");

        ImageView back_arrow = (ImageView) findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("type", mCallFrom);
                    setResult(500, intent);
                    finish();
                }*/

                startActivity(new Intent(getBaseContext(),MainActivity.class));
                finish();
            }
        });


        webview = findViewById(R.id.webview);


        goForPayment();
        //  loadHTML(null);


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wView, String url) {
                /*Intent intent = new Intent(getBaseContext(), PaymentSuccess.class);

                if (url.contains("BSEPaymentSuccess.aspx")) {
                    intent.putExtra("message", "success");
                } else  {
                    intent.putExtra("message", "fail");

                }

                startActivity(intent);*/
                wView.loadUrl(url);
                return true;
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


    private void loadHTML(Uri uri) {
        webview.getSettings().setJavaScriptEnabled(true);
        String data = mSession.getDeviceToken();
        // webview.loadData(data, "text/html", "UTF-8");
        webview.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);

        // webview.setWebViewClient(new WebViewClient());

        // webview.loadUrl("file:///" + uri);
        //content://com.tvs.ashutoshmf/bank_raw_data.html/bank_raw_data.html
    }

    private void goForPayment() {
        final ProgressDialog mBar = ProgressDialog.show(PaymentNewActivity.this, null, null, true, false);
        mBar.setContentView(R.layout.progress_piggy);
        mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        String url = Config.START_PAYMENT;
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("UCC", mUCC_Code);
            jsonParam.put("Bid", AppConstants.APP_BID);
            jsonParam.put("Passkey", mSession.getPassKey());
        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                mBar.dismiss();
                try {
                    if (object.optBoolean("Status")) {
                        String rawHtmlData = object.optString("ServiceMSG");
                        if (rawHtmlData.equals("Payment initiated for given OrderNo. Please wait some time.")) {
                            showCommonDailog(PaymentNewActivity.this, "Payment Status", rawHtmlData);
                            rawHtmlData = rawHtmlData.replaceAll("\\r\\n|\\r|\\n", "");
                            rawHtmlData = rawHtmlData.replaceAll("\\r\\t|\\r|\\t", "");
                            Uri imageUri = saveRawData(rawHtmlData);
                            loadHTML(imageUri);
                        } else {
                            rawHtmlData = rawHtmlData.replaceAll("\\r\\n|\\r|\\n", "");
                            rawHtmlData = rawHtmlData.replaceAll("\\r\\t|\\r|\\t", "");
                            Uri imageUri = saveRawData(rawHtmlData);
                            mSession.setDevicetokn(rawHtmlData);
                            loadHTML(imageUri);
                        }
                    } else {
                        mApplication.showSnackBar(webview, object.optString("ServiceMSG"));
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
                        mApplication.showSnackBar(webview, jsonObject.optString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                mApplication.showSnackBar(webview, getResources().getString(R.string.no_internet));
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

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentNewActivity.this);
        requestQueue.add(jsonObjectRequest);
    }


    private Uri saveRawData(String raw_data) {
        File imagesFolder = new File(getCacheDir(), "html");
        Uri uri = null;
        try {
            if (!imagesFolder.exists())
                imagesFolder.mkdirs();
            File file = new File(imagesFolder, "bank_raw_data.html");

            FileWriter writer = new FileWriter(file);
            writer.append(raw_data);
            writer.flush();
            writer.close();


            uri = FileProvider.getUriForFile(this, getPackageName(), file);

        } catch (IOException e) {
            System.out.println("IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }


    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            String PageURL = view.getUrl();


        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showCommonDailog(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.comom_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        LinearLayout linerMain = dialogView.findViewById(R.id.linerMain);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);

        tvTitle.setText(title);
        tvMessage.setText(message);

        TextView tvOk = dialogView.findViewById(R.id.tvOk);
        RelativeLayout relSubMenu = dialogView.findViewById(R.id.relativeLayout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= 21) {
            linerMain.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background_inset));
            relSubMenu.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_header_background));
            GradientDrawable drawable = (GradientDrawable) relSubMenu.getBackground();
            drawable.setColor(ContextCompat.getColor(context, R.color.colorGrey_300));
        } else {
            relSubMenu.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey_300));
        }


        tvOk.setOnClickListener(new View.OnClickListener() {
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
}
