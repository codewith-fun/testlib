package investwell.client.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iw.acceleratordemo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import investwell.common.basic.BaseActivity;

public class WebViewActivity extends BaseActivity {
    private WebView browser;
    public static investwell.utils.customView.CustomButton login;
    SwipeRefreshLayout swipeRefreshLayout;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    private ProgressBar mProgressbar;
    private AppApplication mApplication;
    private View viewNoData;
    private ImageView ivErrorImage;
    private TextView tvErrorMessage;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab;
    private TextView mTvOpenInWebBrowser;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private String url = "";

    @Override
    public void onBackPressed() {
        if (browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
            finish();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        browser = findViewById(R.id.webView);
        mApplication = (AppApplication) getApplication();
        mProgressbar = findViewById(R.id.pbHeaderProgress);
        TextView titlename = findViewById(R.id.tv_toolbar_title);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mTvOpenInWebBrowser = (TextView) findViewById(R.id.tv_open_browser);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fwd);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_bckwd);

        if (isConnectingToInternet()) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                titlename.setText(extras.getString("title"));
                browser.setWebViewClient(new CustomWebClient());
                url = !TextUtils.isEmpty(extras.getString("url")) ? extras.getString("url") : "";
                if (url != null && url.contains("play.google")) {
                    String url2 = url.substring(url.indexOf("=") + 1);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(
                            "https://play.google.com/store/apps/details?id=" + url2));
                    startActivity(intent);
                } else if (url != null && url.contains("maps.google")) {

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                } else {
                    browser.loadUrl(url);
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

            finish();
        }

        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            browser.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            browser.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            browser.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        browser.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });


        browser.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(
                    WebView browser, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(WebViewActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
        });


        swipeRefreshLayout = findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

            }
        });

        findViewById(R.id.ivBack).setOnClickListener(view -> finish());
        fab.setOnClickListener(v -> animateFAB());
        mTvOpenInWebBrowser.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_forward);
            fab.startAnimation(fab_close);
            mTvOpenInWebBrowser.startAnimation(fab_close);
            mTvOpenInWebBrowser.setClickable(false);
            isFabOpen = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fab.setImageResource(R.drawable.ic_browser);
                fab.setImageTintList(ContextCompat.getColorStateList(this,R.color.colorWhite));
            }
        } else {

            fab.startAnimation(rotate_backward);
            mTvOpenInWebBrowser.startAnimation(fab_open);
            mTvOpenInWebBrowser.setClickable(true);
            isFabOpen = true;

            fab.setImageResource(R.drawable.dialog_cross);
            fab.setVisibility(View.GONE);

        }
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

    public class CustomWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressbar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith(".pdf") || url.startsWith("whatsapp:")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                // if want to download pdf manually create AsyncTask here
                // and download file

                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            //Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
            mProgressbar.setVisibility(View.INVISIBLE);

        }
    }


    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (browser.canGoBack()) {
                        browser.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private boolean isConnectingToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null || intent.getData() == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }


}
