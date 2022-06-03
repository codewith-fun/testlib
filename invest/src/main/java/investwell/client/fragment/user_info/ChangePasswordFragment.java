package investwell.client.fragment.user_info;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import investwell.broker.activity.BrokerActivity;
import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;
import investwell.utils.customView.CustomButton;
import investwell.utils.customView.CustomTextInputEditText;
import investwell.utils.customView.CustomTextViewLight;
import investwell.utils.customView.CustomTextViewRegular;


/**
 * Created by win.-8 on 11-1-17.
 */

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private CustomTextInputEditText mEtNewPassword, mEtOldPassword, mEtConfirmPassword;

    private ProgressDialog mBar;
    private AppSession mSession;
    private ImageView mivLeft;
    private BrokerActivity mBrokerActivity;
    private MainActivity mainActivity;
    private AppApplication mApplication;
    private CustomButton btnDoItLater, btnReset;
    private CustomTextViewRegular tvErrorMsg;
    private ToolbarFragment fragToolBar;
    private View view;
    private TextInputLayout tilOldPwd, tilNewPwd, tilConfirmNewPwd;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BrokerActivity) {
            this.mBrokerActivity = (BrokerActivity) context;
            mSession = AppSession.getInstance(mBrokerActivity);
            mApplication = (AppApplication) mBrokerActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());

        } else if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
            mSession = AppSession.getInstance(mainActivity);
            mApplication = (AppApplication) mainActivity.getApplication();
            mSession = AppSession.getInstance(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_change_password, container, false);
        setUpToolBar();
        setInitializer();
        setListeners();
        return view;
    }

    private void setInitializer() {
        if (mBrokerActivity != null) {
            mBrokerActivity.setMainVisibility(this, null);
        } else {
            mainActivity.setMainVisibility(this, null);
        }
        tvErrorMsg = view.findViewById(R.id.tv_error_old);
        btnDoItLater = view.findViewById(R.id.btn_do_it_later);
        btnReset = view.findViewById(R.id.btn_reset_now);
        mEtOldPassword = view.findViewById(R.id.et_old_password);
        mEtNewPassword = view.findViewById(R.id.et_new_password);
        mEtConfirmPassword = view.findViewById(R.id.et_confirm_new_password);
        tilConfirmNewPwd = view.findViewById(R.id.til_cp_confirm_new_password);
        tilNewPwd = view.findViewById(R.id.til_cp_new_password);
        tilOldPwd = view.findViewById(R.id.til_cp_old_password);
    }


    private void setListeners() {
        btnReset.setOnClickListener(this);
        btnDoItLater.setOnClickListener(this);
        mEtNewPassword.addTextChangedListener(textWatcher);
        mEtOldPassword.addTextChangedListener(textWatcher);
        mEtConfirmPassword.addTextChangedListener(textWatcher);
        //setAnimation();
    }


    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||

                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    tvErrorMsg.setVisibility(View.GONE);
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        }

    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 3) {
                tvErrorMsg.setVisibility(View.VISIBLE);
            }
            clearErrorMessages();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void setUpToolBar() {
        fragToolBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (fragToolBar != null) {
            fragToolBar.setUpToolBar(getResources().getString(R.string.toolBar_title_change_password), true, false, false, false, false, false, false, "");
        }
    }

    private void clearErrorMessages() {
        tilOldPwd.setError("");
        tilNewPwd.setError("");
        tilConfirmNewPwd.setError("");
    }

    //*****************************************************************
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_reset_now) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }

            if (mEtOldPassword.getText().toString().equals("")) {
                tilOldPwd.setError(getResources().getString(R.string.change_password_error_empty_currrent_password));
            } else if (mEtNewPassword.getText().toString().equals("")) {
                tilNewPwd.setError(getResources().getString(R.string.change_password_error_empty_new_pwd));
            } else if (mEtNewPassword.getText().toString().length() < 6 && !isValidPassword(mEtNewPassword.getText().toString())) {
                tilNewPwd.setError(getResources().getString(R.string.change_pwd_tooltip_txt));
            } else if (mEtConfirmPassword.getText().toString().equals("")) {
                tilConfirmNewPwd.setError(getResources().getString(R.string.change_password_error_empty_confrim_new_password));
            } else if (mEtConfirmPassword.getText().toString().length() < 6 && !isValidPassword(mEtConfirmPassword.getText().toString())) {
                tilConfirmNewPwd.setError(getResources().getString(R.string.change_pwd_tooltip_txt));
            } else if (!mEtNewPassword.getText().toString().equals((mEtConfirmPassword.getText().toString()))) {
                tilConfirmNewPwd.setError(getResources().getString(R.string.change_password_error_password_not_match));
            } else {
                updateProfileData();
            }
        }
    }



    public void updateProfileData() {
        if (getActivity() != null) {
            final ProgressDialog mBar = ProgressDialog.show(getActivity(), null, null, true, false);
            mBar.setContentView(R.layout.progress_piggy);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(mBar.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Bid", AppConstants.APP_BID);
            params.put(AppConstants.CUSTOMER_ID, mSession.getCID());
            params.put("OldPassword", mEtOldPassword.getText().toString());
            params.put("NewPassword", mEtNewPassword.getText().toString());
            params.put("Passkey", mSession.getPassKey());
            params.put("EmailId", mSession.getEmail());

            String url = Config.CHANGE_PASSWORD;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    mBar.dismiss();
                    try {
                        if (object.optString("Status").equals("True")) {

                            mApplication.showSnackBar(btnReset,object.optString("ServiceMSG"));
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            mApplication.showSnackBar(btnReset,object.optString("ServiceMSG"));
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
                            mApplication.showSnackBar(btnReset,error.getMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof NoConnectionError)
                        mApplication.showSnackBar(btnReset,getResources().getString(R.string.no_internet));
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

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
        }
    }


}
