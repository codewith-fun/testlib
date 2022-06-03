package investwell.client.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.common.calculator.utils.Utils;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyDocsAdapter extends RecyclerView.Adapter<MyDocsAdapter.MyViewHolder>{

    public ArrayList<JSONObject> mDataList;
    Context context;
    private String mDocId;
    private RequestQueue requestQueue;
    private MainActivity mActivity;
    private AppSession mSession;
    String filePath = "";
    ProgressDialog progressDialog;
    private AppApplication mApplication;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    AsyncTask mMyTask;
    public MyDocsAdapter(Context context, ArrayList<JSONObject> jsonObject) {
        this.context = context;
        this.mDataList = jsonObject;
        progressDialog=new ProgressDialog(context);
    }
    @NonNull
    @Override
    public MyDocsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_mydoc_detail, parent, false);
        MyDocsAdapter.MyViewHolder vh = new MyDocsAdapter.MyViewHolder(v);
        setInitializer();
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final MyDocsAdapter.MyViewHolder holder, final int position) {

        mActivity = (MainActivity) context;
        mApplication = (AppApplication) mActivity.getApplication();
        final ArrayList<JSONObject> list = new ArrayList<>();
        final JSONObject jsonObject1 = mDataList.get(position);

        holder.tvDate.setText(jsonObject1.optString("DocUploadDate"));
        holder.tvDocName.setText(jsonObject1.optString("DocName"));

        holder.ivDocView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadDoc (jsonObject1.optString("cid"), jsonObject1.optString("DocId"), 0, jsonObject1.optString("DocFile"));
            }
        });
        holder.ivDocDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadDoc (jsonObject1.optString("cid"), jsonObject1.optString("DocId"), 1, jsonObject1.optString("DocFile") );
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateList(List<JSONObject> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvDocName;
        ImageView ivDocView, ivDocDown;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvDocName = view.findViewById(R.id.tvDocName);
            ivDocView = view.findViewById(R.id.ivDocView);
            ivDocDown = view.findViewById(R.id.ivDocDown);
        }
    }
    // Method to show Progress bar
    private void showProgressDialogWithTitle(String title,String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        //progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.show();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }

    private void downloadDoc(  String cid, String docID, int downView, String docName) {
        if(downView == 1){
            showProgressDialogWithTitle("Downloading..","Downloading...");
        }

        String url = Config.CLIENT_DOC_DOWNLOAD;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put("Cid", cid);
            jsonObject.put("DocID", docID);
            jsonObject.put("FormatReq", "Y");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    hideProgressDialogWithTitle();
                    ArrayList<JSONObject> list = new ArrayList<>();
                    try {
                        if (jsonObject.optString("Status").equalsIgnoreCase("True")) {
                            filePath = jsonObject.getString("ServiceMSG");
                            Pattern p = Pattern.compile(URL_REGEX);
                            Matcher m = p.matcher(filePath);
                            if(m.find()) {
                                if (downView == 0) {
                                     if(filePath.toLowerCase().contains(".pdf")) {
                                         viewPdf(filePath);
                                     } else {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("filePath", filePath);
                                        bundle.putInt("fileAction", downView);
                                        bundle.putString("docName", docName);
                                        mActivity.displayViewOther(115, bundle);
                                    }
                                } else {
                                    try {
                                        requestStoragePermission();
                                        new DownloadAsyncTask().execute(filePath, docName);
                                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + docName);
                                        intent.setData(Uri.fromFile(file));
                                        getApplicationContext().sendBroadcast(intent);
                                        showResultDialog("Successfuly download");
                                    }catch(Exception e){
                                        showResultDialog("Download failed");
                                    }
                                }
                            }
                          /*  else{
                                 Toast.makeText(mActivity, filePath,Toast.LENGTH_SHORT).show();
                            }*/

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                    } else {

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

            requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }

    }
    public void viewPdf(String url) {
        mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

    }
    private void setInitializer() {
        mSession = AppSession.getInstance(mActivity);
    }

    private void requestStoragePermission() {
        Dexter.withActivity(mActivity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //(AppApplication) mActivity.getApplication().showSnackBar(" ", "All permissions are granted!");
                            //createImage();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showResultDialog(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
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
            }
        });
        alertDialog.show();

    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);
        new Fragment().startActivityForResult(intent, 101);
    }

}
//-------------------------------------------------
class DownloadAsyncTask extends AsyncTask<String, String, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... fileArg) {
        int count;
        try {
            URL url = new URL(fileArg[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            int lenghtOfFile = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(),8192);
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/Download/"+fileArg[1]);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
        }
        return null;
    }


}
