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
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;

import com.android.volley.VolleyError;
import com.iw.acceleratordemo.R;
import investwell.utils.AppSession;

public class PaymentForAdditionalPurchage extends Activity {

    private WebView webview;
    private TextView heading;
    private AppSession mSession;
    private ProgressDialog mBar;
    private String mURL = "";
    private String mCallFrom = "";
    private AppApplication mApplication;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mSession = AppSession.getInstance(this);
        mApplication = (AppApplication) getApplication();
        mURL = getIntent().getStringExtra("url");

        ImageView back_arrow = (ImageView) findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                    Intent intent = new Intent();
                    intent.putExtra("type", mCallFrom);
                    setResult(500, intent);
                    finish();*/

                startActivity(new Intent(getBaseContext(),MainActivity.class));
                finish();

            }
        });


        webview = findViewById(R.id.webview);
        loadHTML(null);
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


    private void loadHTML(String rawData) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(mURL);
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            String PageURL = view.getUrl();

            if (PageURL.equalsIgnoreCase("http://m.investwell.in/need_to_changed")) {
                mApplication.showSnackBar(webview, getResources().getString(R.string.payment_success_txt));
                // finish();
            } else {

                //Toast.makeText(Payment.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

            }

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

    @Override
    public void onBackPressed(){

       /* Intent intent = new Intent();
        intent.putExtra("type", mCallFrom);
        setResult(500, intent);
        finish();*/

        startActivity(new Intent(getBaseContext(),MainActivity.class));
        finish();
    }
}