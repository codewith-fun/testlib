package investwell.client.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class My_Insurance_Adapter extends RecyclerView.Adapter<My_Insurance_Adapter.MyViewHolder> {


    public ArrayList<JSONObject> jsonObject;
    Context context;
    private MainActivity mActivity;
    private AppSession mSession;
    private AppApplication mApplication;
    ImageView mIvDownload;
    ProgressDialog progressDialog;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    public My_Insurance_Adapter(Context context, ArrayList<JSONObject> jsonObject) {

        this.context = context;
        this.jsonObject = jsonObject;
        this.mActivity=(MainActivity)context;
        progressDialog=new ProgressDialog(context);
        mApplication = (AppApplication) mActivity.getApplication();
    }

    @NonNull
    @Override
    public My_Insurance_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_insurance_item, parent, false);

        My_Insurance_Adapter.MyViewHolder vh = new My_Insurance_Adapter.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final My_Insurance_Adapter.MyViewHolder holder, final int position) {
        mActivity = (MainActivity) context;
        mSession = AppSession.getInstance(context);

        DecimalFormat myFormatter = new DecimalFormat("##,##,###.00");
        final JSONObject jsonObject1 = jsonObject.get(position);
        holder.Applicant_Name.setText(jsonObject1.optString("Applicant"));
        holder.policy_number.setText( jsonObject1.optString("PolicyNo"));
        holder.insurance_type.setText(jsonObject1.optString("InsType"));

        holder.sum_assured.setText(jsonObject1.optString("SumInsured"));
        holder.premium.setText(jsonObject1.optString("Amount"));
        holder.due_date.setText(jsonObject1.optString("NextPremium"));
        holder.mTvRemarks.setText(jsonObject1.optString("Remarks"));
        holder.tvPolicyName.setText(!TextUtils.isEmpty(jsonObject1.optString("PolicyName")) ? jsonObject1.optString("PolicyName") : "");
        holder.tvPolicyDetails.setText(!TextUtils.isEmpty(jsonObject1.optString("PolicyDetail")) ? jsonObject1.optString("PolicyDetail") : "");
        if (holder.due_date.getText().toString().equalsIgnoreCase("Fully Paid")) {
            holder.due_date.setTextColor(Color.parseColor("#5DB64C"));
        } else {

            holder.due_date.setTextColor(Color.parseColor("#000000"));
        }

        if (jsonObject1.optString("Remarks").isEmpty()) {
            holder.mTvRemarks.setVisibility(View.GONE);
            holder.view_line2.setVisibility(View.GONE);
        } else {
            holder.mTvRemarks.setVisibility(View.VISIBLE);
            holder.view_line2.setVisibility(View.VISIBLE);
        }


        if (jsonObject1.optString("PolicyNo").isEmpty()){
            mIvDownload.setVisibility(View.INVISIBLE);
        }else{
            mIvDownload.setVisibility(View.VISIBLE);
        }


        mIvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload(jsonObject1.optString("PolicyNo"));
            }
        });


    }
    // Method to show Progress bar
    private void showProgressDialogWithTitle(String title,String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        progressDialog.setCancelable(false);
        //Setting Title
        progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.show();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }
    @Override
    public int getItemCount() {

        return jsonObject.size();

    }

    public void updateList(List<JSONObject> list) {

        jsonObject.clear();
        jsonObject.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Applicant_Name, policy_number, insurance_type, sum_assured, premium, due_date, mTvRemarks, tvPolicyName, tvPolicyDetails;
        View view_line2;


        public MyViewHolder(View view) {
            super(view);

            Applicant_Name = view.findViewById(R.id.applicant_name);
            mIvDownload = view.findViewById(R.id.ivDownload);
            policy_number = view.findViewById(R.id.policy_number);
            insurance_type = view.findViewById(R.id.insurance_type);
            sum_assured = view.findViewById(R.id.sum_assured);
            premium = view.findViewById(R.id.premium);
            due_date = view.findViewById(R.id.due_date);
            mTvRemarks = view.findViewById(R.id.tvremarks);
            view_line2 = view.findViewById(R.id.view_line2);
            tvPolicyName = view.findViewById(R.id.tv_policy_name);
            tvPolicyDetails = view.findViewById(R.id.tv_policy_details);



        }
    }

    private void startDownload(String PolicyNo){
        showProgressDialogWithTitle("Downloading..","Hey ! "+mSession.getFullName()+" Please wait...");
        String url = Config.Download_Insurance;
        JSONObject jsonObject = new JSONObject();

        try{

            jsonObject.put("Passkey", mSession.getPassKey());
            jsonObject.put("Bid", AppConstants.APP_BID);
            jsonObject.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            jsonObject.put("PolicyNo", PolicyNo);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressDialogWithTitle();
                    if (response.optString("Status").equalsIgnoreCase("True")){
                        Pattern p = Pattern.compile(URL_REGEX);
                        Matcher m = p.matcher(response.optString("ServiceMSG"));//replace with string to compare
                        if(m.find()) {
                            DownloadFromUrl(response.optString("ServiceMSG"));
                        }else{
                            Toast.makeText(context,response.optString("ServiceMSG"),Toast.LENGTH_SHORT).show();
                        }


                    }else{

                        Toast.makeText(context,response.optString("ServiceMSG"),Toast.LENGTH_SHORT).show();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialogWithTitle();
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

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void DownloadFromUrl(String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

    }
}
