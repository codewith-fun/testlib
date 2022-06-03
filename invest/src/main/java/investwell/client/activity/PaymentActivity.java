package investwell.client.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iw.acceleratordemo.R;

import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.customView.CustomButton;

public class PaymentActivity extends Activity {

    public static boolean done = false;
    private WebView webview;
    private LinearLayout dashboard_layout;
    private CustomButton Dashboard;
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
        heading = findViewById(R.id.heading);
        mSession = AppSession.getInstance(this);
        heading.setText("Payment");

        mUCC_Code = getIntent().getStringExtra("ucc_code");
        mCallFrom = getIntent().getStringExtra("type");

        ImageView back_arrow = (ImageView) findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  if (webview.canGoBack()) {

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


        webview =  findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        String url = "http://m.investwell.in/hcver8/pages/bseCart/purPayment.jsp?bid=" + AppConstants.APP_BID + "&ucc=" + mUCC_Code;
        webview.loadUrl(url);

      /*  Dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*String url = getString(R.string.domain_new)+"/Investwell.svc/LoginAuthentication/" + Pref.getString(getBaseContext(), "keyvalue") + "/" + AppConstants.VALUE_BROKER_ID + "/" + Pref.getString(getBaseContext(),"email_value") + "/" + Pref.getString(getBaseContext(),"password");
                new Authenticate().execute(url);*//*

                Intent intent = new Intent();
                intent.putExtra("type", mCallFrom);
                setResult(500, intent);
                finish();
            }
        });*/


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


    @Override
    public void onBackPressed() {
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

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

         /*   mBar = ProgressDialog.show(PaymentActivity.this, null, null, true, false);
            mBar.setContentView(R.layout_gridview_type_two_a.progress_piggy);
            mBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
*/
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);

            String PageURL = view.getUrl();
         /*   if (mBar != null) {
                mBar.dismiss();
            }*/

            if (PageURL.equalsIgnoreCase("http://m.investwell.in/hcver8/pages/bseCart/bsePaymentUrl.jsp")) {

                done = true;
                mApplication.showSnackBar(webview, getResources().getString(R.string.payment_success_txt));
                /*                dashboard_layout.setVisibility(View.VISIBLE);*/

            } else {

                //Toast.makeText(Payment.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }

            // System.out.println("@#@#@" + PageURL);

        }
    }
}
