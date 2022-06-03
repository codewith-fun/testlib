package investwell.client.fragment.goalbased.Fragment;


import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;


import org.json.JSONException;
import org.json.JSONObject;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;


public class FeedbackFragment extends Fragment implements CustomDialog.DialogBtnCallBack {
    private TextInputEditText mName, mPhone, mMessage, mEditextMail;
    private TextInputLayout tilName, tilPhone, tilMessage, tilEtMail;
    private String service;
    private RatingBar ratingBar;
    private AppSession mSession;
    private Button mSend;
    private String mQuery;
    private ImageView mBack;
    private MainActivity mActivity;
    private CustomDialog customDialog;
    Bundle mBundle;
    public ToolbarFragment fragToolBar;
    private AppApplication appApplication;
    private String enquiryTitle = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
            mSession = AppSession.getInstance(mActivity);
            appApplication = (AppApplication) mActivity.getApplication();

        }
    }

    public FeedbackFragment() {
        // Required empty public constructor
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            clearErrorMessages();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        mSession = AppSession.getInstance(getActivity());
        setUpToolBar();
        mName = view.findViewById(R.id.etName);
        mPhone = view.findViewById(R.id.etMobile);
        mMessage = view.findViewById(R.id.etMessage);
        mEditextMail = view.findViewById(R.id.etMail);
        tilName = view.findViewById(R.id.til_name);
        tilPhone = view.findViewById(R.id.til_contact);
        tilMessage = view.findViewById(R.id.til_desc);
        tilEtMail = view.findViewById(R.id.til_mail);
        mName.setText(mSession.getFullName());
        mPhone.setText(mSession.getMobileNumber());
        mBundle = getArguments();
        customDialog = new CustomDialog(this);
        if (mBundle != null) {
            if (!TextUtils.isEmpty(mBundle.getString("titleEnquiry"))) {
                enquiryTitle = "(" + mBundle.getString("titleEnquiry") + ")";
            } else {
                enquiryTitle = "";
            }
            mName.setText(mSession.getFullName());
            mPhone.setText(mSession.getMobileNumber());
            mEditextMail.setText(mSession.getEmail());

        }


        mName.addTextChangedListener(textWatcher);
        mMessage.addTextChangedListener(textWatcher);
        mPhone.addTextChangedListener(textWatcher);
        mSend = view.findViewById(R.id.send_btn);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        return view;
    }

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.help_main_enquiry),
                    true, false, false, false, false, false, false, "");

        }
    }

    private void clearErrorMessages() {
        tilMessage.setError("");
        tilPhone.setError("");
        tilName.setError(""
        );
    }

    private void sendRequest() {

        if (mName.getText().toString().isEmpty())
            tilName.setError(getResources().getString(R.string.error_enquiry_enmpty_name));
        else if (mPhone.getText().toString().isEmpty())
            tilPhone.setError(getResources().getString(R.string.error_enquiry_empty_phone));
        else if (mPhone.getText().length() < 10)
            tilPhone.setError(getResources().getString(R.string.signup_error_mobile_no_length));

        else if (mMessage.getText().toString().isEmpty())
            tilMessage.setError(getResources().getString(R.string.error_enqiry_empty_message));
        else {
            String url = Config.SERVICE_REQ;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Passkey", mSession.getPassKey());
                jsonObject.put("Bid", AppConstants.APP_BID);
                jsonObject.put("Mobile", mPhone.getText().toString());
                jsonObject.put("Name", mName.getText().toString());
                jsonObject.put("EmailID", mEditextMail.getText().toString());
                jsonObject.put("Query", mMessage.getText().toString());
                jsonObject.put("QueryType", enquiryTitle);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("Status").equalsIgnoreCase("True")) {
                            showDialog();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appApplication.showSnackBar(tilMessage, getResources().getString(R.string.no_internet));
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                            try {
                                JSONObject jsonObject = new JSONObject(volleyError.getMessage());
                                appApplication.showSnackBar(tilMessage, jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (error instanceof NoConnectionError)
                            appApplication.showSnackBar(tilMessage, getResources().getString(R.string.no_internet));
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(jsonObjectRequest);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void showDialog() {
        customDialog.showDialog(getActivity(), "Message", "Information has been received. Thank You.", "OK", "", true, false);
    }

    private void onOkDialogBtnClick() {
        mActivity.displayViewOther(22, null);
        mActivity.removeAllStack();
    }

    @Override
    public void onDialogBtnClick(View view) {
        if (view.getId() == R.id.btDone) {
            onOkDialogBtnClick();
        }
    }
}
