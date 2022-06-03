package investwell.client.fragment.UploadExistingInvestmentModule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;

public class FragUploadModule extends Fragment implements View.OnClickListener {
    private Bundle mBundle;
    private AppSession mSession;
    private MainActivity mActivity;
    private TextInputEditText mEtName, mEtEmail, mEtPhone;
    private EditText mEtPassword;
    private String uploadedFileName, mMessage;
    private TextView mSeletedFile;
    File file;
    Uri selectedFileURI;
    public ToolbarFragment fragToolBar;
    private AppApplication mApplication;
    private CustomDialog customDialog;

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mActivity);
            mActivity.setMainVisibility(this, null);
            mApplication = (AppApplication) mActivity.getApplication();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_mutual_fund_lab, container, false);
        mSession = AppSession.getInstance(getActivity());
        mActivity = (MainActivity) getActivity();

        setUpToolBar();
        mEtName = view.findViewById(R.id.TiEditext1);
        mEtEmail = view.findViewById(R.id.TiEditext2);
        mEtPhone = view.findViewById(R.id.TiEditext3);
        mEtPassword = view.findViewById(R.id.et_password);
        mSeletedFile = view.findViewById(R.id.tvSelectDoc);


        if (mSession.getFullName().isEmpty()){
            mEtName.setVisibility(View.VISIBLE);
        }else{
            mEtName.setVisibility(View.GONE);
            mEtName.setText(mSession.getFullName());
        }

        if (mSession.getEmail().isEmpty()){
            mEtEmail.setVisibility(View.VISIBLE);
        }else{
            mEtEmail.setVisibility(View.GONE);
            mEtEmail.setText(mSession.getEmail());
        }

        if (mSession.getMobileNumber().isEmpty()){
            mEtPhone.setVisibility(View.VISIBLE);
        }else{
            mEtPhone.setVisibility(View.GONE);
            mEtPhone.setText(mSession.getMobileNumber());
        }






        view.findViewById(R.id.tvSelectDoc).setOnClickListener(this);
        view.findViewById(R.id.btnContinue).setOnClickListener(this);
        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText(getString(R.string.Mutual_Fund_Lab_content1) + " " + getString(R.string.Mutual_Fund_Lab_content2));
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.upload_my_CAS_txt), true, false, false, false, false, false, false, "");

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tvSelectDoc) {
            showFileChooser();
        } else if (id == R.id.btnContinue) {
            checkValidation();
        }
    }

    private void checkValidation() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        /* mEtName.setText(mSession.getFullName());
        mEtEmail.setText(mSession.getEmail());
        mEtPhone.setText(mSession.getMobileNumber());*/
        if (mEtName.getText().toString().equals("")) {

            mApplication.showSnackBar(mEtName, "Please eneter full name");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString()).matches()) {
            Toast.makeText(getActivity(), "Please eneter valid email address.", Toast.LENGTH_SHORT).show();
            mApplication.showSnackBar(mEtName, "Please eneter full name");
        } else if (mEtPhone.getText().toString().length() < 10) {
            Toast.makeText(getActivity(), "Please enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
        } else {
            if (!TextUtils.isEmpty(uploadedFileName)) {
                PostDataAsyncTask postDataAsyncTask = new PostDataAsyncTask();
                postDataAsyncTask.execute();
            } else {
                Toast.makeText(getActivity(), "Please select document", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                selectedFileURI = data.getData();
                file = new File(selectedFileURI.getPath().toString());
                uploadedFileName = file.getName();
                StringTokenizer tokens = new StringTokenizer(uploadedFileName, ":");

                mSeletedFile.setText(uploadedFileName);
            }
        }
    }

    public class PostDataAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setCancelable(false);
            pDialog.setMessage("Please wait ...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... strings) {
            /*http://nativeapi.my-portfolio.in/BSESignatureUpload.svc/UploadFile?passkey
            ={PASSKEY}&bid={BID}&cid={CID}&name={NAME}&mobile={MOBILE}&email={EMAIL}&fileName={FILENAME*/

            String namePassword = mEtName.getText().toString().trim()+"CAS Password:"+mEtPassword.getText().toString().trim()+"";
            String url_path = Config.UPLOAD_DOCUMENT +
                    "passkey=" + mSession.getPassKey() +
                    "&bid=" + AppConstants.APP_BID +
                    "&cid=" + mSession.getCID() +
                    "&StreamFile=" + file +
                    "&name=" + namePassword +
                    "&mobile=" + mEtPhone.getText().toString().trim() +
                    "&email=" + mEtEmail.getText().toString().trim() +
                    "&fileName=" + uploadedFileName.trim();

            HttpURLConnection conn = null;

            int maxBufferSize = 1024;
            try {
                URL url = new URL(url_path);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(1024);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data");

                OutputStream outputStream = conn.getOutputStream();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedFileURI);

                int bytesAvailable = inputStream.available();
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.i("result", line);
                    mMessage = line;
                }
                reader.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pDialog.dismiss();

           // mApplication.showSnackBar(mEtEmail, mMessage);
            mActivity.getSupportFragmentManager().popBackStack();
            System.out.println("RESULT***********"+result);
            Log.e("", "RESULT : " + result);
            try {

                JSONObject jsonObject = new JSONObject(mMessage);
                String message = jsonObject.optString("ServiceMSG");

                mApplication.showCommonDailog(mActivity, mActivity, true, getResources().getString(R.string.dialog_title_message), message, "message", true, true);

            }catch (Exception e){
                e.printStackTrace();
            }





        }
    }

}



