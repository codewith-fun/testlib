package investwell.client.fragment.goalbased.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.iw.acceleratordemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import investwell.client.activity.MainActivity;
import investwell.client.fragment.others.ToolbarFragment;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;
import investwell.utils.Config;

public class FragCreateGoal extends Fragment implements View.OnClickListener {

    private SimpleDateFormat dateFormat_ymd = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormat_dmy = new SimpleDateFormat("dd-MMM-yyyy");
    private EditText mEt_GoalName, mEt_GoalAmount, mEt_GoalDuration, mEt_GoalExpReturn, mEt_GoalInflation;
    private MainActivity mActivity;
    private AppSession mSession;
    private Bundle mBundle;
    private ImageView mGoalImage;
    private TextView mGoalName, mTvCreate, mTvTitle;
    private String mGoalCategoryId, mTargetDate, mGoalAction = "Create", mGoalId;
    private DatePickerDialog mDatePick;

    private boolean mIsRightDateFormate = false;
    private Calendar mCal;
    double w_delay = 0;
    double w_tot = 0;
    double amt_inv = 0;
    double nYears = 0, amtD = 0, rorD = 0, inflate = 0;
    private ToolbarFragment toolbarFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_creategoal, container, false);
        mActivity.setMainVisibility(this, null);
        mSession = AppSession.getInstance(mActivity);
        mBundle = getArguments();
        setUpToolBar();

        mTvCreate = view.findViewById(R.id.tv_createGoal);
        mTvCreate.setOnClickListener(this);

        mGoalImage = view.findViewById(R.id.iv_goalicon);
        mGoalName = view.findViewById(R.id.tv_goaltitle);
        mEt_GoalName = view.findViewById(R.id.et_goalname);
        mEt_GoalAmount = view.findViewById(R.id.et_targetamount);
        mEt_GoalDuration = view.findViewById(R.id.et_targetdate);
        mEt_GoalExpReturn = view.findViewById(R.id.et_expectedreturn);
        mEt_GoalInflation = view.findViewById(R.id.et_inflation);


        if (mBundle != null && mBundle.containsKey("category_data")) {
            try {
                JSONObject object = new JSONObject(mBundle.getString("category_data"));
                if (object.has("GoalID") && !object.optString("GoalID").equalsIgnoreCase("")) {
                    mGoalId = object.optString("GoalID");
                    mGoalAction = "Modify";
                    mTvCreate.setText(getResources().getString(R.string.create_goal_update_txt));

                    mGoalName.setText(object.optString("GoalCategory"));
                    String path = object.optString("GoalPic");
                    Picasso.get().load(path).placeholder(R.mipmap.tranparent).into(mGoalImage);
                    mEt_GoalName.setText(object.optString("GoalName"));
                    mEt_GoalAmount.setText(object.optString("GoalAmount").replace(",", ""));
                    mEt_GoalDuration.setText(object.optString("TargetDate").replaceAll("-", ""));
                    mEt_GoalExpReturn.setText(object.optString("ExpectedReturn"));
                    mEt_GoalInflation.setText(object.optString("Inflation"));
                    mGoalCategoryId = object.optString("GoalCategoryID");
                    mIsRightDateFormate = true;
                } else {
                    mGoalName.setText(object.optString("CategoryName"));
                    String path = object.optString("CategoryIcon");
                    Picasso.get().load(path).placeholder(R.mipmap.tranparent).into(mGoalImage);
                    mGoalCategoryId = object.optString("CategoryID");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //Toast.makeText(mActivity, "Please try again later on", Toast.LENGTH_SHORT).show();
            //mActivity.getSupportFragmentManager().popBackStack();
        }
        /*Date d = new Date();
        mCal = Calendar.getInstance();
        mCal.setTime(d);*/
//        cal.add(Calendar.YEAR, duration_years);
//        futureDate = cal.getTime();
//        mEt_GoalDuration.setText(String.valueOf(dateFormat_dmy.format(new Date())));


        mEt_GoalDuration.addTextChangedListener(new TextWatcher() {
            int beforeTextChangedLength;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeTextChangedLength = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                // text is being removed
                if (beforeTextChangedLength > length)
                    return;

                String str = editable.toString();

                if (str.length() == 10) {
                    if (validateDateFormat(str)) {
                        mIsRightDateFormate = true;
                    } else {
                        mIsRightDateFormate = false;
                        mEt_GoalDuration.setError(getResources().getString(R.string.create_goal_duration_txt));
                        mEt_GoalDuration.requestFocus();
                    }
                } else {
                    mIsRightDateFormate = false;
                    //  mEt_GoalDuration.setError("Please enter date of birth in form DD-MM-YYYY");
                }
            }
        });


//        createDialogWithoutDateField().show();
        return view;
    }

    private void setUpToolBar() {
        toolbarFragment = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.frag_toolBar);
        if (toolbarFragment != null) {
            toolbarFragment.setUpToolBar(getResources().getString(R.string.main_nav_title_define_your_goal), true, false, false, false, false, false, false,"");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.et_targetdate) {//                createDialogWithoutDateField().show();
//                datePick();
        } else if (id == R.id.tv_createGoal) {
            String goalname = mEt_GoalName.getText().toString();
            String goalamt = mEt_GoalAmount.getText().toString();
            String goaldate = mEt_GoalDuration.getText().toString();
            String goalreturn = mEt_GoalExpReturn.getText().toString();
            String goalinfla = mEt_GoalInflation.getText().toString();
            if (goalname.equals("")) {
                mEt_GoalName.setError(getResources().getString(R.string.create_goal_error_empty_goal_name));
                mEt_GoalName.requestFocus();
            } else if (goalamt.equals("") || goalamt.equals("0")) {
                if (Integer.valueOf(goalamt) < 1) {
                    mEt_GoalAmount.setError(getResources().getString(R.string.create_goal_error_inavalid_goal_amount));
                    mEt_GoalAmount.requestFocus();
                }
            } else if (goaldate.equals("")) {
                mEt_GoalDuration.setError(getResources().getString(R.string.create_goal_error_goal_date));
                mEt_GoalDuration.requestFocus();
            } else if (goaldate.length() < 10) {
                mEt_GoalDuration.setError(getResources().getString(R.string.create_goal_invalid_goal_date_format));
                mEt_GoalDuration.requestFocus();
            } else if (!mIsRightDateFormate) {
                mEt_GoalDuration.setError(getResources().getString(R.string.create_goal_empty_future_date));
                mEt_GoalDuration.requestFocus();
            } else if (goalreturn.equals("")) {
                mEt_GoalExpReturn.setError(getResources().getString(R.string.create_goal_error_return));
                mEt_GoalExpReturn.requestFocus();
            } else if (Double.valueOf(goalreturn) <= 0) {
                mEt_GoalExpReturn.setError(getResources().getString(R.string.create_goal_error_expected_rate));
                mEt_GoalExpReturn.requestFocus();
            } else if (Double.valueOf(goalinfla) < 0) {
                mEt_GoalInflation.setError(getResources().getString(R.string.create_goal_error_inflation_rate));
                mEt_GoalInflation.setText("");
                mEt_GoalInflation.requestFocus();
            } else if (Double.valueOf(goalinfla) > 30) {
                mEt_GoalInflation.setError(getResources().getString(R.string.create_goal_error_invalid_infaltion_rate));
                mEt_GoalInflation.setText("");
                mEt_GoalInflation.requestFocus();
            } else {
                createGoal();
            }
        } else if (id == R.id.ivLeft) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }


    private void createGoal() {
        String url = Config.GOAL_Create;
        JSONObject param = new JSONObject();
        try {
            param.put("Bid", AppConstants.APP_BID);
            param.put("Passkey", mSession.getPassKey());
            param.put("Cid", mSession.getCID());
//            param.put("Cid", "01C02581");
            param.put("GoalName", mEt_GoalName.getText().toString());
            param.put("GoalCategoryID", mGoalCategoryId);
            param.put("GoalAmount", mEt_GoalAmount.getText().toString());
            param.put("RiskProfileID", mSession.getRiskCode());
            param.put("Inflation", mEt_GoalInflation.getText().toString());
            param.put("ExpectedReturn", mEt_GoalExpReturn.getText().toString());
            param.put("Priority", "0");
            param.put("GoalID", mGoalId);
            param.put("GoalAction", mGoalAction);
            param.put("TargetDate", mEt_GoalDuration.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response.optBoolean("Status")) {
                    //Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                    mActivity.displayViewOther(70, null);
//                    next();
                } else {
                    Toast.makeText(mActivity, response.optString("ServiceMSG"), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    try {
                        JSONObject jsonObject = new JSONObject(error.getMessage());
                        Toast.makeText(mActivity, jsonObject.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof NoConnectionError)
                    Toast.makeText(mActivity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }

        });

        objectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(objectRequest);
    }


    private void datePick() {
        mCal = Calendar.getInstance();
        int year = mCal.get(Calendar.YEAR);
        int month = mCal.get(Calendar.MONTH);
        final int day = mCal.get(Calendar.DAY_OF_MONTH);
        mDatePick = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                mEt_GoalDuration.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
//                mTargetDate = year + "-" + (month + 1) + "-" + dayOfMonth;


                mCal.set(Calendar.YEAR, year);
                mCal.set(Calendar.MONTH, month);
                mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                mEt_GoalDuration.setText(String.valueOf(dateFormat_dmy.format(mCal.getTime())));
                mTargetDate = String.valueOf(dateFormat_ymd.format(mCal.getTime()));

            }
        }, year, month, day);
        mDatePick.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        mDatePick.show();

    }


    public boolean validateDateFormat(String dateToValdate) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);
        Date parsedDate = null;
        boolean isValid = false;
        try {
            parsedDate = formatter.parse(dateToValdate);
            Date today = new Date();
            if (parsedDate.after(today)) {
                isValid = true;
            } else {
                isValid = false;
            }

        } catch (ParseException e) {
            //Handle exception
            isValid = false;
        }
        return isValid;
    }


}
