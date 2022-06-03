package investwell.client.fragment.goalbased.Fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iw.acceleratordemo.R;

import org.json.JSONObject;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceRequestFragment extends Fragment implements CustomDialog.DialogBtnCallBack , ToolbarFragment.ToolbarCallback{
    private EditText mName, mPhone, mMessage;
    private String service;
    private Spinner spinner;
    private AppSession mSession;
    private ImageView mBack;
    private MainActivity mActivity;
    private CustomDialog customDialog;
    Bundle mBundle;
    public ToolbarFragment toolbarFragment;
    public ServiceRequestFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
            mActivity.setMainVisibility(this, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_gridview_type_two_a for this fragment
        View view = inflater.inflate(R.layout.fragment_services_request, container, false);
        mSession = AppSession.getInstance(getActivity());
        mName = view.findViewById(R.id.etName);
        setUpToolBar();
        mPhone = view.findViewById(R.id.etPhone);
        mMessage = view.findViewById(R.id.etMessage);
        spinner = view.findViewById(R.id.service_spinner);
        customDialog = new CustomDialog(this);
        Button mSend = view.findViewById(R.id.send_btn);
        mActivity = (MainActivity) getActivity();
        mBundle=getArguments();
        if (mBundle != null) {
            mName.setText(mSession.getFullName());
            mPhone.setText(mSession.getMobileNumber());
        }




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                service = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        return view;
    }
    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);

            if (toolbarFragment != null) {
                toolbarFragment.setUpToolBar(getResources().getString(R.string.toolBar_title_service_request), true, false, false, false, false, false, false,"");
                toolbarFragment.setCallback(this);
            }

        }



    private void sendRequest() {
        {
            if (mName.getText().toString().isEmpty())
                mName.setError("Please Enter Name");
            else if (mPhone.getText().toString().isEmpty())
                mPhone.setError("Please Enter Contact number");
            else if (mMessage.getText().toString().isEmpty())
                mMessage.setError("Enter Description");
            else {
                String url = Config.SERVICE_REQ;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Passkey", mSession.getPassKey());
                    jsonObject.put("Bid", AppConstants.APP_BID);
                    jsonObject.put("Mobile", mPhone.getText().toString());
                    jsonObject.put("Name", mName.getText().toString());
                    jsonObject.put("EmailID", "");
                    jsonObject.put("Query", mMessage.getText().toString());
                    jsonObject.put("QueryType", service);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (response.optString("Status").equalsIgnoreCase("True")) {

                                // Toast.makeText(getActivity(), "Request has been sent. We will get back to you.", Toast.LENGTH_LONG).show();

                                showDialog();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Something went wrong! Please Try Again Later.", Toast.LENGTH_SHORT).show();

                        }
                    });

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(jsonObjectRequest);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void showDialog() {
        customDialog.showDialog(getActivity(), "Message", "Request has been sent. We will get back to you soon.", "OK", "", true, false);
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

    @Override
    public void onToolbarItemClick(View view) {

    }
}
